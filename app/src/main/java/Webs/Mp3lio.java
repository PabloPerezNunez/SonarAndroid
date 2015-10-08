package Webs;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ppn.com.mp3down.Principal;


public class Mp3lio extends Web implements Comparable<Web>{

	private static final int NUMERO_MIN_CANCIONES = 10;
    static final int CALIDAD = 50;
    public boolean fin_busqueda = true;

	@Override
	public void buscaMusica(final String s) throws Exception {


        new Thread(new Runnable() {

            public void run(){
                Document doc = null;
                Elements posts;
                boolean resultados = true;

                try {
                    doc = Jsoup.connect("http://mp3lio.com/" + s.replaceAll(" ", "-") ).userAgent("Chrome").get();

                    posts = doc.select("div[class*=item]");

                    for (Element p : posts) {

                        fin_busqueda = false;

                        String nombre = p.select(".meta strong").first().text();
                        String dir = p.getElementsByClass("meta").select("a").first().attr("href");
                        URL url = new URL(dir);

                        String[] nombre_artista = separaNombre(nombre);

                        //Estos links no son reproducibles, portanto se saltan
                        if (url.toString().contains("get-tube")){
                            break;
                        }

                        //Se busca si el titulo de la cancion contiene algun elemento de la busqueda
                        boolean contiene = contiene(s.split(" "),nombre_artista);
                        Cancion cancion=null;

                        //Si no contiene ninguna coincidencia, no se anade la cancion
                        if (contiene){

                            if (nombre_artista.length>1){
                                cancion = new CancionMp3lio(nombre_artista[1],nombre_artista[0], url);
                            }

                            else{
                                cancion = new CancionMp3lio(nombre, url);
                            }
                            Principal.addCancion(cancion);
                        }
                    }

                    fin_busqueda = true;


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

    private boolean contiene(String[] busqueda, String[] nombre) {

        boolean contiene = false;

        for (String s : busqueda){

            for (String d : nombre){

                contiene = d.contains(s);

                if (contiene) break;
            }

            if (contiene) break;

        }

        return contiene;
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
