/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author thiago
 */
public class GeraradorID {

    private static int idAtual = 0;

    public static int obterNumeroAtual() {
        idAtual++;
        return idAtual;
    }
}
