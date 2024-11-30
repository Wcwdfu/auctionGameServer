package server.matching;

import java.net.Socket;

public class MatchingFactory {

    public static final MatchingFactory INSTANCE = new MatchingFactory();
    private final MatchingQueue matchingQueue;

    private MatchingFactory(){
        matchingQueue=new MatchingQueue();
        //TODO 추후 삭제해야한다
//        MatchingUser matchingUser1 = new MatchingUser("유저1",new Socket());
//        MatchingUser matchingUser2 = new MatchingUser("유저2",new Socket());
//        matchingQueue.add(matchingUser1);
//        matchingQueue.add(matchingUser2);
    }

    public MatchingQueue getMatchingQueue(){
        return matchingQueue;
    }
}
