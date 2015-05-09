package exemplo;

import java.rmi.server.*;
import java.rmi.registry.*;

public class HelloServer implements Hello {

    public String sayHello() {
        return "Hello, mateus!";
    }

    public static void main(String args[]) {
        try {
            HelloServer obj = new HelloServer();
            Hello objref = (Hello) UnicastRemoteObject.exportObject(obj, 2000);
            // Bind the remote object in the registry
            Registry registry = LocateRegistry.getRegistry(); // opcional: host
            registry.bind("Hello", objref);
            System.err.println("Server ready");
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
