package edu.bbte.crypt.afim1689.feladat6;

import edu.bbte.crypt.afim1689.CustomSSLSocketFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

public class Server implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Server.class);

    @Override
    public void run() {
        try {
            SSLServerSocketFactory socketFactory = CustomSSLSocketFactory.createCustomServer(
                    "/feladat6/test.jks"
            );
            SSLServerSocket socket = (SSLServerSocket) socketFactory.createServerSocket(443);
            socket.setNeedClientAuth(true);
            socket.setEnabledCipherSuites(socketFactory.getSupportedCipherSuites());
            socket.setEnabledProtocols(new String[]{"TLSv1.2"});

            SSLSocket clientSocket = (SSLSocket) socket.accept();
            clientSocket.addHandshakeCompletedListener(event -> {
                LOG.info("Handshake complete");

                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            clientSocket.startHandshake();
        } catch (CertificateException | KeyStoreException | NoSuchAlgorithmException | KeyManagementException | IOException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }


    }
}
