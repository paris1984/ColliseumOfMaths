package jlmartin.es.colliseumofmaths;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;


/**
 * Created by ILM on 07/03/2015.
 */
public class Explosion {
    private int ancho_sprite;
    private int alto_sprite;
    private final int NUMERO_IMAGENES_EN_SECUENCIA=17;
    public float coordenada_x, coordenada_y; //coordenadas donde se dibuja el control
    private Game Game;
    private int estado;
    private MediaPlayer mediaPlayer; //reproduce explosión

    /*Constructor con coordenadas iniciales y número de explosión*/
    public Explosion(Game j, float x, float y){
        coordenada_x=x;
        coordenada_y=y;
        Game=j;
        ancho_sprite=Game.explosion.getWidth()/NUMERO_IMAGENES_EN_SECUENCIA;
        alto_sprite=Game.explosion.getHeight();
        estado=-1; //recien creado
        mediaPlayer=MediaPlayer.create(j.getContext(), R.raw.explosion);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        mediaPlayer.start();
    }

    public void ActualizarEstado(){
        //incrementamos el estado al siguiente momento de la explosión
        estado++;
    }

    public void Dibujar(Canvas c, Paint p){
        int posicionSprite=estado*ancho_sprite;

        if(!HaTerminado()) {
            //Calculamos el cuadrado del sprite que vamos a dibujar
            Rect origen = new Rect(posicionSprite, 0, posicionSprite + ancho_sprite,
                    alto_sprite);

            //calculamos donde vamos a dibujar la porcion del sprite
            Rect destino = new Rect((int)coordenada_x,(int) coordenada_y,(int) coordenada_x + ancho_sprite,
                    (int) coordenada_y + Game.explosion.getHeight());

            c.drawBitmap(Game.explosion, origen, destino, p);


        }

    }


    public boolean HaTerminado(){
        return estado>=NUMERO_IMAGENES_EN_SECUENCIA;
    }

}
