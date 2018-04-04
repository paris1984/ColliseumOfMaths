package jlmartin.es.colliseumofmaths;

import java.util.Random;

/**
 * Created by jose.mrodriguez on 04/04/2018.
 */

public class CalculoEcuaciones {

    public boolean dificil;
    private Random aleatorio = new Random();

    public CalculoEcuaciones(boolean dificil) {
        this.dificil = dificil;
    }

    public Ecuacion generarEcuacion(){
        Ecuacion ecuacion = new Ecuacion();
        if(!dificil) {

            ecuacion.operador1 = aleatorio.nextInt(10);
            ecuacion.operador2 = aleatorio.nextInt(10);
            ecuacion.simbolo = "+";
            ecuacion.calcularResultado();
        }else{
            ecuacion.operador1 = aleatorio.nextInt(50)+50;
            ecuacion.operador2 = aleatorio.nextInt(3);
            ecuacion.simbolo = "*";
            ecuacion.calcularResultado();
        }
        return ecuacion;

    }


}
