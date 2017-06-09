import java.util.*;

/**
 * Author: bing
 * Date: 2017-05-28 23:02
 * Email: dhuzbb@163.com、
 * 参考博客 http://wuzhiwei.net/doudizhu_solver/?utm_source=tuicool&utm_medium=referral
 * 将其Python代码翻译为Java代码如下
 */

class Poker {

    // 是否允许三带一
    private static final boolean ALLOW_THREE_ONE = true;
    // 是否允许三带一对
    private static final boolean ALLOW_THREE_TWO = false;
    // 是否允许四带二
    private static final boolean ALLOW_FOURTH_TWO_ONES = true;
    // 是否允许四带两对
    private static final boolean ALLOW_FOURTH_TWO_PAIRS = false;

    /**
     * 模拟每次出牌
     *
     * @param my_cards    当前我的牌
     * @param enemy_cards 对手的牌
     * @param last_hand   上一手的手牌
     * @param cache       缓存
     * @return 我是否必赢
     */
    static boolean hand_out(List<Integer> my_cards,
                            List<Integer> enemy_cards,
                            CardType last_hand,
                            HashMap<String, Boolean> cache) {
        if (my_cards == null || my_cards.size() == 0) {
            return true;
        }
        if (enemy_cards == null || enemy_cards.size() == 0) {
            return false;
        }
        // 构建缓存的Key，查看缓存中是否存在当前这种情况
        // 如果存在，则直接返回
        String key = getKey(my_cards, enemy_cards, last_hand);
        if (cache.containsKey(key)) {
            return cache.get(key);
        }

        // 获取我可以出的所有手牌
        List<CardType> all_hands = getAllCardTypeFromCards(my_cards);

        for (CardType hand : all_hands) {
            if (last_hand != null && can_hand2_beat_hand1(last_hand, hand) ||
                    last_hand == null && !hand.getType().equals(COMB_TYPE.PASS)) {
                if (!hand_out(enemy_cards, make_hand(my_cards, hand), hand, cache)) {
                    cache.put(key, true);
                    return true;
                }
            } else if (last_hand != null && hand.getType().equals(COMB_TYPE.PASS)) {
                if (!hand_out(enemy_cards, my_cards, null, cache)) {
                    cache.put(key, true);
                    return true;
                }
            }
        }
        cache.put(key, false);
        return false;
    }

    // 根据牌，获取此副牌所有可能的牌型
    // 牌型数据结构为：牌类型，主牌，副牌
    private static List<CardType> getAllCardTypeFromCards(List<Integer> cards) {
        if (cards == null || cards.size() == 0) {
            return null;
        }

        List<CardType> result = new ArrayList<>();

        // 默认包含让牌
        result.add(new CardType(COMB_TYPE.PASS));

        // 获取每个牌和其对应的数量
        Map<Integer, Integer> cardNum = getCardAndNum(cards);

        for (Integer first : cardNum.keySet()) {
            Integer num = cardNum.get(first);

            if (num >= 1) {
                // 单张
                result.add(new CardType(COMB_TYPE.SINGLE, first));
            }

            if (num >= 2) {
                // 一对
                result.add(new CardType(COMB_TYPE.PAIR, first));
            }

            if (num >= 3) {
                // 三带零
                result.add(new CardType(COMB_TYPE.TRIPLE_ZERO, first));

                // 三带一
                if (ALLOW_THREE_ONE) {
                    for (Integer second : cardNum.keySet()) {
                        if (!first.equals(second)) {
                            result.add(new CardType(COMB_TYPE.TRIPLE_ONE, first, second));
                        }
                    }
                }

                // 三带一对
                if (ALLOW_THREE_TWO) {
                    for (Integer second : cardNum.keySet()) {
                        if (cardNum.get(second) > 2 && !first.equals(second)) {
                            result.add(new CardType(COMB_TYPE.TRIPLE_TWO, first, second));
                        }
                    }
                }
            }

            if (num == 4) {
                // 炸弹
                result.add(new CardType(COMB_TYPE.BOMB, first));

                // 四带二
                if (ALLOW_FOURTH_TWO_ONES) {
                    for (Integer second : cardNum.keySet()) {
                        for (Integer third : cardNum.keySet()) {
                            // 四带两张单只
                            if (!first.equals(second) && !first.equals(third) && second < third) {
                                result.add(new CardType(COMB_TYPE.FOURTH_TWO_ONES, first, second, third));
                            }
                            // 四带一对
                            if (!first.equals(second) && second.equals(third) && cardNum.get(second) >= 2) {
                                result.add(new CardType(COMB_TYPE.FOURTH_TWO_ONES, first, second, third));
                            }
                        }
                    }
                }

                // 四带两对
                if (ALLOW_FOURTH_TWO_PAIRS) {
                    for (Integer second : cardNum.keySet()) {
                        for (Integer third : cardNum.keySet()) {
                            if (!first.equals(second)
                                    && !first.equals(third)
                                    && cardNum.get(second) >= 2
                                    && cardNum.get(third) >= 2
                                    && second <= third) {
                                result.add(new CardType(COMB_TYPE.FOURTH_TWO_PAIRS, first, second, third));
                            }
                        }
                    }
                }
            }
        }

        // 王炸
        if (cards.contains(16) && cards.contains(17)) {
            result.add(new CardType(COMB_TYPE.KING_PAIR));
        }

        // 顺子
        for (int start = 3; start <= 10; start++) {
            for (int end = 7; end <= 14; end++) {
                if (end - start >= 4) {
                    // 构造从start到end的顺子
                    List<Integer> stright = new ArrayList<>();
                    for (int item = start; item <= end; item++) {
                        stright.add(item);
                    }
                    if (cards.containsAll(stright)) {
                        result.add(new CardType(COMB_TYPE.STRIGHT, end - start + 1, start));
                    }
                }
            }
        }
        return result;
    }

