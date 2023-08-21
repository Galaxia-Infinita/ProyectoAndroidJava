package pe.com.minijuegozombies;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import pe.com.minijuegozombies.databinding.ActivityMenuBinding;
import pe.com.minijuegozombies.databinding.ActivityRegistroBinding;

public class Menu extends AppCompatActivity {

    private ActivityMenuBinding bindingM;

    FirebaseAuth auth;
    FirebaseUser user;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference JUGADORES;

    Dialog dialog;

    private StorageReference referenciaDeAlmacenamiento;
    private String rutaAlmacenamiento= "FotosDePerfil/*";

    /*PERMISOS PARA ACEEDER Y CAMBIAR IMAGEN*/
    private static final int CODIGO_DE_SOLICITUD_DE_ALMACENAMIENTO = 200;
    private static final int CODIGO_PARA_LA_SELECCION_DE_LA_IMAGEN = 300;

    /*MATRICES PARA CAMBIO DE IMAGEN*/
    private String [] permisoDeAlmacenamiento;
    private Uri imagen_url;
    private String perfil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        bindingM = ActivityMenuBinding.inflate(getLayoutInflater());
        setContentView(bindingM.getRoot());

        auth=FirebaseAuth.getInstance();
        user=auth.getCurrentUser();

        firebaseDatabase = FirebaseDatabase.getInstance();
        JUGADORES= firebaseDatabase.getReference("DB JUGADORES");

        dialog= new Dialog(Menu.this);

