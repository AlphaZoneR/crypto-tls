package edu.bbte.crypt.afim1689.feladat2;

import edu.bbte.crypt.afim1689.CustomSSLSocketFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
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
                    "/feladat2/server.p12"
            );

            SSLServerSocket socket = (SSLServerSocket) socketFactory.createServerSocket(443);
            socket.setNeedClientAuth(true);
            socket.setEnabledCipherSuites(socketFactory.getSupportedCipherSuites());
            socket.setEnabledProtocols(new String[]{"TLSv1.2"});

            while (true) {
                try {
                    var clientSocket = (SSLSocket) socket.accept();
                    clientSocket.startHandshake();
                    StringWriter writer = new StringWriter();
                    IOUtils.copy(clientSocket.getInputStream(), writer, StandardCharsets.UTF_8);
                    String request = writer.toString();
                    LOG.info("Got request:\n{}", request);
                } catch (SSLException exception) {
                    LOG.error("Handshake exception", exception);
                }
            }
        } catch (CertificateException | KeyStoreException | NoSuchAlgorithmException | KeyManagementException | IOException | UnrecoverableKeyException e) {
            e.printStackTrace();
        }
    }
}
