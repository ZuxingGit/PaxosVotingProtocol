package members;

import utils.LamportID;

import java.io.*;
import java.net.Socket;

public class Proposer {
    private int disconnectionRate = 5; //5%
    private static int port = 4560;
    public Socket socket = null;
    public InputStreamReader inputStreamReader = null;
    public OutputStreamWriter outputStreamWriter = null;
    public BufferedReader bufferedReader = null;
    public BufferedWriter bufferedWriter = null;

    public Proposer(Socket socket, int disconnectionRate) {
        this.socket = socket;
        this.disconnectionRate = disconnectionRate;
        try {
            inputStreamReader = new InputStreamReader(socket.getInputStream());
            outputStreamWriter = new OutputStreamWriter(socket.getOutputStream());

            this.bufferedReader = new BufferedReader(inputStreamReader);
            this.bufferedWriter = new BufferedWriter(outputStreamWriter);
        } catch (IOException e) {
            closeAll();
        }
    }

//    public String getResponse() {
//        StringBuilder msgReceived = new StringBuilder();
//        try {
//            while (socket.isConnected()) {
//                String line = bufferedReader.readLine();
//                while (line != null && !line.isEmpty()) {
//                    msgReceived.append(line).append("\n");
//                    line = bufferedReader.readLine();
//                }
//                if (msgReceived == null || msgReceived.toString().isEmpty()) {
//                    System.out.println("The opposite member disconnected");
//                    break;
//                }
//                
//                msgReceived.setLength(0);
//            }
//        } catch (IOException e) {
////                e.printStackTrace();
//        } finally {
////                System.out.println("Connection Stopped!");
//            closeAll();
//        }
//        return msgReceived.toString();
//    }

    public String vote(String msg) {
        StringBuilder msgReceived = msgReceived = new StringBuilder();
        try {
            bufferedWriter.write(msg + "\n");
            bufferedWriter.newLine();
            bufferedWriter.flush();

            String line = bufferedReader.readLine();
            while (line != null && !line.isEmpty()) {
                msgReceived.append(line).append("\n");
                line = bufferedReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return msgReceived.toString();
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