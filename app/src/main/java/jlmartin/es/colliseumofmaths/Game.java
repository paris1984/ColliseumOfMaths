package jlmartin.es.colliseumofmaths;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by ILM on 27/02/2015.
 */
public class Game extends SurfaceView implements SurfaceHolder.Callback, SurfaceView.OnTouchListener {
    private SurfaceHolder holder;
    public LoopGame bucle;
    private Activity actividad;


    public int AltoPantalla;
    public int AnchoPantalla;


    private static final String TAG = Game.class.getSimpleName();

    /*Array de Touch */
    private ArrayList<Touch> toques = new ArrayList<Touch>();
    boolean hayToque = false;

    private Bitmap fondo; //Bitmap auxiliar para cargar en el array los recursos
    private static final int MAX_IMAGENES_FONDO = 1; //imagenes que componen el escenario
    Bitmap imagenes[] = new Bitmap[MAX_IMAGENES_FONDO]; // Arrays de imágenes
    /* Array de recursos que componen el escenario*/
    int recursos_imagenes[] = {R.drawable.stonetile};
    //coordenadas y del fondo actual y del siguiente
    int yImgActual, yImgSiguiente;

    /*índices del array de imagenes para alternar el fondo*/
    int img_actual = 0;


    /* Controles */
    private final int IZQUIERDA = 0;
    private final int DERECHA = 1;
    private final int ABAJO = 2;
    private final int ARRIBA = 3;
    private final int DISPARO = 4;

    private final float VELOCIDAD_HORIZONTAL; //pixels por frame
    Control controles[] = new Control[5];

    /* Enemigos */
    Bitmap enemigo_listo;
    //public int total_enemigos; //Enemigos para acabar el Game
    private int enemigos_minuto = 50; //número de enemigos por minuto
    private int frames_para_nuevo_enemigo = 0; //frames que restan hasta generar nuevo enemigo
    private int enemigos_muertos = 0; //Contador de enemigos muertos
    private int enemigos_creados = 0;

    /*Puntos */
    private int Puntos = 0;
    private int Nivel = 0;
    private int PUNTOS_CAMBIO_NIVEL = 2000;

    private Boolean dificil = false;
    private Ecuacion ecuacion;
    private Cronometro cron = new Cronometro();
    private String direccion;

    /* Fin de Game */
    private boolean victoria = false, derrota = false;

    /* Lista Enemigos */
    private ArrayList<Enemigo> lista_enemigos = new ArrayList<Enemigo>();

    /* Disparos */
    private ArrayList<Disparo> lista_disparos = new ArrayList<Disparo>();

    //Bitmap disparo;
    private int frames_para_nuevo_disparo = 0;
    //entre shot y shot deben pasar al menos MAX_FRAMES_ENTRE_DISPARO
    private final int MAX_FRAMES_ENTRE_DISPARO = LoopGame.MAX_FPS / 4;  //4 disparos por segundo aprox.
    private boolean nuevo_disparo = false;

    /*explosiones*/

    private ArrayList<Explosion> lista_explosiones = new ArrayList<Explosion>();
    Bitmap explosion;

    /*Nave*/
    Bitmap nave;
    float xNave; //Coordenada X de la nave, variará con gestos de tipo fling
    float yNave; //Coordenada Y de la nave


    final float SEGUNDOS_EN_RECORRER_PANTALLA_HORIZONTAL = 5;

    /* sonidos */
    MediaPlayer mediaPlayer; //para reproducir la música de fondo

