package pe.com.minijuegozombies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Puntajes extends AppCompatActivity {
    LinearLayoutManager mLayoutManager;
    RecyclerView recyclerViewUsuarios;
    Adaptador adaptador;
    List<Usuario> usuarioList;

    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_puntajes);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBar actionBar= getSupportActionBar();
        actionBar.setTitle("Puntajes");

        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        mLayoutManager = new LinearLayoutManager(this);
        firebaseAuth = FirebaseAuth.getInstance();
        recyclerViewUsuarios= findViewById(R.id.recyclerViewUsuarios);

        mLayoutManager.setReverseLayout(true); //ordena de Z-A
        mLayoutManager.setStackFromEnd(true); //empieza desde arriba sin tener deliz
        recyclerViewUsuarios.setHasFixedSize(true);
        recyclerViewUsuarios.setLayoutManager(mLayoutManager);
        usuarioList= new ArrayList<>();

        obtenerTodosUsuarios();

    }

    private void obtenerTodosUsuarios() {
        FirebaseUser fUser= FirebaseAuth.getInstance().getCurrentUser(); //para obtener usuario actual
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("DB JUGADORES");
        ref.orderByChild("Zombies").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usuarioList.clear();
                for(DataSnapshot ds: snapshot.getChildren()){
                    //del modelo Usuario
                    Usuario usuario= ds.getValue(Usuario.class);

                    /*if(!usuario.getUid().equals(fUser.getUid())){
                        usuarioList.add(usuario)
                    }*/

                    usuarioList.add(usuario);

                    adaptador= new Adaptador(Puntajes.this, usuarioList);
                    recyclerViewUsuarios.setAdapter(adaptador);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}