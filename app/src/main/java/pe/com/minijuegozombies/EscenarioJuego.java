package pe.com.minijuegozombies;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Random;

import pe.com.minijuegozombies.databinding.ActivityEscenarioJuegoBinding;

public class EscenarioJuego extends AppCompatActivity {

    private ActivityEscenarioJuegoBinding bindingE;

    String UIDS, NOMBRES, ZOMBIES;

    int AnchoPantalla;
    int AltoPantalla;

    boolean GameOver =false;
    Dialog miDialog;

    Random aleatorio;

    int contador=0;

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference JUGADORES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escenario_juego);

        //mantiene vertical para que no afecte puntaje en pleno juego al cambiar orientacion de pantalla
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        bindingE = ActivityEscenarioJuegoBinding.inflate(getLayoutInflater());
        setContentView(bindingE.getRoot());

        miDialog= new Dialog(EscenarioJuego.this);

        firebaseAuth= FirebaseAuth.getInstance();
        user= firebaseAuth.getCurrentUser();
        firebaseDatabase= FirebaseDatabase.getInstance();
        JUGADORES= firebaseDatabase.getReference("DB JUGADORES");

        Bundle intent= getIntent().getExtras();
        UIDS= intent.getString("UID");
        NOMBRES= intent.getString("NOMBRE");
        ZOMBIES= intent.getString("ZOMBIE");

        bindingE.TvContador.setText(ZOMBIES);
        bindingE.TvNombre.setText(NOMBRES);

        Pantalla();
        CuentaAtras();

        /*AL HACER CLICK EN LA IMAGEN DE ZOMBIE*/
        bindingE.TvZombie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!GameOver) {
                    contador++;
                    bindingE.TvContador.setText(String.valueOf(contador));
                    bindingE.TvZombie.setImageResource(R.drawable.zimg);

                    /*para que vuelva a imagen inicial*/
                    new Handler().postDelayed(() -> {
                        bindingE.TvZombie.setImageResource(R.drawable.z1);
                        Movimiento();
                    }, 500);
                }
            }
        });

    }

    //para obtener tamaño de pantalla
    private void Pantalla(){
        Display display= getWindowManager().getDefaultDisplay();
        Point point=new Point();
        display.getSize(point);

        AnchoPantalla= point.x;
        AltoPantalla= point.y;

        String ANCHOS= String.valueOf(AnchoPantalla);
        String ALTOS= String.valueOf(AltoPantalla);

        bindingE.AnchoTv.setText(ANCHOS);
        bindingE.AltoTv.setText(ALTOS);

        aleatorio= new Random();
    }

    //para cargar imagen en otro lugar de la pantalla (dinamismo)
    private void Movimiento(){
        int min=0;
        int MaximoX= AnchoPantalla-bindingE.TvZombie.getWidth(); /*maximo coordenada x*/
        int MaximoY= AltoPantalla-bindingE.TvZombie.getHeight(); /*maximo coordenada y*/

        int randomX= aleatorio.nextInt(((MaximoX-min)+1)+min);
        int randomY= aleatorio.nextInt(((MaximoY-min)+1)+min);

        bindingE.TvZombie.setX(randomX);
        bindingE.TvZombie.setY(randomY);
    }

    //metodo para retroceder tiempo desde un tiempo establecido
    private void CuentaAtras(){
        new CountDownTimer(30000, 1000) {
            //se ejecuta cada segundo
            public void onTick(long millisUntilFinished) {
                long segundosRestantes= millisUntilFinished/1000;
                bindingE.TvTiempo.setText(segundosRestantes+"S");
            }
            //al acabar el tiempo
            public void onFinish() {
                bindingE.TvTiempo.setText("0S");
                GameOver = true;
                MensajeGameOver();
                guardarResultados("Zombies",contador);
            }
        }.start();
    }

    //esto se mostrará cuando finalice la partida
    private void MensajeGameOver() {
        miDialog.setContentView(R.layout.gameover);

        TextView numerotxt=miDialog.findViewById(R.id.numerotxt);
        Button jugarDeNuevo=miDialog.findViewById(R.id.jugarDeNuevo);
        Button irMenu=miDialog.findViewById(R.id.irMenu);
        Button puntaje=miDialog.findViewById(R.id.puntajes);

        String zombies= String.valueOf(contador);
        numerotxt.setText(zombies);

        jugarDeNuevo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                contador=0;
                miDialog.dismiss();
                bindingE.TvContador.setText("0");
                GameOver= false;
                CuentaAtras();
                Movimiento();
            }
        });

        irMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EscenarioJuego.this, Menu.class));
            }
        });

        puntaje.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EscenarioJuego.this, Puntajes.class));
                //Toast.makeText(EscenarioJuego.this, "PUNTRAJE", Toast.LENGTH_SHORT).show();
            }
        });

        miDialog.show();
        miDialog.setCancelable(false);
    }

    //actualiza resultados en db mendiante HashMap al final de partida
    private void guardarResultados(String key, int zombies){
        HashMap<String,Object> hashMap= new HashMap<>();
        hashMap.put(key,zombies);
        JUGADORES.child(user.getUid()).updateChildren(hashMap).
                addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(EscenarioJuego.this,"El puntaje ha sido actualizado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
}