package com.example.prototype_therminal.serversocket;

import android.os.Handler;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;

class CommunicationThread implements Runnable {
    Handler updateConversationHandler;
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
            updateConversationHandler.post(new ServerThread.updateUIThread(list.toString()));
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