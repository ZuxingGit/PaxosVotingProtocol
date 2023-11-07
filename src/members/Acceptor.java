package members;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

public class Acceptor extends Thread {
    private int disconnectionRate = 5; // maybe try 5%??
    private ServerSocket serverSocket;
    private int port;
    public HashMap<String, String> map = new HashMap<>();
    private String name;
    private boolean running = false;

    public Acceptor(int port) {
        this.port = port;
        this.name = "M" + Integer.toString(port).substring(3);
    }

    public Acceptor(int port, int disconnectionRate) {
        this.port = port;
        this.disconnectionRate = disconnectionRate;
        this.name = "M" + Integer.toString(port).substring(3);
    }

    public void startAcceptor() {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println(name + " started on port:" + port);
            this.start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void stopAcceptor() {
        running = false;
        this.interrupt();
    }

    public void run() {
        running = true;
        while (running) {
            try {
                Socket socket = serverSocket.accept();
                AcceptorRequestHandler acceptorRequestHandler = new AcceptorRequestHandler(socket, map, disconnectionRate, name);
                acceptorRequestHandler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        int port = 4564;
        System.out.println("Acceptor start on port:" + port);
        Acceptor Acceptor = new Acceptor(port);
        Acceptor.startAcceptor();

        Acceptor.stopAcceptor();
    }
}

class AcceptorRequestHandler extends Thread {
    private Socket socket;
    private HashMap<String, String> map;
    private int disconnectionRate;
    private String name;

    AcceptorRequestHandler(Socket socket, HashMap<String, String> map, int disconnectionRate, String name) {
        this.socket = socket;
        this.map = map;
        this.disconnectionRate = disconnectionRate;
        this.name = name;
    }

    public void run() {
        InputStreamReader inputStreamReader = null;
        OutputStreamWriter outputStreamWriter = null;
        BufferedReader bufferedReader = null;
        BufferedWriter bufferedWriter = null;

        try {
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            bufferedReader = new BufferedReader(inputStreamReader);
            bufferedWriter = new BufferedWriter(outputStreamWriter);

            while (true) {
                StringBuilder msgReceived = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line != null && !line.isEmpty()) {
                    msgReceived.append(line).append("\n");
                    line = bufferedReader.readLine();
                }
                if (msgReceived == null || msgReceived.toString().isEmpty()) {
                    // System.out.println("The opposite member disconnected");
                    break;
                }

                if (100 * Math.random() <= disconnectionRate) {
                    System.out.println(name + " is offline! Missed a message from " + msgReceived); //no response, pretend to be offline
                    break;
                } else {
                    System.out.println(name + " received from " + msgReceived.toString().trim());
                    String responseMsg = "";
                    if (msgReceived.toString().contains("PREPARE") && msgReceived.toString().contains("ID")) {
                        int ID = Integer.parseInt(msgReceived.substring(msgReceived.indexOf("ID:") + 3).trim());
                        if (map.containsKey("ID") && ID <= Integer.parseInt(map.get("ID"))) {
                            responseMsg = "fail";
                        } else {
                            map.put("ID", String.valueOf(ID));
                            responseMsg += "PROMISE> ID:" + ID;
                            if (map.containsKey("acceptedID") && map.containsKey("acceptedValue")) {
                                responseMsg += " acceptedID:" + map.get("acceptedID") + " acceptedValue:" + map.get("acceptedValue");
                            }
                        }
                    } else if (msgReceived.toString().contains("PROPOSE") && msgReceived.toString().contains("value")) {
                        int ID = Integer.parseInt(msgReceived.substring(msgReceived.indexOf("ID:") + 3, msgReceived.indexOf("value:")).trim());
                        String value = msgReceived.substring(msgReceived.indexOf("value:") + 6).trim();
                        if (map.containsKey("ID") && ID != Integer.parseInt(map.get("ID"))) {
                            responseMsg = "fail";
                        } else {
                            map.put("acceptedID", String.valueOf(ID));
                            map.put("acceptedValue", value);
                            responseMsg += "ACCEPT> ID:" + ID + " value:" + value;
                        }
                    }
                    System.out.println(name + " answers: " + responseMsg + "\n");
                    bufferedWriter.write(name + ": " + responseMsg + "\n");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }

                if ("BYE".equalsIgnoreCase(msgReceived.toString())) {
                    break;
                }
                msgReceived.setLength(0);
            }

            socket.close();
            inputStreamReader.close();
            outputStreamWriter.close();
            bufferedReader.close();
            bufferedWriter.close();
        } catch (IOException e) {
//            e.printStackTrace();
        } finally {
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
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
