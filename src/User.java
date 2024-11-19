import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


//접속한 User
public class User {
    private Socket sock;
    private ClientHandler clientHandler;
    private String name;
    private boolean isPlaying;  //현재 게임 진행에 참여 중인지
    private boolean isParticipating; //경매 round 참여
    private HashMap<String, Integer> goods = new HashMap<String, Integer>();   //게임 중에 모든 goods들
    private HashMap<String, Integer> items = new HashMap<String, Integer>();
    private int balance;  //잔액

    public User(Socket sock, boolean isPlaying) {
        this.sock = sock;
        this.isPlaying = isPlaying;
        balance = 100;
        goods.put("쿠", 0); //"쿠", "건구스", "건덕이", "건붕이"
        goods.put("건구스", 0);
        goods.put("건덕이", 0);
        goods.put("건붕이", 0);
    }

    public User(Socket sock, boolean isPlaying, String name, ClientHandler clientHandler) {
        this.sock = sock;
        this.isPlaying = isPlaying;
        this.name = name;
        balance = 100;
        this.clientHandler = clientHandler;
        goods.put("쿠", 0); //"쿠", "건구스", "건덕이", "건붕이"
        goods.put("건구스", 0);
        goods.put("건덕이", 0);
        goods.put("건붕이", 0);
    }


    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isParticipating() {
        return isParticipating;
    }

    public void setParticipating(boolean participating) {
        isParticipating = participating;
    }

    public HashMap<String, Integer> getGoods() {
        return goods;
    }

    public void setGoods(HashMap<String, Integer> goods) {
        this.goods = goods;
    }

    public void addGoods(String good) {
        goods.put(good, goods.get(good) + 1);
    }

    public  HashMap<String, Integer> getItems() {
        return items;
    }

    public void addItem(String item) {
        items.put(item, items.get(item) + 1);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getBalance() {
        return balance;
    }

    public void subFunds(int amount) {
        balance -= amount;
    }

    public void addFunds(int amount) {
        balance += amount;
        sendMessage("잔액 추가됨: " + amount + "원. 현재 잔액: " + balance + "원");
    }

    public void sendMessage(String message) {
        clientHandler.sendMessage(message);
    }

    public String getUserCommand() {
        return clientHandler.getUserCommand();
    }


}
