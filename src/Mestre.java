/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.rmi.RemoteException;

/**
 *
 * @author thiago
 */
public interface Mestre extends java.rmi.Remote, Ordenar {

    /**
     * Método usado para registro de um novo escravo no mestre
     *
     * @param escravo - o escravo que deseja ser adicionado ao mestre
     * @throws java.rmi.RemoteException
     */
    public void registraEscravo(Escravo escravo) throws RemoteException;

    /**
     * Método usado para retirar um escravo do mestre
     *
     * @param idEscravo - id do escravo que deseja retirar
     * @throws java.rmi.RemoteException
     */
    public void retirarEscravo(int idEscravo) throws RemoteException;

    /**
     * Método que recupera a quantidade de escravos associados ao mestre
     *
     * @return
     * @throws java.rmi.RemoteException
     */
    public int getQuantidadeEscravos() throws RemoteException;
}
