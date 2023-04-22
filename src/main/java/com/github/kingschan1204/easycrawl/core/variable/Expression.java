package com.github.kingschan1204.easycrawl.core.variable;

import java.util.Map;

public interface Expression {
    /**
     *
     * @param args 参数
     * @return 解析表达式后的结果
     */
    String execute(Map<String, String> args);
}
