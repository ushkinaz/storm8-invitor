package net.ushkinaz.storm8.digger;

import java.util.regex.Matcher;

public class MatcherHelper {

    private MatcherHelper() {
    }

    public static boolean isMatchFound(Matcher matcher) {
        return matcher.find();
    }

    public static String match(Matcher matcher, int group) {
        return matcher.group(group);
    }

    public static String match(Matcher matcher) {
        return match(matcher, 1);
    }

    /**
     * Returns integer value from 1st group
     *
     * @param matcher
     * @param group
     * @return int
     */
    public static int matchInteger(Matcher matcher, int group) {
        String result = match(matcher, group);
        if (result == null) {
            result = "0";
        }
        return Integer.parseInt(result.replace(",", ""));
    }

    public static int matchInteger(Matcher matcher) {
        return matchInteger(matcher, 1);
    }
}