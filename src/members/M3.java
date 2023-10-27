package members;

import constant.FixedValues;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class M3 {
    private static final int disconnectionRate = 20; //Mostly online, occasionally offline
    public Socket socket;
    public InputStreamReader inputStreamReader = null;
    public OutputStreamWriter outputStreamWriter = null;
    public BufferedReader bufferedReader = null;
    public BufferedWriter bufferedWriter = null;

    public M3(Socket socket) {
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
        int port = 4560;
        System.out.println("M3 start on port:" + port);
        Socket socket = new Socket(IP, port);
        M3 M3 = new M3(socket);
        M3.response();
        M3.vote();
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
            }
        }).start();
    }

    public void vote() {
        try {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
//                System.out.println();
                String msgToSend = "";
                if ("VOTE".equalsIgnoreCase(input)) {
                    msgToSend = "ID:01, VALUE:2";
                    bufferedWriter.write(msgToSend + "\n");
                    bufferedWriter.newLine();
                    bufferedWriter.flush();
                }
            }
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