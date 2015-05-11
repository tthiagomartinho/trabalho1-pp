/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.Serializable;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thiago
 */
public class MestreImpl implements Mestre {

    List<Escravo> escravos;

    public MestreImpl() {
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
        int tamanhoLista = numeros.size();
        int tamanhoEscravos = escravos.size();
        int qtd = tamanhoLista / tamanhoEscravos;
        int rst = tamanhoLista % tamanhoEscravos;

        List<Thread> listaThreads = new ArrayList<>();
        List<ExecutarEscravo> escravosExecutando = new ArrayList<>();
        int indiceInicial = 0;
        int indiceFinal = qtd;

        /*Inicia as threads chamando os escravos para executarem o sort*/
        for (int i = 0; i < tamanhoEscravos; i++) {
            List<Integer> subList = new ArrayList<>(numeros.subList(indiceInicial, indiceFinal));
            ExecutarEscravo executarEscravo = new ExecutarEscravo(escravos.get(i), subList);
            escravosExecutando.add(executarEscravo);
            Thread thread = new Thread(executarEscravo);
            System.err.println("Executar Thread");
            listaThreads.add(thread);
            thread.start();
            indiceInicial = indiceFinal;
            indiceFinal += qtd;
        }

        /*Espera todas as threads morrerem*/
        for (Thread thread : listaThreads) {
            try {
                thread.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(MestreImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        /*Consolidam o resultado*/
        List<Integer> numerosOrdenados = new ArrayList<>();
        for (ExecutarEscravo ee : escravosExecutando) {
            numerosOrdenados.addAll(ee.listaNumeros);
        }

        /*Ordena o vetor consolidado*/
        System.out.println("Eu sou o Mestre. Os escravos acabaram. O resultado é :" + numerosOrdenados);
        Collections.sort(numerosOrdenados);
        System.out.println("Eu sou o Mestre e a minha parte ordenada é :" + numerosOrdenados);

        return numerosOrdenados;
    }

    public static void main(String args[]) {
        try {
            Registry registry = LocateRegistry.getRegistry(); // opcional: host
            try {
                Mestre stub = (Mestre) registry.lookup("Mestre");
                if (stub != null) {
                    registry.unbind("Mestre");
                }
            } catch (RemoteException | NotBoundException ex) {
            }

            MestreImpl obj = new MestreImpl();
            Mestre objref = (Mestre) UnicastRemoteObject.exportObject(obj, 0);
            // Bind the remote object in the registry

            registry.bind("Mestre", objref);
            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public class ExecutarEscravo implements Runnable, Remote, Serializable {

        private final Escravo escravo;
        private List<Integer> listaNumeros;

        public ExecutarEscravo(Escravo escravo, List<Integer> listaNumeros) {
            this.escravo = escravo;
            this.listaNumeros = listaNumeros;
        }

        @Override
        public void run() {
            try {
                System.err.println("Indo Ordernar o Vetor no Escravo");
                listaNumeros = escravo.ordenarVetor(listaNumeros);
                System.err.println("Terminei de Ordenar o Vetor no Escravo");
            } catch (RemoteException ex) {
                Logger.getLogger(MestreImpl.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
