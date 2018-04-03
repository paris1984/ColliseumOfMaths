package jlmartin.es.colliseumofmaths;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.Log;

/**
 * Created by ILM on 05/03/2015.
 */
public class Disparo {
    public float coordenada_x, coordenada_y; //coordenadas donde se dibuja el control
    private Game Game;
    private float velocidad;
    private Integer contador = 0;
    private MediaPlayer mediaPlayer; //para reproducir el sonido de shot
    private final float MAX_SEGUNDOS_EN_CRUZAR_PANTALLA=3;
    /*Constructor con coordenadas iniciales y número de shot*/
    public Disparo(Game j,float x, float y){
        Game=j;
        coordenada_x=x;
        coordenada_y=y-j.disparo.getHeight()+15;
        velocidad=j.AltoPantalla/MAX_SEGUNDOS_EN_CRUZAR_PANTALLA/LoopGame.MAX_FPS; //adaptar velocidad al tamaño de pantalla
        Log.i(Game.class.getSimpleName(),"Velocidad de shot: " + velocidad);
        mediaPlayer=MediaPlayer.create(j.getContext(), R.raw.disparo);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mediaPlayer.start();
    }



    //se actualiza la coordenada y nada más
    public void ActualizaCoordenadas(){
        coordenada_y-=velocidad;
        contador++;
    }

    public void Dibujar(Canvas c, Paint p) {
        c.drawBitmap(Game.disparo, coordenada_x, coordenada_y, p);
    }

    public int Ancho(){
        return Game.disparo.getWidth();
    }

    public int Alto(){
        return Game.disparo.getHeight();
    }

    public boolean FueraDePantalla() {
        return contador==3;
    }

}
