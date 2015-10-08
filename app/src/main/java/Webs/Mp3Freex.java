package Webs;

import android.graphics.Color;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ppn.com.mp3down.Principal;


public class Mp3Freex extends Web implements Comparable<Web>{


    @Override
	public void buscaMusica(final String s) throws Exception {


        new Thread(new Runnable() {

            public void run(){
                Document doc = null;
                Elements posts;
                boolean resultados = true;

                try {


                    doc = Jsoup.connect("http://mp3freex.org/" + s.replaceAll(" ", "-").toLowerCase() + "-download").userAgent("Chrome").timeout(5000).get();

                    posts = doc.select("div[class*=actl]");

                    for (Element p : posts) {

                        String nombre = p.select("span[class*=res_title]").first().text();
                        String dir = p.select("a[class*=btn btn-success mp3download]").first().attr("data-online");
                        URL url = new URL("http://mp3freex.org/download-mp3/"+dir);

                        String[] nombre_artista = separaNombre(nombre);

                        boolean contiene = contiene(s.split(" "), nombre.split(" "));
                        Cancion cancion=null;

                        if (contiene) {

                            String datos = p.select("span[class*=label label-info]").first().text();
                            int calidad = 0;
                            String duracion = "";

                            //Si existen los datos de la cancion.

                            if (!datos.isEmpty()) {
                                String datos_separados[] = datos.split("[|]");
                                if (datos_separados.length==2){
                                    calidad = Integer.parseInt(datos_separados[1].replaceAll("\\D", "").replaceAll("\\s", ""));
                                    duracion = datos_separados[0].trim();
                                    if (!duracion.contains(":")){
                                        duracion = duracion +" s";
                                    }
                                    if (duracion.matches("\\d{2}:")){
                                        duracion = duracion +"00";
                                    }
                                    if (duracion.matches("\\d{1}:\\d{2}")){
                                        duracion = "0"+duracion;
                                    }
                                }
                            }

                            if (nombre_artista.length > 1) {
                                cancion = new CancionMp3Freex(capitalizeFirst(nombre_artista[1]), capitalizeFirst(nombre_artista[0]), url, calidad,duracion);

                            } else {
                                cancion = new CancionMp3Freex(capitalizeFirst(nombre), url,calidad,duracion);
                            }
                            Principal.addCancion(cancion);

                        }
                    }

                    if (posts.size()==0){
                        Principal.noResultados();
                    }

                    fireBusquedaTerminadaEvent();


                }catch (Exception e){
                    Principal.snackBar(Color.RED,"Error de conexion");
                    e.printStackTrace();

                }

            }
        }).start();

	}

    public String[] separaNombreOld(String nombre){

        String [] artista_nombre = null;

        char c = '\u2013';
        String regex="( - )|(-)|(-)|"+c;

        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(nombre);

        if (m.find())
        {
            artista_nombre = nombre.split(regex);
            //Si la longitud es mayor de 1, hay titulo y artista
            if (artista_nombre.length>1){
                artista_nombre[0]=artista_nombre[0].trim();
                artista_nombre[1]=artista_nombre[1].trim();
            }

        }

        else{
            artista_nombre = new String[]{nombre};
        }

        // Si el nombre de la cancion y del artista coinciden, probablemente sea un error de nombrado.
        if (artista_nombre.length>2 && (artista_nombre[0].compareTo(artista_nombre[1])==0)){
            artista_nombre[0]=artista_nombre[0].trim();
            artista_nombre[1]=artista_nombre[artista_nombre.length-1].trim();
        }

        return artista_nombre;

    }

    public String[] separaNombre(String nombre){

        String [] artistas = null;
        String [] ret = new String[2];


        nombre=nombre.trim();

        nombre=nombre.toLowerCase();

//        artistas = nombre.split("(\\s*,\\s*)|(\\s+ft\\s+)|(\\s+feat\\s+)|(\\s+feat.\\s+)|(\\s+ft.\\s+)|(\\s+featuring.\\s+)|(\\s+featuring\\s+)|(\\s+&\\s+)");

        char c = '\u2013';
        artistas=nombre.split("(\\s*-\\s*)|(\\s*-\\s*)|(\\s*"+c+"\\s*)");

        //Si la longitud de el vector es de mas de 2 se ha dividido mal PE: Ken-y o artistas con guion
        if (artistas.length>2){
            ret[0]=artistas[0];
            for (int i =1;i<artistas.length-1;i++){
                ret[0]=ret[0]+"-"+artistas[i];
            }
            ret[1]=artistas[artistas.length-1];

        }
        //Caso ideal
        if (artistas.length==2){
            ret=artistas;
        }
        //Peor
        if (artistas.length<2){
            ret[0]="Artista desconocido";
            ret[1]=artistas[0];

        }

        return ret;
    }


    private boolean contiene(String[] busqueda, String[] nombre) {

        boolean contiene = false;

        for (String s : busqueda){

            for (String d : nombre){

                contiene = d.toLowerCase().contains(s.toLowerCase());

                if (contiene) break;
            }

            if (contiene) break;

        }

        return contiene;
    }

    public String capitalizeFirst(String s){

        String ret = s;

        if (s.length()!=0){
            StringBuilder rackingSystemSb = new StringBuilder(ret.toLowerCase());
            rackingSystemSb.setCharAt(0, Character.toUpperCase(rackingSystemSb.charAt(0)));
            ret = rackingSystemSb.toString();
        }
        return ret;
    }

    @Override
    public int compareTo(Web web) {
        return new Integer(web.CALIDAD).compareTo(CALIDAD);
    }
}