    public Game(Activity context) {
        super(context);
        actividad = context;
        holder = getHolder();
        holder.addCallback(this);

        IniciarMusicaJuego();

        CalculaTamañoPantalla();

        ecuacion = new CalculoEcuaciones(dificil).generarEcuacion();
        cron.start();

        /*Carga la nave*/
        nave = BitmapFactory.decodeResource(getResources(), R.drawable.pj);

        /*Carga la explosión*/
        explosion = BitmapFactory.decodeResource(getResources(), R.drawable.explosion);

        /*posición inicial de la Nave */
        xNave = AnchoPantalla / 2 - nave.getWidth() / 2;//posición inicial de la Nave
        yNave = AltoPantalla / 5 * 4;// posición fija a 4/5 de alto y la mitad de ancho
        VELOCIDAD_HORIZONTAL = AnchoPantalla / SEGUNDOS_EN_RECORRER_PANTALLA_HORIZONTAL / LoopGame.MAX_FPS;
        /* Inicialización de coordenadas de fondo (Se ejecuta primero actualizar()*/
        yImgActual = -1;
        yImgSiguiente = -AltoPantalla - 1;
        CargaBackground();
        CargaControles();
        CargaEnemigos();

        //listener para onTouch
        setOnTouchListener(this);

    }

    private void IniciarMusicaJuego() {
        mediaPlayer = MediaPlayer.create(actividad, R.raw.battle);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.start();
    }

    public void CargaEnemigos() {
        frames_para_nuevo_enemigo = bucle.MAX_FPS * 60 / enemigos_minuto;
        enemigo_listo = BitmapFactory.decodeResource(getResources(), R.drawable.malo);
    }

    public void CargaControles() {
        float aux;

        //flecha_izda
        controles[IZQUIERDA] = new Control(getContext(), 0, AltoPantalla / 5 * 4 + nave.getHeight() - 50);
        controles[IZQUIERDA].Cargar(R.drawable.flecha_izda);
        controles[IZQUIERDA].nombre = "IZQUIERDA";
        //flecha_derecha
        controles[DERECHA] = new Control(getContext(),
                controles[IZQUIERDA].Ancho() + controles[IZQUIERDA].coordenada_x + 5, controles[0].coordenada_y);
        controles[DERECHA].Cargar(R.drawable.flecha_dcha);
        controles[DERECHA].nombre = "DERECHA";
        //flecha_arriba
        controles[ARRIBA] = new Control(getContext(),
                controles[IZQUIERDA].Ancho() / 2 + controles[IZQUIERDA].coordenada_x,
                controles[IZQUIERDA].coordenada_y - 80);
        controles[ARRIBA].Cargar(R.drawable.flecha_arriba);
        controles[ARRIBA].nombre = "ARRIBA";
        //flecha_abajo
        controles[ABAJO] = new Control(getContext(),
                controles[IZQUIERDA].Ancho() / 2 + controles[IZQUIERDA].coordenada_x, controles[IZQUIERDA].coordenada_y + 80);
        controles[ABAJO].Cargar(R.drawable.flecha_abajo);
        controles[ABAJO].nombre = "ABAJO";

        //shot
        aux = 5.0f / 7.0f * AnchoPantalla; //en los 5/7 del ancho
        controles[DISPARO] = new Control(getContext(), aux, controles[0].coordenada_y);
        controles[DISPARO].Cargar(R.drawable.disparo);
        controles[DISPARO].nombre = "DISPARO";
    }

