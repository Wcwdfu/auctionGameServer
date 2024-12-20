package server;

import server.io.Output;
import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;
import server.matching.MatchingThread;

public class AuctionServer {
    private static final int PORT = 12345;
    public static LinkedBlockingQueue<User> waitUsers = new LinkedBlockingQueue<>();
    public static  LinkedBlockingQueue<User> bidUsers = new LinkedBlockingQueue<User>();
    private static WaitingThread waitingThread = null;


    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);
        System.out.println("서버가 실행 중입니다");

        waitingThread = new WaitingThread();
        waitingThread.start();

        try {
            while (true) {
                Socket clientSocket = listener.accept();

                Output.INSTANCE.addUserSocket(clientSocket);
                new MatchingThread(clientSocket).run();

            }
        } finally {
            listener.close();
        }
    }
}
