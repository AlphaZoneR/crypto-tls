package edu.bbte.crypt.afim1689;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.*;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Objects;

public class CustomSSLSocketFactory {
    private static final char[] PASSPHRASE = "password".toCharArray();
    private static final Logger LOG = LoggerFactory.getLogger(CustomSSLSocketFactory.class);
    private static final char[] HEX_DIGITS = "0123456789ABCDEF".toCharArray();

    public static SSLSocketFactory createCustom(List<CertificateData> certificateDataList) throws CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, IOException {
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, PASSPHRASE);

        CertificateFactory certificateFactory = CertificateFactory.getInstance("X509");

        for (final CertificateData certificateData : certificateDataList) {
            X509Certificate cert = (X509Certificate) certificateFactory.generateCertificate(certificateData.getCertificateInputStream());
            ks.setCertificateEntry(certificateData.getAlias(), cert);

            MessageDigest sha1 = MessageDigest.getInstance("SHA1");
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("\n").append("\tSubject ").append(cert.getSubjectDN()).append("\n");
            stringBuilder.append("\tIssuer: ").append(cert.getIssuerDN()).append("\n");
            sha1.update(cert.getEncoded());
            stringBuilder.append("\tsha1: ").append(toHexString(sha1.digest())).append("\n");
            md5.update(cert.getEncoded());
            stringBuilder.append("\tmd5: ").append(toHexString(md5.digest())).append("\n");
            stringBuilder.append("\tversion: ").append(cert.getVersion()).append("\n");
            stringBuilder.append("\tserial-number: ").append(cert.getSerialNumber()).append("\n");
            stringBuilder.append("\tnot-before: ").append(cert.getNotBefore()).append("\n");
            stringBuilder.append("\tnot-after: ").append(cert.getNotAfter()).append("\n");
            stringBuilder.append("\tpublic-key-enc-type: ").append(cert.getPublicKey().getAlgorithm()).append("\n");
            if (Objects.nonNull(cert.getSubjectAlternativeNames())) {
                cert.getSubjectAlternativeNames().forEach(l -> l.forEach(k -> stringBuilder.append("\talternative subject name: ").append(k).append("\n")));
            }
            md5.update(cert.getPublicKey().getEncoded());
            stringBuilder.append("\tpublic-key: ").append(toHexString(md5.digest())).append("\n");
            LOG.info("Adding certificate: ");
            LOG.info(stringBuilder.toString());
        }

        SSLContext context = SSLContext.getInstance("TLS");
        TrustManagerFactory tmf =
                TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(ks);
        X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
        context.init(null, new TrustManager[]{defaultTrustManager}, null);
        return context.getSocketFactory();
    }

    public static SSLServerSocketFactory createCustomServer(final String keyStoreFileName) throws CertificateException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException, IOException, UnrecoverableKeyException {
        SSLContext context = SSLContext.getInstance("TLS");
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509");
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(CustomSSLSocketFactory.class.getResourceAsStream(keyStoreFileName), "password".toCharArray());
        LOG.info("Hello: {}", (Object) keyStore.getCertificateChain("1"));
        keyManagerFactory.init(keyStore, "password".toCharArray());
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509");
        trustManagerFactory.init(keyStore);
        context.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        return context.getServerSocketFactory();
    }

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes) {
            b &= 0xff;
            sb.append(HEX_DIGITS[b >> 4]);
            sb.append(HEX_DIGITS[b & 15]);
            sb.append(' ');
        }
        return sb.toString();
    }
}
