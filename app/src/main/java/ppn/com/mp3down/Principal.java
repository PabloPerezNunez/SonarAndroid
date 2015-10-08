package ppn.com.mp3down;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.TransitionInflater;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.TreeSet;

import Webs.Cancion;
import Webs.CancionEvent;
import Webs.CancionEventListener;
import Webs.Mp3Freex;
import Webs.Web;
import Webs.WebEvent;
import Webs.WebListener;
import gui.Ajustes;
import gui.ReproductorMini;

public class Principal extends ActionBarActivity implements WebListener {

    //public static TreeSet<Cancion> canciones;
   // public static Semaphore sem_link_des, sem_buscando, sem_buffering;
    public static DatbaseHandler database;
    public static LinearLayout colaDescarga;
    public static SwipeRefreshLayout swipeLayout;
    public static AutoCompleteTextView txtBuscar;
    public ImageButton btnClear,btnMenu,btnBack;
    public Button btnHistorial,btnAjustes,btnDescargas;
    public static Cancion cancion;
    public static int indiceCancion;
    public static Context contexto;
    public static TreeSet <Web> webs;
    public static TreeSet <Cancion> descargas, descargasFallidas,playlist;
    public static ReproductorMini reproductorMini;
    public static DrawerLayout drawer_layout;
    public static RelativeLayout layoutBusqueda;
    public static SharedPreferences preferencias;

    public static ListView lista_de_canciones;
    public static ListaCancionesAdapter adaptador_lista_canciones;

    public static final String OPCION_PLAY_WIFI = "play_wifi" ;
    public static final String OPCION_IMAGENES_WIFI  = "imagenes_wifi" ;
    public static final String OPCION_DESCARGA_WIFI  = "descarga_wifi" ;
    public static final String OPCION_CARPETA_DESCARGAS = "carpeta_descarga" ;
    public static final String OPCION_LIMPIAR_CANCIONES = "eliminar_historial_canciones" ;
    public static final String OPCION_LIMPIAR_BUSQUEDAS = "eliminar_historial_busquedas" ;


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Al finalizar la aplicacion se elimina la notificacion en caso de existir
        Principal.log("ONDESTROY");
        reproductorMini.hideNotification();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT>=21){
            getWindow().setSharedElementExitTransition(TransitionInflater.from(this).inflateTransition(R.transition.change_image_transform));
        }

        setContentView(R.layout.ventana_principal);


        contexto = Principal.this.getApplicationContext();
        preferencias = PreferenceManager.getDefaultSharedPreferences(contexto);

