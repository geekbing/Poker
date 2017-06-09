/**
 * Author: bing
 * Date: 2017-05-28 23:26
 * Email: dhuzbb@163.com
 */

// 定义打出的每一手手牌，包括让牌
class CardType {
    private COMB_TYPE type;
    private Integer first;
    private Integer second;
    private Integer third;

    CardType(COMB_TYPE type) {
        this.type = type;
    }

    CardType(COMB_TYPE type, Integer first) {
        this.type = type;
        this.first = first;
    }

    CardType(COMB_TYPE type, Integer first, Integer second) {
        this.type = type;
        this.first = first;
        this.second = second;
    }

    CardType(COMB_TYPE type, Integer first, Integer second, Integer third) {
        this.type = type;
        this.first = first;
        this.second = second;
        this.third = third;
    }

    COMB_TYPE getType() {
        return type;
    }

    Integer getFirst() {
        return first;
    }

    Integer getSecond() {
        return second;
    }

    Integer getThird() {
        return third;
    }
}
