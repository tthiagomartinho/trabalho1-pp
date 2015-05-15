import java.io.IOException;
import java.io.PrintWriter;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClienteTestes {

    public static void versaoParalela(String host, int nElementos, PrintWriter writer) throws RemoteException, NotBoundException {
        Registry registry = LocateRegistry.getRegistry(host);
        Mestre stub = (Mestre) registry.lookup("Mestre");

        List<Integer> numeros = ClienteTestes.gerarNumerosAleatorios(nElementos);
        double nanoTime = System.nanoTime();
        List<Integer> resultado = stub.ordenarVetor(numeros);
        double nanoTime2 = System.nanoTime();
        double nanoTime3 = (nanoTime2 - nanoTime) / 1000000000.0;
        writer.println(nElementos + " ; " + nanoTime3);
    }

    public static void versaoSequencial(int nElementos, PrintWriter writer) {
        List<Integer> numeros = ClienteTestes.gerarNumerosAleatorios(nElementos);
        long nanoTime = System.nanoTime();
        Collections.sort(numeros);
        double nanoTime2 = System.nanoTime();
        double nanoTime3 = (nanoTime2 - nanoTime) / 1000000000.0;
        writer.println(nElementos + " ; " + nanoTime3);
    }

    public static void main(String[] args) {
        try {
            int nElementos;
            
            
            
            
            if (args.length > 1) {
                PrintWriter writer = new PrintWriter("saida.csv", "UTF-8");
                writer.println("numero de elementos ; tempo");
                switch (args[0]) {
                    case "p":
                        String host = args.length > 2 ? args[2] : null;
                        
                        for (String line : Files.readAllLines(Paths.get(args[1]))) {
                            nElementos = Integer.valueOf(line);
                            versaoParalela(host, nElementos,writer);
                        }                        
                        
                        break;
                    case "s":
                        for (String line : Files.readAllLines(Paths.get(args[1]))) {
                            nElementos = Integer.valueOf(line);
                            versaoSequencial(nElementos,writer);
                        } 
                        break;
                }
                writer.close();
            } else {
                System.out.println("primeiro argumento: p(paralelo) ou s(sequencial) /n"
                        + "segundo argumento: o arquivo contento os tamanhos do vetores testados separados por quebra de linha /n"
                        + "terceiro argumento: host(p/ versao paralela e distribuida)");
            }
        } catch (IOException ex) {
            Logger.getLogger(ClienteTestes.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("informe um aquivo existente");
        } catch (NotBoundException ex) {
            Logger.getLogger(ClienteTestes.class.getName()).log(Level.SEVERE, null, ex);
            System.err.println("registry nao achado");
            
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
