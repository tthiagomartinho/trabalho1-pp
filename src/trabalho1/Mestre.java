/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho1;

import java.rmi.RemoteException;

/**
 *
 * @author thiago
 */
public interface Mestre extends java.rmi.Remote, Ordenar {

    public void registraEscravo(Escravo escravo) throws RemoteException;

    public void retirarEscravo(Escravo escravo) throws RemoteException;
}
