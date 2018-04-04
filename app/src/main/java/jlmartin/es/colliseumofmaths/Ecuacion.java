package jlmartin.es.colliseumofmaths;

/**
 * Created by jose.mrodriguez on 04/04/2018.
 */

public class Ecuacion {
    public Integer operador1;
    public Integer operador2;
    public String simbolo;
    public Integer resultado;

    public Integer calcularResultado(){
        Integer resultado = 0;
        switch (simbolo){
            case "+":
                resultado =  operador1+operador2;
                break;
            case "*":
                resultado = operador1*operador2;
                break;
        }
        this.resultado = resultado;
        return resultado;
    }

    @Override
    public String toString() {
        return "Este es tu desafio :"+operador1+simbolo+operador2;
    }
}
