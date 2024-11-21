package io;

import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Output {

    public static Output INSTANCE = new Output();
    private List<PrintWriter> outputWriters=new ArrayList<>();

    private Output(){

    }

    public void addUserSocket(Socket socket){
        try{
            this.outputWriters.add(new PrintWriter(socket.getOutputStream(), true));
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void broadcastMessage(String message){
        for (PrintWriter outputWriter : outputWriters) {
            outputWriter.println(message);
        }
    }
}
