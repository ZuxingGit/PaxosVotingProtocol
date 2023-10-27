package members;

import constant.FixedValues;

import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class M1 {
    private static final int disconnectionRate = 0; //M1's network is perfect
    private static int port = 4561;
    private static ServerSocket serverSocket;
    public Socket socket;
    public InputStreamReader inputStreamReader = null;
    public OutputStreamWriter outputStreamWriter = null;
    public BufferedReader bufferedReader = null;
    public BufferedWriter bufferedWriter = null;
    private static HashMap<String, String> map = new HashMap<>();

    public M1(Socket socket) {
        this.socket = socket;
        try {
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            this.bufferedReader = new BufferedReader(inputStreamReader);
            this.bufferedWriter = new BufferedWriter(outputStreamWriter);
        } catch (IOException e) {
            closeAll(socket, bufferedReader, bufferedWriter, inputStreamReader, outputStreamWriter);
        }
    }


    public static void main(String[] args) throws IOException {
        String IP = FixedValues.hostIP;
        Acceptor acceptor = new Acceptor(port);// Listen for a connection
        acceptor.startAcceptor();
        
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();

            if ("VOTE".equalsIgnoreCase(input)) {
                int[] ports = FixedValues.ports;
                for (int targetPort :
                        ports) {
                    if (targetPort != port) {
                        System.out.println(targetPort);
                        // connect to a member on this specific targetPort
                        Socket socket = null;
                        try {
                            socket = new Socket(IP, targetPort);  //connect to other members
                        } catch (IOException e) {
                            if (e.getClass() == ConnectException.class)
                                System.out.println("No member started on port:" + targetPort);
                            continue;
                        }
                        M1 M1 = new M1(socket);
                        M1.response();
                        M1.vote();
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
                e.printStackTrace();
            } finally {
                System.out.println("Connection Stopped!");
                closeAll(socket, bufferedReader, bufferedWriter, inputStreamReader, outputStreamWriter);
            }
        }).start();
    }

    public void vote() {
        try {
            String msgToSend = "";
            msgToSend = "ID:01, VALUE:1";
            bufferedWriter.write(msgToSend + "\n");
            bufferedWriter.newLine();
            bufferedWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeAll(Socket socket, BufferedReader bufferedReader, BufferedWriter bufferedWriter, InputStreamReader inputStreamReader, OutputStreamWriter outputStreamWriter) {
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
            System.exit(0);
        } catch (IOException e) {
            e.getStackTrace();
        }
    }
}