//        sem_link_des= new Semaphore(1);
//        sem_buscando= new Semaphore(1);
//        sem_buffering= new Semaphore(1);

        database = new DatbaseHandler(contexto);

        //database.onCreate(database.getWritableDatabase());

        webs = new TreeSet<Web>();

        Mp3Freex mp3Freex = new Mp3Freex();
        mp3Freex.addWebListener(this);
        webs.add(mp3Freex);

        playlist = new TreeSet<Cancion>();
        descargas = new TreeSet<Cancion>();
        descargasFallidas = database.getAllErrorDownloads();

        txtBuscar = (AutoCompleteTextView) findViewById(R.id.txtBuscar);

        reproductorMini = (ReproductorMini) findViewById(R.id.reproductor);
        reproductorMini.setVisibility(View.INVISIBLE);

        btnClear = (ImageButton) findViewById(R.id.btnClear);
        btnMenu = (ImageButton) findViewById(R.id.btnSettings);
        btnBack = (ImageButton) findViewById(R.id.descarga_back);
        btnHistorial = (Button) findViewById(R.id.btn_historial);
        btnAjustes = (Button) findViewById(R.id.btn_ajustes);
        btnDescargas = (Button) findViewById(R.id.btn_descargas);

        swipeLayout= (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        layoutBusqueda = (RelativeLayout) findViewById(R.id.layoutBarraBusqueda);
        colaDescarga = (LinearLayout) findViewById(R.id.layoutDescarga);

        lista_de_canciones = (ListView) findViewById(R.id.listaNueva);
        adaptador_lista_canciones = new ListaCancionesAdapter(contexto);

        lista_de_canciones.setAdapter(adaptador_lista_canciones);

        drawer_layout = (DrawerLayout) findViewById(R.id.drawer_layout);

        swipeLayout.setColorSchemeColors(R.color.myAccentColor, R.color.myPrimaryColor);

        listeners();

        capturaIntent();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onStop() {
        super.onStop();

//        if (btnReproductor.getCancion()!=null){
//            btnReproductor.showNotification();
//        }
        if (Principal.cancion!=null){
            reproductorMini.showNotification();
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        invalidateOptionsMenu();

        hideKeyboard();
        if (drawer_layout.isDrawerOpen(Gravity.LEFT)){
            drawer_layout.closeDrawer(Gravity.LEFT);
        }
        else{
            drawer_layout.openDrawer(Gravity.LEFT);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        String[] EXAMPLE = database.getAllSearchSongs();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,R.layout.item_busqueda, EXAMPLE);
        txtBuscar.setAdapter(adapter);

        reproductorMini.hideNotification();
        //btnReproductor.hideNotification();


    }

    @Override
    public void onBackPressed() {

        if (drawer_layout!=null && drawer_layout.isDrawerOpen(Gravity.LEFT)){
            drawer_layout.closeDrawers();
            
        }else{

//            if (slide != null &&(slide.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED || slide.getPanelState() == SlidingUpPanelLayout.PanelState.ANCHORED)) {
//                slide.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//            }
//            else{
//                moveTaskToBack(true);
//            }
            moveTaskToBack(true);

        }



    }

    private void listeners () {

        txtBuscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (txtBuscar.getText().toString().compareTo("")!=0){
                    btnClear.setVisibility(View.VISIBLE);
                }
                else{
                    btnClear.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtBuscar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                boolean handled = false;
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    buscar(txtBuscar.getText().toString());
                    handled = true;
                }
                return handled;
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                txtBuscar.setText("");
            }
        });

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawer_layout.closeDrawer(Gravity.LEFT);
            }
        });

        btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                drawer_layout.openDrawer(Gravity.LEFT);

            }
        });

        btnAjustes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(contexto, Ajustes.class);
                startActivity(i);
                drawer_layout.closeDrawer(Gravity.LEFT);

            }
        });

        btnDescargas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(contexto, Descargas.class);
                startActivity(i);
                drawer_layout.closeDrawer(Gravity.LEFT);

            }
        });

        btnHistorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(contexto, Historial.class);
                startActivity(i);
                drawer_layout.closeDrawer(Gravity.LEFT);

            }
        });

        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (txtBuscar.getText().length() != 0) {
                    try {
                        buscar(txtBuscar.getText().toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        lista_de_canciones.setOnScrollListener(new AbsListView.OnScrollListener() {

            private int mLastFirstVisibleItem;


            @Override
            public void onScrollStateChanged(AbsListView view, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {

                //layoutBusqueda.requestLayout();

                if (mLastFirstVisibleItem < i) {

                    int newSize = 160 - (i * 10);
                    if (newSize >= 0) {
                        //layoutBusqueda.getLayoutParams().height=newSize;
                    }
                }
                if (mLastFirstVisibleItem > i) {

                    int newSize = 160 - (i * 10);
                    if (newSize >= 0) {
                        //layoutBusqueda.getLayoutParams().height=newSize;
                    }
                }

                mLastFirstVisibleItem = i;
            }
        });



    }

    private void capturaIntent() {

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        try {
            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
                    txtBuscar.setText(sharedText);
                    buscar(sharedText);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addCancion(final Cancion c){


        try {

            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {
                public void run() {
                    adaptador_lista_canciones.addCancion(c);
                    adaptador_lista_canciones.notifyDataSetChanged();
                }
            });


        }catch(Exception e){
            e.printStackTrace();
        }

    }

    public static void noResultados(){

        if (webs.size()==1){

            Principal.snackBar(Color.RED,contexto.getString(R.string.no_resultados));
        }

    }

    public void buscar(String s){

        if (txtBuscar.getText().length() != 0) {
            try {
                hideKeyboard();

                s = eliminaEspeciales(s);

                database.addSearchSong(s);

                for (Web w : webs){
                    w.buscaMusica(s);
                }

                onResume();

                adaptador_lista_canciones.clear();
                adaptador_lista_canciones.notifyDataSetChanged();

                swipeLayout.setRefreshing(true);


            } catch (Exception e) {
                //e.printStackTrace();
            }
        }

    }

    public static void cambiaCancion(Cancion c, int indice, boolean automatico){


        if (c!=cancion) {

            //Se elimina todos los listeners para evitar la autoreproduccion en caso de que sea el usuario el que selecciona la cancion
            if (!automatico)c.removeallListeners();

            //Se almacena la posicion en el vector asociada a la cancion
            indiceCancion = indice;

            //Se lanza el evento de cancion cambiada (Null la primera vez)
            if (cancion!=null)cancion.fireCancionCambiadaEvent();

            //Se almacena como cancion actual
            cancion=null;
            cancion = c;

            //Se añade al historial de canciones
            database.addHistorySong(cancion);

            //Se añade el escuchador de eventos de esa cancion
            cancion.addCancionListener(reproductorMini);

            //Se lanza el evento correspondiente
            cancion.fireCancionSeleccionadaEvent();

            //Se obtienen los datos de la cancion
            cancion.descarga();


        }
        /////////////////////////////////
        if (reproductorMini.getVisibility()==View.INVISIBLE){
            if (Build.VERSION.SDK_INT >= 21) {
                int cx = 0;
                int cy = 50;

                // get the final radius for the clipping circle
                int finalRadius = Math.max(reproductorMini.getWidth(), 50);

                // create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(reproductorMini, cx, cy, 0, finalRadius);

                reproductorMini.setVisibility(View.VISIBLE);
                anim.start();
            } else {
                reproductorMini.setVisibility(View.VISIBLE);
            }

            lista_de_canciones.setLayoutParams(new RelativeLayout.LayoutParams(lista_de_canciones.getWidth(),lista_de_canciones.getHeight()-800));
            swipeLayout.requestLayout();


        }

    }

    private void hideKeyboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    public static void toast(final String texto) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            public void run() {

                Toast toast = Toast.makeText(contexto, texto, Toast.LENGTH_LONG);
                View vieew = toast.getView();
                vieew.setBackgroundColor(Color.parseColor("#BD8BDC"));
                vieew.setBackgroundResource(R.drawable.toast);
                toast.setView(vieew);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }
        });

    }

    public static void snackBar( int color,String s){

        Snackbar sn = Snackbar.make(swipeLayout, s, Snackbar.LENGTH_LONG);
        sn.getView().setBackgroundColor(color);
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)sn.getView().getLayoutParams();
        params.height= (int) convertDpToPixel(50);
        sn.getView().setLayoutParams(params);
        sn.show();
    }

    public static void log(String s) {
        Log.d("MP3LOG", s);
    }

    public static boolean hasWIFIConnection() {
        ConnectivityManager cm = (ConnectivityManager) contexto.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiNetwork = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiNetwork != null && wifiNetwork.isConnected())
        {
            return true;
        }
        return false;

    }

    public static float convertDpToPixel(float dp){
        Resources resources = contexto.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }

    public static String eliminaEspeciales(String search) {

        String Original = "ÁáÉéÍíÓóÚúÜüÑñ()[]{}.,:";
        String Modificado = "AaEeIiOoUuUuNn         ";

        char[] texto = search.toLowerCase().toCharArray();

        for(int i =0 ;i<texto.length; i++){
            int pos = Original.indexOf(texto[i]);
            if (pos>-1){
                texto[i]= Modificado.charAt(pos);
            }
        }

        String ret = new String(texto).trim();

        //Si hay mas de 1 espacio junto se eliminan los que sobren
            ret = ret.replaceAll("\\s+", " ");


        return  ret;
    }

    public static String toUnicode(String dir) {

        char[] url = dir.toCharArray();
        String newUrl = "";

        for (int i =0 ; i<url.length;i++){

            if (url[i]<='z'){
                newUrl = newUrl + url[i];
            }
            else {
                newUrl = newUrl + String.format("\\u%04x", (int) url[i]);
            }
        }

        return newUrl;
    }

    public static TreeSet<Cancion> getCanciones() {
        return adaptador_lista_canciones.getCanciones();

    }

    @Override
    public void busquedaTerminada(WebEvent e) {
        Handler h = new Handler(Looper.getMainLooper());
        h.post(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(false);
            }
        });

        new HiloCreaLista().start();

    }

    public static void anadirCola(final Cancion c, boolean toast) {

        Cancion cancion = c;
        cancion.addCancionListener(new CancionEventListener() {
            @Override
            public void cancionBuffered(CancionEvent e) {
                c.play();
            }

        });



        playlist.add(cancion);

        if (toast) {
            snackBar(R.color.myPrimaryColor, c.getTitle() + " añadida.");
        }

    }

    public class HiloCreaLista extends Thread {

        public void run() {

            playlist.clear();

            for (Cancion c : adaptador_lista_canciones.getCanciones()){
                anadirCola(c,false);
            }

        }
    }
}
