package jlmartin.es.colliseumofmaths;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

/**
 * Created by ILM on 04/03/2015.
 */
public class Enemigo {

    public final int ENEMIGO_INTELIGENTE=0; //enemigo que sigue a la nave
    public final int enemigo_listo=1;       //enemigo que se mueve aleatoriamente

    public final float VELOCIDAD_ENEMIGO_INTELIGENTE=3;
    public final float VELOCIDAD_ENEMIGO_TONTO=2;
    public float velocidad;

    public float coordenada_x, coordenada_y; //coordenadas donde se dibuja el control
    public int tipo_enemigo; //imagen del control

    public float direccion_vertical=1;  //inicialmente hacia abajo
    public float direccion_horizontal=1; //inicialmente derecha
    
    private int Nivel;

    private Game juego;


    public Enemigo(Game j, int n){
        juego=j;
        Nivel=n;

        //20 segundos en cruzar * factor de inteligencia y nivel
        float VELOCIDAD_ENEMIGO=j.AltoPantalla/20f/LoopGame.MAX_FPS;

        //probabilidad de enemigo tonto 80%, enemigo listo 20%
        if(Math.random()>0.20) {
            tipo_enemigo = enemigo_listo;
            velocidad = (VELOCIDAD_ENEMIGO_TONTO+Nivel)*VELOCIDAD_ENEMIGO;
            Log.i(Game.class.getSimpleName(),"Velocidad de los enemigos tontos "+velocidad);
        }
        else {
            tipo_enemigo = ENEMIGO_INTELIGENTE;
            velocidad = (VELOCIDAD_ENEMIGO_INTELIGENTE+Nivel)*VELOCIDAD_ENEMIGO;
            Log.i(Game.class.getSimpleName(),"Velocidad de los enemigos inteligentes "+velocidad);
        }

        //para el enemigo tonto se calcula la dirección aleatoria
        if(Math.random()>0.5)
            direccion_horizontal=1; //derecha
        else
            direccion_horizontal=-1; //izquierda

        if(Math.random()>0.5)
            direccion_vertical=1; //abajo
        else
            direccion_vertical=-1; //arriba


        CalculaCoordenadas();

    }

    public void CalculaCoordenadas(){
        double x; //aleatorio
        /* Posicionamiento del enemigo */

        //dividimos la probabilidad para que salga entre los 3 lados de la pantalla (arriba, derecha o izda)

        x=Math.random();

        if(x<=0.20){
            //sale por abajo
            coordenada_x = (int)(Math.random()* (juego.AnchoPantalla-juego.enemigo_listo.getWidth()));
            coordenada_y=juego.AltoPantalla-juego.enemigo_listo.getHeight();

        }else if(x>0.20 && x<=0.60){
            //sale por la izda
            coordenada_x=0;
            coordenada_y = (int)(Math.random()* (juego.AltoPantalla-juego.enemigo_listo.getHeight()));

        }else{
            //sale por la derecha
            coordenada_x=juego.AnchoPantalla-juego.enemigo_listo.getWidth();
            coordenada_y = (int)(Math.random()* (juego.AltoPantalla-juego.enemigo_listo.getHeight()));
        }
    }

    //Actualiza la coordenada del enemigo con respecto a la coordenada de la nave
    public void ActualizaCoordenadas(){
        if(tipo_enemigo==ENEMIGO_INTELIGENTE) {
            if (juego.xNave > coordenada_x)
                coordenada_x+=velocidad;
            else if (juego.xNave < coordenada_x)
                coordenada_x-=velocidad;

            if(Math.abs(coordenada_x-juego.xNave)<velocidad)
                coordenada_x=juego.xNave; //si está muy cerca se pone a su altura

            if( coordenada_y>=juego.AltoPantalla-juego.enemigo_listo.getHeight()
                    && direccion_vertical==1)
                direccion_vertical=-1;
            if(coordenada_y<=0 && direccion_vertical ==-1)
                direccion_vertical=1;

            coordenada_y+=direccion_vertical*velocidad;
        }
        else{
            //el enemigo tonto hace caso omiso a la posición de la nave,
            //simplemente pulula por la pantalla
            coordenada_x+=direccion_horizontal*velocidad;
            coordenada_y+=direccion_vertical*velocidad;
            //Cambios de direcciones al llegar a los bordes de la pantalla
            if(coordenada_x<=0 && direccion_horizontal==-1)
                direccion_horizontal=1;
            if(coordenada_x>juego.AnchoPantalla-juego.enemigo_listo.getWidth() && direccion_horizontal==1)
                direccion_horizontal=-1;
            if(coordenada_y>=juego.AltoPantalla && direccion_vertical ==1)
                direccion_vertical=-1;
            if(coordenada_y<=0 && direccion_vertical==-1)
                direccion_vertical=1;
        }
    }

    public void Dibujar(Canvas c, Paint p){
        if(tipo_enemigo==enemigo_listo)
            c.drawBitmap(juego.enemigo_listo,coordenada_x,coordenada_y,p);
        else
            c.drawBitmap(juego.enemigo_listo,coordenada_x,coordenada_y,p);
    }

    public int Ancho(){
        if(tipo_enemigo==enemigo_listo)
            return juego.enemigo_listo.getWidth();
        else
            return juego.enemigo_listo.getWidth();
    }

    public int Alto(){
        if(tipo_enemigo==enemigo_listo)
            return juego.enemigo_listo.getHeight();
        else
            return juego.enemigo_listo.getHeight();
    }

}
