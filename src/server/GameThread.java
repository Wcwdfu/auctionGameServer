package server;

import java.util.*;

import static server.ItemManager.itemActivation;

public class GameThread extends Thread {

    //경매품목 - 굿즈(쿠,건덕이,건구스,건붕이)
    private static final List<String> goods = Arrays.asList("쿠", "건구스", "건덕이", "건붕이");
    //경매품목 - 아이템(황소의 분노, 일감호의 기적, 스턴건)
    private static final List<String> items = Arrays.asList("황소의 분노", "일감호의 기적", "스턴건");
    //경매품목 - 아이템
    public static String auctionItem;
    public static User highestBidder;
    private HashMap<User, Integer> userBidPrices = new HashMap<>();
    private int currentBid = 0;
    private static boolean endGame;
    private boolean stageIsOngoing;
    //게임에 참여한 player들 배열
    private User[] players;

    //이번 경매 round에 참여한 player들
    private ArrayList<User> participatingUsers = new ArrayList<>();
    private TimerManager timerManager;


    public static boolean isEndGame() {
        return endGame;
    }

    public void setEndGame(boolean endGame) {
        this.endGame = endGame;
    }

    public static List<String> getGoods() {
        return goods;
    }

    public static List<String> getItems() {
        return items;
    }


    public int participatingNum() {
        return participatingUsers.size();
    }


    public GameThread() {
        endGame = false;
        players = AuctionServer.bidUsers.toArray(new User[AuctionServer.bidUsers.size()]);

        for(User user : players) {
            userBidPrices.put(user, 0);
        }
    }

