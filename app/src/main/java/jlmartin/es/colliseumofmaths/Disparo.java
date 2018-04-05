package jlmartin.es.colliseumofmaths;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by ILM on 05/03/2015.
 */
public class Disparo {
    public float coordenada_x, coordenada_y; //coordenadas donde se dibuja el control
    Bitmap disparo;

    private float velocidad;
    private Integer contador = 0;
    private MediaPlayer mediaPlayer; //para reproducir el sonido de shot
    private final float MAX_SEGUNDOS_EN_CRUZAR_PANTALLA=3;
    /*Constructor con coordenadas iniciales y número de shot*/
    public Disparo(Game g,Bitmap j,float x, float y){
        disparo=j;
        coordenada_x=x;
        coordenada_y=y-disparo.getHeight()+15;
        velocidad=g.AltoPantalla/MAX_SEGUNDOS_EN_CRUZAR_PANTALLA/LoopGame.MAX_FPS; //adaptar velocidad al tamaño de pantalla
        Log.i(Game.class.getSimpleName(),"Velocidad de shot: " + velocidad);
        mediaPlayer=MediaPlayer.create(g.getContext(), R.raw.disparo);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mediaPlayer.start();
    }



    //se actualiza la coordenada y nada más
    public void ActualizaCoordenadas(String direccion){
        switch (direccion){
            case "ARRIBA":
                coordenada_y-=velocidad;
                break;
            case "ABAJO":
                coordenada_y+=velocidad;
                break;
            case "DERECHA":
                coordenada_x+=velocidad;
                break;
            case "IZQUIERDA":
                coordenada_x-=velocidad;
                break;
        }
        contador++;
    }

    public void Dibujar(Canvas c, Paint p) {
        c.drawBitmap(disparo, coordenada_x, coordenada_y, p);
    }

    public int Ancho(){
        return disparo.getWidth();
    }

    public int Alto(){
        return disparo.getHeight();
    }

    public boolean FueraDePantalla() {
        return contador==3;
    }

}
