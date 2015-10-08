package gui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import ppn.com.mp3down.R;


public class itemAyuda extends LinearLayout {

    private Context context;
    private String text;

    public itemAyuda(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public itemAyuda(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public itemAyuda(Context contexto, String txt) {
        super(contexto.getApplicationContext());
        context=contexto;
        text = txt;
        inicializa();
    }



    public void inicializa() {

        String infService = Context.LAYOUT_INFLATER_SERVICE;
        LayoutInflater li =(LayoutInflater)getContext().getSystemService(infService);
        li.inflate(R.layout.item_ayuda, this, true);

        TextView textoAyuda = (TextView) findViewById(R.id.txtAyuda);
        textoAyuda.setText(text);



    }

}


