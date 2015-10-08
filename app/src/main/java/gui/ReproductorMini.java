package gui;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.RemoteViews;
import android.widget.TextView;

import Webs.Cancion;
import Webs.CancionEvent;
import Webs.CancionListener;
import ppn.com.mp3down.HiloSpotifyCover;
import ppn.com.mp3down.NotificationBroadcast;
import ppn.com.mp3down.Principal;
import ppn.com.mp3down.R;

public class ReproductorMini extends RelativeLayout implements CancionListener {

    private Context contexto;
    private Notification notification;
    private NotificationManager nm;
    public static TextView nombreCancion,nombreArtista;
    public static ImageButton botonPlay;
    public static ImageView portada;
    public static ProgressBar progressCancion, progressBuffer;
    public Handler handler;

    public static final String NOTIFY_PLAY = "notification.play";
    public static final String NOTIFY_NEXT = "notification.next";


    public ReproductorMini(Context context) {
        super(context);
        init(context);
    }
    public ReproductorMini(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);

    }
    public ReproductorMini(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);

    }
    public void init(final Context context) {
        inflate(getContext(), R.layout.reproductor, this);
        contexto = context;

        handler = new Handler(Looper.getMainLooper());

        nombreCancion = (TextView) findViewById(R.id.txtTitulo);
        nombreArtista = (TextView) findViewById(R.id.txtArtista);

        portada = (ImageView) findViewById(R.id.portada);

        botonPlay = (ImageButton) findViewById(R.id.btnPlay);

        progressCancion = (ProgressBar) findViewById(R.id.progresoCancion);
        progressBuffer= (ProgressBar) findViewById(R.id.progresoBuffer);

        botonPlay.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Principal.cancion.play();
            }
        });

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Build.VERSION.SDK_INT >= 21) {

                    Intent i = new Intent(context, Detalles.class);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation((Activity) context,
                            Pair.create((View) portada, "sharedportada"));
                    context.startActivity(i, options.toBundle());

                } else {
                    Intent i = new Intent(context, Detalles.class);
                    context.startActivity(i);
                }

            }
        });


    }

    public void showNotification() {

        RemoteViews simpleView = new RemoteViews(contexto.getPackageName(), R.layout.notificacion_audio);

        notification = new NotificationCompat.Builder(contexto).setSmallIcon(R.drawable.ic_stat_name).build();
        notification.flags |= Notification.FLAG_NO_CLEAR;
        notification.contentView = simpleView;
        notification.contentView.setTextViewText(R.id.textSongName, Principal.cancion.getTitle());
        notification.contentView.setTextViewText(R.id.textAlbumName, Principal.cancion.getArtist());

        setNotificationListeners(simpleView, contexto);

        nm = (NotificationManager) contexto.getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(7, notification);

    }
    public void hideNotification() {

        if (nm != null) {
            nm.cancel(7);
        }
    }
    private static void setNotificationListeners(RemoteViews view, Context context) {

        Intent i =new Intent(context,NotificationBroadcast.class);
        i.setAction(NOTIFY_PLAY);

        PendingIntent pPlay = PendingIntent.getBroadcast(context, 1, i, 0);
        i.setAction(NOTIFY_NEXT);
        PendingIntent pNext = PendingIntent.getBroadcast(context, 1, i, 0);
//        PendingIntent pPlay = PendingIntent.getBroadcast(context, 0, new Intent(NOTIFY_PLAY, NotificationBroadcast.class), 0);
//
//        PendingIntent pNext = PendingIntent.getBroadcast(context, 0, new Intent(NOTIFY_NEXT), 0);

        view.setOnClickPendingIntent(R.id.btnPlay, pPlay);
        view.setOnClickPendingIntent(R.id.btnNext, pNext);

        PendingIntent pOpen = PendingIntent.getActivity(context, 0, new Intent(context, Principal.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);
        view.setOnClickPendingIntent(R.id.notificacionAudio, pOpen);

    }
    public Drawable getPortada() {
        return portada.getDrawable();
    }
    public void setPortada(final Bitmap im) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                portada.setImageBitmap(im);
                //portada.setColorFilter(Color.rgb(30,215,96), android.graphics.PorterDuff.Mode.MULTIPLY);

            }
        });
    }
    private void preparaPlayer() {

        handler.post(new Runnable() {
            @Override
            public void run() {
                Boolean cover_only_wifi = Principal.preferencias.getBoolean(Principal.OPCION_IMAGENES_WIFI, true);
                Boolean down_only_wifi = Principal.preferencias.getBoolean(Principal.OPCION_DESCARGA_WIFI, true);


                if (((cover_only_wifi && Principal.hasWIFIConnection()) || (!cover_only_wifi)) && !Principal.cancion.hasError()) {
                    new HiloSpotifyCover(contexto, Principal.cancion.getArtist()).start();
                }

                if (((down_only_wifi && Principal.hasWIFIConnection()) || (!down_only_wifi)) && !Principal.cancion.hasError()) {
                    ReproductorMini.this.setEnabled(true);
                }
            }
        });

    }
    public void siguienteCancion() {

        if (Principal.playlist.size()!=0) {
            Cancion siguiente = Principal.playlist.first();
            Principal.cambiaCancion(siguiente,0,true);
            Principal.playlist.remove(siguiente);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void cancionSeleccionada(final CancionEvent e) {
        //Se reinician todos los elementos del reproductor
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressBuffer.setVisibility(VISIBLE);
                progressCancion.setVisibility(VISIBLE);

                botonPlay.setVisibility(INVISIBLE);
                botonPlay.setEnabled(true);

                portada.setImageResource(R.mipmap.vinyl);

                Principal.cancion = e.getSource();
                nombreArtista.setText(Principal.cancion.getArtist());
                nombreCancion.setText(Principal.cancion.getTitle());
                progressCancion.setProgress(0);

                botonPlay.setImageResource(R.mipmap.ic_play_arrow_grey600_24dp);
            }
        });


    }

    @Override
    public void cancionStartBuffered(CancionEvent e) {
    }

    @Override
    public void cancionBuffered(CancionEvent e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                botonPlay.setVisibility(VISIBLE);
                progressBuffer.setVisibility(INVISIBLE);
            }
        });

    }

    @Override
    public void cancionPreparada(CancionEvent e) {
        preparaPlayer();
    }

    @Override
    public void cancionError(CancionEvent e) {

        handler.post(new Runnable() {
            @Override
            public void run() {
                botonPlay.setImageResource(R.mipmap.ic_clear_grey600_24dp);
                botonPlay.setVisibility(VISIBLE);
                progressBuffer.setVisibility(INVISIBLE);
                botonPlay.setEnabled(false);
                setEnabled(false);
            }
        });

        //Si hay playlist, pasar a la siguiente
        siguienteCancion();

        }

    @Override
    public void cancionCambiada(CancionEvent e) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                progressCancion.setVisibility(INVISIBLE);
            }
        });

    }

    @Override
    public void cancionCompleta(CancionEvent e) {
        //Pasar a la siguiente si se puede por los ajustes
        siguienteCancion();

    }

    @Override
    public void cancionPlay(CancionEvent event) {
        new HiloPlayer().start();
        handler.post(new Runnable() {
            @Override
            public void run() {
                botonPlay.setImageResource(R.mipmap.ic_pause_grey600_24dp);

            }
        });
    }

    @Override
    public void cancionPause(CancionEvent event) {
        //Lo propio si se pausa la cancion
        handler.post(new Runnable() {
            @Override
            public void run() {
                botonPlay.setImageResource(R.mipmap.ic_play_arrow_grey600_24dp);
            }
        });
    }

    /////////////////////////////////////////////////////////////////////////////////////////////////////////


    public class HiloPlayer extends Thread {
        public double posicion;

        @Override
        public void run() {

            final double duracion = Principal.cancion.getMediaPlayer().getDuration();

            while (Principal.cancion.getMediaPlayer().isPlaying()) {
                try {
                    Thread.sleep(200);
                    posicion = Principal.cancion.getMediaPlayer().getCurrentPosition();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            progressCancion.setMax((int) duracion);
                            progressCancion.setProgress((int) posicion);
                        }
                    });


                } catch (Exception e) {
                }
            }
            //Se vuelve a comprovar la posicion actual, dado que al actualizar cada 200 ms, puede que no esté al dia


            //Cuando se para, se oculta la barra de progreso
            handler.post(new Runnable() {
                @Override
                public void run() {

                    progressCancion.setProgress(0);

                }
            });
        }
    }


}