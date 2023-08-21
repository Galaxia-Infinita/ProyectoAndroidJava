package pe.com.minijuegozombies;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class Adaptador extends RecyclerView.Adapter<Adaptador.MyHolder>{
    private Context context;
    private List<Usuario> usuarioList;

    //constructor
    public Adaptador(Context context, List<Usuario> usuarioList) {
        this.context = context;
        this.usuarioList = usuarioList;
    }

    //para inflar diseño del layout jugador en recyclerview
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.jugadores,parent,false);
        return new MyHolder(view);
    }

    //para obtener los datos del modelo clase usuario
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int i) {
        final String imagen= usuarioList.get(i).getImagen();
        final String nombres= usuarioList.get(i).getNombre();
        final String correo= usuarioList.get(i).getEmail();
        final String edad= usuarioList.get(i).getEdad();
        final String pais= usuarioList.get(i).getPais();
        int zombies= usuarioList.get(i).getZombies();
        //conversion a string para poder mostrarlo en EditText
        final String z = String.valueOf(zombies);

        //pasando datos a EditTexts
        holder.nombreJugador.setText(nombres);
        holder.correoJugador.setText(correo);
        holder.edadJugador.setText(edad);
        holder.paisJugador.setText(pais);
        holder.puntajeJugador.setText(z);

        //imagen de los jugadores
        try {
            //se cargará si el usuario tiene foto de perfil
            Picasso.get().load(imagen).into(holder.imagenJugador);

        }catch (Exception e){
            //si no tiene foto de perfil
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context, DetalleJugador.class);
                intent.putExtra("Imagen",imagen);
                intent.putExtra("Nombres",nombres);
                intent.putExtra("Email",correo);
                intent.putExtra("Edad",edad);
                intent.putExtra("Pais",pais);
                intent.putExtra("Zombies",z);

                context.startActivity(intent);
                Toast.makeText(context, ""+correo, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return usuarioList.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder{
        CircleImageView imagenJugador;
        TextView nombreJugador, correoJugador, puntajeJugador, edadJugador, paisJugador;
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            //INICIALIZAR
            imagenJugador=itemView.findViewById(R.id.imagenJugador);
            nombreJugador=itemView.findViewById(R.id.nombreJugador);
            correoJugador=itemView.findViewById(R.id.correoJugador);
            puntajeJugador=itemView.findViewById(R.id.puntajeJugador);
            edadJugador=itemView.findViewById(R.id.edadJugador);
            paisJugador=itemView.findViewById(R.id.paisJugador);
        }
    }
}
