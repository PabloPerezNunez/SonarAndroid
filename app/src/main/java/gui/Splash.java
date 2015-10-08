package gui;

/**
 * Created by PPN on 13/09/2015.
 */

import android.animation.Animator;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.ImageView;

import ppn.com.mp3down.Principal;
import ppn.com.mp3down.R;

public class Splash extends Activity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 3000;
    ImageView logo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ventana_splash);

        logo = (ImageView) findViewById(R.id.imgLogo);

        logo.post(new Runnable() {
            @Override
            public void run() {

                if (Build.VERSION.SDK_INT >= 21) {

                    int cx = logo.getWidth() / 2;
                    int cy = logo.getHeight() /2;

                    // get the final radius for the clipping circle
                    int finalRadius = Math.max(logo.getWidth(), logo.getHeight());

                    // create the animator for this view (the start radius is zero)
                    Animator anim = ViewAnimationUtils.createCircularReveal(logo, cx, cy, 0, finalRadius);

                    anim.setDuration(0);
                    logo.requestLayout();

                    logo.setVisibility(View.VISIBLE);
                    anim.start();
                }else{
                    logo.setVisibility(View.VISIBLE);
                }

            }
        });

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                // This method will be executed once the timer is over
                // Start your app main activity
                Intent i = new Intent(Splash.this, Principal.class);
                startActivity(i);

                // close this activity
                finish();
            }
        }, SPLASH_TIME_OUT);
    }

}