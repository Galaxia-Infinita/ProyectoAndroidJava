package pe.com.minijuegozombies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import pe.com.minijuegozombies.databinding.ActivityRegistroBinding;

public class Registro extends AppCompatActivity {

    private ActivityRegistroBinding bindingR;

    FirebaseAuth auth;  //autentificacion

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        bindingR = ActivityRegistroBinding.inflate(getLayoutInflater());
        setContentView(bindingR.getRoot());

        auth=FirebaseAuth.getInstance();

        Date date=new Date();
            SimpleDateFormat fecha=new SimpleDateFormat("d 'de' MMMM 'del' yyyy"); // d/m/a
        String stringfecha=fecha.format(date);

        bindingR.fechaTxt.setText(stringfecha);
        bindingR.Registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email= bindingR.correoEt.getText().toString();
                String password= bindingR.passEt.getText().toString();

                /*Validacion correo*/
                if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                    bindingR.correoEt.setError("Correo Inválido");
                    bindingR.correoEt.setFocusable(true);
                }
                /*Validacion contraseña*/
                else if(password.length()<6){
                    bindingR.passEt.setError("mayor a 6 digitos");
                    bindingR.passEt.setFocusable(true);
                }
                else{
                    RegistrarJugador(email,password);
                }
            }
        });

        progressDialog= new ProgressDialog(Registro.this);
        progressDialog.setMessage("Registrando, un momento");
        progressDialog.setCancelable(false);
    }

    /*registrar jugador*/
    private void RegistrarJugador(String email, String password) {
        progressDialog.show();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        /*si fue registrado*/
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            FirebaseUser user= auth.getCurrentUser();

                            int contador=0;

                            assert user !=null; //usuario no es nulo

                            // strings que seran registradas en firebase
                            String uidString= user.getUid();
                            String correoString= bindingR.correoEt.getText().toString();
                            String passString= bindingR.passEt.getText().toString();
                            String nombreString= bindingR.nombreEt.getText().toString();
                            String edadString= bindingR.edadET.getText().toString();
                            String paisString= bindingR.paisEt.getText().toString();
                            String fechaString= bindingR.fechaTxt.getText().toString();

                            //hacer posible el envio de datos a firebase con claves
                            HashMap<Object, Object> DatosJugador= new HashMap<>();
                            DatosJugador.put("Uid",uidString);
                            DatosJugador.put("Email",correoString);
                            DatosJugador.put("Password",passString);
                            DatosJugador.put("Nombre",nombreString);
                            DatosJugador.put("Edad",edadString);
                            DatosJugador.put("Pais",paisString);
                            DatosJugador.put("Imagen","");
                            DatosJugador.put("Fecha",fechaString);
                            DatosJugador.put("Zombies",contador);

                            FirebaseDatabase database= FirebaseDatabase.getInstance(); //instanciando DB
                            DatabaseReference reference= database.getReference("DB JUGADORES"); //nombre DB
                            reference.child(uidString).setValue(DatosJugador);
                            //redireccionar al menu del juego
                            startActivity(new Intent(Registro.this,Menu.class));
                            Toast.makeText(Registro.this, "USUARIO REGISTRADO", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                        else {
                            progressDialog.dismiss();
                            Toast.makeText(Registro.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Registro.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}