    public void CargaBackground() {
        //cargamos todos los fondos en un array
        for (int i = 0; i < 1; i++) {
            fondo = BitmapFactory.decodeResource(getResources(), recursos_imagenes[i]);
            if (imagenes[i] == null)
                imagenes[i] = fondo.createScaledBitmap(fondo, AnchoPantalla, AltoPantalla, true);
            fondo.recycle();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // se crea la superficie, creamos el game loop

        // Para interceptar los eventos de la SurfaceView
        getHolder().addCallback(this);

        // creamos el game loop
        bucle = new LoopGame(getHolder(), this);

        // Hacer la Vista focusable para que pueda capturar eventos
        setFocusable(true);

        //comenzar el bucle
        bucle.start();
    }

    public void CalculaTamañoPantalla() {
        if (Build.VERSION.SDK_INT > 13) {
            Display display = actividad.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            AnchoPantalla = size.x;
            AltoPantalla = size.y;
        } else {
            Display display = actividad.getWindowManager().getDefaultDisplay();
            AnchoPantalla = display.getWidth();  // deprecated
            AltoPantalla = display.getHeight();  // deprecated
        }
        Log.i(Game.class.getSimpleName(), "alto:" + AltoPantalla + "," + "ancho:" + AnchoPantalla);
    }

    /**
     * Este método actualiza el estado del Game. Contiene la lógica del videojuego
     * generando los nuevos estados y dejando listo el sistema para un repintado.
     */
    public void actualizar() {

        actualiza_fondo();

        /* Controles */
        if (!derrota) {
            if (controles[IZQUIERDA].pulsado) {
                direccion="IZQUIERDA";
                nave = BitmapFactory.decodeResource(getResources(), R.drawable.pj_izda);
                if (xNave > 0)
                    xNave = xNave - VELOCIDAD_HORIZONTAL;
            }

            if (controles[DERECHA].pulsado) {
                direccion="DERECHA";
                nave = BitmapFactory.decodeResource(getResources(), R.drawable.pj_drcha);
                if (xNave < AnchoPantalla - nave.getWidth())
                    xNave = xNave + VELOCIDAD_HORIZONTAL;
            }

            if (controles[ARRIBA].pulsado) {
                direccion="ARRIBA";
                nave = BitmapFactory.decodeResource(getResources(), R.drawable.pj_arrba);
                if (yNave > 0)
                    yNave = yNave - VELOCIDAD_HORIZONTAL;
            }

            if (controles[ABAJO].pulsado) {
                direccion="ABAJO";
                nave = BitmapFactory.decodeResource(getResources(), R.drawable.pj);
                if (yNave < AltoPantalla - nave.getHeight())
                    yNave = yNave + VELOCIDAD_HORIZONTAL;
            }

            /* Disparo */
            if (controles[DISPARO].pulsado)
                nuevo_disparo = true;

            if (frames_para_nuevo_disparo == 0) {
                if (nuevo_disparo) {
                    CreaDisparo();
                    nuevo_disparo = false;
                }
                //nuevo ciclo de disparos
                frames_para_nuevo_disparo = MAX_FRAMES_ENTRE_DISPARO;
            }
            frames_para_nuevo_disparo--;
        }

        //Los disparos se mueven
        for (Iterator<Disparo> it_disparos = lista_disparos.iterator(); it_disparos.hasNext(); ) {
            Disparo d = it_disparos.next();
            d.ActualizaCoordenadas(direccion);

            if (d.FueraDePantalla()) {
                it_disparos.remove();
            }
        }

        /*Enemigos*/
        if (frames_para_nuevo_enemigo == 0) {
            CrearNuevoEnemigo();
            //nuevo ciclo de enemigos
            frames_para_nuevo_enemigo = bucle.MAX_FPS * 60 / enemigos_minuto;
        }
        frames_para_nuevo_enemigo--;

        //Los enemigos persiguen al jugador
        for (Enemigo e : lista_enemigos) {
            e.ActualizaCoordenadas();
        }

        //colisiones
        for (Iterator<Enemigo> it_enemigos = lista_enemigos.iterator(); it_enemigos.hasNext(); ) {
            Enemigo e = it_enemigos.next();
            for (Iterator<Disparo> it_disparos = lista_disparos.iterator(); it_disparos.hasNext(); ) {
                Disparo d = it_disparos.next();
                if (Colision(e, d)) {
                    /* Creamos un nuevo objeto explosión */
                    lista_explosiones.add(new Explosion(this, e.coordenada_x, e.coordenada_y));
                    /* eliminamos de las listas tanto el shot como el enemigo */
                    try {
                        it_enemigos.remove();
                        it_disparos.remove();
                    } catch (Exception ex) {
                    }
                    enemigos_muertos++; //un enemigo menos para el final

                    /*Puntos*/
                    if (e.tipo_enemigo == e.ENEMIGO_INTELIGENTE)
                        Puntos += 1;
                    else
                        Puntos += 1;
                }
            }
        }

        //actualizar explosiones
        for (Iterator<Explosion> it_explosiones = lista_explosiones.iterator(); it_explosiones.hasNext(); ) {
            Explosion exp = it_explosiones.next();
            exp.ActualizarEstado();
            if (exp.HaTerminado()) it_explosiones.remove();
        }

        //cada PUNTOS_CAMBIO_NIVEL puntos se incrementa la dificultad
        if (Nivel != Puntos / PUNTOS_CAMBIO_NIVEL) {
            Nivel = Puntos / PUNTOS_CAMBIO_NIVEL;
            enemigos_minuto += (20 * Nivel);
        }

        if (!derrota && !victoria)
            CompruebaFinJuego();

    }

    public void CompruebaFinJuego() {

        for (Enemigo e : lista_enemigos) {
            if (ColisionNave(e)) {
                lista_explosiones.add(new Explosion(this, e.coordenada_x, e.coordenada_y));
                derrota = true;
            }
        }

        if (!derrota) {
            if (enemigos_muertos == ecuacion.resultado && yNave<=this.AltoPantalla*0.10) {

                victoria = true;
            }else if (enemigos_muertos > ecuacion.resultado && yNave<=this.AltoPantalla*0.10){
                derrota = true;
            }
            if(!cron.isAlive()){
                derrota = true;
            }
        }

    }

    public boolean ColisionNave(Enemigo e) {
        int alto_mayor = e.Alto() > nave.getHeight() ? e.Alto() : nave.getHeight();
        int ancho_mayor = e.Ancho() > nave.getWidth() ? e.Ancho() : nave.getWidth();
        float diferenciaX = Math.abs(e.coordenada_x - xNave);
        float diferenciaY = Math.abs(e.coordenada_y - yNave);
        return diferenciaX < ancho_mayor && diferenciaY < alto_mayor;
    }

    public boolean Colision(Enemigo e, Disparo d) {
        int alto_mayor = e.Alto() > d.Alto() ? e.Alto() : d.Alto();
        int ancho_mayor = e.Ancho() > d.Ancho() ? e.Ancho() : d.Ancho();
        float diferenciaX = Math.abs(e.coordenada_x - d.coordenada_x);
        float diferenciaY = Math.abs(e.coordenada_y - d.coordenada_y);
        return diferenciaX < ancho_mayor && diferenciaY < alto_mayor;
    }

    /**
     * Este método dibuja el siguiente paso de la animación correspondiente
     */
    public void renderizar(Canvas canvas) {
        if (canvas != null) {
            //pinceles
            Paint myPaint = new Paint();
            myPaint.setStyle(Paint.Style.FILL);
            myPaint.setColor(Color.WHITE);

            Paint myPaint2 = new Paint();
            myPaint2.setStyle(Paint.Style.FILL);
            myPaint2.setTextSize(50);

            //dibujamos el fondo
            canvas.drawBitmap(imagenes[img_actual], 0, yImgActual, null);


            //Si ha ocurrido un toque en la pantalla "Touch", dibujar un círculo
            if (hayToque) {
                synchronized (this) {
                    for (Touch t : toques) {
                        canvas.drawCircle(t.x, t.y, 100, myPaint);
                        //canvas.drawText(t.index + "", t.x, t.y, myPaint2);
                    }
                }
            }

            if (!derrota)
                canvas.drawBitmap(nave, xNave, yNave, null);

            //dibuja los enemigos
            for (Enemigo e : lista_enemigos) {
                e.Dibujar(canvas, myPaint);
            }

            //dibuja los disparos
            for (Disparo d : lista_disparos) {
                d.Dibujar(canvas, myPaint);
            }

            //dibuja las explosiones
            for (Explosion exp : lista_explosiones)
                exp.Dibujar(canvas, myPaint);

            //dibuja los controles
            myPaint.setAlpha(200);
            for (int i = 0; i < 5; i++) {
                controles[i].Dibujar(canvas, myPaint);
            }

            //escribe los puntos
            myPaint.setTextSize(AnchoPantalla / 25); //25 es el número de letras aprox que sale en una línea
            canvas.drawText("PUNTOS " + Puntos + " - Nivel " + Nivel + " - Tiempo restante:"+cron.tiempo, 50, 50, myPaint);
            canvas.drawText(ecuacion.toString(), 50, 100, myPaint);

            if (victoria) {
                myPaint.setAlpha(0);
                myPaint.setColor(Color.RED);
                myPaint.setTextSize(AnchoPantalla / 10);
                canvas.drawText("VICTORIA!!", 50, AltoPantalla / 2 - 100, myPaint);
                myPaint.setTextSize(AnchoPantalla / 20);
                canvas.drawText("Las tropas enemigas han sido derrotadas", 50, AltoPantalla / 2 + 100, myPaint);
            }

            if (derrota) {
                myPaint.setAlpha(0);
                myPaint.setColor(Color.RED);
                myPaint.setTextSize(AnchoPantalla / 10);
                canvas.drawText("DERROTA!!", 50, AltoPantalla / 2 - 100, myPaint);
                myPaint.setTextSize(AnchoPantalla / 20);
                canvas.drawText("La raza humana está condenada!!!!", 50, AltoPantalla / 2 + 100, myPaint);
            }

        }
    }

    public void CreaDisparo() {
        switch (direccion){
            case "ARRIBA":
                lista_disparos.add(new Disparo(this,BitmapFactory.decodeResource(getResources(), R.drawable.shot), xNave, yNave));
                break;
            case "ABAJO":
                lista_disparos.add(new Disparo(this,BitmapFactory.decodeResource(getResources(), R.drawable.shot_abajo), xNave-10, yNave+(nave.getHeight()*2)));
                break;
            case "DERECHA":
                lista_disparos.add(new Disparo(this,BitmapFactory.decodeResource(getResources(), R.drawable.shot_derecha), xNave+nave.getWidth(), yNave+nave.getHeight()));
                break;
            case "IZQUIERDA":
                lista_disparos.add(new Disparo(this,BitmapFactory.decodeResource(getResources(), R.drawable.shot_izda), xNave-(nave.getWidth()*2), yNave+nave.getHeight()));
                break;

        }

    }

    public void CrearNuevoEnemigo() {

        lista_enemigos.add(new Enemigo(this, Nivel));
        enemigos_creados++;

    }

    public void actualiza_fondo() {
        //nueva posición del fondo

    }


    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        // cerrar el thread y esperar que acabe
        boolean retry = true;
        while (retry) {
            try {
                fin();
                bucle.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int index;
        int x, y;

        // Obtener el pointer asociado con la acción
        index = MotionEventCompat.getActionIndex(event);

        x = (int) MotionEventCompat.getX(event, index);
        y = (int) MotionEventCompat.getY(event, index);

        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                hayToque = true;

                synchronized (this) {
                    toques.add(index, new Touch(index, x, y));
                }

                //se comprueba si se ha pulsado
                for (int i = 0; i < 5; i++)
                    controles[i].comprueba_pulsado(x, y);
                break;

            case MotionEvent.ACTION_POINTER_UP:
                synchronized (this) {
                    toques.remove(index);
                }

                //se comprueba si se ha soltado el botón
                for (int i = 0; i < 5; i++)
                    controles[i].comprueba_soltado(toques);
                break;

            case MotionEvent.ACTION_UP:
                synchronized (this) {
                    toques.clear();
                }
                hayToque = false;
                //se comprueba si se ha soltado el botón
                for (int i = 0; i < 5; i++)
                    controles[i].comprueba_soltado(toques);
                break;
        }

        return true;
    }

    public void fin() {
        bucle.fin();
        mediaPlayer.release();
        for (int i = 0; i < MAX_IMAGENES_FONDO; i++)
            imagenes[i].recycle();
        nave.recycle();
        enemigo_listo.recycle();

    }
}
