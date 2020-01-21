# Crypto-tls

### Exercise 1

I downloaded the certificate from mozilla, and used it to connect to the server. This step is unnecessary, because after callind startHandshake, when the handshake is completed, and we have a handshakeCompletedListener, we receive the issuerND and subjectND from that event. But I wanted to build my own keystore, with my own trusted certificate.

### Exercise 2

If the server is ran, and bnr.ro points  to 127.0.0.1 in the hosts file, we receive a warning, that firefox realized, that the certificate is not from bnr.ro, so we cannot access the site. However (and this issue persists), if I try `https://localhost`, I get keytool chain empty, so I am unable to access the server.


### Exercise 3-4-5

Descriptions on generating the rootCA, clientCA and serverCA can be found in `src/main/resources/feladat3/steps.txt`

### Excersise 6

Same problem applies here, as at the second exercise. I created a keystore, storing the private key and the certificate, but I just can't get it to run. "KeyStore chain empty". 