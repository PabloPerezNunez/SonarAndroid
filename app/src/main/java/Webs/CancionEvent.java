package Webs;

/**
 * Created by PPN on 12/09/2015.
 */
public class CancionEvent {

    private Cancion source;

    public CancionEvent(Cancion source){
        this.source = source;
    }

    public Cancion getSource(){
        return source;
    }

}
