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

    public static void versaoParalela(String host,String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(host);
        Mestre stub = (Mestre) registry.lookup("Mestre");

        List<Integer> numeros = Cliente.gerarNumerosAleatorios(args);
//        System.err.println("Eu sou o cliente. Quero esse vetor ordenado: " + numeros);
        double nanoTime = System.nanoTime();
        numeros = stub.ordenarVetor(numeros);
        double nanoTime2 = System.nanoTime();
        double nanoTime3 = (nanoTime2 - nanoTime)/1000000000.0;
//        System.out.println("Eu sou o cliente. Isso foi o que eu recebi: " + numeros);
        System.out.println("Eu sou o cliente. O resultado demorou " + nanoTime3 + " segunos para ficar pronto");
    }

    public static void versaoSequencial(String[] args) {
        List<Integer> numeros = Cliente.gerarNumerosAleatorios(args);
//        System.out.println(numeros);
        long nanoTime = System.nanoTime();
        Collections.sort(numeros);
        double nanoTime2 = System.nanoTime();
        double nanoTime3 = (nanoTime2 - nanoTime)/1000000000.0;
        System.out.println("Eu sou o cliente. O resultado demorou " + nanoTime3 + " segunos para ficar pronto");
    }

    public static void main(String[] args) {
        //String host = (args.length < 1) ? null : args[0];
        try {
            if(args.length==3){
                if(args[0].equals("p")){
                    versaoParalela(args[2],args);
                    //versaoParalela(host);
                }else if(args[0].equals("s")){
                    versaoSequencial(args);
                }
                //System.out.println("host: "+host);
            }else{
                System.out.println("primeiro argumento: p(paralelo) ou s(sequencial), segundo argumento: tamanho do vetor, terceiro argumento: host");
            }
                
//            versaoSequencial();
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public static List<Integer> gerarNumerosAleatorios(String[] args) {
        List<Integer> numeros = new ArrayList<>();
        Random r = new Random();
        int n1 = Integer.parseInt(args[1]); // tamanho dos vetores gerados
        
        if(!(n1>0 && n1<=10000000)){
            n1 = r.nextInt(10000000); // caso o tamanho passado náo seja valido e calculado um tamanho aleatorio
        }
        
        
        // generate a uniformly distributed int random numbers

        for (int i = 0; i < n1; i++) {
            numeros.add(r.nextInt(10000000));
        }
        return numeros;
    }
    
    public static List<Integer> gerarNumerosAleatoriosParaTeste(String[]args) {
        List<Integer> numeros = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < 6; i++) {
            numeros.add(r.nextInt());
        }
        return numeros;
    }
}
