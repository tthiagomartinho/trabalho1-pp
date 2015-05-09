/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author thiago
 */
public class Principal {
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

       List<Integer> numeros = gerarNumerosAleatorios();
       
        Collections.sort(numeros);
    }
    
    public static List<Integer> gerarNumerosAleatorios() {
        List<Integer> numeros = new ArrayList<>();
        Random r = new Random();
        int n1; // tamanho dos vetores gerados

        n1 = r.nextInt(1000000);
        // generate a uniformly distributed int random numbers
        int[] integers = new int[n1];
        for (int i = 0; i < integers.length; i++) {
            numeros.add(r.nextInt());
        }

        return numeros;
    }
}
