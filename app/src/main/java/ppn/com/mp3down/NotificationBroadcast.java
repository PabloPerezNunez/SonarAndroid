package ppn.com.mp3down;

/**
 * Created by PPN on 09/09/2015.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class NotificationBroadcast extends BroadcastReceiver {


    public static final String NOTIFY_PLAY = "notification.play";
    public static final String NOTIFY_NEXT = "notification.next";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(NOTIFY_PLAY)) {
            //Principal.btnReproductor.play();
            Principal.cancion.play();
        }
        if (intent.getAction().equals(NOTIFY_NEXT)) {
            //Principal.btnReproductor.play();
            Principal.reproductorMini.siguienteCancion();
            Principal.log("Siguiente");
        }

    }
}


