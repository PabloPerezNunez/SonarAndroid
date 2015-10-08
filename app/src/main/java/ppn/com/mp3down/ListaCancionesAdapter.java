package ppn.com.mp3down;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.TreeMap;
import java.util.TreeSet;

import Webs.Cancion;

/**
 * Created by PPN on 10/06/2015.
 */
class ListaCancionesAdapter extends BaseAdapter {

    private Context context;
    public TreeMap<Integer ,Cancion> canciones;
    public int elementos;

    public ListaCancionesAdapter(Context contexto){

        context=contexto;
        canciones =  new TreeMap<Integer, Cancion>();
        elementos=0;
    }

    @Override
    public int getCount() {
        return elementos;
    }

    @Override
    public Cancion getItem(int i) {
        return canciones.get(i);
    }


    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_cancion, parent, false);
        }

        TextView nombreCancion = (TextView) convertView.findViewById(R.id.txtNombre);

        TextView artistaCancion = (TextView)convertView.findViewById(R.id.txtArtista);

        final ImageButton desplegar = (ImageButton) convertView.findViewById(R.id.btnOpciones);

        final Cancion cancion = canciones.get(position);

        nombreCancion.setText(cancion.getTitle());

        artistaCancion.setText(cancion.getArtist());


        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Principal.cambiaCancion(cancion, position,false);
            }
        });


        desplegar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(Principal.contexto, desplegar);
                //Inflating the Popup using xml file
                popup.getMenuInflater().inflate(R.menu.popup_menu, popup.getMenu());

                popup.getMenu().getItem(0).setTitle(cancion.getDuracion());
                popup.getMenu().getItem(0).setEnabled(false);

                popup.getMenu().getItem(1).setTitle(cancion.getCalidad()+" bpm");
                popup.getMenu().getItem(1).setEnabled(false);

                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {

                        switch (menuItem.getItemId()) {
                            case R.id.buscarArtista:
                                Principal.txtBuscar.setText(cancion.getArtist());
                                break;
                            case R.id.buscarCancion:
                                Principal.txtBuscar.setText(cancion.getTitle() + " " + cancion.getArtist());
                                break;
                            case R.id.anadirAcola:
                                Principal.anadirCola(cancion,true);
                                break;
                            default:
                                break;
                        }

                        return false;
                    }
                });

                popup.show();//showing popup menu
            }
        });

        Animation anim = new TranslateAnimation(0,0,10,0);
        anim.setDuration(200);
        convertView.setAnimation(anim);

        anim = null;


        return convertView;
    }

    public void addCancion(Cancion cancion){

        canciones.put(elementos, cancion);
        elementos++;

    }

    public void clear() {
        canciones.clear();
        elementos=0;

    }

    public TreeSet<Cancion> getCanciones(){
        TreeSet<Cancion> ret =  new TreeSet<Cancion>();

        for (int i=0; i<elementos; i++){
            ret.add(canciones.get(i));
        }

        return ret;
    }


}
