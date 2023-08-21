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

import pe.com.minijuegozombies.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding bindingL;

    FirebaseAuth auth; //usado aqui solo para login

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindingL = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bindingL.getRoot());

        // click en login
        bindingL.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bindingL.btnLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String email= bindingL.correoLogin.getText().toString();
                        String pass= bindingL.passLogin.getText().toString();

                        auth=FirebaseAuth.getInstance(); //intanciando

                        /*Validacion correo*/
                        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                            bindingL.correoLogin.setError("Correo Inválido");
                            bindingL.correoLogin.setFocusable(true);
                        }
                        /*Validacion contraseña*/
                        else if(pass.length()<6){
                            bindingL.passLogin.setError("mayor a 6 digitos");
                            bindingL.passLogin.setFocusable(true);
                        }
                        else{
                            LogearJugador(email,pass);
                        }
                    }
                });

                progressDialog= new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Iniciando, un momento");
                progressDialog.setCancelable(false);
            }
        });

        bindingL.btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity.this,Registro.class);
                startActivity(intent);
            }
        });
    }

    // metodo verificacion de datos para login
    private void LogearJugador(String email, String pass) {
        progressDialog.show();
        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            progressDialog.dismiss();
                            FirebaseUser user= auth.getCurrentUser(); //autenticando usuario actual
                            startActivity(new Intent(MainActivity.this, Menu.class));
                            assert user !=null; //afirmamos usuario no nulo
                            Toast.makeText(MainActivity.this, "BIENVENIDO(A) "+ user.getEmail(), Toast.LENGTH_SHORT).show();
                            finish();
                        }else {
                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Ha ocurrido un error", Toast.LENGTH_SHORT).show();
                        }
                    } //si es que falla el logeo mostrar el error
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}