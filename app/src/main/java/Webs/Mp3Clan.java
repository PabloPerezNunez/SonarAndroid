package Webs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ppn.com.mp3down.Principal;


public class Mp3Clan extends Web implements Comparable<Web>{

	private static final int NUMERO_MIN_CANCIONES = 10;
    static final int CALIDAD = 80;
    public boolean fin_busqueda = true;


    @Override
	public void buscaMusica(final String s) throws Exception {


        new Thread(new Runnable() {

            public void run(){
                Document doc = null;
                Elements posts;
                boolean resultados = true;

                try {
                    doc = Jsoup.connect("http://mp3clan.com/mp3/" + s.replaceAll(" ", "_") + ".html").userAgent("Chrome").get();

                    posts = doc.select("li[class*=mp3list-play]");

                    for (Element p : posts) {

                        fin_busqueda = false;

                        String nombre = p.select("div").first().text();
                        String dir = p.select("a").first().attr("href");
                        URL url = new URL(dir);

                        String[] nombre_artista = separaNombre(nombre);

                        if (nombre_artista.length>1){
                            Principal.addCancion(new CancionMp3Clan(nombre_artista[1],nombre_artista[0], url));

                        }
                        else{
                            Principal.addCancion(new CancionMp3Clan(nombre, url));
                        }
                    }

                    fin_busqueda = true;

                    if (posts.size()==0){
                        Principal.noResultados();
                    }

                }catch (Exception e){
                    Principal.toast("Error de conexion");

                }

            }
        }).start();

	}

    public String[] separaNombre(String nombre){

        String [] artista_nombre = null;

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
