package com.github.kingschan1204.text;

import com.github.kingschan1204.easycrawl.helper.regex.RegexHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.stream.Collectors;

@DisplayName("正则测试")
public class RegexTest {

    @Test
    public void charsetTest() {
        String content = "\t<meta http-equiv=\"Content-Type\" content=\"text/html; charset =gbk \"/>\n<html lang=\"zh\" data-hairline=\"true\" class=\"itcauecng\" data-theme=\"light\"><head><META CHARSET=\"utf-8 \"/><title data-rh=\"true\">这是一个测试</title>";
        List<String> list = RegexHelper.find(content, "<(?i)meta(\\s+|.*)(?i)charSet(\\s+)?=.*/>");
        System.out.println(list);
        list.forEach(r -> {
            System.out.println(RegexHelper.find(r, "(?i)charSet(\\s+)?=.*\"").stream().map(s -> s.replaceAll("(?i)charSet|=|\"|\\s", "")).collect(Collectors.joining(",")));
        });
        System.err.println(RegexHelper.findFirst(content, "<(?i)meta(\\s+|.*)(?i)charSet(\\s+)?=.*/>"));
    }

}
