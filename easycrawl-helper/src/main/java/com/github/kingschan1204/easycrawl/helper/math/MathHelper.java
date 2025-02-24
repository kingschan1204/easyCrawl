package com.github.kingschan1204.easycrawl.helper.math;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author kingschan
 * 2023-5-24
 */
public class MathHelper {

    private BigDecimal value;
    //整数、负数、小数、科学计数
    private static final String pattern = "^[-+]?[0-9]*\\.?[0-9]+([eE][-+]?[0-9]+)?$";

    public MathHelper(BigDecimal value) {
        this.value = value;
    }

    public static MathHelper of(String text) {
        return new MathHelper(toBigDecimal(text));
    }
    public static MathHelper of(BigDecimal value) {
        return new MathHelper(value);
    }

    static BigDecimal toBigDecimal(String text) {
        assert null != text;
        assert text.matches(pattern);
        return new BigDecimal(text);
    }

    /**
     * 相加
     *
     * @param val 要加的值
     * @return this
     */
    public MathHelper add(String val) {
        this.value = this.value.add(toBigDecimal(val));
        return this;
    }

    /**
     * 相减
     *
     * @param val 要减的值
     * @return this
     */
    public MathHelper subtract(String val) {
        this.value = this.value.subtract(toBigDecimal(val));
        return this;
    }

    /**
     * 相乘
     *
     * @param val 值
     * @return this
     */
    public MathHelper multiply(String val) {
        this.value = this.value.multiply(toBigDecimal(val));
        return this;
    }

    /**
     * 除
     *
     * @param val 要除的值
     * @return this
     */
    public MathHelper divide(String val) {
        this.value = this.value.divide(toBigDecimal(val));
        return this;
    }

    /**
     * 保留几位小数
     *
     * @param v            小数位
     * @param roundingMode 浮动模式
     * @return this
     * UP: 任何非零的小数部分都会向上舍入 ( 2.3 -> 3 )。
     * DOWN: 丢弃小数部分，去掉小数而不进行进位,不管小数部分是多少 ( 2.9 -> 2 )
     * CEILING : 对于正数，行为与 UP 类似；对于负数，行为与 DOWN 类似。即正数向上舍入，负数向下舍入（向零方向）。2.3 -> 3   -2.3 -> -2
     * FLOOR : 对于正数，行为与 DOWN 类似；对于负数，行为与 UP 类似。即正数向下舍入，负数向上舍入。 2.9 -> 2   -2.9 -> -3
     * HALF_UP : 四舍五入的常见方式 如果小数部分大于或等于0.5，则向上舍入；否则向下舍入 2.5 -> 3   2.49 -> 2   -2.5 -> -3
     * HALF_DOWN : 如果小数部分大于0.5，则向上舍入；如果等于或小于0.5，则向下舍入。2.5 -> 2  2.51 -> 3 -2.5 -> -2
     * HALF_EVEN : 银行家舍入规则，四舍六入五留双 当小数部分为0.5时，判断前一位数字的奇偶性。奇数向上舍入，偶数向下舍入；其他情况与 HALF_UP 类似。
     * 1.5 -> 2（1是奇数，向上舍入）
     * 2.5 -> 2（2是偶数，向下舍入）
     * 2.51 -> 3
     * UNNECESSARY : 不进行舍入，如果结果无法精确表示，将抛出异常。只适用于精确计算的情况，要求结果必须是精确的。
     * 总结
     * 向上舍入：UP, CEILING, HALF_UP
     * 向下舍入：DOWN, FLOOR, HALF_DOWN
     * 四舍五入变体：HALF_UP, HALF_DOWN, HALF_EVEN
     * 严格要求精确：UNNECESSARY
     */
    public MathHelper scale(int v, RoundingMode roundingMode) {
        this.value = this.value.setScale(v, roundingMode);
        return this;
    }

    public MathHelper scale(int v) {
        this.value = this.value.setScale(v, RoundingMode.DOWN);
        return this;
    }

    public double doubleValue() {
        return this.value.doubleValue();
    }

    public String stringValue() {
        return this.value.toPlainString();
    }

    public String pretty() {
        final BigDecimal YI = new BigDecimal("100000000");
        if (this.value.compareTo(YI) >= 0) {
            // 如果大于1亿，除以1亿，并保留两位小数
            BigDecimal result = value.divide(YI, 2, RoundingMode.HALF_UP);
            return result + "亿";
        }
        final BigDecimal WAN = new BigDecimal("10000");
        if (this.value.compareTo(WAN) >= 0) {
            // 如果大于1万，除以1万，并保留两位小数
            BigDecimal result = value.divide(WAN, 2, RoundingMode.HALF_UP);
            return result + "万";
        }
        return scale(2,RoundingMode.HALF_UP).stringValue();

    }

    public static void main(String[] args) {
        String[] values = {"1.66", "22424.8", "-142", "-212.22222222222222", "1.99714E13"};
        for (String val : values) {
            System.out.println(MathHelper.of(val).scale(1).add("10").stringValue());
        }
    }
}
