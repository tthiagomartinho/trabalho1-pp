
import java.rmi.RemoteException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

public interface Escravo extends Ordenar {

    public int getId() throws RemoteException;

    public void setId(int id) throws RemoteException;
    
    public void terminarEscravo() throws RemoteException;
}
