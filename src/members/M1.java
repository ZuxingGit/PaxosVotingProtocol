package members;

import constant.FixedValues;
import utils.LamportID;

import java.io.*;
import java.net.ConnectException;
import java.net.Socket;
import java.util.Scanner;

public class M1 {
    private static final int disconnectionRate = 0; //0% offline, 100% online
    private static final int port = 4561;
    private static final String name = "M1";
    private static Long ID; //proposal number
    private static String value = name + "_become_the_president"; //proposal value

    public static void main(String[] args) {
        String IP = FixedValues.hostIP;
        Acceptor acceptor = new Acceptor(port, disconnectionRate);
        acceptor.startAcceptor();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            String input = scanner.nextLine();
            if ("VOTE".equalsIgnoreCase(input)) {
                int[] ports = FixedValues.ports;
                ID = LamportID.getNextNumber();
                int countPromise = 0;
                int countAccept = 0;
                Long maxAcceptedId = 0L;
                for (int targetPort :
                        ports) {
                    if (targetPort != port) {
                        // connect to a member on this specific targetPort
                        Socket socket;
                        try {
                            socket = new Socket(IP, targetPort);  //connect to other members
                        } catch (IOException e) {
                            if (e.getClass() == ConnectException.class)
                                System.out.println("No member started on port:" + targetPort + "\n");
                            continue;
                        }
                        Proposer proposer = new Proposer(socket, disconnectionRate);
                        String msgToSend = "";
                        msgToSend += "PREPARE> ID:" + ID;
                        String msgReceived = proposer.vote(name + ": " + msgToSend);
                        if (msgReceived == null || msgReceived.toString().isEmpty()) {
                            System.out.println("M" + (targetPort % 10) + " is offline! No response.\n");
                            continue;
                        }

                        if (100 * Math.random() <= disconnectionRate) {
                            System.out.println(name + " is offline! Missed a message from " + msgReceived); //no response, pretend to be offline
                            continue;
                        } else {
                            System.out.println(name + " received from " + msgReceived);
                            if (msgReceived.toString().contains("PROMISE") && msgReceived.toString().contains("ID")) {
                                if (msgReceived.toString().contains("acceptedID") && msgReceived.toString().contains("acceptedValue")) {
                                    Long idFromPromise = Long.valueOf(msgReceived.substring(msgReceived.indexOf("ID:") + 3, msgReceived.indexOf("acceptedID:")).trim());
                                    if (idFromPromise == ID) {
                                        Long acceptedID = Long.valueOf(msgReceived.substring(msgReceived.indexOf("acceptedID:") + 11, msgReceived.indexOf("acceptedValue:")).trim());
                                        if (maxAcceptedId < acceptedID) {
                                            maxAcceptedId = acceptedID;
                                            value = msgReceived.substring(msgReceived.indexOf("acceptedValue:") + 14).trim();
                                        }
                                        countPromise++;
                                    }
                                } else {
                                    Long idFromResponse = Long.valueOf(msgReceived.substring(msgReceived.indexOf("ID:") + 3).trim());
                                    if (idFromResponse == ID) {
                                        countPromise++;
                                    }
                                }
                            } else if (msgReceived.toString().contains("fail") && msgReceived.toString().contains("ID")) {
                                Long idFromResponse = Long.valueOf(msgReceived.substring(msgReceived.indexOf("ID:") + 3).trim());
                                LamportID.setCurrentNumber(idFromResponse); //synchronize the ID value to the biggest from all responses
                            }
                        }
                        proposer.closeAll();
                    }
                }
                if (countPromise >= ports.length / 2 + 1) {// half+1 means a majority, they promised
                    System.out.println("Received Promises from " + countPromise + " acceptors");
                    for (int targetPort :
                            ports) {
                        if (targetPort != port) {
                            // connect to a member on this specific targetPort
                            Socket socket;
                            try {
                                socket = new Socket(IP, targetPort);  //connect to other members
                            } catch (IOException e) {
                                if (e.getClass() == ConnectException.class)
                                    System.out.println("No member started on port:" + targetPort + "\n");
                                continue;
                            }
                            Proposer proposer = new Proposer(socket, disconnectionRate);
                            String msgToSend = "";
                            msgToSend += "PROPOSE> ID:" + ID + " value:" + value;
                            String msgReceived = proposer.vote(name + ": " + msgToSend);
                            if (msgReceived == null || msgReceived.toString().isEmpty()) {
                                System.out.println("M" + (targetPort % 10) + " is offline! No response.\n");
                                continue;
                            }

                            if (100 * Math.random() <= disconnectionRate) {
                                System.out.println(name + " is offline! Missed a message from " + msgReceived); //no response, pretend to be offline
                                continue;
                            } else {
                                System.out.println(name + " received from " + msgReceived);
                                if (msgReceived.toString().contains("ACCEPT") && msgReceived.toString().contains("ID") && msgReceived.toString().contains("value")) {
                                    Long receivedID = Long.valueOf(msgReceived.substring(msgReceived.indexOf("ID:") + 3, msgReceived.indexOf("value:")).trim());
                                    String receivedValue = msgReceived.substring(msgReceived.indexOf("value:") + 6).trim();
                                    if (ID == receivedID && value.equals(receivedValue)) {
                                        countAccept++;
                                    }
                                }
                            }
                            proposer.closeAll();
                        }
                    }
                    if (countAccept >= ports.length / 2 + 1) {// half+1 means a majority, they accepted
                        System.out.println("Received Accepts from " + countAccept + " acceptors");
                        acceptor.map.put("acceptedID", String.valueOf(ID));
                        acceptor.map.put("acceptedValue", value);
                        System.out.println(value + " accepted by the majority!");
                    }
                }
            }
        }
    }
}