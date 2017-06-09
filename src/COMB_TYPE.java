/**
 * Author: 郑彬彬
 * Date: 2017-06-09 15:22
 * Email: zhengbinbin@58ganji.com
 */
// 定义牌类型枚举变量
enum COMB_TYPE {
    PASS,               // 过牌
    SINGLE,             // 单张
    PAIR,               // 对子
    TRIPLE_ZERO,        // 三带零
    TRIPLE_ONE,         // 三带一
    TRIPLE_TWO,         // 三带一对
    FOURTH_TWO_ONES,    // 四带二
    FOURTH_TWO_PAIRS,   // 四带两对
    STRIGHT,            // 顺子
    BOMB,               // 炸弹
    KING_PAIR           // 王炸
}