    // 给定牌cards，求打出手牌hand后的牌
    private static List<Integer> make_hand(List<Integer> cards, CardType hand) {
        List<Integer> cards_clone = new ArrayList<>(cards);
        switch (hand.getType()) {
            case SINGLE:             // 单张
                cards_clone.remove(hand.getFirst());
                break;
            case PAIR:               // 对子
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getFirst());
                break;
            case TRIPLE_ZERO:        // 三带零
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getFirst());
                break;
            case TRIPLE_ONE:         // 三带一
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getSecond());
                break;
            case TRIPLE_TWO:         // 三带一对
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getSecond());
                cards_clone.remove(hand.getSecond());
                break;
            case FOURTH_TWO_ONES:    // 四带二
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getSecond());
                cards_clone.remove(hand.getThird());
                break;
            case FOURTH_TWO_PAIRS:   // 四带两对
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getSecond());
                cards_clone.remove(hand.getSecond());
                cards_clone.remove(hand.getThird());
                cards_clone.remove(hand.getThird());
                break;
            case STRIGHT:            // 顺子
                for (int i = hand.getSecond(); i <= hand.getFirst() + hand.getSecond() - 1; i++) {
                    cards_clone.remove(new Integer(i));
                }
                break;
            case BOMB:               // 炸弹
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getFirst());
                cards_clone.remove(hand.getFirst());
                break;
            case KING_PAIR:          // 王炸
                cards_clone.remove(new Integer(16));
                cards_clone.remove(new Integer(17));
                break;
            default:
                break;
        }
        return cards_clone;
    }

    // hand1先出，问后出的hand2是否能打过hand1
    private static boolean can_hand2_beat_hand1(CardType hand1, CardType hand2) {
        if (hand2.getType() == COMB_TYPE.PASS) {
            return false;
        }
        if (hand1 == null || hand1.getType() == COMB_TYPE.PASS) {
            return true;
        }
        if (hand1.getType() == hand2.getType()) {
            if (hand1.getType() == COMB_TYPE.STRIGHT) {
                return hand1.getFirst().equals(hand2.getFirst()) && hand2.getSecond() > hand1.getSecond();
            }
            return hand2.getFirst() > hand1.getFirst();
        } else if (hand2.getType() == COMB_TYPE.BOMB || hand2.getType() == COMB_TYPE.KING_PAIR) {
            return hand2.getType().compareTo(hand1.getType()) > 0;
        }
        return false;
    }

    // 从牌中计算每个牌和其对应的数量
    private static Map<Integer, Integer> getCardAndNum(List<Integer> cards) {
        Map<Integer, Integer> cardNum = new LinkedHashMap<>();
        for (Integer card : cards) {
            if (cardNum.containsKey(card)) {
                cardNum.put(card, cardNum.get(card) + 1);
            } else {
                cardNum.put(card, 1);
            }
        }
        return cardNum;
    }

    // 根据my_cards、enemy_cards、last_hand计算得到Key
    private static String getKey(List<Integer> my_cards, List<Integer> enemy_cards, CardType last_hand) {
        StringBuilder my_str = new StringBuilder();
        for (Integer my_card : my_cards) {
            my_str.append(my_card.toString());
        }
        StringBuilder enemy_str = new StringBuilder();
        for (Integer enemy_card : enemy_cards) {
            enemy_str.append(enemy_card);
        }
        String last_str;
        if (last_hand == null) {
            last_str = "null";
        } else {
            last_str = (last_hand.getFirst() != null ? last_hand.getFirst().toString() : "null") +
                    (last_hand.getSecond() != null ? last_hand.getSecond().toString() : "null") +
                    (last_hand.getThird() != null ? last_hand.getThird().toString() : "null");
        }
        return my_str + enemy_str.toString() + last_str;
    }

    // 输出所有的手牌
    static void printCards(String prefix, List<Integer> cards) {
        System.out.print(prefix);
        for (Integer card : cards) {
            System.out.print(card + " ");
        }
        System.out.println();
    }
}
