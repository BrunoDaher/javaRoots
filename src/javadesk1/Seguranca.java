package javadesk1;

import java.security.*;

public class Seguranca {

    private PublicKey pubKey;

    public PublicKey getPubKey() {
        return pubKey;
    }

    public void setPubKey(PublicKey pubKey) {
        this.pubKey = pubKey;
    }

    public Seguranca() {
    }

    public void inicio(String mensagem) {
        //assina mensagem (insere chave privada)
        byte[] msgAssinada = assina(mensagem);
        //Guarda Chave Pública para ser Enviada ao Destinatário
        PublicKey pubKey = getPubKey();
        //Destinatário recebe dados corretos
        System.out.println(confere(pubKey, mensagem, msgAssinada));
    }

    public byte[] assina(String mensagem) {
        Signature sig;
        byte[] assinatura = null;
        try {
            sig = Signature.getInstance("DSA");
            //Geração das chaves públicas e privadas
            KeyPairGenerator kpg = KeyPairGenerator.getInstance("DSA");
            SecureRandom secRan = new SecureRandom();
            kpg.initialize(512, secRan);
            KeyPair keyP = kpg.generateKeyPair();
            this.pubKey = keyP.getPublic();
            PrivateKey priKey = keyP.getPrivate();

            //Inicializando Obj Signature com a Chave Privada
            sig.initSign(priKey);

            //Gerar assinatura
            sig.update(mensagem.getBytes());
            assinatura = sig.sign();

        } catch (NoSuchAlgorithmException | SignatureException | InvalidKeyException ex) {
            System.out.println("erro" + this.getClass().getName());
        }

        return assinatura;
    }

    public boolean confere(PublicKey pubKey, String mensagem, byte[] msgAssinada) {
        boolean check = false;
        Signature clientSig;
        try {
            clientSig = Signature.getInstance("DSA");
            //verifica chave Publica
            clientSig.initVerify(pubKey);

            //submete a mensagemm
            clientSig.update(mensagem.getBytes());

            if (clientSig.verify(msgAssinada)) {
                //verifica a assinatura
                System.out.println("A Mensagem recebida foi assinada corretamente.");
                check = true;
            } else {
                //Mensagem não pode ser validada
                System.out.println("A Mensagem recebida NÃO pode ser validada.");
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException | SignatureException ex) {
            //Logger.getLogger(DestinatarioAssiDig.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("erro" + this.getClass().getName());
        }

        return check;

    }

}
