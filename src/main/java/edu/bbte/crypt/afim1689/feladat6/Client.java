package edu.bbte.crypt.afim1689.feladat6;

import edu.bbte.crypt.afim1689.CertificateData;
import edu.bbte.crypt.afim1689.CustomSSLSocketFactory;
import edu.bbte.crypt.afim1689.feladat1.Client1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.List;

public class Client implements Runnable {
    private static final Logger LOG = LoggerFactory.getLogger(Client.class);

    @Override
    public void run() {
        try {
            Thread.sleep(1000);
            SSLSocketFactory factory = CustomSSLSocketFactory.createCustom(List.of(
                    new CertificateData(
                            Client1.class.getResourceAsStream("/feladat6/clientCA.crt"),
                            "client-ca"
                    )
            ));

            SSLSocket socket = (SSLSocket) factory.createSocket("localhost", 443);
            socket.addHandshakeCompletedListener(event -> {
                LOG.info("Handshake complete");
                try {
                    socket.close();
                } catch (IOException e) {
                    LOG.error("Error closing socket", e);
                }
            });
            socket.startHandshake();
        } catch (CertificateException | KeyStoreException | NoSuchAlgorithmException | KeyManagementException | IOException | InterruptedException e) {
            LOG.error("Client error", e);
        }
    }
}
