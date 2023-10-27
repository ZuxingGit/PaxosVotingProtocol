package members;


import constant.FixedValues;

import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class Proposer {
    private static final int disconnectionRate = 5; //5%
    private static int port = 4560;
    private static ServerSocket serverSocket;
    public Socket socket = null;
    public InputStreamReader inputStreamReader = null;
    public OutputStreamWriter outputStreamWriter = null;
    public BufferedReader bufferedReader = null;
    public BufferedWriter bufferedWriter = null;
    private static HashMap<String, String> map = new HashMap<>();

    public Proposer(Socket socket) {
        this.socket = socket;
        try {
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            this.bufferedReader = new BufferedReader(inputStreamReader);
            this.bufferedWriter = new BufferedWriter(outputStreamWriter);
        } catch (IOException e) {
            closeAll();
        }
    }


    public static void main(String[] args) throws IOException {
        String IP = FixedValues.hostIP;
        Acceptor acceptor = new Acceptor(port);// Listen for a connection
        System.out.println("Proposer started on port:" + port);
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
                                System.out.println("No members on port:" + targetPort);
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

    public void response() {
        new Thread(() -> {
            StringBuilder msgReceived = new StringBuilder();
            try {
                while (socket.isConnected()) {
                    String line = bufferedReader.readLine();
                    while (line != null && !line.isEmpty()) {
                        msgReceived.append(line).append("\n");
                        line = bufferedReader.readLine();
                    }
                    if (msgReceived == null || msgReceived.toString().isEmpty()) {
                        System.out.println("The opposite member disconnected");
                        break;
                    }
                    System.out.println(msgReceived);
                    msgReceived.setLength(0);
                }
            } catch (IOException e) {
//                e.printStackTrace();
            } finally {
//                System.out.println("Connection Stopped!");
                closeAll();
            }
        }).start();

//        if (100 * Math.random() <= disconnectionRate) {
//            //no response, pretend to be offline
//        } else {
//
//        }
    }

    public void vote() {
        try {
            String msgToSend = "";
            msgToSend = "ID:01, VALUE:3";
            bufferedWriter.write(msgToSend + "\n");
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeAll() {
        try {
            if (socket != null)
                socket.close();
            if (inputStreamReader != null)
                inputStreamReader.close();
            if (outputStreamWriter != null)
                outputStreamWriter.close();
            if (bufferedReader != null)
                bufferedReader.close();
            if (bufferedWriter != null)
                bufferedWriter.close();
//            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

//class ProposerRequestHandler extends Thread {
//    private Socket socket;
//    private boolean hasStarted;
//
//    ProposerRequestHandler(Socket socket) {
//        this.socket = socket;
//    }
//
//    public void run() {
//        InputStreamReader inputStreamReader = null;
//        OutputStreamWriter outputStreamWriter = null;
//        BufferedReader bufferedReader = null;
//        BufferedWriter bufferedWriter = null;
//
//        try {
//            inputStreamReader = new InputStreamReader(socket.getInputStream());
//            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());
//
//            bufferedReader = new BufferedReader(inputStreamReader);
//            bufferedWriter = new BufferedWriter(outputStreamWriter);
//
//            while (true) {
//                StringBuilder msgReceived = new StringBuilder();
//                String line = bufferedReader.readLine();
//                while (line != null && !line.isEmpty()) {
//                    msgReceived.append(line).append("\n");
//                    line = bufferedReader.readLine();
//                }
//
//                System.out.println(msgReceived);
//
//                String responseMsg = "received";
//                bufferedWriter.write(responseMsg);
//                bufferedWriter.newLine();
//                bufferedWriter.flush();
//
//                if ("BYE".equalsIgnoreCase(msgReceived.toString())) {
//                    break;
//                }
//                msgReceived.setLength(0);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//}