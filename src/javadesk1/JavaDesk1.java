/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

 /*
@author BrunoDaher_UTFPR

 */
package javadesk1;

import java.awt.*;
import java.awt.event.*;
import java.security.PublicKey;
import java.util.*;
import java.util.stream.IntStream;

public class JavaDesk1 {

    private final multicast multi;
    private final HashMap<PublicKey, Dados> users;
    private final Seguranca seg;
    private final Dados dados;
    private final Tela janela;
    private Thread recmsg;

    public static void main(String[] args) {
        ///JavaDesk1 cli = new JavaDesk1(0);

        for (int i = 0; i < 3; i++) {
            //new JavaDesk1(3);
            new JavaDesk1(i);
        }

    }

    public JavaDesk1(int n) {

        //seguran
        seg = new Seguranca();

        //multicast
        multi = new multicast(6789, "228.0.0.1");

        //pacote q será enviado (serializado)
        dados = new Dados(); //nome
        //int rand = new Random().nextInt(4);
        //rand = n;

        Color[] color = {Color.YELLOW, Color.WHITE, Color.GRAY, Color.RED};
        dados.nome = "th" + n;

        //interface TELA
        janela = new Tela(color[n], dados.nome);
        acoes();

        //usuarios
        users = new HashMap<>();

        //inicia escuta
        escutaMulti();

        //protocolo (pacote de dados a transmir)
        setDados(dados.nome, "log", "ola", seg.assina("ola"), seg.getPubKey(), "Todos");

        //grava na lista
        users.put(dados.chavePublica, dados);

        //inicia
        multi.sendMsg(dados);
        janela.attLista(dados.nome);
        janela.UsaRec.setText(dados.Recurso);

    }

    public void attJanUser() {
        String list = "";
        for (Object object : users.values()) {
            list += ((Dados) object).nome + "\n";
        }
        janela.attLista(list);
    }

    public void setDados(String n, String a, String m, byte[] sig, PublicKey ch, String destino) {
        dados.nome = n; //nome
        dados.acao = a; //acao
        dados.msg = m; //mensgagem
        dados.msgAssinada = sig; // mensagem assinada
        dados.chavePublica = ch; //chavePublica
        dados.destino = destino;
    }

    public void fluxo(Dados pacote) {
        //se alguem logar
        //reply só pra fonte
        if ("log".equals(pacote.acao) && !pacote.nome.equals(dados.nome)) {
            //att Usuarios
            users.put(pacote.chavePublica, pacote);
            //seta destino <- remetente
            dados.destino = pacote.nome;
            //altera assunto
            dados.acao = "replyLog";
            //reply
            multi.sendMsg(dados);
        }

        //"CAIXA DE ENTRADA"//
        if (pacote.destino.equals(dados.nome)) {

            if ("replyLog".equals(pacote.acao)) {
                users.put(pacote.chavePublica, pacote);
            }

            if ("sair".equals(pacote.acao)) {
                users.remove(pacote.chavePublica);
            }

            if ("Recurso".equals(pacote.acao)) {
                //altera flag de destinatario
                dados.destino = pacote.nome;
                //altera assunto
                dados.acao = "replyRecurso";
                //AVALIA ENTRADA NA S.CRITICA
                //se este cliente esta usando recurso
                if (dados.Recurso.equals("HELD")) {
                    if (pacote.Recurso.equals("WANTED") && dados.relogio < pacote.relogio) {
                        //enfilera pedido
                        pacote.prioridade = 1;
                        users.replace(pacote.chavePublica, pacote);
                        //se o meu status é held
                    }
                }
                //REPLY
                multi.sendMsg(dados);
            }

            //quando recebe reply
            String text = "";

            //coleta msgs direcionadas
            if ("replyRecurso".equals(pacote.acao)) {

                String tex = "";
                //confere assinatura do recurso solicitado
                if (seg.confere(pacote.chavePublica, pacote.msg, pacote.msgAssinada)) {
                    text = "Autenticado";
                    System.out.println(pacote.Recurso);
                }
                text += tex + pacote.nome + ":" + pacote.Recurso;
                janela.attAutentic(text);
                users.replace(pacote.chavePublica, pacote);
            }
        }
        //FIM CAIXA DE ENTRADA
    }

    public void escutaMulti() {

        recmsg = new Thread() {
            public void run() {
                while (true) {
                    fluxo((Dados) multi.receiveMsg());
                    attJanUser();
                }
            }
        };

        recmsg.start();
    }

    public void acoes() {

        janela.tela.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent evt) {
                dados.acao = "sair";
                multi.sendMsg(dados);
            }
        });

        //BOTAO REQUEST RECURSO
        //Itera lista
        janela.Rec.addActionListener((ActionEvent evt) -> {

            //altera assunto
            dados.acao = "Recurso";
            //SETA ESTADO DO RECURSO
            dados.Recurso = "WANTED";

            janela.Rec.setBackground(Color.yellow);
            //VARRE A LISTA E DISPARA REQUESTS

            int[] cont = new int[users.size()];
            int c = 0;

            // itera lista de logados (ate entao)
            // dispara request do recurso a cada um
            for (PublicKey key : users.keySet()) {
                if (!users.get(key).nome.equals(dados.nome)) {
                    dados.destino = users.get(key).nome;
                    //envia mensagem pra cada um pedindo recurso
                    //caso a lista tenha mais de 1 pessoa
                    //(alguem além de si mesmo)
                    if (users.size() > 1) {
                        try {
                            //envia dados
                            Thread.sleep(1000);
                            multi.sendMsg(dados);
                        } catch (InterruptedException ex) {
                            System.out.println("erro");
                        }
                    }
                }
            }

            //itera lista de users
            //verifica status do recurso
            //se todos forem "RELEASED"
            //assume o recurso
            for (PublicKey key : users.keySet()) {
                //if (users.get(key).nome.equals(dados.nome)) {
                if (users.get(key).Recurso.equals("RELEASED")) {
                    cont[c] = 1;
                }
                c++;
                //}
            }

            System.out.println(Arrays.toString(cont));
            System.out.println(cont.length);
            System.out.println(IntStream.of(cont).sum());

            if (IntStream.of(cont).sum() == (cont.length) - 1) {
                dados.Recurso = "HELD";
                janela.Rec.setBackground(Color.GREEN);
                janela.Rec.setEnabled(!janela.Rec.isEnabled());
            }
        });

        //APRESENTA LISTA
        janela.jlist.addActionListener((ActionEvent evt) -> {
            attJanUser();
        });

    }
}
