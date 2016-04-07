package net.myonlinestuff.torrentdl.service;

import org.apache.commons.lang3.StringUtils;

public class StringFuzyy {

    public static int fuzzyLogic(String original, String search) {
        final int distance = StringUtils.getLevenshteinDistance(original, search);
        return 100 - distance;
    }
}
