import java.util.*;

public class GameThread extends Thread {

    //경매품목 - 굿즈(쿠,건덕이,건구스,건붕이)
    private final List<String> goods = Arrays.asList("쿠", "건구스", "건덕이", "건붕이");
    //경매품목 - 아이템(건구스의지원금, 황소의분노, 일감호의기적, 스턴건)
    private final List<String> items = Arrays.asList("건구스의 지원금", "황소의 분노", "일감호의 기적", "스턴건");
    //경매품목 - 아이템
    private static String currentItem;
    private User highestBidder;
    private int currentBid = 0;
    private boolean endGame;
    //게임에 참여한 player들 배열
    private User[] players;
    //이번 경매 round에 참여한 player들
    private ArrayList<User> participatingUsers = new ArrayList<>();
    private TimerManager timerManager;


    public GameThread() {
        endGame = false;
        synchronized (AuctionServer.bidUsers) {  //솔직히 동기화 블럭 없어도 될 것 같음
            players = AuctionServer.bidUsers.toArray(new User[AuctionServer.bidUsers.size()]);
        }
    }

    public void run() {
        timerManager = new TimerManager();

        while(!endGame) {
            startNewAuctionRound();

            checkParticipation();

            timerManager.bidding();
            ClientHandler.bidUsers_broadcastMessage("입찰 마감");

            endGame = endAuctionRound();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    //주기적으로 새로운 경매품목을 랜덤으로 선택하고 클라이언트에게 알림
    private void startNewAuctionRound() {
        Random random = new Random();

        // 60% 확률로 굿즈, 40% 확률로 아이템 선택
        if (random.nextInt(100) < 60) {
            // 굿즈 선택: 4개 중 하나를 동일한 확률로 선택
            currentItem = goods.get(random.nextInt(goods.size()));
        } else {
            // 아이템 선택(지원금10%, 외 나머지3개 30%동일)
            int itemChance = random.nextInt(100);
            if (itemChance < 10) {
                currentItem = "건구스의 지원금"; // 10% 확률
            } else if (itemChance < 40) {
                currentItem = "황소의 분노"; // 30% 확률
            } else if (itemChance < 70) {
                currentItem = "일감호의 기적"; // 30% 확률
            } else {
                currentItem = "스턴건"; // 30% 확률
            }
        }

        currentBid = 0;
        highestBidder = null;
        ClientHandler.bidUsers_broadcastMessage("경매를 시작합니다. 경매품목: " + currentItem);
    }


    private void checkParticipation() {
        ClientHandler.bidUsers_broadcastMessage("응찰하시겠습니까?");

        timerManager.participating();  //6초 동안 응찰받기
        ClientHandler.bidUsers_broadcastMessage("응찰 마감");

        for(User player : players) {

            if(player.isParticipating()){  //이번 round 참여
                participatingUsers.add(player);
                ClientHandler.bidUsers_broadcastMessage(player.getName() + " 님이 경매에 참여했습니다");
            } else {
                player.sendMessage("경매에 불참합니다");
                player.sendMessage("게임이 끝날때까지 대기합니다");
            }
        }
    }


    //입찰 최고가 갱신
    public synchronized void placeBid(User player, int bidAmount) {
        if(player == highestBidder) {  //현재 자신이 최고 입찰자라면 여러번 갱신되면 안됨
            player.sendMessage("연속으로 입찰할 수 없습니다.");
            return;
        }

        int tempBid = currentBid + bidAmount;  //player가 입찰하므로써 갱신되는 최고입찰가 임시 생성

        if (player.getBalance() >= tempBid) { //해당 player가 그만큼의 돈을 가지고 있는지
            interruptTimer();
            currentBid = tempBid;
            highestBidder = player;
            ClientHandler.bidUsers_broadcastMessage("새로운 최고 입찰: " + currentBid + "원");
        }
        else {  //잔액 부족
            player.sendMessage("잔액이 부족합니다!");
        }
    }

    private synchronized void interruptTimer() {
        timerManager.interrupt();
    }


    public boolean endAuctionRound() {
        System.out.println("낙찰자와 승리자를 판정합니다.");
        ClientHandler.bidUsers_broadcastMessage("낙찰자와 승리자를 판정합니다.");

        if (highestBidder != null) {
            highestBidder.subFunds(currentBid);
            ClientHandler.bidUsers_broadcastMessage("낙찰자: " + highestBidder.getName() + " - 금액: " + currentBid);

            if(goods.contains(currentItem)) {  //낙찰 물건이 굿즈
                highestBidder.addGoods(currentItem);
            }
//            else if(items.contains(currentItem)) {  //낙찰 물건이 아이템인데 아직 서버에서 관리할지 안정해서 주석처리
//                highestBidder.addItem(currentItem);
//            }
        } else {
            ClientHandler.bidUsers_broadcastMessage("낙찰자 없음");
        }

        for (User player : players) {
            if (!player.isParticipating()) {
                player.addFunds(5);
            }

            player.setParticipating(false); //경매 round 참여 불참
            participatingUsers.remove(player);
        }

        if(checkWinner()) {
            System.out.println("게임 종료");
            ClientHandler.bidUsers_broadcastMessage("게임 종료");
            return true;  //endGame = true
        }

        return false; //endGame = false
    }

    private boolean checkWinner() {
        for(User player : players) {
            HashMap<String, Integer> goods = player.getGoods();
            System.out.print(player.getName() + ": ");
            for(Map.Entry<String, Integer> entry : goods.entrySet()) {
                System.out.print(entry.getKey() + "(" + entry.getValue() + "개)");
            }
            System.out.println();
            if (allElementAreDifferent(goods) || allElementAreSame(goods)) {
                System.out.println("승리자는 " + player.getName());
                ClientHandler.bidUsers_broadcastMessage("승리자는 " + player.getName());
                return true;
            }
        }
        return false;
    }


    //건덕이 건구스 건붕이 건구스 건붕이 건국스 쿠 쿠 쿠
    private boolean allElementAreDifferent(HashMap<String, Integer> goods) {

        for(Integer value : goods.values()) {
            if(value == 0) {
                return false;
            }
        }
        //전부 다 1개 이상 존재
        return true;
    }

    //승리조건 판정 중 같은굿즈 조건판정
    private boolean allElementAreSame(HashMap<String, Integer> goods) {

        for(Integer value : goods.values()) {
            if(value >= 3) {
                return true;
            }
        }
        return false;
    }
}