/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javadesk1;

import java.io.*;
import java.net.*;
import java.util.Date;

public class multicast {

    private int porta;
    private InetAddress grupo;
    private MulticastSocket multiSock;

    public multicast(int p, String endereco) {
        porta = p;
        try {
            grupo = InetAddress.getByName(endereco);
            multiSock = new MulticastSocket(porta);
            multiSock.joinGroup(grupo);
        } catch (UnknownHostException ex) {
            System.out.println("erroConexão");
        } catch (IOException ex) {
            System.out.println("erroMulticast");
        }
    }

    public void logout() {
        try {
            multiSock.leaveGroup(grupo);
        } catch (IOException ex) {
            System.out.println("erroSair do grupo");
        }

        multiSock.close();
    }

    public Dados receiveMsg() {

        Dados d = new Dados();
        d = null;
        String retorno = "";

        try {
            byte[] buffer = new byte[5000];
            DatagramPacket msgIn = new DatagramPacket(buffer, buffer.length);
            //recebe no so
            multiSock.receive(msgIn);
            retorno = new String(msgIn.getData());
            d = Deserializa(buffer);

        } catch (IOException ex) {
            System.out.println("erroRecebendoDatagram");
        }

        return d;
    }

    public void sendMsg(Dados dados) {
        try {
            //att horario no envio
            dados.relogio = new Date().getTime();

            byte[] m = Serializa(dados);
            DatagramPacket msgOut = new DatagramPacket(m, m.length, grupo, porta);
            multiSock.send(msgOut);
        } catch (IOException ex) {
            System.out.println("erroEnviandoDatagram");
        }
    }

    public void sendMsgTTL(Dados dados, int tempo) {
        try {
            byte[] m = Serializa(dados);
            DatagramPacket msgOut = new DatagramPacket(m, m.length, grupo, porta);
            multiSock.send(msgOut);

            multiSock.setSoTimeout(tempo);

            boolean continueSending = true;
            int counter = 0;
            while (continueSending && counter < 10) {
                // send to server omitted
                counter++;
                System.out.println(receiveMsg().acao); // no response received after 1 second. continue sending
                continueSending = false; // a packet has been received : stop sending
            }

        } catch (IOException ex) {
            System.out.println("erroEnviandoDatagram");
        }
    }

    public byte[] Serializa(Dados obj) {

        //System.out.println(this.getClass() + " " + texto);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try (ObjectOutputStream outputStream = new ObjectOutputStream(out)) {
            outputStream.writeObject(obj);
        } catch (IOException ex) {
            System.out.println("erroSerializando");
        }

        //conversão em bytes
        return out.toByteArray();

    }

    public Dados Deserializa(byte[] buffer) {

        //System.out.println(this.getClass() + " " + texto);
        Dados d = new Dados();

        try {

            ByteArrayInputStream byteStream = new ByteArrayInputStream(buffer);
            ObjectInputStream is = new ObjectInputStream(new BufferedInputStream(byteStream));
            //l = (ArrayList) is.readObject();
            d = (Dados) is.readObject();

            //System.out.println(l);
        } catch (ClassNotFoundException ex) {
            System.out.println("erro de leitura de objeto");
        } catch (IOException ex) {
            System.out.println("erro Cast Objeto");
        }
        return d;
    }
}
