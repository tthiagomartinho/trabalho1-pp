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

    /**
     * Método usado para registro de um novo escravo no mestre
     *
     * @param escravo - o escravo que deseja ser adicionado ao mestre
     * @throws java.rmi.RemoteException
     */
    @Override
    public void registraEscravo(Escravo escravo) throws RemoteException {
        System.out.println("Registrando Escravo");
        int id = GeraradorID.obterNumeroAtual();
        escravos.put(id, escravo);
        escravo.setId(id);
    }

    /**
     * Método usado para retirar um escravo do mestre
     *
     * @param idEscravo - id do escravo que deseja retirar
     * @throws java.rmi.RemoteException
     */
    @Override
    public void retirarEscravo(int idEscravo) throws RemoteException {
        if (escravos.containsKey(idEscravo)) {
            escravos.remove(idEscravo);
            System.out.println("Escravo " + idEscravo + " Removido. Ainda me restam " + escravos.size() + " escravos");
        }
    }
    
    /**
     * Método que recupera a quantidade de escravos associados ao mestre
     *
     * @return
     * @throws java.rmi.RemoteException
     */
    @Override
    public int getQuantidadeEscravos() throws RemoteException {
        return escravos.size();
    }

    /**
     * Método remoto que recebe a chamada do cliente para ordernar uma lista de
     * inteiros
     *
     * @param numeros - lista de inteiros que deseja-se ordernar
     * @return a lista de inteiros ordenada
     * @throws java.rmi.RemoteException
     */
    @Override
    public List<Integer> ordenarVetor(List<Integer> numeros) throws RemoteException {
        return ordenarVetor(numeros, false);
    }

    /**
     * Método remoto que recebe a chamada do cliente para calcular o overhead de
     * comunicação das chamadas remotas. A lista não é ordenada.
     *
     * @param numeros - lista de inteiros que deseja-se ordernar
     * @return a lista de inteiros passada como parâmetro
     * @throws java.rmi.RemoteException
     */
    @Override
    public List<Integer> calcularOverhead(List<Integer> numeros) throws RemoteException {
        return ordenarVetor(numeros, true);
    }

    /**
     * Método utilizado para ordenar uma lista de inteiros
     *
     * @param numeros - lista de inteiros que deseja-se ordernar
     * @param calcularOverhead - flag que indica se o método é pra de fato
     * ordenar a lista ou apenas calcular o overhead de comunicação
     * @return a lista de inteiros ordenada
     * @throws java.rmi.RemoteException
     */
    public List<Integer> ordenarVetor(List<Integer> numeros, boolean calcularOverhead) throws RemoteException {

        //Divide a tarefa e executa
        delegarTrabalho(numeros, calcularOverhead);

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
            for (ExecutarEscravo escravoExecutando : escravosExecutando) {
                if (!escravoExecutando.sucesso) {
                    escravosFaltosos.add(escravoExecutando);
                    numerosNaoOrdenados.addAll(escravoExecutando.listaNumeros);
                    retirarEscravo(escravoExecutando.idEscravo);
                }
            }

            if (!escravosFaltosos.isEmpty()) {
                for (ExecutarEscravo escravoExecutando : escravosFaltosos) {
                    escravosExecutando.remove(escravoExecutando);
                }
                delegarTrabalho(numerosNaoOrdenados, calcularOverhead);
            } else {
                existeEscravoFaltoso = false;
            }
        }

        List<Integer> numerosOrdenados = escravosExecutando.get(0).listaNumeros;
        if (!calcularOverhead) {
            /*Consolidam o resultado*/
            for (int i = 1; i < escravosExecutando.size(); i++) {
                List<Integer> aux = escravosExecutando.get(i).listaNumeros;
                numerosOrdenados = mergeLists(numerosOrdenados, aux);
            }
        }
        listaThreads.clear();
        escravosExecutando.clear();

        return numerosOrdenados;
    }

    /**
     * Método utilizado para dividir o trabalho de ordenar a lista para os n
     * escravos
     *
     * @param numeros - lista de inteiros que deseja-se ordernar
     * @param calcularOverhead - flag que indica se o método é pra de fato
     * ordenar a lista ou apenas calcular o overhead de comunicação
     * @throws java.rmi.RemoteException
     */
    public void delegarTrabalho(List<Integer> numeros, boolean calcularOverhead) throws RemoteException {

        int tamanhoLista = numeros.size();
        int tamanhoEscravos = escravos.size();
        int qtd = tamanhoLista / tamanhoEscravos;
        int rst = tamanhoLista % tamanhoEscravos;

        int indiceInicial = 0;
        int indiceFinal = qtd + rst;

        /*Inicia as threads chamando os escravos para executarem o sort*/
        for (Map.Entry<Integer, Escravo> entrySet : escravos.entrySet()) {
            Integer key = entrySet.getKey(); //pega o Id do escravo
            Escravo escravo = entrySet.getValue(); //pega o escravo
            List<Integer> subList = new ArrayList<>(numeros.subList(indiceInicial, indiceFinal));
            ExecutarEscravo executarEscravo = new ExecutarEscravo(key, escravo, subList, calcularOverhead);
            escravosExecutando.add(executarEscravo);
            Thread thread = new Thread(executarEscravo, key.toString()); //cira uma thread cujo nome é o ID do escravo
            listaThreads.add(thread);
            thread.start();
            indiceInicial = indiceFinal;
            indiceFinal += qtd;
        }
    }

    /**
     * Método utilizado para fazer o merge das listas ordenadas pelos escravos
     *
     * @param list1
     * @param list2
     * @return a lista de inteiros ordenada
     */
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
            String host = args.length > 1 ? args[1] : null;
            Registry registry = LocateRegistry.getRegistry(host);
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

    /**
     * Método utilizado para iniciar uma thread quando o mestre morrer e assim
     * poder desconectar os escravos
     */
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

    /**
     * Subclasse que é executada por uma thread para efetuar o paralelismo
     * durante a ordenação da lista
     */
    public class ExecutarEscravo implements Runnable {

        private final int idEscravo;
        private final Escravo escravo;
        private List<Integer> listaNumeros;
        private boolean sucesso;
        private boolean calcularOverhead;

        public ExecutarEscravo(int idEscravo, Escravo escravo, List<Integer> listaNumeros, boolean calcularOverhead) {
            this.idEscravo = idEscravo;
            this.escravo = escravo;
            this.listaNumeros = listaNumeros;
            this.sucesso = false;
            this.calcularOverhead = calcularOverhead;
        }

        @Override
        public void run() {
            try {
                if (calcularOverhead) {
                    listaNumeros = escravo.calcularOverhead(listaNumeros);
                } else {
                    listaNumeros = escravo.ordenarVetor(listaNumeros);
                }
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
