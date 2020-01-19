package edu.bbte.crypt.afim1689.feladat2;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        System.getProperties().setProperty("javax.net.debug", "ssl");
        var serverThread = new Thread(new Server());
        serverThread.start();
        serverThread.join();
    }
}
