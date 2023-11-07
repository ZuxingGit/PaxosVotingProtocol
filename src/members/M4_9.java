package members;

import constant.FixedValues;

public class M4_9 {
    public static void main(String[] args) {
        int[] ports = FixedValues.ports;
        Acceptor M4 = new Acceptor(ports[3]);
        M4.startAcceptor();
        Acceptor M5 = new Acceptor(ports[4]);
        M5.startAcceptor();
        Acceptor M6 = new Acceptor(ports[5]);
        M6.startAcceptor();
        Acceptor M7 = new Acceptor(ports[6]);
        M7.startAcceptor();
        Acceptor M8 = new Acceptor(ports[7]);
        M8.startAcceptor();
        Acceptor M9 = new Acceptor(ports[8]);
        M9.startAcceptor();
    }
}
