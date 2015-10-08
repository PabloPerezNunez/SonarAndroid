package gui;

import android.content.Context;
import android.content.res.Resources;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import Webs.Cancion;
import ppn.com.mp3down.Descargas;
import ppn.com.mp3down.R;


public class itemDescarga extends LinearLayout {

    public static boolean cancelar;
    private Cancion cancion;
    private Context context;

    private ProgressBar progressBar;

    public itemDescarga(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public itemDescarga(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public itemDescarga(Context contexto, Cancion c, boolean error) {
        super(contexto.getApplicationContext());
        cancion=c;
        context=contexto;
        if (error){
            inicializaError();
        }
        else {
            inicializa();
        }
    }

    public void setCancelar(boolean cancelar) {
        itemDescarga.cancelar = cancelar;
    }

    public Cancion getCancion() {
        return cancion;
    }

    public ProgressBar getProgressBar() {
        return progressBar;
    }

    public void inicializa() {

        setCancelar(false);
        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li =(LayoutInflater)getContext().getSystemService(infService);
        li.inflate(R.layout.item_descarga, this, true);

        TextView nombreCancion = (TextView) findViewById(R.id.txtNombre);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.setProgress(0);

        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                Descargas.cancelaDescarga(cancion);

                return false;
            }
        });

        String [] artista_nombre;

        char c = '\u2013';
        String regex="( - )|"+c;

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(cancion.getTitle());

        if (m.find())
        {
            artista_nombre = cancion.getTitle().split(regex);
            if (artista_nombre.length>1){
                nombreCancion.setText(artista_nombre[1].trim());
               // artistaCancion.setText(artista_nombre[0].trim());
            }

        }

        else{
            nombreCancion.setText(cancion.getTitle());
            //artistaCancion.setText("");
        }


    }

    public void inicializaError() {

        setCancelar(false);

        setBackground(getResources().getDrawable(R.drawable.descarga_error));

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li =(LayoutInflater)getContext().getSystemService(infService);
        li.inflate(R.layout.item_descarga, this, true);

        TextView nombreCancion = (TextView) findViewById(R.id.txtNombre);
        String [] artista_nombre;

        char c = '\u2013';
        String regex="( - )|"+c;

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(cancion.getTitle());

        if (m.find())
        {
            artista_nombre = cancion.getTitle().split(regex);
            if (artista_nombre.length>1){
                nombreCancion.setText(artista_nombre[1].trim());
                // artistaCancion.setText(artista_nombre[0].trim());
            }

        }

        else{
            nombreCancion.setText(cancion.getTitle());
            //artistaCancion.setText("");
        }


        ImageView icono = (ImageView) findViewById(R.id.icono);
        icono.setImageResource(R.mipmap.ic_clear_white_24dp);

        setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Descargas.reDescarga(cancion);
            }
        });
        setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Descargas.cancelaDescarga(cancion);
                return false;
            }
        });



    }

    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
}


