package pe.com.minijuegozombies;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        int DURACION_SPLASH= 1500;

        //handler ejecuta linea de codigo en tiempo determinado
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= new Intent(Splash.this,Menu.class);
                startActivity(intent);
            }
        },DURACION_SPLASH);
    }
}