package Webs;

/**
 * Created by PPN on 18/09/2015.
 */
public class WebEvent {

    private Web source;

    public WebEvent(Web source){
        this.source = source;
    }

    public Web getSource(){
        return source;
    }
}
