package server;

import java.io.*;
import java.net.*;
import server.matching.MatchingUser;


public class ClientHandler extends Thread {
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;
    private final User currentUser;

    private String userCommand = null;


    public ClientHandler(Socket socket, MatchingUser matchingUser) throws IOException {
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);

        //User는 접속하자마자 clientName을 보냄
        String clientName = matchingUser.getName();

        currentUser = new User(socket, false, clientName, this);

        waitUsers_broadcastMessage(currentUser.getName() + " 님이 참가했습니다.");
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public String getUserCommand() {
        return userCommand;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    //브로드캐스트
    public static void bidUsers_broadcastMessage(String message) {
        for (User client : AuctionServer.bidUsers) {
            client.sendMessage(message);
        }
    }

    public void waitUsers_broadcastMessage(String message) {
        for (User client : AuctionServer.waitUsers) {
            client.sendMessage(message);
        }
    }

    @Override
    public void run() {

        String userMessage = null;
        String chatMessage = null;

        try {
            while (true) {
                userMessage = in.readLine();

                if (userMessage.startsWith("호가")) {
                    int bidAmount = Integer.parseInt(userMessage.split(" ")[1]);  //호가 금액
                    WaitingThread.gameThread.placeBid(currentUser, bidAmount);
                    userCommand = userMessage.split(" ")[0];

                } else if (userMessage.startsWith("채팅")) {
                    chatMessage = userMessage.substring(3);
                    userCommand = userMessage.split(" ")[0];
                    bidUsers_broadcastMessage("채팅" + currentUser.getName() + ": " + chatMessage);

                } else if (currentUser.isOneChance() && userMessage.startsWith("응찰")) {
                    currentUser.setParticipating(true);
                    currentUser.setOneChance(false);
                    currentUser.sendMessage("경매에 응찰합니다");

                } else if (currentUser.isOneChance() && userMessage.startsWith("불응찰")) {
                    currentUser.setParticipating(false);
                    currentUser.setOneChance(false);
                    currentUser.sendMessage("경매에 불응찰합니다");

                }
            }
        } catch (IOException e) {
            System.out.println("연결 종료: " + currentUser.getName());
        } finally {
            try {
                socket.close();

                if (AuctionServer.waitUsers.contains(currentUser)) {
                    AuctionServer.waitUsers.remove(currentUser);
                } else if (AuctionServer.bidUsers.contains(currentUser)) {
                    AuctionServer.bidUsers.remove(currentUser);

                }
            } catch (IOException e) {
                System.out.println("소켓 종료 오류");
            }
        }
    }
}
