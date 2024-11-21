import io.Output;
import java.io.*;
import java.net.*;
import java.util.concurrent.LinkedBlockingQueue;
import matching.MatchingThread;

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

               /* ClientHandler clientHandler = new ClientHandler(clientSocket);  //clientHandler에서 생성자에서 User생성
                User user = clientHandler.getCurrentUser();
                System.out.println(user.getName() +" ");
                waitUsers.add(user);
                clientHandler.start();
                System.out.println(waitUsers.size());*/
            }
        } finally {
            listener.close();
        }
    }
}
