package edu.mco364;

public class ClientMain {

    public static void main(String[] args) {
        PongClient application;

        if (args.length == 0)
            application = new PongClient("192.168.1.3");
        else
            application = new PongClient(args[0]);

        application.runClient();
    }

}
