package com.github.kingschan1204.easycrawl.helper.regex;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author kingschan
 */
public class RegexHelper {
    /**
     * 文件名
     */
    public static final String REGEX_FILE_NAME = "^[\\u4E00-\\u9FA5A-Za-z0-9_]+\\.\\w+$";
    /**
     * 表达式
     */
    public static final String REGEX_EXPRESSION = "\\$\\{(\\w|\\s|\\=)+\\}";
    /**
     * 科学计算
     */
    public static final String REGEX_SCIENTIFIC_NOTATION = "\\d+\\.\\d+E(\\+)?\\d+";
    /**
     * html meta标签charset属性提取
     */
    public static final String REGEX_HTML_CHARSET = "<(?i)meta(\\s+|.*)(?i)charSet(\\s+)?=.*/>";

    /**
     * 提取文本中匹配正则的字符串
     *
     * @param text
     * @param regx 正则
     * @return 结果
     */
    public static List<String> find(String text, String regx) {
        List<String> list = new ArrayList<>();
        try {
            Pattern pattern = Pattern.compile(regx);
            Matcher matcher = pattern.matcher(text);
            while (matcher.find()) {
                list.add(matcher.group());
            }
            return list;
        } catch (Exception e) {
            e.printStackTrace();
            return list;
        }

    }

    public static String findFirst(String text, String regx,int index) {
        Pattern pattern = Pattern.compile(regx);
        Matcher matcher = pattern.matcher(text);
        if (matcher.find()) {
            return matcher.group(index);
        }
        return "";
    }
    public static String findFirst(String text, String regx) {
       return findFirst(text,regx,0);
    }
}
