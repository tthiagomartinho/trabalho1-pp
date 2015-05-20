
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Cliente {

    /**
     * Método usado para ordenar a lista de inteiros de forma paralela
     *
     * @param stub - a interface remota do mestre
     * @param writer - buffer pra escrita em um arquivo de saída
     * @param numeros - lista
     * @throws java.rmi.RemoteException
     * @throws java.rmi.NotBoundException
     */
    public static void versaoParalela(Mestre stub, PrintWriter writer, List<Integer> numeros) throws RemoteException, NotBoundException {
        double nanoTime = System.nanoTime();
        List<Integer> resultado = stub.ordenarVetor(numeros);
        double nanoTime2 = System.nanoTime();
        double nanoTime3 = (nanoTime2 - nanoTime) / 1000000000.0;
        if (isListaOrdenada(resultado)) {
            writer.print(nanoTime3 + ";");
        } else {
            System.out.println("Erro: A lista não está ordenada.");
        }
    }

    /**
     * Método usado para ordenar a lista de inteiros de forma sequencial
     *
     * @param writer - buffer pra escrita em um arquivo de saída
     * @param numeros - lista
     */
    public static void versaoSequencial(PrintWriter writer, List<Integer> numeros) {
        long nanoTime = System.nanoTime();
        Collections.sort(numeros);
        double nanoTime2 = System.nanoTime();
        double nanoTime3 = (nanoTime2 - nanoTime) / 1000000000.0;
        writer.print(nanoTime3 + ";");
    }

    /**
     * Método usado para ordenar a lista de inteiros de forma sequencial
     *
     * @param stub
     * @param writer - buffer pra escrita em um arquivo de saída
     * @param numeros - lista
     * @throws java.rmi.RemoteException
     * @throws java.rmi.NotBoundException
     */
    public static void calcularOverhead(Mestre stub, PrintWriter writer, List<Integer> numeros) throws RemoteException, NotBoundException {
        double nanoTime = System.nanoTime();
        List<Integer> resultado = stub.calcularOverhead(numeros);
        double nanoTime2 = System.nanoTime();
        double nanoTime3 = (nanoTime2 - nanoTime) / 1000000000.0;
        writer.print(nanoTime3);
    }

    /**
     * Método usado para verificar se uma lista está ordenada
     *
     * @param numeros - lista de números
     * @return verdadeiro se a lista está ordenada. Falso caso contrário
     */
    public static boolean isListaOrdenada(List<Integer> numeros) {
        for (int i = 0; i < numeros.size() - 1; i++) {
            if (numeros.get(i) > numeros.get(i + 1)) {
                return false;
            }
        }
        return true;
    }

    public static void main(String[] args) {
        try {
            int nElementos;
            String nomeArquivoEntrada = args.length > 0 ? args[0] : null;
            String host = args.length > 1 ? args[1] : null;
            Registry registry = LocateRegistry.getRegistry(host);
            Mestre stub = (Mestre) registry.lookup("Mestre");

            PrintWriter writer = new PrintWriter("saidas/" + stub.getQuantidadeEscravos() + "_" + nomeArquivoEntrada + ".csv", "UTF-8");
            writer.println("numero de elementos ; tempo sequencial ; tempo paralelo ; tempo overhead");
            for (String line : Files.readAllLines(Paths.get(nomeArquivoEntrada), Charset.defaultCharset())) {
                nElementos = Integer.valueOf(line);
                List<Integer> numeros = Cliente.gerarNumerosAleatorios(nElementos);
                writer.print(nElementos + ";");
                versaoSequencial(writer, new ArrayList<>(numeros));
                versaoParalela(stub, writer, new ArrayList<>(numeros));
                calcularOverhead(stub, writer, new ArrayList<>(numeros));
                writer.println();
            }
            writer.close();
        } catch (NotBoundException ex) {
            System.err.println("Não foi possível localizar o servidor remoto");
            ex.printStackTrace();
        } catch (IOException ex) {
            System.err.println("Um erro inesperado ocorreu");
            ex.printStackTrace();
        } catch (NumberFormatException ex) {
            System.err.println("Erro ao ler dados do arquivo");
            ex.printStackTrace();
        }
    }

    /**
     * Método usado para gerar uma lista de números inteiros de forma aleatória
     *
     * @param nElementos - o tamanho da lista
     * @return a lista de inteiros
     */
    public static List<Integer> gerarNumerosAleatorios(int nElementos) {
        List<Integer> numeros = new ArrayList<>();
        Random r = new Random();

        if (!(nElementos > 0 && nElementos <= 10000000)) {
            nElementos = r.nextInt(10000000); // caso o tamanho passado nao seja valido e calculado um tamanho aleatorio
        }

        for (int i = 0; i < nElementos; i++) {
            numeros.add(r.nextInt(10000000));
        }
        return numeros;
    }
}
