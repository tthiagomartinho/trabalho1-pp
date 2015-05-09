/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho1;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author thiago
 */
public class MestreServer implements Mestre {

    List<Escravo> escravos;

    public MestreServer() {
        escravos = new ArrayList<>();
    }

    @Override
    public void registraEscravo(Escravo escravo) throws RemoteException {
        System.out.println("Registrando Escravo");
        escravos.add(escravo);
    }

    @Override
    public void retirarEscravo(Escravo escravo) throws RemoteException {
        escravos.remove(escravo);
    }

    @Override
    public List<Integer> ordenarVetor(List<Integer> numeros) throws RemoteException {
        escravos.get(0).ordenarVetor(numeros);
        return numeros;
    }

    public static void main(String args[]) {
        try {
            MestreServer obj = new MestreServer();
            Mestre objref = (Mestre) UnicastRemoteObject.exportObject(obj, 2000);
            // Bind the remote object in the registry
            Registry registry = LocateRegistry.getRegistry(); // opcional: host

            registry.bind("Mestre", (Remote) objref);
            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
