package gui;

import android.content.Context;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.RelativeLayout;

/**
 * Created by PPN on 16/05/2015.
 */
public class Interpolador {

    float x_destino,y_destino,width_destino,height_destino,x_origen,y_origen,width_origen,height_origen,x_actual,y_actual,width_actual,height_actual,diff_x, diff_y,diff_height,diff_width;
    AccelerateDecelerateInterpolator interpolator;


    public void interpola(float ratio,View origen, View destino, Context context){
        int posiciones[] = new int[2];

        if (interpolator==null){
            interpolator = new AccelerateDecelerateInterpolator();


            destino.getLocationOnScreen(posiciones);

            x_destino=destino.getX();
            y_destino=destino.getY();
            width_destino = destino.getWidth();
            height_destino = destino.getHeight();

            origen.getLocationOnScreen(posiciones);

            x_origen=origen.getX();//posiciones[0];
            y_origen=origen.getY();
            width_origen = origen.getWidth();
            height_origen = origen.getHeight();


        /*Calculo del valor a mover*/

            diff_width = width_destino/width_origen;
            diff_height = height_destino/height_origen;
        }


        if (y_destino<y_origen){
            float d=interpolator.getInterpolation(ratio)*(y_origen-y_destino);
            y_actual = y_origen - d;
        }
        else{
            float d=interpolator.getInterpolation(ratio)*(y_destino-y_origen);
            y_actual = y_origen + d;
        }
        if (x_destino<x_origen){
            float d=interpolator.getInterpolation(ratio)*(x_origen-x_destino);
            x_actual = x_origen - d;
        }
        else{
            float d=interpolator.getInterpolation(ratio)*(x_destino-x_origen);
            x_actual = x_origen + d;
        }

        width_actual =(1.0f-interpolator.getInterpolation(ratio)) + diff_width;
        height_actual =(1.0f-interpolator.getInterpolation(ratio)) + diff_height;


        if (width_actual>1 && height_actual >1){

            origen.setLayoutParams(new RelativeLayout.LayoutParams((int)width_origen, (int)height_origen));
        }
        else{
            origen.setLayoutParams(new RelativeLayout.LayoutParams((int) (width_origen*width_actual), (int) (height_origen*height_actual)));
        }


        if (ratio>0.5f){
            //origen.bringToFront();
        }
        origen.setX(x_actual);
        origen.setY(y_actual);

        //Log.d("MP3LOG", "Origen--" + x_origen + "," + y_origen + "," + width_origen + "," + height_origen);

       // Log.d("MP3LOG","Destino--"+x_destino+","+y_destino+","+width_destino+","+height_destino);

    }


}
