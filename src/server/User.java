package server;

import java.net.Socket;
import java.util.HashMap;


//접속한 server.User
public class User {
    private final Socket sock;
    private ClientHandler clientHandler;
    private String name;
    private boolean isPlaying;  //현재 게임 진행에 참여 중인지
    private boolean isParticipating; //경매 round 참여
    private boolean oneChance; //경매 응찰,불응찰 여부
    private HashMap<String, Integer> goods = new HashMap<String, Integer>();   //모든 굿즈들
    private HashMap<String, Integer> items = new HashMap<String, Integer>();   //모든 아이템들
    private int balance; //잔액
    private int subsidy=1;
    private boolean stungun; //스턴건 맞았는지


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
        //"쿠", "건구스", "건덕이", "건붕이"
        goods.put("쿠", 0);
        goods.put("건구스", 0);
        goods.put("건덕이", 0);
        goods.put("건붕이", 0);

        //"황소의 분노" ,"일감호의 기적", "스턴건"
        items.put("황소의 분노", 1);
        items.put("일감호의 기적", 0);
        items.put("스턴건", 0);
    }


    public boolean isPlaying() {
        return isPlaying;
    }

    public boolean isOneChance() {
        return oneChance;
    }

    public void setOneChance(boolean oneChance) { // Setter
        this.oneChance = oneChance;
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

    public void addGoods(String good) {
        goods.put(good, goods.get(good) + 1);
    }

    public HashMap<String, Integer> getItems() {
        return items;
    }

    public void addItem(String item) {
        items.put(item, items.get(item) + 1);
    }

    public String getName() {
        return name;
    }

    public int getBalance() {
        return balance;
    }

    public void subFunds(int amount) {
        balance -= amount;
    }
    public void addFunds(int amount) {
        balance += amount;
    }
    public void addSubsidy(int num){
        subsidy+=1;
    }
    public int getSubsidy(){
        return subsidy;
    }

    public void sendMessage(String message) {
        clientHandler.sendMessage(message);
    }

    public String getUserCommand() {
        return clientHandler.getUserCommand();
    }

    public void useItem(String itemName) {
            Integer integer = items.get(itemName);
        items.put(itemName,integer-1);
    }


    public boolean isStungun() {
        return stungun;
    }

    public void setStungun(boolean stungun) {
        this.stungun = stungun;
    }
}
