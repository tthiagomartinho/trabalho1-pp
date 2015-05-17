
import java.rmi.RemoteException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
public interface Escravo extends Ordenar {

    /**
     * Método utilizado para recuperar o ID do escravo registrado pelo mestre
     *
     * @return a lista de inteiros ordenada
     * @throws java.rmi.RemoteException
     */
    public int getId() throws RemoteException;

    /**
     * Método utilizado para atribuir ao escravo um ID
     *
     * @param id
     * @throws java.rmi.RemoteException
     */
    public void setId(int id) throws RemoteException;

    /**
     * Método utilizado para o mestre terminar o escravo quando necessário
     *
     * @throws java.rmi.RemoteException
     */
    public void terminarEscravo() throws RemoteException;
}
