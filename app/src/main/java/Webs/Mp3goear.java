package Webs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ppn.com.mp3down.Principal;


public class Mp3goear extends Web implements Comparable<Web>{

	private static final int NUMERO_MIN_CANCIONES = 10;
    static final int CALIDAD = 20;


    @Override
	public void buscaMusica(final String s) throws Exception {


        new Thread(new Runnable() {

            public void run(){
                Document doc = null;
                Elements posts;
                boolean resultados = true;

                try {
                    doc = Jsoup.connect("http://mp3goear.com/mp3/" + s.replaceAll(" ", "-")+".html" ).userAgent("Chrome").get();

                    posts = doc.select("div[id*=song_html]");

                    for (Element p : posts) {

                        String nombre = p.select("div[id*=right_song]").first().select("b").text();
                        String dir = p.select("a:not(.btn)").first().attr("data-href");

                        //Si no tienen link, se descartan
                        if (dir.isEmpty()){
                            break;
                        }

                        URL url = new URL(dir);

                        String[] nombre_artista = separaNombre(nombre);


                        if (nombre_artista.length>1){
                            Principal.addCancion(new CancionMp3goear(nombre_artista[1],nombre_artista[0], url));

                        }
                        else{
                            Principal.addCancion(new CancionMp3goear(nombre, url));
                        }
                    }

                    if (posts.size()==0){
                        Principal.noResultados();
                    }

                }catch (Exception e){
                    Principal.toast("Error de conexion");
                    e.printStackTrace();

                }

            }
        }).start();

	}

    public String[] separaNombre(String nombre){

        String [] artista_nombre = null;
        nombre = nombre.replaceAll("(mp3)","");

        char c = '\u2013';
        String regex="( - )|"+c;

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(nombre);

        if (m.find())
        {
            artista_nombre = nombre.split(regex);
            artista_nombre[0]=artista_nombre[0].trim();
            artista_nombre[1]=artista_nombre[1].trim();
        }

        else{
            artista_nombre = new String[]{nombre};
        }

        return artista_nombre;

    }

    @Override
    public int compareTo(Web web) {
        return new Integer(web.CALIDAD).compareTo(CALIDAD);
    }
}
