package com.example.prototype_therminal.serversocket;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class ServerThread{
    public static final int SERVERPORT = 6000;
    private ServerSocket serverSocket;
    Handler updateConversationHandler;

    public ServerRunnable serverRunnable = new ServerRunnable();
    public class ServerRunnable implements Runnable {


        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SERVERPORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {

                try {

                    socket = serverSocket.accept();

                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class CommunicationThread implements Runnable {
        public Runnable runnable;
        private Socket clientSocket;
        public ArrayList<String> list = new ArrayList<String>();
        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;

            try {

                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            list.clear();
            while (!Thread.currentThread().isInterrupted()) {

                try {
                    String read = input.readLine();
                    list.add(read);
                    if(read.length() == 0){
                        break;
                    }



                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {

                updateConversationHandler.post(runnable);
                String OUTPUT = "<html><head><title>Example</title></head><body><p>Worked!!!</p></body></html>";
                String OUTPUT_HEADERS = "HTTP/1.1 200 OK\r\n" +
                        "Content-Type: text/html\r\n" +
                        "Content-Length: ";
                String OUTPUT_END_OF_HEADERS = "\r\n\r\n";

                DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
                out.writeBytes(OUTPUT_HEADERS+ OUTPUT.length()+OUTPUT_END_OF_HEADERS);
                out.writeBytes(OUTPUT);
                out.close();


            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    static class updateUIThread implements Runnable {
        private String msg;

        public updateUIThread(String str) {
            this.msg = str;
        }

        @Override
        public void run() {

        }
    }

}


