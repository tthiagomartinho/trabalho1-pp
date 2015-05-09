/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho1;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author thiago
 */
public class Escravo implements Ordenar, Serializable {

    public Escravo() {
    }

    @Override
    public List<Integer> ordenarVetor(List<Integer> numeros) throws RemoteException {
        System.out.println("Ordenando Vetor");
        Collections.sort(numeros);
        return numeros;
    }

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {
            Escravo escravo = new Escravo();
            Registry registry = LocateRegistry.getRegistry(host);
            Mestre stub = (Mestre) registry.lookup("Mestre");
            stub.registraEscravo(escravo);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

}
