package com.engine.starship.utils.io.net;

import com.badlogic.gdx.Gdx;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class DebugServer {

    public static int PORT  = 70;

    public static void startServer(){
        Thread thread = new Thread(){
            @Override
            public void run() {
                try {
                    ServerSocket serverSocket = new ServerSocket(PORT);
                    while (Gdx.graphics.isContinuousRendering()){
                        Socket socket = serverSocket.accept();
                        String msg = socket.getInputStream().toString();
                        System.out.println(msg);
                    }
                    serverSocket.close();
                }catch (Exception exception){
                    exception.printStackTrace();
                }
                super.run();
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    public static void msgServer(){
        try (Socket socket = new Socket("192.168.1.10",PORT)) {
         if (socket.isConnected()){
             OutputStream stream = socket.getOutputStream();
             stream.write("hi".getBytes());
         }
        }catch (Exception exception){
            exception.printStackTrace();
        }
    }
}
