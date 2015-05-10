/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author thiago
 */
public class Cliente {

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {
            Registry registry = LocateRegistry.getRegistry(host);
            Mestre stub = (Mestre) registry.lookup("Mestre");

            List<Integer> numeros = Cliente.gerarNumerosAleatorios();

            stub.ordenarVetor(numeros);
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
        for (int i = 0; i < integers.length; i++) {
            numeros.add(r.nextInt());
        }

        return numeros;
    }
}