    @Override
    public void run() {
        timerManager = new TimerManager();

//        ItemManager itemManager = new ItemManager();

        while (!endGame) {
//            ClientHandler.bidUsers_broadcastMessage("승리자는 kyu");
//            try {
//                sleep(30000);
//
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
            startNewAuctionRound();

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }

            boolean skip = checkParticipation();

            if (!skip) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                ClientHandler.bidUsers_broadcastMessage("경매를 시작합니다!");
                stageIsOngoing = true; //경매가 진행되는동안만 1,5원 호가 버튼 작동하게 하기 위함
                timerManager.bidding();

//                ///여기서 일감호의 기적 체크해야함
//                if (itemActivation.get("일감호의 기적")) {
////                    timerManager.interrupt();
//                    itemActivation.replace("황소의 분노", false);
//                    itemActivation.replace("일감호의 기적", false);
//                    itemActivation.replace("스턴건", false);
//
//                    ClientHandler.bidUsers_broadcastMessage("곧 다음 라운드가 시작됩니다...");
//                    try {
//                        Thread.sleep(2000);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
//                    continue;
//                }
//                ////////////////////////////////

                ClientHandler.bidUsers_broadcastMessage("입찰 마감");

                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            endGame = endAuctionRound();
            itemActivation.replace("황소의 분노", false);
            itemActivation.replace("일감호의 기적", false);
            itemActivation.replace("스턴건", false);
            try {
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }

    // 위 run을 깔끔하게 정리해보려 했으나 버그이슈로 주석처리
//    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

//    public void run() {
//        timerManager = new TimerManager();
//
//        scheduler.schedule(this::startNewAuctionRound, 0, TimeUnit.SECONDS);
//        scheduler.schedule(this::checkParticipation, 3, TimeUnit.SECONDS);
//        scheduler.schedule(() -> {
//            ClientHandler.bidUsers_broadcastMessage("경매를 시작합니다!");
//            stageIsOngoing = true; // 경매가 진행되는 동안만 1,5원 호가 버튼 작동
//            timerManager.bidding();
//            ClientHandler.bidUsers_broadcastMessage("입찰 마감");
//        }, 5, TimeUnit.SECONDS);
//        scheduler.schedule(() -> {
//            endGame = endAuctionRound();
//            if (endGame) {
//                scheduler.shutdown();
//                ClientHandler.bidUsers_broadcastMessage("게임 종료");
//            }
//        }, 10, TimeUnit.SECONDS);
//    }


    //주기적으로 새로운 경매품목을 랜덤으로 선택하고 클라이언트에게 알림
    private void startNewAuctionRound() {
        Random random = new Random();

        // 60% 확률로 굿즈, 40% 확률로 아이템 선택
        if (random.nextInt(100) < 60) {
            // 굿즈 선택: 4개 중 하나를 동일한 확률로 선택
            auctionItem = goods.get(random.nextInt(goods.size()));
        } else {
            // 아이템 선택(지원금10%, 외 나머지3개 30%동일)
            int itemChance = random.nextInt(100);
            if (itemChance < 15) {
                auctionItem = "건구스의 지원금"; // 15% 확률
            } else if (itemChance < 40) {
                auctionItem = "황소의 분노"; // 25% 확률
            } else if (itemChance < 70) {
                auctionItem = "일감호의 기적"; // 30% 확률
            } else {
                auctionItem = "스턴건"; // 30% 확률
            }
        }

//        auctionItem = "스턴건";

        initializeBidPrice();
        ClientHandler.bidUsers_broadcastMessage("경매를 시작합니다. 경매품목: " + auctionItem);
    }

    private void initializeBidPrice() {
        for(User user : players) {
            userBidPrices.replace(user, 0);
        }

        currentBid = 0;
        highestBidder = null;
    }

    private void calculateHighestBidder() {
        int max = 0;
        for(Map.Entry<User, Integer> entry : userBidPrices.entrySet()) {
            if(max < entry.getValue()) {
                highestBidder = entry.getKey();
                max = entry.getValue();
            }
        }
        currentBid = max;
        if(currentBid == 0)
            highestBidder = null;
    }


    private boolean checkParticipation() {
        ClientHandler.bidUsers_broadcastMessage("응찰하시겠습니까?");
        for (User player : players) {
            player.setOneChance(true);
        }

        timerManager.participating();  //10초 동안 응찰받기
        ClientHandler.bidUsers_broadcastMessage("응찰 마감");

        for (User player : players) {

            if (player.isParticipating()) {  //이번 round 참여
                participatingUsers.add(player);
                player.setParticipating(true);
                ClientHandler.bidUsers_broadcastMessage("참여명단" + player.getName() + " 님이 경매에 참여했습니다");
            } else {
                player.sendMessage("경매에 불참합니다");
                player.sendMessage("게임이 끝날때까지 대기합니다");
            }
        }

        if (participatingUsers.isEmpty()) {
            ClientHandler.bidUsers_broadcastMessage("경매 참여자가 없습니다.");
            return true;
        } else if (participatingUsers.size() == 1) {
            ClientHandler.bidUsers_broadcastMessage("경매 참여자가 한 명입니다.");
            highestBidder = participatingUsers.get(0);
            return true;
        }

        return false;
    }


    //입찰 최고가 갱신
    public synchronized void placeBid(User player, int bidAmount) {

        if (stageIsOngoing) {
            if (player == highestBidder) {  //현재 자신이 최고 입찰자라면 여러번 갱신되면 안됨
                return;
            }

            int tempBid = currentBid + bidAmount;  //player가 입찰하므로써 갱신되는 최고입찰가 임시 생성

            if (player.isParticipating() && player.getBalance() >= tempBid) { //해당 player가 그만큼의 돈을 가지고 있는지
                interruptTimer();
                userBidPrices.replace(player, tempBid);
                calculateHighestBidder();
//                currentBid = tempBid;
//                highestBidder = player;
                ClientHandler.bidUsers_broadcastMessage("현재입찰가: " + currentBid + "원 " + "[+" + bidAmount + "]");
                player.sendMessage("소지금" + player.getBalance());

                ClientHandler.bidUsers_broadcastMessage("호가효과음");
            } else if (player.isParticipating()) {  //잔액 부족
                player.sendMessage("잔액이 부족합니다!");
            } else {
                player.sendMessage("경매에 불응찰한 상태입니다.");
            }
        }
    }

    private synchronized void interruptTimer() {
        timerManager.interrupt();
    }


    // 한 라운드의 종료
    public boolean endAuctionRound() {
        stageIsOngoing = false;
        System.out.println("낙찰자와 승리자를 판정합니다.");
        ClientHandler.bidUsers_broadcastMessage("이번 라운드가 종료되었습니다.");

        if (itemActivation.get("황소의 분노")) { // 황소의 분노를 사용한 경우 강제 유찰
            ClientHandler.bidUsers_broadcastMessage("메인" + "누군가 황소의 분노를 사용하여 이번 라운드는 강제유찰 되었습니다.");
            //돈을 냈다면 회수
            if (highestBidder != null) {
                highestBidder.subFunds(currentBid);
            }
        }

        else if (itemActivation.get("일감호의 기적")) {
            if (goods.contains(auctionItem)) {  //낙찰 물건이 굿즈
                highestBidder.addGoods(auctionItem);
            } else if (items.contains(auctionItem)) {  //낙찰 물건이 아이템
                highestBidder.addItem(auctionItem);
            }

        } else if (highestBidder != null) {
            highestBidder.subFunds(currentBid);
            ClientHandler.bi_broadcastMessage(highestBidder.getName());

            if (goods.contains(auctionItem)) {  //낙찰 물건이 굿즈
                highestBidder.addGoods(auctionItem);
            } else if (items.contains(auctionItem)) {  //낙찰 물건이 아이템
                highestBidder.addItem(auctionItem);
            } else {
                highestBidder.addSubsidy(1);
            }
        } else {
            ClientHandler.bidUsers_broadcastMessage("메인" + "이번 라운드는 유찰되었습니다.");
        }

        // 라운드 끝마다 플레이어들의 소지금계산, 소지금, 소지품목 정보 클라이언트로 전송
        for (User player : players) {
            //경매 불응찰시 5원 주기(스턴건으로인한 불응찰은 제외)
            if (!player.isParticipating() && !player.isStungun()) {
                player.addFunds(5); //
            }
            //지원금은 경매 참불 여부 상관없이 제공
            player.addFunds(5 * player.getSubsidy());

            player.setParticipating(false); //경매 round 참여 불참
            player.setStungun(false);
            participatingUsers.remove(player);
            player.sendMessage("소지금" + player.getBalance());

            // 모든 소유품들의 정보 전송
            StringBuilder allItemsMessage = new StringBuilder("소유 물품: ");
            // 굿즈 정보 포함
            for (Map.Entry<String, Integer> entry : player.getGoods().entrySet()) {
                allItemsMessage.append(entry.getKey()).append(" x").append(entry.getValue()).append(", ");
            }
            // 아이템 리스트도 포함
            for (Map.Entry<String, Integer> entry : player.getItems().entrySet()) {
                allItemsMessage.append(entry.getKey()).append(" x").append(entry.getValue()).append(", ");
            }

            player.sendMessage(allItemsMessage.toString());
        }

        if (checkWinner()) {
            System.out.println("게임 종료");
            ClientHandler.bidUsers_broadcastMessage("게임 종료");
            return true;  //endGame = true
        }

        return false; //endGame = false

    }

    private boolean checkWinner() {
        for (User player : players) {
            HashMap<String, Integer> goods = player.getGoods();
            System.out.print(player.getName() + ": ");
            for (Map.Entry<String, Integer> entry : goods.entrySet()) {
                System.out.print(entry.getKey() + "(" + entry.getValue() + "개)");
            }
            System.out.println();
            if (allElementAreDifferent(goods) || allElementAreSame(goods)) {
                System.out.println("승리자는 " + player.getName());
                ClientHandler.bidUsers_broadcastMessage("메인"+"승리자는 " + player.getName()+"님 입니다. 축하합니다!");
                return true;
            }
        }
        return false;
    }


    //건덕이 건구스 건붕이 건구스 건붕이 건국스 쿠 쿠 쿠
    private boolean allElementAreDifferent(HashMap<String, Integer> goods) {

        for (Integer value : goods.values()) {
            if (value == 0) {
                return false;
            }
        }
        //전부 다 1개 이상 존재
        return true;
    }

    //승리조건 판정 중 같은굿즈 조건판정
    private boolean allElementAreSame(HashMap<String, Integer> goods) {

        for (Integer value : goods.values()) {
            if (value >= 3) {
                return true;
            }
        }
        return false;
    }

    //황소의 분노 사용함수
    public void useAnger(String message) {
        String[] strings = message.split(";");
        String itemName = strings[1];
        String userName = strings[2];
        for (User player : players) {
            if (player.getName().equals(userName)) {
                player.useItem(itemName);
            }
        }
        itemActivation.replace("황소의 분노", true);
    }

    public void useStunGun(String targetUser, User currentUser) {
        currentUser.useItem("스턴건");

        for(User user : AuctionServer.bidUsers) {
            if(user.getName().equals(targetUser)) {
                user.setOneChance(false);
                user.setParticipating(false);
                user.setStungun(true);
                user.sendMessage("스턴건에 맞았습니다. 강제로 불응찰 상태로 전환됩니다.");
                ClientHandler.bidUsers_broadcastMessage("participatingListUpdate " + targetUser);
                ClientHandler.bidUsers_broadcastMessage("메인"+targetUser+"님이 스턴건을 맞아 강제불응찰 상태로 변경됩니다.");
                participatingUsers.remove(user);

                userBidPrices.replace(user, 0);
                calculateHighestBidder();

                if(participatingUsers.size() == 1) {
                    timerManager.interrupt();
                    highestBidder = participatingUsers.get(0);
                    currentBid = 0;
                } else if (participatingUsers.size() == 0) {
                    timerManager.interrupt();
                }
                break;
            }
        }
    }
}
