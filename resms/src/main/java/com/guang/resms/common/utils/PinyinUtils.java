package com.guang.resms.common.utils;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.pinyin.engine.pinyin4j.Pinyin4jEngine;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 拼音工具类
 */
@Component
public class PinyinUtils {
    
    private static final Pinyin4jEngine pinyinEngine = new Pinyin4jEngine();
    
    /**
     * 获取字符串的拼音首字母（大写）
     * 
     * @param chinese 中文字符串
     * @return 拼音首字母，如："北京" -> "BJ"
     */
    public static String getFirstLetter(String chinese) {
        if (StrUtil.isBlank(chinese)) {
            return "";
        }
        
        try {
            String pinyin = pinyinEngine.getPinyin(chinese, "");
            // 只保留字母，并转换为大写
            return pinyin.replaceAll("[^A-Za-z]", "").toUpperCase();
        } catch (Exception e) {
            // 如果转换失败，返回原字符串的首字母
            return chinese.substring(0, 1).toUpperCase();
        }
    }
    
    /**
     * 获取字符串的拼音（全拼）
     * 
     * @param chinese 中文字符串
     * @return 完整拼音，如："北京" -> "BEIJING"
     */
    public static String getPinyin(String chinese) {
        if (StrUtil.isBlank(chinese)) {
            return "";
        }
        
        try {
            return pinyinEngine.getPinyin(chinese, "").toUpperCase();
        } catch (Exception e) {
            return chinese.toUpperCase();
        }
    }
    
    /**
     * 获取字符串的拼音（带空格分隔）
     * 
     * @param chinese 中文字符串
     * @return 带空格的拼音，如："北京" -> "BEI JING"
     */
    public static String getPinyinWithSpace(String chinese) {
        if (StrUtil.isBlank(chinese)) {
            return "";
        }
        
        try {
            return pinyinEngine.getPinyin(chinese, " ").toUpperCase();
        } catch (Exception e) {
            return chinese.toUpperCase();
        }
    }
    
    /**
     * 获取拼音首字母（单个字符）
     * 
     * @param chineseChar 中文字符
     * @return 首字母，如：'北' -> 'B'
     */
    public static char getFirstLetter(char chineseChar) {
        try {
            String pinyin = pinyinEngine.getPinyin(String.valueOf(chineseChar), "");
            if (StrUtil.isNotBlank(pinyin)) {
                return pinyin.charAt(0);
            }
        } catch (Exception e) {
            // 转换失败，返回原字符
        }
        return chineseChar;
    }
    
    /**
     * 按拼音首字母分组
     * 
     * @param list 字符串列表
     * @return 按首字母分组的Map
     */
    public static Map<String, List<String>> groupByInitial(List<String> list) {
        if (list == null || list.isEmpty()) {
            return new TreeMap<>();
        }
        
        return list.stream()
            .filter(StrUtil::isNotBlank)
            .collect(Collectors.groupingBy(
                city -> {
                    String initial = getFirstLetter(city);
                    if (StrUtil.isBlank(initial)) {
                        return "#";
                    }
                    // 取第一个字符作为分组键
                    String firstChar = initial.substring(0, 1);
                    // 如果是字母则返回，否则归到#组
                    return firstChar.matches("[A-Z]") ? firstChar : "#";
                },
                TreeMap::new,  // 使用TreeMap保证按键排序
                Collectors.collectingAndThen(
                    Collectors.toList(),
                    cities -> {
                        // 对每个分组内的城市按拼音排序
                        cities.sort((c1, c2) -> {
                            String p1 = getPinyin(c1);
                            String p2 = getPinyin(c2);
                            return p1.compareTo(p2);
                        });
                        return cities;
                    }
                )
            ));
    }
    
    /**
     * 按拼音排序
     * 
     * @param list 字符串列表
     * @return 按拼音排序后的列表
     */
    public static List<String> sortByPinyin(List<String> list) {
        if (list == null || list.isEmpty()) {
            return new ArrayList<>();
        }
        
        return list.stream()
            .filter(StrUtil::isNotBlank)
            .sorted((s1, s2) -> {
                String p1 = getPinyin(s1);
                String p2 = getPinyin(s2);
                return p1.compareTo(p2);
            })
            .collect(Collectors.toList());
    }
    
    /**
     * 判断字符串是否以指定拼音开头
     * 
     * @param chinese 中文字符串
     * @param pinyinPrefix 拼音前缀
     * @return 是否匹配
     */
    public static boolean startsWithPinyin(String chinese, String pinyinPrefix) {
        if (StrUtil.isBlank(chinese) || StrUtil.isBlank(pinyinPrefix)) {
            return false;
        }
        
        String pinyin = getPinyin(chinese);
        return pinyin.startsWith(pinyinPrefix.toUpperCase());
    }
    
    /**
     * 获取拼音的首字母简写（每个字的首字母）
     * 
     * @param chinese 中文字符串
     * @return 拼音简写，如："北京市" -> "BJS"
     */
    public static String getPinyinAbbr(String chinese) {
        if (StrUtil.isBlank(chinese)) {
            return "";
        }
        
        StringBuilder abbr = new StringBuilder();
        for (int i = 0; i < chinese.length(); i++) {
            char c = chinese.charAt(i);
            if (isChineseCharacter(c)) {
                abbr.append(getFirstLetter(c));
            } else {
                abbr.append(c);
            }
        }
        return abbr.toString().toUpperCase();
    }
    
    /**
     * 判断字符是否为中文字符
     */
    private static boolean isChineseCharacter(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }
}