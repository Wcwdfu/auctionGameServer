import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class AuctionServer {
    private static final int PORT = 12345;
    private static final List<ClientHandler> clients = Collections.synchronizedList(new ArrayList<>());

    private static final List<String> goods = Arrays.asList("쿠", "건구스", "건덕이", "건붕이");
    private static final List<String> items = Arrays.asList("건구스의 지원금", "황소의 분노", "일감호의 기적", "스턴건");
    private static String currentItem;
    private static int currentBid = 0;
    private static ClientHandler highestBidder = null;

    public static void main(String[] args) throws IOException {
        ServerSocket listener = new ServerSocket(PORT);
        System.out.println("서버가 실행 중입니다");

//        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(AuctionServer::startNewAuctionRound, 0, 10, TimeUnit.SECONDS);

        try {
            while (true) {
                Socket clientSocket = listener.accept();

                // 클라이언트를 관리하는 핸들러 생성 및 추가
                ClientHandler clientHandler = new ClientHandler(clientSocket);
                synchronized (clients) {
                    clients.add(clientHandler);
                }

                // 새로운 스레드로 클라이언트 핸들러 실행
                new Thread(clientHandler).start();

                // 서버 초기 상태에서 첫 경매 라운드 시작 (테스트용으로 2명인경우 시작)
                if (clients.size() == 2 && currentItem == null) {
                    startNewAuctionRound();
                }

                // 서버 초기 상태에서 첫 경매 라운드 시작 (실제는 이걸써야함)
//                if (clients.size() == 4 && currentItem == null) {
//                    startNewAuctionRound();
//                }
            }
        } finally {
            listener.close();
        }
    }

    private static void startNewAuctionRound() {
        Random random = new Random();
        currentItem = random.nextInt(100) < 60
                ? goods.get(random.nextInt(goods.size()))
                : items.get(random.nextInt(items.size()));

        currentBid = 0;
        highestBidder = null;
        broadcastMessage("경매를 시작합니다. 경매품목: " + currentItem);
    }

    public static synchronized void placeBid(ClientHandler client, int bidAmount) {
        System.out.println("호가 요청: " + client.getClientName() + ", 금액: " + bidAmount);
        System.out.println("호가 전 잔액: " + client.getBalance());
        // 같은 사람이 두 번 연속 입찰할 수 없음
        if (client == highestBidder) {
            client.sendMessage("연속으로 입찰할 수 없습니다.");
            return;
        }

        // 잔액 확인
        if (client.getBalance() < bidAmount) {
            client.sendMessage("잔액 부족으로 호가 실패.");
            return;
        }

        // 잔액 차감 및 입찰 정보 갱신
        client.decreaseBalance(bidAmount);
        currentBid += bidAmount;
        highestBidder = client;

        broadcastMessage("현재입찰가: " + currentBid + "원 (+" + bidAmount + ")");
    }

    public static synchronized void broadcastMessage(String message) {
        synchronized (clients) {
            for (ClientHandler client : clients) {
                client.sendMessage(message);
            }
        }
    }

    public static void endAuctionRound() {
        if (highestBidder != null) {
            broadcastMessage("낙찰자: " + highestBidder.getClientName() + " - 금액: " + currentBid);
        } else {
            broadcastMessage("낙찰자 없음");
        }

        synchronized (clients) {
            for (ClientHandler client : clients) {
                if (!client.isParticipating()) {
                    client.addFunds(5);
                }
            }
        }

        startNewAuctionRound();
    }

    public static synchronized void removeClient(ClientHandler client) {
        synchronized (clients) {
            clients.remove(client);
            broadcastMessage(client.getClientName() + " 님이 연결을 종료했습니다.");
        }
    }
}
