/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author thiago
 */
public class EscravoImpl implements Escravo {
    
    private int id;
    
    public EscravoImpl() {
    }

    @Override
    public List<Integer> ordenarVetor(List<Integer> numeros) throws RemoteException {
        System.out.println("Ordenando Vetor " + numeros);
        Collections.sort(numeros);
        System.out.println("A minha parte ordenada é " + numeros);
        return numeros;
    }

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {
            EscravoImpl escravo = new EscravoImpl();
            Escravo objref = (Escravo) UnicastRemoteObject.exportObject(escravo, 0);
            System.out.println(host);
            Registry registry = LocateRegistry.getRegistry(host);
            Mestre stub = (Mestre) registry.lookup("Mestre");
            stub.registraEscravo((Escravo) objref);
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }
}
