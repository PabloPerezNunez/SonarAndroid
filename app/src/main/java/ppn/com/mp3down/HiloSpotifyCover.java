package ppn.com.mp3down;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.InputStream;


public class HiloSpotifyCover extends Thread{

	private String nombre;
    private Context contexto;

	public HiloSpotifyCover(Context argcontext, String nm){

		nombre=nm;
        contexto=argcontext;

	}
		
	
	public void run(){

        try {

            String json=getJson();

            JSONObject jObject = new JSONObject(json);
            //Objeto principal
            jObject = jObject.getJSONObject("artists");
            //Lista de resultados
            JSONArray resultados = jObject.getJSONArray("items");

            if (resultados.length()>0) {
                //Primer resultado
                JSONObject obj = resultados.getJSONObject(0);
                //Imagen del primer resultado
                JSONObject imagen = obj.getJSONArray("images").getJSONObject(0);
                //Guardar imagen
                final Bitmap bp = getBitmapFromURL(imagen.getString("url"));
                //Poner imagen en ImageView

                Principal.reproductorMini.setPortada(bp);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public String getJson(){

        Document doc=null;
        String[] artists ;
        String search=nombre;

        String regex="(\\s*,\\s*)|(\\s+ft\\s+)|(\\s+feat\\s+)|(\\s+feat.\\s+)|(\\s+ft.\\s+)|(\\s+featuring.\\s+)|(\\s+featuring\\s+)|(\\s+&\\s+)";
        search = search.toLowerCase();
        artists = search.split(regex);

        if (artists.length >= 2) {
            search = Principal.eliminaEspeciales(artists[0]) + "%22OR%22" + Principal.eliminaEspeciales(artists[1]);
        }
        if (artists.length == 1) {
            search = Principal.eliminaEspeciales(artists[0]);
        }

        search=search.replaceAll("\\s+", "+");

        search="https://api.spotify.com/v1/search?q=%22"+search+"%22&type=artist&limit=1";


        try {
            doc = Jsoup.connect(search).userAgent("Chrome").timeout(10000).ignoreContentType(true).get();


        } catch (Exception e) {
            e.printStackTrace();
        }


        return doc.text();
    }

    public Bitmap getBitmapFromURL(String src) {
        Bitmap mIcon11 = null;
        try {
            InputStream in = new java.net.URL(src).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", "");
            e.printStackTrace();
        }
        return mIcon11;
    }

    public void mustraImagen(final Bitmap bp){

        Handler handler = new Handler(Looper.getMainLooper());

        handler.post(new Runnable() {

            public void run() {

            }
        });
    }

}
