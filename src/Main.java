import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        // 农民牌
        List<Integer> farmer = new ArrayList<>();
        farmer.add(3);
        farmer.add(3);
        farmer.add(3);
        farmer.add(3);
        farmer.add(4);
        farmer.add(5);
        farmer.add(6);
        farmer.add(7);
        farmer.add(10);
        farmer.add(10);
        farmer.add(14);
        farmer.add(14);
        farmer.add(14);
        farmer.add(14);

        // 地主牌
        List<Integer> lord = new ArrayList<>();
        lord.add(9);
        lord.add(9);
        lord.add(9);
        lord.add(11);
        lord.add(11);
        lord.add(16);
        lord.add(17);

        Poker.printCards("农民手牌为：", farmer);
        Poker.printCards("地主手牌为：", lord);

        HashMap<String, Boolean> cache = new HashMap<>();
        boolean flag = Poker.hand_out(farmer, lord, null, cache);
        if (flag) {
            System.out.println("计算结果为：农民必胜。");
        } else {
            System.out.println("计算结果为：农民必输。");
        }
    }
}
