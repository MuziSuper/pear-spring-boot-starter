package cn.muzisheng.pear;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class PluralUtil {
    private static final Set<String> O_EXCEPTIONS = new HashSet<>(Arrays.asList(
            "photo", "piano", "halo", "memo", "silo", "logo"
    ));

    private static final Set<String> IRREGULAR_PLURALS = new HashSet<>(Arrays.asList(
            "child", "foot", "tooth", "goose", "man", "woman", "mouse", "louse", "person"
    ));

    private static final Set<String> UNCOUNTABLES = new HashSet<>(Arrays.asList(
            "sheep", "deer", "moose", "fish", "series", "species", "aircraft", "rice", "money"
    ));

    public static String pluralize(String word) {
        if (word == null || word.isEmpty()) {
            return word;
        }

        word = word.toLowerCase(); // 统一转小写处理

        if (UNCOUNTABLES.contains(word)) {
            return word; // 不可数名词不变
        }

        if (IRREGULAR_PLURALS.contains(word)) {
            return getIrregularPlural(word); // 处理不规则单词
        }

        if (word.matches(".*[^aeiou]y$")) {
            return word.substring(0, word.length() - 1) + "ies"; // baby → babies
        }

        if (word.matches(".*(s|x|z|ch|sh)$")) {
            return word + "es"; // box → boxes, church → churches
        }

        if (word.endsWith("o")) {
            if (O_EXCEPTIONS.contains(word)) {
                return word + "s"; // photo → photos, piano → pianos
            }
            return word + "es"; // potato → potatoes, hero → heroes
        }

        return word + "s"; // 默认加 "s"
    }

    private static String getIrregularPlural(String word) {
        return switch (word) {
            case "child" -> "children";
            case "foot" -> "feet";
            case "tooth" -> "teeth";
            case "goose" -> "geese";
            case "man" -> "men";
            case "woman" -> "women";
            case "mouse" -> "mice";
            case "louse" -> "lice";
            case "person" -> "people";
            default -> word;
        };
    }

}
