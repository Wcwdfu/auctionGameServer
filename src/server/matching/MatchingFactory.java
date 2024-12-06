package server.matching;

import java.net.Socket;

public class MatchingFactory {

    public static final MatchingFactory INSTANCE = new MatchingFactory();
    private final MatchingQueue matchingQueue;

    private MatchingFactory(){
        matchingQueue=new MatchingQueue();
        //TODO 추후 삭제해야한다

    }

    public MatchingQueue getMatchingQueue(){
        return matchingQueue;
    }
}
