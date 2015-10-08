package gui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import Webs.Cancion;
import ppn.com.mp3down.Historial;
import ppn.com.mp3down.Principal;
import ppn.com.mp3down.R;


public class itemHistorial extends LinearLayout {

    private Cancion cancion;
    private Context context;

    public itemHistorial(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public itemHistorial(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public itemHistorial(Context contexto, Cancion c) {
        super(contexto.getApplicationContext());
        cancion=c;
        context=contexto;
        inicializa();
    }



    public void inicializa() {

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li =(LayoutInflater)getContext().getSystemService(infService);
        li.inflate(R.layout.item_historial, this, true);

        TextView nombreCancion = (TextView) findViewById(R.id.txtNombre);

        TextView artistaCancion = (TextView) findViewById(R.id.txtArtista);

        TextView calidad = (TextView) findViewById(R.id.txtCalidad);

        nombreCancion.setText(cancion.getTitle());

        artistaCancion.setText(cancion.getArtist());

        if (cancion.getCalidad()!=0){

            if (cancion.getCalidad()>=320){
                calidad.setBackgroundColor(Color.GREEN);
            }
            if (cancion.getCalidad()>128 && cancion.getCalidad()<320 ){
                calidad.setBackgroundColor(Color.YELLOW);
            }
            if (cancion.getCalidad()<=128){
                calidad.setBackgroundColor(Color.RED);
            }

        }
        else{
            calidad.setVisibility(INVISIBLE);
        }

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                Principal.cambiaCancion(cancion, 0, false);
                ((Activity)context).finish();
            }
        });

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                ((Historial)context).eliminaCancion(cancion);
                return true;
            }
        });



    }

    public void inicializaTexto(String s) {

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li =(LayoutInflater)getContext().getSystemService(infService);
        li.inflate(R.layout.item_cancion_vacio, this, true);

        TextView mensaje = (TextView) findViewById(R.id.txtNombreMensaje);
        mensaje.setText(s);

    }

}


