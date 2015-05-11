/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author thiago
 */
public class Cliente {

    public static void versaoParalela(String host) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(host);
        Mestre stub = (Mestre) registry.lookup("Mestre");

        List<Integer> numeros = Cliente.gerarNumerosAleatoriosParaTeste();
        long nanoTime = System.nanoTime();
        System.err.println("Eu sou o cliente. Quero esse vetor ordenado: " + numeros);
        numeros = stub.ordenarVetor(numeros);
        nanoTime = System.nanoTime() - nanoTime;
        System.err.println(nanoTime);
        System.out.println("Eu sou o cliente. Isso foi o que eu recebi: " + numeros);
    }

    public static void versaoSequencial() {
        List<Integer> numeros = Cliente.gerarNumerosAleatoriosParaTeste();
        System.out.println(numeros);
        long nanoTime = System.nanoTime();
        Collections.sort(numeros);
        long nanoTime2 = System.nanoTime();
//        nanoTime = System.nanoTime() - nanoTime;
        long nanoTime3 = (nanoTime2 - nanoTime)/1000000000;
//        System.out.println(numeros.size() + ", " + nanoTime.toString());
//        System.out.println((nanoTime2 - nanoTime)/Math.pow(10, -9));
        System.out.println(nanoTime3);
        System.out.println(numeros);
    }

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {
            versaoParalela(host);
//            versaoSequencial();
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public static List<Integer> gerarNumerosAleatorios() {
        List<Integer> numeros = new ArrayList<>();
        Random r = new Random();
        int n1; // tamanho dos vetores gerados

        n1 = r.nextInt(1000000);
        // generate a uniformly distributed int random numbers
        int[] integers = new int[n1];

        for (int i = 0; i < 1000000; i++) {
            numeros.add(r.nextInt());
        }

        return numeros;
    }
    
    public static List<Integer> gerarNumerosAleatoriosParaTeste() {
        List<Integer> numeros = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < 6; i++) {
            numeros.add(r.nextInt());
        }
        return numeros;
    }
}
