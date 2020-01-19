package edu.bbte.crypt.afim1689.feladat1;

import edu.bbte.crypt.afim1689.CertificateData;
import edu.bbte.crypt.afim1689.CustomSSLSocketFactory;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Client1 {
    private static final Logger LOG = LoggerFactory.getLogger(Client1.class);

    public static void main(String[] args) throws Exception {
        String host = "www.bnr.ro";
        int port = 443;

        LOG.info("Creating KeyStore ...");
        SSLSocketFactory factory = CustomSSLSocketFactory.createCustom(List.of(
                new CertificateData(
                        Client1.class.getResourceAsStream("/feladat1/bnr-ro.crt"),
                        "bnr-ro"
                )
        ));

        StringBuilder getRequestBuilder = new StringBuilder();

        getRequestBuilder
                .append("GET /Home.aspx HTTP/1.1\r\n")
                .append("Host: www.bnr.ro\r\n")
                .append("Connection: close\r\n")
                .append("\r\n");


        LOG.info("Opening connection to {}:{}", host, port);
        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
        socket.setSoTimeout(5000);
        socket.setNeedClientAuth(true);
        socket.setUseClientMode(true);

        socket.addHandshakeCompletedListener(event -> {
            LOG.info("Handshake finished, cypher used: {}", event.getCipherSuite());
            try {
                LOG.info("Peer principal: {}",  event.getPeerPrincipal().getName());
            } catch (SSLPeerUnverifiedException e) {
                e.printStackTrace();
            }
            try (PrintWriter printWriter = new PrintWriter(socket.getOutputStream())) {
                printWriter.print(getRequestBuilder.toString());
                printWriter.flush();
                LOG.info("Request sent, waiting for response");

                StringWriter writer = new StringWriter();
                IOUtils.copy(socket.getInputStream(), writer, StandardCharsets.UTF_8);
                String response = writer.toString();

                var fileWriter = new FileWriter("bnr-ro.resp");
                fileWriter.write(response);
                socket.close();
                LOG.info("File saved as {}", "bnr-ro.resp");
            } catch (IOException e) {
                LOG.info("isSocketClosed: {}", socket.isClosed());
                LOG.error("Socket error", e);
            }
        });

        try {
            LOG.info("Starting SSL handshake...");
            socket.startHandshake();
        } catch (SSLException | SocketTimeoutException e) {
            LOG.error("SSLException", e);
        }
    }


}