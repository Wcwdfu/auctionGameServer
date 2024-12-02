package server.matching;

import java.net.Socket;
import server.User;

public class MatchingUser {

    private String name;

    private Socket socket;

    public MatchingUser(String name, Socket socket) {
        this.name = name;
        this.socket = socket;
    }

    public String getName() {
        return name;
    }

    public Socket getSocket() {
        return socket;
    }

}
