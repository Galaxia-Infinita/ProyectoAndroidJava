package pe.com.minijuegozombies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import pe.com.minijuegozombies.databinding.ActivityCambiarContraseniaBinding;
import pe.com.minijuegozombies.databinding.ActivityMenuBinding;

public class CambiarContrasenia extends AppCompatActivity {
    private ActivityCambiarContraseniaBinding bindingC;

    DatabaseReference BASEDATOS;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_contrasenia);

        bindingC = ActivityCambiarContraseniaBinding.inflate(getLayoutInflater());
        setContentView(bindingC.getRoot());

        BASEDATOS= FirebaseDatabase.getInstance().getReference("DB JUGADORES");
        firebaseAuth= FirebaseAuth.getInstance();
        user= FirebaseAuth.getInstance().getCurrentUser();

        bindingC.cambiarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String actual=bindingC.actualPass.getText().toString().trim();
                String nuevo=bindingC.nuevoPass.getText().toString().trim();

                if(TextUtils.isEmpty(actual)){
                    Toast.makeText(CambiarContrasenia.this, "LLenar campo actual password", Toast.LENGTH_SHORT).show();
                }
                if(TextUtils.isEmpty(nuevo)){
                    Toast.makeText(CambiarContrasenia.this, "LLenar campo nuevo password", Toast.LENGTH_SHORT).show();
                }
                if(!TextUtils.isEmpty(actual) && !TextUtils.isEmpty(nuevo) && actual.length()>=6 && nuevo.length()>=6){
                    CambioPass(actual,nuevo);
                }
            }
        });
    }

    private void CambioPass(String actual, String nuevo) {
        AuthCredential authCredential= EmailAuthProvider.getCredential(user.getEmail(), actual);
        user.reauthenticate(authCredential)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void avoid) {
                        user.updatePassword(nuevo)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        String value= bindingC.nuevoPass.getText().toString().trim();
                                        HashMap<String, Object> result= new HashMap<>();
                                        result.put("Password",value);
                                        BASEDATOS.child(user.getUid()).updateChildren(result)
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void unused) {

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(CambiarContrasenia.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                                firebaseAuth.signOut();
                                                startActivity(new Intent(CambiarContrasenia.this, MainActivity.class));
                                                Toast.makeText(CambiarContrasenia.this, "Se ha cerrado sesión", Toast.LENGTH_SHORT).show();
                                                finish();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(CambiarContrasenia.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(CambiarContrasenia.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}