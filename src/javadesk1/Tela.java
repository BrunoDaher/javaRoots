package javadesk1;

import java.awt.Color;
import java.awt.GridLayout;
import javax.swing.*;

public class Tela {

    public JFrame tela = new JFrame();
    private String titulo, nomeProcesso;
    public JButton jlist, Rec, UsaRec;
    private JTextArea lista, autentic;
    private JScrollPane scrollLista, scrollAutentic;

    public Tela(Color cor, String n) {

        nomeProcesso = n;

        //TELA
        tela.setBounds(10, 10, 200, 400);
        tela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        tela.getContentPane().setLayout(new GridLayout(5, 1));
        tela.setTitle(nomeProcesso);

        //ELEMENTOS
        //BOTAO LISTA
        jlist = new JButton("Lista");
        jlist.setBackground(cor);

        //BOTAO RECURSO
        Rec = new JButton("Recurso");
        Rec.setBackground(Color.CYAN);
        Rec.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));

        //BOTAO SAIR
        UsaRec = new JButton("");
        UsaRec.setEnabled(false);

        //instala textArea
        lista = new JTextArea();
        scrollLista = new JScrollPane();
        scrollLista.setViewportView(lista);

        //instala textArea
        autentic = new JTextArea();
        scrollAutentic = new JScrollPane();
        scrollAutentic.setViewportView(autentic);

        //FINALIZAÇÃO ADIÇÃO DE ELEMENTOS
        tela.add(jlist);
        tela.add(Rec);
        tela.add(scrollLista);
        tela.add(UsaRec);
        tela.add(scrollAutentic);

        //TORNA VISIVEL
        tela.setVisible(true);
    }

    public void attLista(String msg) {
        lista.setText("");
        lista.setText(msg);
        tela.revalidate();
    }

    public void attAutentic(String msg) {
        //autentic.setText("");

        autentic.append(msg + "\n");

        tela.revalidate();
    }

    public void attRecBotao(String msg) {

        UsaRec.setText(msg);
        if (msg.equals("WANTED")) {
            UsaRec.setBackground(Color.YELLOW);
        }
        if (msg.equals("RELEASED")) {
            UsaRec.setBackground(Color.green);
        }
        if (msg.equals("HELD")) {
            UsaRec.setBackground(Color.RED);
        }

    }

}