        referenciaDeAlmacenamiento = FirebaseStorage.getInstance().getReference();
        permisoDeAlmacenamiento = new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE};

        bindingM.jugarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Menu.this, "Jugar", Toast.LENGTH_SHORT).show();
                Intent intent= new Intent(Menu.this, EscenarioJuego.class);

                String UidS = bindingM.uid.getText().toString();
                String NombreS = bindingM.nombre.getText().toString();
                String ZombieS = bindingM.zombies.getText().toString();

                intent.putExtra("UID",UidS);
                intent.putExtra("NOMBRE",NombreS);
                intent.putExtra("ZOMBIE",ZombieS);

                startActivity(intent);
                Toast.makeText(Menu.this, "ENVIANDO PARAMETROS", Toast.LENGTH_SHORT).show();
            }
        });

        bindingM.editarBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Menu.this,"EDITAR", Toast.LENGTH_SHORT).show();
                editarDatos();
            }
        });

        bindingM.cambiarPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Menu.this,CambiarContrasenia.class));
                Toast.makeText(Menu.this,"CAMBIAR", Toast.LENGTH_SHORT).show();
            }
        });

        bindingM.puntuacionesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(Menu.this,Puntajes.class);
                startActivity(intent);
                Toast.makeText(Menu.this, "Puntuaciones", Toast.LENGTH_SHORT).show();
            }
        });

        bindingM.acercaDeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acercaDe();
                //Toast.makeText(Menu.this, "Acerca de", Toast.LENGTH_SHORT).show();
            }
        });

        bindingM.cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cerrarSesion();
            }
        });
    }

    //metodo solo para acerca de y mostrar developer creador
    private void acercaDe() {
        Button ok;

        dialog.setContentView(R.layout.acercade);

        ok= dialog.findViewById(R.id.ok);

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    //metodo para cambiar o editar los datos
    private void editarDatos() {
        //arreglo con opciones a elegir
        String[] opciones= {"Foto de perfil","Cambiar edad","Cambiar pais"};

        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(i == 0){
                    perfil= "Imagen";
                    actualizarFotoPerfil();
                }
                if(i == 1){
                    actualizarEdad("Edad");
                }
                if(i == 2){
                    actualizarPais("Pais");
                }
            }
        });
        builder.create().show();
    }

    private void actualizarFotoPerfil() {
        String [] opciones= {"Galeria"};
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Seleccionar imagen de: ");
        builder.setItems(opciones, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialog, int i) {
                if(i == 0){
                    //SELECCIONO GALERIA
                    if(!comprobarPermisoAlmacenamiento()){
                        //si no se habilitó el permiso
                        solicitarPermisosAlmacenamiento();
                    }else{
                        //si se habilitó el permiso
                        elegirImagenDeGaleria();
                    }
                }
            }
        });
        builder.create().show();
    }

    //este metodo abre la galeria, usado en metodo actualizarFotoPerfil
    private void elegirImagenDeGaleria() {
        Intent intentGaleria= new Intent(Intent.ACTION_PICK);
        intentGaleria.setType("image/*");
        startActivityForResult(intentGaleria, CODIGO_PARA_LA_SELECCION_DE_LA_IMAGEN);
    }

    //comprobar permisos de almacenamiento habilitados o no, usado en actualizarFotoPerfil
    private boolean comprobarPermisoAlmacenamiento() {
        boolean resultado= ContextCompat.checkSelfPermission(Menu.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                (PackageManager.PERMISSION_GRANTED);
        return resultado;
    }

    //se llama cuando usuario permita o deniegue cuadro dialogo
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case  CODIGO_DE_SOLICITUD_DE_ALMACENAMIENTO:{
                //seleccion de la galeria
                if(grantResults.length >0){
                    boolean escrituraDeAlmacenamientoAceptado=grantResults[0]==PackageManager.PERMISSION_GRANTED;

                    if(escrituraDeAlmacenamientoAceptado){
                        //permiso fue habilitado
                        elegirImagenGaleria();
                    }else{
                        //si usuario dijo que no
                        Toast.makeText(this,"HABILITE EL PERMISO A GALERIA", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    //se llama cuando jugador ya ha elegido imagen de galeria
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode == RESULT_OK){
            //obtener URI de imagen
            if(requestCode == CODIGO_PARA_LA_SELECCION_DE_LA_IMAGEN){
                imagen_url = data.getData();
                subirFoto(imagen_url);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    //este metodo cambia foto perfil y actualiza la db (para Imagen)
    private void subirFoto(Uri imagen_url) {
        String rutaDeArchivoNombre= rutaAlmacenamiento +""+perfil+""+user.getUid();
        StorageReference storageReference= referenciaDeAlmacenamiento.child(rutaDeArchivoNombre);
        storageReference.putFile(imagen_url)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Task<Uri> uriTask= taskSnapshot.getStorage().getDownloadUrl();
                        while (!uriTask.isSuccessful());
                        Uri download= uriTask.getResult();
                        if(uriTask.isSuccessful()){
                            HashMap<String,Object> resultado= new HashMap<>();
                            resultado.put(perfil, download.toString());
                            JUGADORES.child(user.getUid()).updateChildren(resultado)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(Menu.this, "LA IMAGEN HA SIDO CAMBIADA", Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(Menu.this, "HA OCURRIDO UN ERROR", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }else {
                            Toast.makeText(Menu.this, "ALGO HA SALIDO MAL", Toast.LENGTH_SHORT).show();
                        }

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Menu.this, "ALGO HA SALIDO MAL", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //este metodo abre la galeria, usado en actualizarFotoPerfil
    private void elegirImagenGaleria() {
        Intent intentGaleria= new Intent(Intent.ACTION_PICK);
        intentGaleria.setType("imagen/*");
        startActivityForResult(intentGaleria, CODIGO_PARA_LA_SELECCION_DE_LA_IMAGEN);
    }

    //permiso de almacenamiento en tiempo de ejecucion, usado en actualizarFotoPerfil
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void solicitarPermisosAlmacenamiento() {
        requestPermissions(permisoDeAlmacenamiento,CODIGO_DE_SOLICITUD_DE_ALMACENAMIENTO);
    }

    //cambio de edad, para metodo editarDatos
    private void actualizarEdad(final String key) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Cambiar: "+key);
        LinearLayoutCompat linearLayoutCompat= new LinearLayoutCompat(this);
        linearLayoutCompat.setOrientation(LinearLayoutCompat.VERTICAL);
        linearLayoutCompat.setPadding(10,10,10,10);
        EditText editText= new EditText(this);
        editText.setHint("Ingrese "+key);
        linearLayoutCompat.addView(editText);
        builder.setView(linearLayoutCompat);

        //click en actualizar
        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value= editText.getText().toString().trim();
                HashMap<String,Object> result= new HashMap<>();
                result.put(key,value);
                JUGADORES.child(user.getUid()).updateChildren(result)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void avoid) {
                                Toast.makeText(Menu.this,"EDAD ACTUALIZADA", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Menu.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Menu.this,"CANCELADO POR USUARIO", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }

    //cambio de pais, para metodo editarDatos
    private void actualizarPais(final String key) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        builder.setTitle("Cambiar: "+key);
        LinearLayoutCompat linearLayoutCompat= new LinearLayoutCompat(this);
        linearLayoutCompat.setOrientation(LinearLayoutCompat.VERTICAL);
        linearLayoutCompat.setPadding(10,10,10,10);
        EditText editText= new EditText(this);
        editText.setHint("Ingrese "+key);
        linearLayoutCompat.addView(editText);
        builder.setView(linearLayoutCompat);

        //click en actualizar
        builder.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String value= editText.getText().toString().trim();
                HashMap<String,Object> result= new HashMap<>();
                result.put(key,value);
                JUGADORES.child(user.getUid()).updateChildren(result)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void avoid) {
                                Toast.makeText(Menu.this,"PAIS ACTUALIZADO", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(Menu.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(Menu.this,"CANCELADO POR USUARIO", Toast.LENGTH_SHORT).show();
            }
        });
        builder.create().show();
    }

    //se ejecuta cuando se abre el minijuego
    @Override
    protected void onStart(){
        usuarioLogeado();
        super.onStart();
    }

    //comprueba si jugador iniciado sesion
    private void usuarioLogeado(){
        if(user != null){
            consulta();
            Toast.makeText(this, "Jugador en Linea", Toast.LENGTH_SHORT).show();
        }
        else{
            startActivity(new Intent(Menu.this,MainActivity.class));
            finish();
        }
    }

    //cerrar sesion
    private void cerrarSesion(){
        auth.signOut();
        startActivity(new Intent(Menu.this, MainActivity.class));
        Toast.makeText(this, "Sesión Cerrada", Toast.LENGTH_SHORT).show();
    }

    //metodo consulta
    private void consulta(){
        //consulta
        Query query= JUGADORES.orderByChild("Email").equalTo(user.getEmail());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot ds: snapshot.getChildren()){
                    //obtencion de datos
                    String zombiesString= ""+ds.child("Zombies").getValue();
                    String uidString= ""+ds.child("Uid").getValue();
                    String nombreString= ""+ds.child("Nombre").getValue();
                    String emailString= ""+ds.child("Email").getValue();
                    String edadString= ""+ds.child("Edad").getValue();
                    String paisString= ""+ds.child("Pais").getValue();
                    String imagen= ""+ds.child("Imagen").getValue();
                    String fechaString= ""+ds.child("Fecha").getValue();

                    //seteo de datos
                    bindingM.zombies.setText(zombiesString);
                    bindingM.uid.setText(uidString);
                    bindingM.correo.setText(emailString);
                    bindingM.nombre.setText(nombreString);
                    bindingM.edad.setText(edadString);
                    bindingM.pais.setText(paisString);
                    bindingM.fecha.setText(fechaString);

                    try {
                        Picasso.get().load(imagen).into(bindingM.imagenPerfil);
                    }catch (Exception e){
                        Picasso.get().load(R.drawable.jug).into(bindingM.imagenPerfil);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}