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

    public static void versaoParalela(String host, String[] args) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(host);
        Mestre stub = (Mestre) registry.lookup("Mestre");

        List<Integer> numeros = Cliente.gerarNumerosAleatorios(args);
        double nanoTime = System.nanoTime();
        List<Integer> resultado = stub.ordenarVetor(numeros);
        double nanoTime2 = System.nanoTime();
        double nanoTime3 = (nanoTime2 - nanoTime) / 1000000000.0;
        System.out.println("Eu sou o cliente. O resultado demorou " + nanoTime3 + " segunos para ficar pronto");
        System.err.println("O array está ordenado? " + isCollectionSorted(resultado));
        System.err.println("O array de entrada e saida são do mesmo tamanho? " + (numeros.size() == resultado.size()));
    }

    public static boolean isCollectionSorted(List list) {
        List<Integer> copy = new ArrayList<>(list);
        Collections.sort(copy);
        return copy.equals(list);
    }

    public static void versaoSequencial(String[] args) {
        List<Integer> numeros = Cliente.gerarNumerosAleatorios(args);
        long nanoTime = System.nanoTime();
        Collections.sort(numeros);
        double nanoTime2 = System.nanoTime();
        double nanoTime3 = (nanoTime2 - nanoTime) / 1000000000.0;
        System.out.println("Eu sou o cliente. O resultado demorou " + nanoTime3 + " segunos para ficar pronto");
    }

    public static void main(String[] args) {
        try {
            if (args.length > 1) {
                switch (args[0]) {
                    case "p":
                        System.err.println(args.length);
                        String host = args.length > 2 ? args[2] : null;
                        versaoParalela(host, args);
                        break;
                    case "s":
                        versaoSequencial(args);
                        break;
                }
            } else {
                System.out.println("primeiro argumento: p(paralelo) ou s(sequencial), segundo argumento: tamanho do vetor, terceiro argumento: host");
            }
        } catch (RemoteException | NotBoundException e) {
            System.out.println("Ocorreu um erro durante o processamento dos dados. Não foi possível ordenar o vetor");
        }
    }

    public static List<Integer> gerarNumerosAleatorios(String[] args) {
        List<Integer> numeros = new ArrayList<>();
        Random r = new Random();
        int n1 = Integer.parseInt(args[1]); // tamanho dos vetores gerados

        if (!(n1 > 0 && n1 <= 10000000)) {
            n1 = r.nextInt(10000000); // caso o tamanho passado náo seja valido e calculado um tamanho aleatorio
        }

        // generate a uniformly distributed int random numbers
        for (int i = 0; i < n1; i++) {
            numeros.add(r.nextInt(10000000));
        }
        return numeros;
    }

    public static List<Integer> gerarNumerosAleatoriosParaTeste(String[] args) {
        List<Integer> numeros = new ArrayList<>();
        Random r = new Random();
        for (int i = 0; i < 6; i++) {
            numeros.add(r.nextInt());
        }
        return numeros;
    }
}
