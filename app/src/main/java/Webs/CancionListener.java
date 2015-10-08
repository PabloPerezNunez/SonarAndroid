package Webs;

/**
 * Created by PPN on 12/09/2015.
 */
public interface CancionListener {

    public void cancionSeleccionada(CancionEvent e);
    public void cancionStartBuffered(CancionEvent e);
    public void cancionBuffered(CancionEvent e);
    public void cancionPreparada(CancionEvent e);
    public void cancionError(CancionEvent e);
    public void cancionCambiada(CancionEvent e);
    public void cancionCompleta(CancionEvent e);
    public void cancionPlay(CancionEvent event);
    public void cancionPause(CancionEvent event);
}
