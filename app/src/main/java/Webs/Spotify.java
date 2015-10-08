package Webs;

import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * Created by PPN on 16/09/2015.
 */
public class Spotify {


    public static boolean isArtist(String s){

        boolean ret = false;

        Document doc=null;
        s=s.trim();
        s=s.replaceAll(" ","+");
        String search="https://api.spotify.com/v1/search?q=%22"+s+"%22&type=artist&limit=1";


        try {
            doc = Jsoup.connect(search).userAgent("Chrome").ignoreContentType(true).get();
            String json=doc.text();

            JSONObject jObject = new JSONObject(json);
            //Objeto principal
            jObject = jObject.getJSONObject("artists");
            //Lista de resultados
            JSONArray resultados = jObject.getJSONArray("items");

            if (resultados.length()>0) ret=true;


        } catch (Exception e) {
            return false;
        }


        return ret;
    }


}
