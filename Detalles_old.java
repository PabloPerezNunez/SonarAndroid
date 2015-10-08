package gui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;

import java.util.Date;

import Webs.Cancion;
import Webs.CancionEvent;
import Webs.CancionListener;
import ppn.com.mp3down.Descargas;
import ppn.com.mp3down.Principal;
import ppn.com.mp3down.R;



public class Detalles_old extends RelativeLayout implements CancionListener {

    public static ProgressDialog dialogoDescarga;
    public static TextView nombreCancion,nombreArtista, duracion,posicionReproduccion;
    public static ImageView portada,portada2,cargando;
    public static View fondoTexto;
    public static RelativeLayout error;
    public static FloatingActionButton btnDescarga;
    public Context contexto;


    public Detalles_old(Context context) {
        super(context);
        init();
    }

    public Detalles_old(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public Detalles_old(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init(){
        inflate(getContext(), R.layout.ventana_detalles, this);

        contexto=getContext();


        portada =(ImageView) findViewById(R.id.portada);


        nombreCancion = (TextView) findViewById(R.id.txtNombreCancion);
        nombreArtista = (TextView) findViewById(R.id.txtNombreArtista);

        //creaReproductor();

        btnDescarga.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                try {

                    Descargas.addDescarga(Principal.cancion);

                    Intent i = new Intent(contexto,Descargas.class);
                    contexto.startActivity(i);

                    btnDescarga.hide(true);



                } catch (Exception e) {
                    e.printStackTrace();
                }
                
            }
        });

        dialogoDescarga  = new ProgressDialog(contexto);
        dialogoDescarga.setTitle("Descargando");
        dialogoDescarga.setIndeterminate(false);
        dialogoDescarga.setMax(100);
        dialogoDescarga.setProgress(0);
        dialogoDescarga.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        error.setVisibility(INVISIBLE);


    }

    public static String miliToString(int milisegundos){

        Date date = new Date(milisegundos);
        return String.format("%tM:%tS",date,date);

    }

    public void cambiaCancion(Cancion c) {

        //Se restablece la pantalla de detalles
        error.setVisibility(INVISIBLE);
        btnDescarga.hide(false);
        //Principal.btnReproductor.hide(false);
        portada.setImageDrawable(contexto.getResources().getDrawable(R.mipmap.vinyl));

        nombreCancion.setText(c.getTitle());
        nombreArtista.setText(c.getArtist());

    }

    //////////////////////////////////////////////////////////////////////////


    @Override
    public void cancionSeleccionada(CancionEvent e) {
        Principal.log("CANCION SELECCIONADA");
        Handler handler = new Handler(Looper.getMainLooper());
//
//        handler.post(new Runnable() {
//            @Override
//            public void run() {
//                btnDescarga.hide(false);
//            }
//        });
    }

    @Override
    public void cancionStartBuffered(CancionEvent e) {
        Principal.log("CANCION CARGADA EN REPRODUCTOR");
        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {
                cargando.setVisibility(VISIBLE);
                Animation anim = AnimationUtils.loadAnimation(contexto, R.anim.rotate);
                cargando.clearAnimation();
                cargando.setAnimation(anim);
                cargando.animate();
            }
        });
    }

    @Override
    public void cancionBuffered(CancionEvent e) {
        Principal.log("CANCION BUFFERED");
        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {
            @Override
            public void run() {
                cargando.setVisibility(INVISIBLE);
                cargando.setAnimation(null);
                //Principal.btnReproductor.show(true);

            }
        });
    }

    @Override
    public void cancionPreparada(CancionEvent e) {
        Principal.log("CANCION PREPARADA");

        Handler handler = new Handler(Looper.getMainLooper());
        final Boolean download_only_wifi =Principal.preferencias.getBoolean(Principal.OPCION_DESCARGA_WIFI, true);

        handler.post(new Runnable() {
            @Override
            public void run() {
                //Se activa la descarga si se puede
                if ((download_only_wifi && Principal.hasWIFIConnection()) || (!download_only_wifi)) {
                    btnDescarga.show(true);
                }
            }
        });

    }

    @Override
    public void cancionError(CancionEvent e) {
        Principal.log("CANCION ERROR:"+e.getSource().getTitle());

        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                error.setVisibility(VISIBLE);
                cargando.setVisibility(INVISIBLE);
            }
        });
    }


    //////////////////////////////////////////////////////////////////////////


}
