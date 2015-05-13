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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author thiago
 */
public class MestreImpl implements Mestre {

    private ConcurrentMap<Integer, Escravo> escravos;
    private List<Thread> listaThreads;
    private List<ExecutarEscravo> escravosExecutando;

    public MestreImpl() {
        escravos = new ConcurrentHashMap<>();
        listaThreads = new ArrayList<>();
        escravosExecutando = new ArrayList<>();
    }

    @Override
    public void registraEscravo(Escravo escravo) throws RemoteException {
        System.out.println("Registrando Escravo");
        int id = GeraradorID.obterNumeroAtual();
        escravos.put(id, escravo);
        escravo.setId(id);
    }

    @Override
    public void retirarEscravo(int idEscravo) throws RemoteException {
        if (escravos.containsKey(idEscravo)) {
            escravos.remove(idEscravo);
            System.out.println("Escravo Removido. Ainda me restam " + escravos.size() + " escravos");
        }
    }

    @Override
    public List<Integer> ordenarVetor(List<Integer> numeros) throws RemoteException {

        //Divide a tarefa e executa
        delegarTrabalho(numeros);

        boolean existeEscravoFaltoso = true;
        while (existeEscravoFaltoso) { //enquanto existirem escravos que falharam

            /*Espera todas as threads morrerem*/
            for (Thread thread : listaThreads) {
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(MestreImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            listaThreads.clear();

            List<ExecutarEscravo> escravosFaltosos = new ArrayList<>();
            List<Integer> numerosNaoOrdenados = new ArrayList<>();
            System.err.println("Indo verificar se existem escravos faltosos");
            for (ExecutarEscravo escravoExecutando : escravosExecutando) {
                if (!escravoExecutando.sucesso) {
                    escravosFaltosos.add(escravoExecutando);
                    numerosNaoOrdenados.addAll(escravoExecutando.listaNumeros);
                    retirarEscravo(escravoExecutando.idEscravo);
                }
            }

            System.err.println("Existem " + escravosFaltosos.size() + " escravos faltosos");
            if (!escravosFaltosos.isEmpty()) {
                for (ExecutarEscravo escravoExecutando : escravosFaltosos) {
                    escravosExecutando.remove(escravoExecutando);
                }
                System.out.print("Escravo Faltoso. Vou delegar o trabalho novamente");
                delegarTrabalho(numerosNaoOrdenados);
            } else {
                existeEscravoFaltoso = false;
            }
        }

        /*Consolidam o resultado*/
        List<Integer> numerosOrdenados = escravosExecutando.get(0).listaNumeros;
        for (int i = 1; i < escravosExecutando.size(); i++) {
            List<Integer> list1aux2 = escravosExecutando.get(i).listaNumeros;
            numerosOrdenados = mergeLists(numerosOrdenados, list1aux2);

        }
        listaThreads.clear();
        escravosExecutando.clear();

        return numerosOrdenados;
    }

    public void delegarTrabalho(List<Integer> numeros) throws RemoteException {

        int tamanhoLista = numeros.size();
        int tamanhoEscravos = escravos.size();
        int qtd = tamanhoLista / tamanhoEscravos;
        int rst = tamanhoLista % tamanhoEscravos;

        int indiceInicial = 0;
        int indiceFinal = qtd + rst;
        System.err.println("Existem " + tamanhoEscravos + " escravos nesse momento");

        /*Inicia as threads chamando os escravos para executarem o sort*/
        for (Map.Entry<Integer, Escravo> entrySet : escravos.entrySet()) {
            Integer key = entrySet.getKey(); //pega o Id do escravo
            Escravo escravo = entrySet.getValue(); //pega o escravo
            List<Integer> subList = new ArrayList<>(numeros.subList(indiceInicial, indiceFinal));
            ExecutarEscravo executarEscravo = new ExecutarEscravo(key, escravo, subList);
            escravosExecutando.add(executarEscravo);
            Thread thread = new Thread(executarEscravo, key.toString()); //cira uma thread cujo nome é o ID do escravo
            listaThreads.add(thread);
            thread.start();
            indiceInicial = indiceFinal;
            indiceFinal += qtd;
        }
    }

    public List<Integer> mergeLists(List<Integer> list1, List<Integer> list2) {
        List<Integer> out = new ArrayList<>();
        Iterator<Integer> i1 = list1.iterator();
        Iterator<Integer> i2 = list2.iterator();
        Integer e1 = i1.hasNext() ? i1.next() : null;
        Integer e2 = i2.hasNext() ? i2.next() : null;
        while (e1 != null || e2 != null) {
            if (e2 == null || e1 != null && e1 < e2) {
                out.add(e1);
                e1 = i1.hasNext() ? i1.next() : null;
            } else {
                out.add(e2);
                e2 = i2.hasNext() ? i2.next() : null;
            }
        }
        return out;
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
            obj.attachShutDownHook();
            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    public void attachShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                for (Map.Entry<Integer, Escravo> entrySet : escravos.entrySet()) {
                    Integer key = entrySet.getKey();
                    Escravo escravo = entrySet.getValue();
                    try {
                        retirarEscravo(key);
                        escravo.terminarEscravo();
                    } catch (RemoteException ex) {
                        Logger.getLogger(MestreImpl.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                System.out.println("Terminando o Mestre");
            }
        });
    }

    public class ExecutarEscravo implements Runnable {

        private final int idEscravo;
        private final Escravo escravo;
        private List<Integer> listaNumeros;
        private boolean sucesso;

        public ExecutarEscravo(int idEscravo, Escravo escravo, List<Integer> listaNumeros) {
            this.idEscravo = idEscravo;
            this.escravo = escravo;
            this.listaNumeros = listaNumeros;
            sucesso = false;
        }

        @Override
        public void run() {
            try {
                listaNumeros = escravo.ordenarVetor(listaNumeros);
                sucesso = true;
            } catch (RemoteException ex) {
                try {
                    retirarEscravo(idEscravo);
                } catch (RemoteException ex1) {
                }
            }
        }
    }
}