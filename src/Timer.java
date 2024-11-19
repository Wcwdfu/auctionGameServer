public class Timer extends Thread {
    private int time;


    public Timer(int time) {
        this.time = time;
    }

    @Override
    public void run() {
        int timeInitial = time;

        while (time > 0) {
            ClientHandler.bidUsers_broadcastMessage(time + "초 남았습니다.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                time = timeInitial;
                continue;
            }
            time--;
        }

        //time = 0되면 종료
    }
}
