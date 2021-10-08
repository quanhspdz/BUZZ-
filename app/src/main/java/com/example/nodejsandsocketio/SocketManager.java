package com.example.nodejsandsocketio;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {
    public static Socket mSocket;

    static {
        try {
            mSocket = IO.socket("https://buzz-server-quanhspdz.herokuapp.com/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
