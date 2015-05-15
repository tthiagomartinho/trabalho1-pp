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

    public static void versaoParalela(String host, int nElementos) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(host);
        Mestre stub = (Mestre) registry.lookup("Mestre");

        List<Integer> numeros = Cliente.gerarNumerosAleatorios(nElementos);
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

    public static void versaoSequencial(int nElementos) {
        List<Integer> numeros = Cliente.gerarNumerosAleatorios(nElementos);
        long nanoTime = System.nanoTime();
        Collections.sort(numeros);
        double nanoTime2 = System.nanoTime();
        double nanoTime3 = (nanoTime2 - nanoTime) / 1000000000.0;
        System.out.println("Eu sou o cliente. O resultado demorou " + nanoTime3 + " segunos para ficar pronto");
    }

    public static void main(String[] args) {
        try {
            int nElementos;
            if (args.length > 1) {
                switch (args[0]) {
                    case "p":
                        System.err.println(args.length);
                        String host = args.length > 2 ? args[2] : null;
                        nElementos = Integer.parseInt(args[1]);
                        versaoParalela(host, nElementos);
                        break;
                    case "s":
                        nElementos = Integer.parseInt(args[1]);
                        versaoSequencial(nElementos);
                        break;
                }
            } else {
                System.out.println("primeiro argumento: p(paralelo) ou s(sequencial) /n"
                        + "segundo argumento: tamanho do vetor /n"
                        + "terceiro argumento: host(p/ versao paralela e distribuida)");
            }
        } catch (RemoteException | NotBoundException e) {
            System.out.println("Ocorreu um erro durante o processamento dos dados. Não foi possível ordenar o vetor");
        }
    }

    public static List<Integer> gerarNumerosAleatorios(int nElementos) {
        List<Integer> numeros = new ArrayList<>();
        Random r = new Random();

        if (!(nElementos > 0 && nElementos <= 10000000)) {
            nElementos = r.nextInt(10000000); // caso o tamanho passado náo seja valido e calculado um tamanho aleatorio
        }

        // generate a uniformly distributed int random numbers
        for (int i = 0; i < nElementos; i++) {
            numeros.add(r.nextInt(10000000));
        }
        return numeros;
    }
}
