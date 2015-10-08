package ppn.com.mp3down;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import Webs.Cancion;
import gui.itemHistorial;


public class Historial extends ActionBarActivity {

    private LinearLayout listaHistorial;
    private ImageButton btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ventana_historial);

        listaHistorial = (LinearLayout) findViewById(R.id.historial_linearLayout);
        btnBack = (ImageButton) findViewById(R.id.historia_back);

        actualizaHistorial();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Historial.this.finish();
            }
        });

    }

    private void actualizaHistorial() {

        listaHistorial.removeAllViews();
        List<Cancion> canciones = Principal.database.getAllHistorySongs();

        for (Cancion c : canciones){
            listaHistorial.addView(new itemHistorial(this,c));
        }

        if (canciones.size()==0){
            TextView tv = new TextView(this);
            tv.setText("No hay elementos en el historial");
            tv.setTextColor(Color.WHITE);
            tv.setGravity(Gravity.CENTER);
            tv.setBackgroundResource(R.drawable.abc_list_selector_disabled_holo_dark);
            listaHistorial.addView(tv);
        }

    }


    public void eliminaCancion(final Cancion cancion) {

        Principal.database.deleteHistorySong(cancion);
        actualizaHistorial();

    }
}
