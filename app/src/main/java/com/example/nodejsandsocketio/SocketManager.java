package com.example.nodejsandsocketio;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class SocketManager {
    public static Socket mSocket;

    static {
        try {
            mSocket = IO.socket("http://192.168.31.214:3000/");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
