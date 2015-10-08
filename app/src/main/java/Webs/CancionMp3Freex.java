package Webs;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.net.URL;

import ppn.com.mp3down.Principal;

public class CancionMp3Freex extends Cancion {


	public CancionMp3Freex(String tit, String art, URL url) {
		super(tit,art, url);
		// TODO Auto-generated constructor stub
	}

	public CancionMp3Freex(String tit, URL url) {
		super(tit, url);
		// TODO Auto-generated constructor stub
	}

	public CancionMp3Freex(String tit, String art, URL url, int calidad,String duracion) {
		super(tit, art, url, calidad,duracion);
	}

	public CancionMp3Freex(String nombre, URL url, int calidad,String duracion) {
		super(nombre, url,calidad,duracion);
	}

	@Override
	public void descarga() {

        new DescargaMP3FREEX().start();
    }

    public class DescargaMP3FREEX extends Thread{

        @Override
        public void run(){
            Document doc = null;

            addCancionListener(new CancionEventListener(){

                @Override
                public void cancionCambiada(CancionEvent e){
                    DescargaMP3FREEX.this.interrupt();
                }

            });

            try

            {

            //Si el link ya es un link a un recurso de audio, se evita este paso

                Connection.Response resp = Jsoup.connect(getLink().toString()).ignoreContentType(true).timeout(5000).execute();
                String contentType = resp.contentType();


                if (contentType.compareTo("audio/mpeg")!=0) {

                    doc = resp.parse();

                    Element script = doc.select("head").first().select("script").last();


                    String url = script.html().trim();

                    url = url.replace("var my_data = '\"", "");
                    url = url.replace("\\/", "/");
                    url = url.replace("\"';", "");

                    if (url.contains("goear")) {
                        throw new Exception();
                    }

                    if (!url.contains(".mp3")) {
                        doc = Jsoup.connect(url)
                                .ignoreContentType(true)
                                .get();

                        url = doc.baseUri();
                        Principal.log("NO CONTIENE MP3:" + url);

                    }

                    //esta vivo?
                    resp = Jsoup.connect(url).ignoreContentType(true).timeout(5000).execute();

                    setLink(new URL(url));
                }

                fireCancionPreparadaEvent();



            } catch (Exception e) {
                setError(true);
                fireCancionErrorEvent();
                Principal.log("Error:"+getLink());
                e.printStackTrace();
            }
        }

    }



}
	
