package members;

import constant.FixedValues;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class M2 {
    private static final int disconnectionRate = 80; //80% offline, 20% online
    private static final int port = 4562;


    public static void main(String[] args) throws IOException {
        String IP = FixedValues.hostIP;
        Acceptor acceptor = new Acceptor(port);
        System.out.println("M2 started on port:" + port);
        acceptor.startAcceptor();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();

            if ("VOTE".equalsIgnoreCase(input)) {
                int[] ports = FixedValues.ports;
                for (int targetPort :
                        ports) {
                    if (targetPort != port) {
                        // connect to a member on this specific targetPort
                        Socket socket = null;
                        try {
                            socket = new Socket(IP, targetPort);  //connect to other members
                        } catch (IOException e) {
                            if (e.getClass() == ConnectException.class)
                                System.out.println("No member started on port:" + targetPort);
                            continue;
                        }
                        Proposer proposer = new Proposer(socket);
                        proposer.response();
                        proposer.vote();
                        proposer.closeAll();
                    }
                }
            }
        }
    }

}