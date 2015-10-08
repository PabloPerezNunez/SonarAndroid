package Webs;
import android.media.MediaPlayer;

import java.io.Serializable;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import ppn.com.mp3down.Principal;


public class Cancion implements Comparable <Cancion>, Serializable, MediaPlayer.OnCompletionListener{
	
	private String title,artist, duracion;
	private URL link;
	private int calidad;
	private boolean error,loaded;
	private Set<CancionListener> listeners;
	private MediaPlayer mediaPlayer;


	public Cancion(String tit, URL url){
		this(tit, "", url, 0, "");
	}
	public Cancion(String tit,String art, URL url){
		this(tit,art,url,0,"");
	}
	public Cancion(String tit,URL url, int calidad,String duracion){
		this(tit,"",url,calidad,duracion);
	}
	public Cancion(String tit, String art,URL url, int calidad,String duracion){
		setTitle(tit);
		setArtist(art);
		setLink(url);
		setCalidad(calidad);
		setDuracion(duracion);
		listeners = new HashSet<CancionListener>();
		error=false;
        loaded=false;
		configuraMediaPlayer();
	}
	private void configuraMediaPlayer() {

		mediaPlayer = new MediaPlayer();


		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mediaPlayer) {
				fireCancionBufferedEvent();
			}
		});


	}

	//=========================================================================================
	//EVENTOS
	//=========================================================================================

	public synchronized void addCancionListener(CancionListener listener){
		listeners.add(listener);
	}
	public synchronized void removeCancionListener(CancionListener listener){
		listeners.remove(listener);
	}
	public synchronized void removeallListeners(){
		listeners.clear();
	}

	public synchronized void fireCancionStartBufferedEvent(){
		CancionEvent event = new CancionEvent(this);
		for (CancionListener listener : listeners){
			listener.cancionStartBuffered(event);
		}
	}
	public synchronized void fireCancionBufferedEvent(){
		CancionEvent event = new CancionEvent(this);
		for (CancionListener listener : listeners){
			listener.cancionBuffered(event);
		}
	}
	public synchronized void fireCancionPreparadaEvent(){

		cargaCancion();

		CancionEvent event = new CancionEvent(this);
		for (CancionListener listener : listeners){
			listener.cancionPreparada(event);
		}
	}
	public synchronized void fireCancionErrorEvent(){
		CancionEvent event = new CancionEvent(this);
		for (CancionListener listener : listeners){
			listener.cancionError(event);
		}
	}
	public synchronized void fireCancionSeleccionadaEvent(){
		CancionEvent event = new CancionEvent(this);
		for (CancionListener listener : listeners){
			listener.cancionSeleccionada(event);
		}
	}
	public synchronized void fireCancionCambiadaEvent(){
		CancionEvent event = new CancionEvent(this);
		for (CancionListener listener : listeners){
			listener.cancionCambiada(event);
		}

		mediaPlayer.reset();
		//configuraMediaPlayer();


	}
	public synchronized void fireCancionCompletaEvent(){
		CancionEvent event = new CancionEvent(this);
		for (CancionListener listener : listeners){
			listener.cancionCompleta(event);
		}
	}
    public synchronized void fireCancionPlayEvent(){
        CancionEvent event = new CancionEvent(this);
        for (CancionListener listener : listeners){
            listener.cancionPlay(event);
        }
    }
    public synchronized void fireCancionPauseEvent(){
        CancionEvent event = new CancionEvent(this);
        for (CancionListener listener : listeners){
            listener.cancionPause(event);
        }
    }

	//=========================================================================================

	public URL getLink() {
		return link;
	}
	public void setLink(URL link) {
		this.link = link;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public int getCalidad() {
		return calidad;
	}
	public void setCalidad(int calidad) {
		this.calidad = calidad;
	}
	public boolean hasError() {
		return error;
	}
	public void setError(boolean error) {
		this.error = error;
	}
	public String getDuracion() {
		return duracion;
	}
	public void setDuracion(String dur) {
		this.duracion = dur;
	}
	public String getArtist() {
		return artist;
	}
	public void setArtist(String artist) {
		this.artist = artist;
	}
	public MediaPlayer getMediaPlayer() {
		return mediaPlayer;
	}
	public void setMediaPlayer(MediaPlayer mediaPlayer) {
		this.mediaPlayer = mediaPlayer;
	}
	public void cargaCancion(){
		loaded=true;

		Boolean play_only_wifi = Principal.preferencias.getBoolean(Principal.OPCION_PLAY_WIFI, true);

		if (((play_only_wifi && Principal.hasWIFIConnection()) || (!play_only_wifi)) && !hasError()) {
			new HiloSetCancion(this).start();
		}
	}
	public void descarga() {}
    public void play() {
        try {

            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                fireCancionPauseEvent();

            } else {
                mediaPlayer.start();
                fireCancionPlayEvent();
				mediaPlayer.setOnCompletionListener(this);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

	@Override
	public int compareTo(Cancion o) {
		// TODO Auto-generated method stub
		return this.getTitle().compareTo(o.getTitle());
	}

	@Override
	public void onCompletion(MediaPlayer mediaPlayer) {
		fireCancionCompletaEvent();
	}

	public int getListenersSize() {
		return listeners.size();
	}

	public class HiloSetCancion extends Thread {

		Cancion cancion;

		public HiloSetCancion(Cancion c){
			cancion=c;
		}

        @Override
        public void run() {

            try {

                //Comprueba el semaforo que indica si esta preparado el link de descarga, y en caso de que no, se bloquea.
                //Si existe un error con el link de descarga, no se prepara la cancion y el reproductor permanece igual.

                cancion.getMediaPlayer().setDataSource(getLink().toString());
                cancion.getMediaPlayer().prepareAsync();
                cancion.fireCancionStartBufferedEvent();

            } catch (Exception e) {
                e.printStackTrace();

            }


        }
    }

}
