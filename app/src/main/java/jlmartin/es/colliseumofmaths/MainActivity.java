package jlmartin.es.colliseumofmaths;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    int AltoPantalla;
    int AnchoPantalla;
    Button botonJuego;
    CheckBox dificil;
    ImageView titulo;
    MediaPlayer mediaPlayer;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        botonJuego = findViewById(R.id.comenzar);
        dificil = findViewById(R.id.dificil);
        titulo = findViewById(R.id.titulo);

        CalculaTamañoPantalla();
        AnimacionBoton();
        IniciaMusicaIntro();
        AnimacionInicial();
        botonJuego.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();mediaPlayer.reset();
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("dificil",dificil.isChecked());
                MainActivity.this.startActivity(intent);
            }
        });

    }

    //metodos publicos
    public void CalculaTamañoPantalla(){
        if(Build.VERSION.SDK_INT > 13) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            AnchoPantalla = size.x;
            AltoPantalla = size.y;
        }
        else{
            Display display = getWindowManager().getDefaultDisplay();
            AnchoPantalla = display.getWidth();  // deprecated
            AltoPantalla = display.getHeight();  // deprecated
        }

        Log.i(this.getClass().getSimpleName(), "alto:" + AltoPantalla + "," + "ancho:" + AnchoPantalla);
    }

    public void AnimacionBoton(){
        if(Build.VERSION.SDK_INT > 10) {
            AnimatorSet animadorBoton = new AnimatorSet();

            ObjectAnimator trasladar = ObjectAnimator.ofFloat(botonJuego, "translationX", -800, 0);
            System.out.println(botonJuego.getWidth() + ":" + AnchoPantalla);
            trasladar.setDuration(3000);
            ObjectAnimator fade = ObjectAnimator.ofFloat(botonJuego, "alpha", 0f, 1f);
            fade.setDuration(3000);
            animadorBoton.play(trasladar).with(fade);
            animadorBoton.start();

            try {
                Thread.sleep(1000L);
            } catch (InterruptedException e) {
                Log.e("ERROR","Error en el sleep");
            }
            AnimatorSet animadorBoton1 = new AnimatorSet();

            ObjectAnimator trasladar1 = ObjectAnimator.ofFloat(dificil, "translationX", -800, 0);
            System.out.println(dificil.getWidth() + ":" + AnchoPantalla);
            trasladar1.setDuration(3000);
            ObjectAnimator fade1 = ObjectAnimator.ofFloat(dificil, "alpha", 0f, 1f);
            fade1.setDuration(3000);
            animadorBoton1.play(trasladar1).with(fade1);
            animadorBoton1.start();
        }
    }

    public void AnimacionInicial(){
        try{

            /*ANIMACIÓN coliseo */
            ImageView coliseo = findViewById(R.id.coliseo);
            coliseo.setVisibility(ImageView.VISIBLE);
            //(xFrom,xTo, yFrom,yTo
            TranslateAnimation animation = new TranslateAnimation(-200, AnchoPantalla/2,-400, AltoPantalla/2);
            animation.setDuration(6000);  // duración de la animación
            coliseo.startAnimation(animation);  // comenzar animación

            AnimatorSet animadorImagen = new AnimatorSet();
            ObjectAnimator trasladar = ObjectAnimator.ofFloat(titulo, "translationX", 0, 0);
            ObjectAnimator fade = ObjectAnimator.ofFloat(titulo, "alpha", 0f, 1f);
            fade.setDuration(8000);
            animadorImagen.play(trasladar).with(fade);
            animadorImagen.start();

        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    public void IniciaMusicaIntro(){
        mediaPlayer = MediaPlayer.create(this, R.raw.heroicdemise);
        mediaPlayer.setVolume(0.5f,0.5f);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
            }
        });
        mediaPlayer.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
    }
}
