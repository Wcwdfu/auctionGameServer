package server.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentLinkedQueue;

public class MatchingQueue {

    private final List<MatchingUser> matchingUserQueue= new ArrayList<>();

    public MatchingQueue() {
    }

    public void add(MatchingUser matchingUser){
        matchingUserQueue.add(matchingUser);
    }

    public int getMatchingSize(){
        return matchingUserQueue.size();
    }

    public List<MatchingUser>getMatchingUsers(){
        return matchingUserQueue;
    }

    @Override
    public String toString(){
        StringJoiner stringJoiner = new StringJoiner(",");
        matchingUserQueue.forEach(matchingUser -> stringJoiner.add(matchingUser.getName()));
        return stringJoiner.toString();
    }



}
