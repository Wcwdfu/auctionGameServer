package server;

import java.util.HashMap;

import static server.GameThread.*;

public class ItemManager {

    public static HashMap<String, Boolean> itemActivation = new HashMap<>();

    public ItemManager() {
        itemActivation.put("황소의 분노", false);
        itemActivation.put("일감호의 기적", false);
        itemActivation.put("스턴건", false);
    }


    public static void miracleActivation(User currentUser) {
        highestBidder = currentUser;
        int currentMircaleCount = currentUser.getItems().get("일감호의 기적");
        currentUser.getItems().replace("일감호의 기적", currentMircaleCount - 1); //일감호의 기적 아이템 갯수 하나 차감
        currentUser.sendMessage("일감호의 기적 사용 성공");
        ClientHandler.bidUsers_broadcastMessage("메인"+"임의의 유저가 일감호의 기적을 사용해서 강제 낙찰 받았습니다");
    }
}
