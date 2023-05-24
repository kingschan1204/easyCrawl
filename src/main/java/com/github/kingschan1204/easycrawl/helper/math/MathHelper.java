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
     * CEILING: 向正无穷方向舍入。
     * FLOOR: 向负无穷方向舍入。
     * UP: 远离零方向舍入（正数舍入为正无穷，负数舍入为负无穷）。
     * DOWN: 向零方向舍入（直接舍弃小数部分）。
     * HALF_UP: 最近数字舍入（五舍六入，四舍五入）。
     * HALF_DOWN: 最近数字舍入（五舍六入，五舍六入）。
     * HALF_EVEN: 最近数字舍入（银行家算法：四舍六入五考虑，若舍弃部分左边的数字为偶数，则舍入后的结果加上1）。
     * UNNECESSARY: 断言请求的操作具有精确的结果，因此不需要舍入。如果对获得此结果的精确度的任何疑问，则会抛出一个 ArithmeticException 异常。
     */
    public MathHelper scale(int v, RoundingMode roundingMode) {
        this.value = this.value.setScale(v, roundingMode);
        return this;
    }

    public MathHelper scale(int v) {
        this.value = this.value.setScale(v,RoundingMode.DOWN);
        return this;
    }

    public double doubleValue() {
        return this.value.doubleValue();
    }

    public String stringValue() {
        return this.value.toPlainString();
    }

    public static void main(String[] args) {
        String[] values = {"1.66", "22424.8", "-142", "-212.22222222222222", "1.99714E13"};
        for (String val : values) {
            System.out.println(MathHelper.of(val).scale(1).add("10").stringValue());
        }
    }
}
