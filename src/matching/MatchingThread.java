package matching;

import io.Output;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class MatchingThread implements Runnable {

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private ScheduledFuture<?> messageTask;
    int count=5;

    public MatchingThread(Socket socket) {
        this.socket = socket;
        try {
            this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.out = new PrintWriter(socket.getOutputStream(), true);
        } catch (Exception e) {
            System.out.println("stream 생성중 에러 발생");
        }

    }

    @Override
    public void run() {
        MatchingFactory matchingFactory = MatchingFactory.INSTANCE;
        MatchingQueue matchingQueue = matchingFactory.getMatchingQueue();
        try {

            String clientName = in.readLine();
            MatchingUser matchingUser = new MatchingUser(clientName);
            matchingQueue.add(matchingUser);
            System.out.println("클라이언트 \"" + clientName + "\" 가 연결되었습니다: " + socket);

            Output.INSTANCE.broadcastMessage("Matching;" + matchingQueue);
            if (matchingQueue.getMatchingSize() == 4) {
                informMatching(out);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void informMatching(PrintWriter out) {

        // 3초 후 1초 간격으로 메시지를 보내는 작업을 시작

        messageTask = scheduler.scheduleAtFixedRate(() -> {
            if (count >= 0) {
                Output.INSTANCE.broadcastMessage("MatchingFinished;" + count);
                count--;
            } else {
                messageTask.cancel(false); // 반복 작업을 취소
                System.out.println("Stopping message task.");
            }
        }, 3, 1, TimeUnit.SECONDS);

    }
}
