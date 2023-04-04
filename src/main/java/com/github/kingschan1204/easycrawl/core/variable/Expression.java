package com.github.kingschan1204.easycrawl.core.variable;

import java.util.Map;

public interface Expression {

    String execute(Map<String, String> args);
}
