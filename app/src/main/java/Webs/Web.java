package Webs;

import java.util.HashSet;
import java.util.Set;

public class Web {

	static final int CALIDAD = 0;
	private Set<WebListener> listeners;

	public Web(){
		listeners = new HashSet<WebListener>();

	}

	public void buscaMusica(String s) throws Exception{

	}

	//=========================================================================================
	//EVENTOS
	//=========================================================================================

	public synchronized void addWebListener(WebListener listener){
		listeners.add(listener);
	}
	public synchronized void removeCancionListener(WebListener listener){
		listeners.remove(listener);
	}
	public synchronized void removeallListeners(){
		listeners.clear();
	}
	public synchronized void fireBusquedaTerminadaEvent(){
		WebEvent event = new WebEvent(this);
		for (WebListener listener : listeners){
			listener.busquedaTerminada(event);
		}
	}


	//=========================================================================================



}
