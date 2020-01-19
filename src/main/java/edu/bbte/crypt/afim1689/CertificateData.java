package edu.bbte.crypt.afim1689;

import java.io.InputStream;

public class CertificateData {
    private final InputStream certificateInputStream;
    private final String alias;

    public CertificateData(final InputStream certificateInputStream, final String alias) {
        this.certificateInputStream = certificateInputStream;
        this.alias = alias;
    }

    public InputStream getCertificateInputStream() {
        return certificateInputStream;
    }

    public String getAlias() {
        return alias;
    }
}
