/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thiago
 */
public class EscravoImpl implements Escravo {

    private int id;
    private Mestre mestre;

    public EscravoImpl() {
    }

    @Override
    public List<Integer> ordenarVetor(List<Integer> numeros) throws RemoteException {
        Collections.sort(numeros);
        return numeros;
    }

    @Override
    public void terminarEscravo() {
        System.out.println("Terminando o Escravo");
        System.exit(0);
    }

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        try {
            EscravoImpl escravo = new EscravoImpl();
            Escravo objref = (Escravo) UnicastRemoteObject.exportObject(escravo, 0);
            System.out.println(host);
            Registry registry = LocateRegistry.getRegistry(host);
            escravo.mestre = (Mestre) registry.lookup("Mestre");
            escravo.mestre.registraEscravo((Escravo) objref);
            escravo.attachShutDownHook();
        } catch (RemoteException | NotBoundException e) {
            System.out.println("Não foi possível registrar o escravo.");
        }
    }

    public void attachShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    mestre.retirarEscravo(id);
                    System.out.println("EXTERMINATING ESCRAVO");
                } catch (RemoteException ex) {
                    Logger.getLogger(EscravoImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
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
