/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.rmi.RemoteException;
import java.util.List;

/**
 *
 * @author thiago
 */
public interface Ordenar extends java.rmi.Remote {

    /**
     * Método remoto que recebe a chamada do cliente para ordernar uma lista de
     * inteiros
     *
     * @param numeros - lista de inteiros que deseja-se ordernar
     * @return a lista de inteiros ordenada
     * @throws java.rmi.RemoteException
     */
    public List<Integer> ordenarVetor(List<Integer> numeros) throws RemoteException;

    /**
     * Método remoto que recebe a chamada do cliente para calcular o overhead de
     * comunicação das chamadas remotas. A lista não é ordenada.
     *
     * @param numeros - lista de inteiros que deseja-se ordernar
     * @return a lista de inteiros passada como parâmetro
     * @throws java.rmi.RemoteException
     */
    public List<Integer> calcularOverhead(List<Integer> numeros) throws RemoteException;

}
