package gui;

import android.animation.Animator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import ppn.com.mp3down.Descargas;
import ppn.com.mp3down.Principal;
import ppn.com.mp3down.R;


public class Detalles extends ActionBarActivity {

    public static ProgressDialog dialogoDescarga;
    public static TextView nombreCancion,nombreArtista, duracion,calidad;
    public static ImageView portada,cargando;
    public static RelativeLayout rootLayout;
    public static Button btnDescarga;
    public Context contexto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT>=21){
            getWindow().setSharedElementEnterTransition(TransitionInflater.from(this).inflateTransition(R.transition.change_image_transform));
        }

        overridePendingTransition(0, 0);

        setContentView(R.layout.ventana_detalles);

        rootLayout =(RelativeLayout) findViewById(R.id.detalllesLayout);

        if (savedInstanceState == null) {
            rootLayout.setVisibility(View.INVISIBLE);

            ViewTreeObserver viewTreeObserver = rootLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        circularRevealActivity();
                    }
                });
            }
        }


        portada =(ImageView) findViewById(R.id.portada);
        portada.setImageDrawable(Principal.reproductorMini.getPortada());


        nombreCancion = (TextView) findViewById(R.id.txtNombreCancion);
        nombreArtista = (TextView) findViewById(R.id.txtNombreArtista);
        duracion = (TextView) findViewById(R.id.txtDuracion);
        calidad = (TextView) findViewById(R.id.txtCalidad);

        btnDescarga = (Button) findViewById(R.id.btnDescarga);

        nombreArtista.setText(Principal.cancion.getArtist());
        nombreCancion.setText(Principal.cancion.getTitle());
        duracion.setText(Principal.cancion.getDuracion());
        calidad.setText(Principal.cancion.getCalidad()+" bpm");

        btnDescarga.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Descargas.addDescarga(Principal.cancion);
                finish();
                Intent i = new Intent(Detalles.this,Descargas.class);
                startActivity(i);

            }
        });

    }

    private void circularRevealActivity() {

        if (Build.VERSION.SDK_INT>=21) {

            int cx = 0;
            int cy = rootLayout.getHeight();

            float finalRadius = Math.max(rootLayout.getWidth(), rootLayout.getHeight());

            // create the animator for this view (the start radius is zero)
            Animator circularReveal = ViewAnimationUtils.createCircularReveal(rootLayout, cx, cy, 0, finalRadius);
            circularReveal.setDuration(500);

            // make the view visible and start the animation
            rootLayout.setVisibility(View.VISIBLE);
            circularReveal.start();

        }
    }




    //////////////////////////////////////////////////////////////////////////


}
