public class TimerParticipating extends Thread {
    private int time;

    @Override
    public void run() {
        time = 6;

        while (time > 0) {
            ClientHandler.bidUsers_broadcastMessage(time + "초 남았습니다.");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            time--;
        }

        //time = 0되면 종료
    }
}
