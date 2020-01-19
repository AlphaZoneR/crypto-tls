package edu.bbte.crypt.afim1689.feladat6;


public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.getProperties().setProperty("javax.net.debug", "ssl:handshake");
        var serverThread = new Thread(new Server());
        var clientThread = new Thread(new Client());
        serverThread.start();
        clientThread.start();
        serverThread.join();
        clientThread.join();
    }
}
