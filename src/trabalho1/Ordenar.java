/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabalho1;

import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author thiago
 */
public interface Ordenar extends java.rmi.Remote {

    public List<Integer> ordenarVetor(List<Integer> numeros) throws RemoteException;
}
