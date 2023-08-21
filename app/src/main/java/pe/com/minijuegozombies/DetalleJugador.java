package pe.com.minijuegozombies;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

import com.squareup.picasso.Picasso;

import pe.com.minijuegozombies.databinding.ActivityDetalleJugadorBinding;
import pe.com.minijuegozombies.databinding.ActivityMenuBinding;

public class DetalleJugador extends AppCompatActivity {
    private ActivityDetalleJugadorBinding bindingD;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_jugador);

        bindingD = ActivityDetalleJugadorBinding.inflate(getLayoutInflater());
        setContentView(bindingD.getRoot());


        setSupportActionBar(bindingD.toolbarDetalles);

        ActionBar actionBar= getSupportActionBar();
        actionBar.setTitle("Detalle Jugador");
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        //recuperando los extras del Adaptador
        String imagen= getIntent().getStringExtra("Imagen");
        String nombre= getIntent().getStringExtra("Nombres");
        String correo= getIntent().getStringExtra("Email");
        String puntaje= getIntent().getStringExtra("Zombies");
        String edad= getIntent().getStringExtra("Edad");
        String pais= getIntent().getStringExtra("Pais");

        //seteo de datos para cajas de DetalleJugador
        bindingD.nombreDetalle.setText("Nombres "+nombre);
        bindingD.correoDetalle.setText("Correo: "+correo);
        bindingD.puntajeDetalle.setText("Puntaje: "+puntaje);
        bindingD.edadDetalle.setText("Edad: "+edad);
        bindingD.paisDetalle.setText("Pais: "+pais);

        //gestionar imagen
        try {
            Picasso.get().load(imagen).into(bindingD.imagenDetalle);

        }catch (Exception e){
            Picasso.get().load(R.drawable.jug).into(bindingD.imagenDetalle);
        }
    }
}