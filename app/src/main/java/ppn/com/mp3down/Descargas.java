package ppn.com.mp3down;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.TreeSet;

import Webs.Cancion;
import gui.itemAyuda;
import gui.itemDescarga;


public class Descargas extends ActionBarActivity {

    private ImageButton btnBack;
    public static LinearLayout listaDescarga;
    private static Context contexto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ventana_descargas);

        btnBack = (ImageButton) findViewById(R.id.descargas_back);
        listaDescarga = (LinearLayout) findViewById(R.id.descargas_linearLayout);
        contexto = Descargas.this.getApplicationContext();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Descargas.this.finish();
            }
        });

        actualizaDescargas();

    }

    public static void actualizaDescargas() {

        Handler handler = new Handler(Looper.getMainLooper());

        if (listaDescarga!=null) {

            Principal.log("Actualizando descargas");

            handler.post(new Runnable() {
                @Override
                public void run() {
                    listaDescarga.removeAllViews();

                    TreeSet<Cancion> canciones = Principal.descargas;
                    TreeSet<Cancion> errores = Principal.descargasFallidas;

                    for (Cancion c : canciones) {
                        listaDescarga.addView(new itemDescarga(contexto, c, false));
                    }

                    if (errores.size()!=0){

                        listaDescarga.addView(new itemAyuda(contexto,"- Pulsa varios segundos sobre una descarga fallida para eliminarla\n- Haz click sobre ella para reintentar la descarga"));

                        for (Cancion c : errores) {
                            listaDescarga.addView(new itemDescarga(contexto, c, true));
                        }

                    }


                    if (canciones.size() == 0 && errores.size()==0) {

                        TextView tv = new TextView(contexto);
                        tv.setText("No hay descargas");
                        tv.setTextColor(Color.WHITE);
                        tv.setGravity(Gravity.CENTER);
                        tv.setBackgroundResource(R.drawable.abc_list_selector_disabled_holo_dark);
                        listaDescarga.addView(tv);
                    }
                }
            });


        }
    }

    public static void addDescarga(Cancion cancion) {

        Boolean down_only_wifi = Principal.preferencias.getBoolean(Principal.OPCION_DESCARGA_WIFI, true);
        //Si solo se permite descargar por wifi y esta disponible o si no esta activada la opcion, se anade la descarga

        if ((down_only_wifi && Principal.hasWIFIConnection())||(!down_only_wifi)) {

            Principal.descargas.add(cancion);
            new HiloDescarga(cancion).start();

            Principal.log("Se anade la descarga " + cancion.getTitle());

        }
        else{
            Principal.toast(Principal.contexto.getString(R.string.descarga_wifi));
        }

        actualizaDescargas();
    }

    public static void finDescarga(Cancion cancion, File file, boolean error) {

        Handler handler = new Handler(Looper.getMainLooper());

        if (error){
            Principal.descargas.remove(cancion);

            Principal.descargasFallidas.add(cancion);
            Principal.database.addErrorDownload(cancion);

            Principal.log("Error al descargar " + cancion.getTitle());

        }
        else {
            Principal.descargas.remove(cancion);

            Notificacion.notify(contexto, cancion.getTitle(), file);

            Principal.log("Descargada " + cancion.getTitle());

        }

        actualizaDescargas();
    }

    public static void reDescarga(final Cancion cancion) {

        Principal.descargasFallidas.remove(cancion);
        Principal.database.deleteErrorDownload(cancion);

        addDescarga(cancion);

        Principal.log("Reintentando descargar " + cancion.getTitle());

        actualizaDescargas();
    }

    public static void cancelaDescarga(Cancion cancion) {

        Principal.descargas.remove(cancion);

        Principal.descargasFallidas.remove(cancion);
        Principal.database.deleteErrorDownload(cancion);

        Principal.log("Eliminando descarga " + cancion.getTitle());
        actualizaDescargas();

    }
}
