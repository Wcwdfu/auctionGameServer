public class WaitingThread extends Thread {

    public static GameThread gameThread;
    public static int gamePlayers_num = 2;

    public WaitingThread() {
        System.out.println("WaitingThread constructor");
        gameThread = null;
    }

    public void run() {
        while(true) {
//            System.out.println(AuctionServer.waitUsers.size() + "gameThread = " + gameThread);
            //대기방 인원 4명 이상 && 진행 중인 게임이 존재하지 않을 때

            for(int i = 0; i < gamePlayers_num; i++) {
                try {
                    User player = AuctionServer.waitUsers.take();  //꺼낼 게 없으면 block됨
                    player.setPlaying(true);
                    AuctionServer.bidUsers.put(player);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

            //게임 시작
            gameThread = new GameThread();
            gameThread.start();
            System.out.println("gameThread run");

            //게임이 끝날 때까지 기다림
            try {
                gameThread.join();
                System.out.println("gameThread join");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            gameThread = null;


//            if(AuctionServer.waitUsers.size() >= gamePlayers_num && gameThread == null) {  //size체크도 synchronized를 걸어야하나?.. add만 해서 딱히 걸리진 않을 것 같긴한데
//                System.out.println("waitUsers.size(): " + AuctionServer.waitUsers.size());
//                //게임 참여 User들 관리하는 부분은 UserInfoThread로 따로 빼서 관리해도 괜찮을듯..?(wait에서 삭제 & bid에 추가)
//                //대기방 인원 4명을 게임중 인원으로 변경
//                for(int i = 0; i < gamePlayers_num; i++) {
//                    User player;
//                    synchronized (AuctionServer.waitUsers) {
//                        player = AuctionServer.waitUsers.remove();
//                    }
//                    player.setPlaying(true);
//                    synchronized (AuctionServer.bidUsers) {
//                        AuctionServer.bidUsers.add(player);
//                    }
//                }
//
//                //게임 시작
//                gameThread = new GameThread();
//                gameThread.start();
//                System.out.println("gameThread run");
//
//                //게임이 끝날 때까지 기다림
//                try {
//                    gameThread.join();
//                    System.out.println("gameThread join");
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//                gameThread = null;
//
//            }
        }

    }

}
