package server;

//import java.util.server.Timer;


public class TimerManager {

//    private server.Timer participating_timer;
//    private server.Timer participating_end_timer;
//    private server.Timer bidding_timer;
//    private server.Timer bidding_end_timer;

    private int p_time = 10;
    private int b_time = 5;
    private int ready = 5;

    private Timer timer;

    public Timer getTimer() {
        return timer;
    }

    TimerManager() {
        timer = new Timer(ready);
        timer.start();
        try {
            timer.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        ClientHandler.bidUsers_broadcastMessage("게임 시작");
    }


    public void participating() {
        timer = new Timer(p_time);
        timer.start();
        try {
            timer.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        server.ClientHandler.bidUsers_broadcastMessage("응찰 마감");
    }

    public void bidding() {
        timer = new Timer(b_time);
        timer.start();
        try {
            timer.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        server.ClientHandler.bidUsers_broadcastMessage("입찰 마감");
    }

    public void interrupt() {
        timer.interrupt();
        //이거 join때 불릴수도있으려나..
    }


//    private TimerTask participating_task = new TimerTask() {
//        @Override
//        public void run() {
//            if(p_time>6) {
//                server.ClientHandler.bidUsers_broadcastMessage(p_time+"초 남았습니다.");
//                p_time--;
//            }
//            else if(p_time==0) {
//                server.ClientHandler.bidUsers_broadcastMessage("응찰 마감");
//                p_time = 6;
//                participating_timer.cancel();
//            }
//        }
//    };

//    private TimerTask participating_end_task = new TimerTask() {
//        @Override
//        public void run() {
//            participating_timer.cancel();
//            p_time = 6;
//        }
//    };

//    private TimerTask bidding_task = new TimerTask() {
//        @Override
//        public void run() {
//            if(b_time>4) {
//                server.ClientHandler.bidUsers_broadcastMessage(b_time + "초 남았습니다.");
//                b_time--;
//            }
//            else if(b_time==0) {
//                server.ClientHandler.bidUsers_broadcastMessage("입찰 마감");
//                b_time = 4;
//                bidding_timer.cancel();
//            }
//        }
//    };

//    private TimerTask bidding_end_task = new TimerTask() {
//        @Override
//        public void run() {
//            bidding_timer.cancel();
//            b_time = 4;
//        }
//    };


    
//    public void participating2() {
//        participating_timer = new server.Timer();
////        participating_end_timer = new server.Timer();
//        participating_timer.schedule(participating_task, 0, 1000);
////        participating_end_timer.schedule(participating_end_task, 6500);
//    }
//
//    public void bidding2() {
//        bidding_timer = new server.Timer();
////        bidding_end_timer = new server.Timer();
//        bidding_timer.schedule(bidding_task, 0, 1000);
////        bidding_end_timer.schedule(bidding_end_task, 4500);
//    }
//
//    //clientHandler에서 경매 입찰이 새로 들어오면 매번 이 함수 호출해서 기존꺼 취소시키고 새로 시작하기
//    public void isBidding2() {
//        bidding_timer.cancel();
////        bidding_end_timer.cancel();
//        b_time = 4;
//
//        bidding_timer = new server.Timer();
////        bidding_end_timer = new server.Timer();
//        bidding_timer.schedule(bidding_task, 0, 1000);
////        bidding_end_timer.schedule(bidding_end_task, 4500);
//    }
}
