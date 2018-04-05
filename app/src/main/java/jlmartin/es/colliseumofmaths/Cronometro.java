package jlmartin.es.colliseumofmaths;

/**
 * Created by Paris on 05/04/2018.
 */

public class Cronometro extends Thread {

    public int tiempo = 60;

    public Cronometro(int tiempo) {
        this.tiempo = tiempo;
    }

    public Cronometro() {
    }

    @Override
    public void run() {

        while(tiempo>0){
            try {
                this.sleep(1000);
                tiempo--;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
