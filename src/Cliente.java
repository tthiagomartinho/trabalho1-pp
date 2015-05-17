
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

    public static void versaoParalela(Mestre stub, int nElementos, PrintWriter writer, List<Integer> numeros) throws RemoteException, NotBoundException {
        double nanoTime = System.nanoTime();
        List<Integer> resultado = stub.ordenarVetor(numeros);
        double nanoTime2 = System.nanoTime();
        double nanoTime3 = (nanoTime2 - nanoTime) / 1000000000.0;
        writer.print(nanoTime3 + ";");
    }

    public static void versaoSequencial(int nElementos, PrintWriter writer, List<Integer> numeros) {
        long nanoTime = System.nanoTime();
        Collections.sort(numeros);
        double nanoTime2 = System.nanoTime();
        double nanoTime3 = (nanoTime2 - nanoTime) / 1000000000.0;
        writer.print(nanoTime3 + ";");
    }

    public static void calcularOverhead(Mestre stub, int nElementos, PrintWriter writer, List<Integer> numeros) throws RemoteException, NotBoundException {
        double nanoTime = System.nanoTime();
        List<Integer> resultado = stub.calcularOverhead(numeros);
        double nanoTime2 = System.nanoTime();
        double nanoTime3 = (nanoTime2 - nanoTime) / 1000000000.0;
        writer.print(nanoTime3);
    }

    public static void main(String[] args) {
        try {
            int nElementos;
            String nomeArquivoEntrada = args.length > 0 ? args[0] : null;
            String host = args.length > 1 ? args[1] : null;
            Registry registry = LocateRegistry.getRegistry(host);
            Mestre stub = (Mestre) registry.lookup("Mestre");

            PrintWriter writer = new PrintWriter(stub.getQuantidadeEscravos() + "_" + nomeArquivoEntrada + ".csv", "UTF-8");
            writer.println("numero de elementos ; tempo sequencial ; tempo paralelo ; tempo overhead");
            for (String line : Files.readAllLines(Paths.get(nomeArquivoEntrada), Charset.defaultCharset())) {
                nElementos = Integer.valueOf(line);
                List<Integer> numeros = Cliente.gerarNumerosAleatorios(nElementos);
                writer.print(nElementos + ";");
                versaoSequencial(nElementos, writer, new ArrayList<>(numeros));
                versaoParalela(stub, nElementos, writer, new ArrayList<>(numeros));
                calcularOverhead(stub, nElementos, writer, new ArrayList<>(numeros));
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

    public static List<Integer> gerarNumerosAleatorios(int nElementos) {
        List<Integer> numeros = new ArrayList<>();
        Random r = new Random();

        if (!(nElementos > 0 && nElementos <= 10000000)) {
            nElementos = r.nextInt(10000000); // caso o tamanho passado nao seja valido e calculado um tamanho aleatorio
        }
        //  System.out.println("sadsdasad\n");

        // generate a uniformly distributed int random numbers
        for (int i = 0; i < nElementos; i++) {
            numeros.add(r.nextInt(10000000));
        }
        return numeros;
    }
}
