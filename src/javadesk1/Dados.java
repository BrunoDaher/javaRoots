/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadesk1;

import java.io.Serializable;
import java.security.*;
import java.util.Date;

/**
 *
 * @author BrunoDaher_UTFPR
 */
public class Dados implements Serializable {

    public String nome;
    public String acao;
    public String msg;
    public byte[] msgAssinada;
    public PublicKey chavePublica;
    public String destino;
    public String Recurso = "RELEASED";
    public long relogio;
    public int prioridade;

}
