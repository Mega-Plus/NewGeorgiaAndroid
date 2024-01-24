package com.example.migeba.Utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class UnicodeConvertorUtil {

    private static final Map<Character, Character> enGeo;
    private static final Map<Character, Character> geoEn;
    private static final Map<Character, Character> geoEn2;
    private static final Map<Character, Character> enGeo2;

    static {
        Map<Character, Character> map = new HashMap<Character, Character>() {

            {
                put('a', 'ა');
                put('b', 'ბ');
                put('g', 'გ');
                put('d', 'დ');
                put('e', 'ე');
                put('v', 'ვ');
                put('z', 'ზ');
                put('T', 'თ');
                put('i', 'ი');
                put('k', 'კ');
                put('l', 'ლ');
                put('m', 'მ');
                put('n', 'ნ');
                put('o', 'ო');
                put('p', 'პ');
                put('J', 'ჟ');
                put('r', 'რ');
                put('s', 'ს');
                put('t', 'ტ');
                put('u', 'უ');
                put('f', 'ფ');
                put('q', 'ქ');
                put('R', 'ღ');
                put('y', 'ყ');
                put('S', 'შ');
                put('C', 'ჩ');
                put('c', 'ც');
                put('Z', 'ძ');
                put('w', 'წ');
                put('W', 'ჭ');
                put('x', 'ხ');
                put('j', 'ჯ');
                put('h', 'ჰ');
            }
        };
        enGeo = Collections.unmodifiableMap(map);

        Map<Character, Character> map2 = new HashMap<Character, Character>() {
            {
                put('ა', 'a');
                put('ბ', 'b');
                put('გ', 'g');
                put('დ', 'd');
                put('ე', 'e');
                put('ვ', 'v');
                put('ზ', 'z');
                put('თ', 'T');
                put('ი', 'i');
                put('კ', 'k');
                put('ლ', 'l');
                put('მ', 'm');
                put('ნ', 'n');
                put('ო', 'o');
                put('პ', 'p');
                put('ჟ', 'J');
                put('რ', 'r');
                put('ს', 's');
                put('ტ', 't');
                put('უ', 'u');
                put('ფ', 'f');
                put('ქ', 'q');
                put('ღ', 'R');
                put('ყ', 'y');
                put('შ', 'S');
                put('ჩ', 'C');
                put('ც', 'c');
                put('ძ', 'Z');
                put('წ', 'w');
                put('ჭ', 'W');
                put('ხ', 'x');
                put('ჯ', 'j');
                put('ჰ', 'h');
            }
        };
        geoEn = Collections.unmodifiableMap(map2);

        Map<Character, Character> map3 = new HashMap<Character, Character>() {
            {
                put('®', 'T');
                put('®', 'J');
                put('¦', 'R');
                put('©', 'S');
                put('«', 'C');
                put('¬', 'Z');
                put('°', 'W');
            }
        };
        geoEn2 = Collections.unmodifiableMap(map2);

        Map<Character, Character> map4 = new HashMap<Character, Character>() {
            {
                put('T', '®');
                put('J', '®');
                put('R', '¦');
                put('S', '©');
                put('C', '«');
                put('Z', '¬');
                put('W', '°');
            }
        };
        enGeo2 = Collections.unmodifiableMap(map2);

    }

    public static String enToGeo(String str) {
        try {
            char[] ch = str.toCharArray();
            StringBuilder retValue = new StringBuilder();
            for (char c : ch) {
                if (containsKey(c)) {
                    retValue.append(enGeo.get(c).toString());
                } else {
                    retValue.append((c));
                }
            }
            return retValue.toString();
        } catch (Exception e) {
            return "";
        }
    }

    public static String geoToEn(String str) {
        char[] ch = str.toCharArray();
        StringBuilder retValue = new StringBuilder();
        for (char c : ch) {
            if (containsKey2(c)) {
                retValue.append(geoEn.get(c).toString());
            } else {
                retValue.append((c));
            }
        }
        return retValue.toString();
    }

    public static String geoToEn2(String str) {
        char[] ch = str.toCharArray();
        StringBuilder retValue = new StringBuilder();
        for (char c : ch) {
            if (containsKey3(c)) {
                retValue.append(geoEn.get(c).toString());
            } else {
                retValue.append((c));
            }
        }
        return retValue.toString();
    }

    public static String enToGeo2(String str) {
        char[] ch = str.toCharArray();
        StringBuilder retValue = new StringBuilder();
        for (char c : ch) {
            if (containsKey4(c)) {
                retValue.append(geoEn.get(c).toString());
            } else {
                retValue.append((c));
            }
        }
        return retValue.toString();
    }

    private static boolean containsKey(char c) {
        for (Character key : enGeo.keySet()) {
            if (key.equals(c)) {
                return true;
            }
        }
        return false;

    }

    private static boolean containsKey2(char c) {
        for (Character key : geoEn.keySet()) {
            if (key.equals(c)) {
                return true;
            }
        }
        return false;

    }

    private static boolean containsKey3(char c) {
        for (Character key : geoEn2.keySet()) {
            if (key.equals(c)) {
                return true;
            }
        }
        return false;

    }

    private static boolean containsKey4(char c) {
        for (Character key : enGeo2.keySet()) {
            if (key.equals(c)) {
                return true;
            }
        }
        return false;

    }

}
