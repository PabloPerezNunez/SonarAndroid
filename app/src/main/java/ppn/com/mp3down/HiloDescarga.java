package ppn.com.mp3down;

import android.content.SharedPreferences;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;

import com.mpatric.mp3agic.ID3v2;
import com.mpatric.mp3agic.ID3v24Tag;
import com.mpatric.mp3agic.Mp3File;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import Webs.Cancion;
import gui.itemDescarga;


public class HiloDescarga extends Thread{
	
	private URL url;
	private String nombre,artista;
	private ProgressBar progress;
    private Cancion cancion;
    private itemDescarga item;
    private int itemNum;

	public HiloDescarga(Cancion c){


        cancion = c;
        nombre=cancion.getTitle();
        artista = cancion.getArtist();
       // progress = item.getProgressBar();

	}
		
	
	public void run(){

        Handler handler = new Handler(Looper.getMainLooper());

        SharedPreferences SP = Principal.preferencias;

        String carpeta = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC).getAbsolutePath();
        String dowload_folder = SP.getString(Principal.OPCION_CARPETA_DESCARGAS, carpeta);

        File file = null;

        try{

            url=cancion.getLink();

            String[] lista = url.toString().split("[.]");
            String ext = "mp3";



            if (artista.isEmpty()){
                file = new File(dowload_folder, "/" + nombre+"."+ext);
            }
            else{
                file = new File(dowload_folder, "/" + artista+" - "+nombre+"."+ext);
            }


            if (file.exists()){

            }

            URLConnection connection = url.openConnection();

            InputStream input = new BufferedInputStream(connection.getInputStream());
            OutputStream output = new FileOutputStream(file);

            int fileLength = connection.getContentLength();

            byte data[] = new byte[1024];
            long total = 0;
            int count;


            while (((count = input.read(data)) != -1)&& Principal.descargas.contains(cancion)) {
                total += count;
                // publishing the progress....
                final int porc = (int) (total * 100 / fileLength);

                handler.post(new Runnable() {
                    public void run() {
                        if (getNumeroDescarga(cancion)!=-1 && Descargas.listaDescarga.getChildAt(getNumeroDescarga(cancion))!=null){

                            ((itemDescarga)Descargas.listaDescarga.getChildAt(itemNum)).getProgressBar().setProgress(porc); //progress.setProgress(porc);

                        }

                    }
                });
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();

            cambiaTags(file);

            if (Principal.descargas.contains(cancion)){
                //Si no se interrumpio la descarga,se elimina de la lista de descargas.
                Descargas.finDescarga(cancion, file, false);

            }
            else{

                if (file.exists()&& file.delete()){
                    Principal.log("Descarga eliminada");
                }

            }


            //Principal.finDescarga(cancion, item, false);


        }catch(Exception e){

            e.printStackTrace();
            Descargas.finDescarga(cancion, file, true);
            Principal.toast(Principal.contexto.getString(R.string.error_descarga));

        }
    }

    private void cambiaTags(File file) {


        try {

            Mp3File mp3file = new Mp3File(file.getAbsolutePath());
            ID3v2 id3v2Tag;


            if (mp3file.hasId3v2Tag()) {
                id3v2Tag = mp3file.getId3v2Tag();
            } else {
                // mp3 does not have an ID3v2 tag, let's create one..
                id3v2Tag = new ID3v24Tag();
                mp3file.setId3v2Tag(id3v2Tag);
            }
            id3v2Tag.setAlbumImage(IOUtils.toByteArray(Principal.contexto.getResources().openRawResource(R.raw.cover)), "image/jpeg");
            mp3file.save(file.getAbsolutePath() + ".new");
            file.delete();

            File tagged = new File(mp3file.getFilename()+ ".new");
            tagged.renameTo(new File(mp3file.getFilename()));



        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public int getNumeroDescarga(Cancion c){
        int i = -1;

        if (Descargas.listaDescarga!=null) {

            for (i = 0; i < Descargas.listaDescarga.getChildCount(); i++) {

                item = (itemDescarga) Descargas.listaDescarga.getChildAt(i);

                if (item.getCancion() == c) {
                    break;
                }

            }

        }
        return i;
    }


}
