/*
 * Openai Llm Extension for Krista
 * Copyright (C) 2025 Krista Software
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>. 
 */

/*

 */
package app.krista.extensions.krista.llms.openai_qa.util;

import org.apache.commons.text.similarity.JaroWinklerDistance;

import java.text.BreakIterator;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Misc. String utilities.
 */
abstract public class StringI18N {

    static public String[] makeArrayFromDelimits(String fullLine, String delimits) {
        if (fullLine == null) {
            return new String[0];
        }

        StringTokenizer st = new StringTokenizer(fullLine, delimits, false);

        int i = 0;
        String[] ret = new String[st.countTokens()];
        while (st.hasMoreTokens()) {
            String tok = st.nextToken();
            tok = trimBoth(tok);
            ret[i++] = tok;
        }
        return ret;
    }

    static public String[] makeArrayFromCommas(String fullLine) {
        return makeArrayFromDelimits(fullLine, ",");
    }

    static public String makeCommasFromArray(String[] tokens) {
        return makeCommasFromArray(tokens, false, null);
    }

    static public String makeCommasFromArray(String[] tokens, boolean space, String andOr) {
        if (tokens == null || tokens.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder(128);
        for (int i = 0; i < tokens.length; i++) {
            if (i > 0) {
                sb.append(',');
                if (space) {
                    sb.append(' ');
                }
            }

            if (i == tokens.length - 1 && andOr != null) {
                sb.append(andOr).append(' ');
            }

            sb.append(tokens[i]);
        }

        return sb.toString();
    }

    public static boolean safeEquals(String a, String b) {
        return !(a == null || b == null) && a.equals(b);
    }

    public static String safeString(String s) {
        return safeString(s, "");
    }

    public static String safeString(String s, String def) {
        if (s == null) {
            return def;
        }
        return s;
    }

    /**
     * Determine if s starts with prefix ignoring case
     *
     * @param s      string to test
     * @param prefix string to look for
     * @return true if s starts with prefix
     */
    public static boolean startsWithIgnoreCase(String s, String prefix) {
        if (s == null || s.length() == 0) {
            return false;
        }

        if (prefix == null || prefix.length() == 0) {
            return true;
        }

        if (prefix.length() > s.length()) {
            return false;
        }

        String tmp = s.substring(0, prefix.length());
        return prefix.equalsIgnoreCase(tmp);
    }

    /**
     * Determine is s is {@code null} or empty string.  If string is all
     * whitespace it will be considered empty.
     * <p>
     * This does -not- use double/triple byte iterator support but whitespace should
     * be safe without it, and we do this 1,000,000 times so let's not get carried away
     *
     * @param s the string to test.
     * @return true if string is empty
     */
    public static boolean isEmpty(String s) {
        if (s == null || s.length() == 0) {
            return true;
        }

        for (int i = 0; i < s.length(); i++) {
            if (!Character.isWhitespace(s.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determine if s is not {@code null} and not empty.
     *
     * @param s the string to test.
     * @return true if string is not empty or null
     */
    public static boolean isNotEmpty(String s) {
        return (!isEmpty(s));
    }

    public static boolean isStringInStrings(String s, String[] list, boolean caseSensitive) {
        if (s == null || list == null) {
            return false;
        }

        for (String aList : list) {
            if (caseSensitive && s.equals(aList)) {
                return true;
            }
            if (!caseSensitive && s.equalsIgnoreCase(aList)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Searches the array of possible sub strings to see if one is contained in the given
     * String s "This is a test" and { "nope", "test", "no way" } would return true
     */
    public static boolean doesStringHaveSubString(String s, String[] subs) {
        if (s == null || subs == null) {
            return false;
        }

        for (String sub : subs) {
            if (s.contains(sub)) {
                return true;
            }
        }

        return false;
    }

    public static int safeIntValue(String s, int defValue, Locale locale) {

        try {
            Number number = safeNumberValue(s, defValue, locale);
            if (number != null)
                return number.intValue();
            else
                return defValue;
        } catch (Throwable ignored) {
            // no worries
            return defValue;
        }
    }

    public static double safeDoubleValue(String s, double defValue, Locale locale) {

        try {
            Number number = safeNumberValue(s, defValue, locale);
            if (number != null)
                return number.doubleValue();
            else
                return defValue;
        } catch (Throwable ignored) {
            // no worries
            return defValue;
        }
    }

    public static long safeLongValue(String s, long defValue, Locale locale) {

        try {
            Number number = safeNumberValue(s, defValue, locale);
            if (number != null)
                return number.longValue();
            else
                return defValue;
        } catch (Throwable ignored) {
            // no worries
            return defValue;
        }
    }

    public static Number safeNumberValue(String s, Number defValue, Locale locale) {

        if (locale == null)
            locale = Locale.US;

        if (hasAlphas(s, locale))
            return defValue;

        NumberFormat f = NumberFormat.getInstance(locale);

        try {
            Number d = f.parse(s);
            return d;
        } catch (ParseException e) {

            return defValue;
        }
    }

    /**
     * Returns an Enum value parsed from a string. The string's value must match exactly
     * with the name of a value defined in the enum. If the string is null or does not
     * match any value contained in the enum then the default is returned
     *
     * @param s         The string value
     * @param enumClass The enum class against which to match the string value
     * @param def       the default enum value
     * @return The value from the given enum class matching the given string, or the
     * default if there is no such value in the enum class or if the string is
     * null
     */
    public static <E extends Enum<E>> E safeEnumValue(String s, Class<E> enumClass, E def) {
        if (s != null) {
            try {
                return Enum.valueOf(enumClass, s);

            } catch (IllegalArgumentException e) {
                // ignore
            }
        }
        return def;
    }

    /**
     * This is a helper method for padding a string to a specified length.  If padding is
     * necessary, spaces will be used.
     *
     * @param text   the text to pad.
     * @param length the length to pad it to.
     * @return the padded string.
     */
    public static String pad(String text, int length) {
        return pad(text, length, ' ');
    }

    /**
     * This is a helper method for padding a string to a specified length.
     *
     * @param text   the text to pad.
     * @param length the length to pad it to.
     * @param ch     the pad character to use.
     * @return the padded string.
     */
    public static String pad(String text, int length, char ch) {
        if (text == null || text.length() < length) {
            StringBuilder buffer = new StringBuilder(length);

            if (text != null) {
                buffer.append(text);
            }

            while (buffer.length() < length) {
                buffer.append(ch);
            }

            text = buffer.toString();
        }

        return text;
    }


    public static String maxLength(String s, int length) {
        if (s == null)
            return "";
        if (s.length() < length)
            return s;
        return s.substring(0, length);
    }


    public static String trimTrailing(String s) {

        if (s == null || s.length() == 0)
            return s;

        return s.trim();
    }

    public static String buildSpaceSeparatedString(Collection<String> collection) {
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (String part : collection) {

            if (!first) {
                sb.append(" ");
            }
            sb.append(part);
            first = false;
        }
        return sb.toString();
    }

    /**
     * Useful for stripping spaces and punctuation from strings...
     * But -keeping- punctuation that we like, like % sign!
     */
    public static String trimNonLetterOrDigit(String s, Locale locale) {
        if (s == null) {
            return null;
        }

        if (s.length() < 1) {
            return s;
        }

        BreakIterator boundary = BreakIterator.getCharacterInstance(locale);
        boundary.setText(s);
        int end = boundary.last();
        int start = boundary.previous();

        while (start != BreakIterator.DONE) {

            int ch = s.codePointAt(start);
            if (Character.isLetterOrDigit(ch) || ch == '%') {
                break;
            }

            end = start;
            start = boundary.previous();
        }

        return s.substring(0, end);
    }


    public static boolean isAllAlpha(String s, Locale locale) {

        return scanForAlphaOrNum(s, true, locale);
    }

    public static boolean isAllNumbers(String s, Locale locale) {

        return scanForAlphaOrNum(s, false, locale);
    }

    public static boolean isAllAlphaNum(String s, Locale locale) {

        return scanForAlphaOrNum(s, null, locale);
    }

    // hacky I know, but it's internal :)
    // alpha is TRUE then eval alpha only
    // alpha is FALSE then eval numbers only
    // alpha is null then eval for alpha&numbers only
    private static boolean scanForAlphaOrNum(String s, Boolean alpha, Locale locale) {
        if (s == null || s.length() == 0) {
            return false;
        }

        BreakIterator iter = BreakIterator.getCharacterInstance(locale);
        iter.setText(s);
        int start = iter.first();
        int i = start;
        for (int end = iter.next(); end != BreakIterator.DONE; end = iter.next()) {

            String codepoint = s.substring(start, end);
            int ch = codepoint.codePointAt(0);

            if (alpha == null) {
                if (!Character.isLetterOrDigit(ch))
                    return false;
            } else if ((alpha && !Character.isLetter(ch)) ||
                    (!alpha && !Character.isDigit(ch))) {
                return false;
            }

            start = end;
        }
        return true;
    }

    public static boolean hasAlphas(String s, Locale locale) {

        if (s == null || s.length() == 0) {
            return false;
        }

        BreakIterator iter = BreakIterator.getCharacterInstance(locale);
        iter.setText(s);
        int start = iter.first();
        int i = start;
        for (int end = iter.next(); end != BreakIterator.DONE; end = iter.next()) {

            String codepoint = s.substring(start, end);
            int ch = codepoint.codePointAt(0);

            if (Character.isLetter(ch))
                return true;

            start = end;
        }
        return false;
    }

    public static String trimLeading(String s) {

        if (s == null) {
            return null;
        }

        int len = s.length();

        if (len < 1) {
            return s;
        }

        int idx = 0;

        while (idx < len) {
            if (Character.isSpaceChar(s.charAt(idx))) {
                idx++;
            } else {
                break;
            }
        }
        return s.substring(idx);
    }

    public static String removeNonDigits(String s) {
        if (s == null) {
            return null;
        }

        int len = s.length();

        if (len < 1) {
            return s;
        }

        int idx = 0;

        StringBuilder sb = new StringBuilder(s.length());
        while (idx < len) {
            if (Character.isDigit(s.charAt(idx))) // this is same a String.trim() method
            {
                sb.append(s.charAt(idx));
            }
            idx++;
        }

        return sb.toString();
    }

    public static String stripNonAlphaNum(String buff, Locale locale) {
        if (buff == null) {
            return null;
        }

        StringBuilder sb = new StringBuilder(buff.length());
        BreakIterator iter = BreakIterator.getCharacterInstance(locale);
        iter.setText(buff);
        int start = iter.first();
        int i = start;
        for (int end = iter.next(); end != BreakIterator.DONE; end = iter.next()) {

            String codepoint = buff.substring(start, end);
            int ch = codepoint.codePointAt(0);
            if (Character.isLetterOrDigit(ch)) {
                sb.append(codepoint);
            }
            start = end;
        }

        return sb.toString();
    }

    /**
     * Every char must be
     *
     * @param buff
     * @return
     */
    public static String trimLeadingNonAlphaNum(String buff, Locale locale) {
        if (buff == null)
            return null;

        BreakIterator iter = BreakIterator.getCharacterInstance(locale);
        iter.setText(buff);
        int start = iter.first();
        int i = start;
        for (int end = iter.next(); end != BreakIterator.DONE; end = iter.next()) {

            String codepoint = buff.substring(start, end);
            int ch = codepoint.codePointAt(0);

            // when we see the 1st alpha/num, we want the rest
            if (Character.isLetterOrDigit(ch)) {
                return buff.substring(start);
            }
            start = end;
        }

        // no alphanum chars at all
        return "";
    }

    public static String trimTrailingNonAlphaNum(String buff, Locale locale) {
        if (buff == null) {
            return null;
        }

        BreakIterator iter = BreakIterator.getCharacterInstance(locale);
        iter.setText(buff);
        int start = iter.last();
        int i = start;
        for (int end = iter.previous(); end != BreakIterator.DONE; end = iter.previous()) {

            String codepoint = buff.substring(end, start);
            int ch = codepoint.codePointAt(0);
            if (Character.isLetterOrDigit(ch)) {
                return buff.substring(0, start);
            }
            start = end;
        }

        return "";
    }


    /**
     * Every char must be
     *
     * @param buff
     * @return
     */
    public static String trimLeadingNonNum(String buff, Locale locale) {
        if (buff == null)
            return null;

        BreakIterator iter = BreakIterator.getCharacterInstance(locale);
        iter.setText(buff);
        int start = iter.first();
        int i = start;
        for (int end = iter.next(); end != BreakIterator.DONE; end = iter.next()) {

            String codepoint = buff.substring(start, end);
            int ch = codepoint.codePointAt(0);

            // when we see the 1st num, we want the rest
            if (Character.isDigit(ch)) {
                return buff.substring(start);
            }
            start = end;
        }

        // no alphanum chars at all
        return "";
    }

    public static String trimTrailingNonNum(String buff, Locale locale) {
        if (buff == null) {
            return null;
        }

        BreakIterator iter = BreakIterator.getCharacterInstance(locale);
        iter.setText(buff);
        int start = iter.last();
        int i = start;
        for (int end = iter.previous(); end != BreakIterator.DONE; end = iter.previous()) {

            String codepoint = buff.substring(end, start);
            int ch = codepoint.codePointAt(0);
            if (Character.isDigit(ch)) {
                return buff.substring(0, start);
            }
            start = end;
        }

        return "";
    }

    public static String trimLeadingTrailingNonAlphaNum(String buff, Locale locale) {

        buff = trimLeadingNonAlphaNum(buff, locale);
        buff = trimTrailingNonAlphaNum(buff, locale);
        return buff;
    }

    public static String trimLeadingTrailingNonNum(String buff, Locale locale) {

        buff = trimLeadingNonNum(buff, locale);
        buff = trimTrailingNonNum(buff, locale);
        return buff;
    }

    public static String trimBoth(String s) {
        if (s == null) {
            return "";
        }

        s = trimLeading(s);
        return s.trim();
    }

    public static String stripNewLines(String buff, Locale locale) {

        StringBuilder sb = new StringBuilder(buff.length());
        BreakIterator iter = BreakIterator.getCharacterInstance(locale);
        iter.setText(buff);
        int start = iter.first();
        int i = start;
        for (int end = iter.next(); end != BreakIterator.DONE; end = iter.next()) {

            String codepoint = buff.substring(start, end);
            int ch = codepoint.codePointAt(0);
            if (!(ch == '\n' || ch == '\r'))
                sb.append(codepoint);
            start = end;
        }
        return sb.toString();
    }

    public static final char CR = '\r';
    public static final char NL = '\n';

    /**
     * When you want to ensure new lines are always the same type before you compare a
     * string you can use this method. We assume the standard pattern of CRLF = one
     * newLine. Multiple CLRFs will get converted to multiple NLs. A single CR will
     * convert to a NL (\n).
     *
     * @param in the string to normalize new-lines in.
     * @return the normalized string.
     */
    public static String normalizeNewLines(String in, Locale locale) {
        if (in == null) {
            return in;
        }

        // Let's optimize away doing this if we don't need to
        if (in.indexOf(CR) < 0) {
            return in;
        }

        StringBuilder sb = new StringBuilder(in.length());
        BreakIterator iter = BreakIterator.getCharacterInstance(locale);
        iter.setText(in);
        int start = iter.first();
        int i = start;
        int prevChar = 0;
        for (int end = iter.next(); end != BreakIterator.DONE; end = iter.next()) {

            String codepoint = in.substring(start, end);
            int ch = codepoint.codePointAt(0);

            switch (ch) {
                case NL:
                    if (prevChar != CR)
                        sb.append(NL);
                    break;
                case CR:
                    sb.append(NL);
                    break;
                default:
                    sb.append(codepoint);
                    break;
            }
            start = end;
            prevChar = ch;
        }

        return sb.toString();
    }

    /**
     * Splits a sentence into words. No need for a period on the end but fine if we have it.
     * If there are punctuation after a word, like "Dr.", you will lose the "."...
     *
     * @param sentence has a bunch of words
     * @param locale   need this to make sure we parse correctly
     * @return array of strings cleaned of punctuation and spaces
     */
    public static String[] parseSentenceIntoWords(String sentence, Locale locale) {

        List<String> results = new ArrayList<>();
        BreakIterator iter = BreakIterator.getWordInstance(locale);
        iter.setText(sentence);
        int start = iter.first();
        for (int end = iter.next();

             end != BreakIterator.DONE;
             start = end, end = iter.next()) {

            String s = sentence.substring(start, end);
            s = trimLeadingTrailingNonAlphaNum(s, locale);
            if (isNotEmpty(s))
                results.add(s);
        }
        return results.toArray(new String[0]);
    }

    /**
     * Splits a sentence into word starts. No need for a period on the end but fine if we have it.
     * This is much like parseSentenceIntoWords, but instead of giving you each word, we give you the
     * 0-based index of where the word starts in the sentence
     * <p>
     * Big diff is no 'cleaning' happens here.  So "I asked the Dr. for some meds; he declined" returns
     * {0, 2, 8, 12, 16, 20, etc}
     *
     * @param sentence has a bunch of words
     * @param locale   need this to make sure we parse correctly
     * @return list of ints for each word
     */
    public static List<Integer> parseSentenceToWordsOffsets(String sentence, Locale locale) {

        List<Integer> results = new ArrayList<>();
        BreakIterator iter = BreakIterator.getWordInstance(locale);
        iter.setText(sentence);
        int start = iter.first();
        for (int end = iter.next(); end != BreakIterator.DONE; start = end, end = iter.next()) {

            results.add(start);
        }
        return results;
    }

    public static String[] parseBufferIntoSentences(String oneOrMoreSentences, Locale locale) {

        List<String> results = new ArrayList<>();
        BreakIterator iter = BreakIterator.getSentenceInstance(locale);
        iter.setText(oneOrMoreSentences);
        int start = iter.first();
        for (int end = iter.next();

             end != BreakIterator.DONE;
             start = end, end = iter.next()) {

            String s = oneOrMoreSentences.substring(start, end);
            s = s.trim();
            results.add(s);
        }
        return results.toArray(new String[0]);
    }

    /**
     * Uses Jaro or whatever we think is best for comparing 2 strings.
     *
     * @return 0 - 1.0 from not close at all to identical
     * @see JaroWinklerDistance
     */
    public static double getSimilarityScore(String s1, String s2) {
        JaroWinklerDistance distance = new JaroWinklerDistance();
        return 1 - distance.apply(s1, s2);
    }

    private static final int[] BAD = {
            '\"',
            '/',
            '\\',
            '\n',
            '\r',
            '\f',
            '\b',
            '\t'
    };

    private static final String[] GOOD = {
            "\\\"",
            "\\/",
            "\\\\",
            "\\n",
            "\\r",
            "\\f",
            "\\b",
            "\\t"
    };

    public static String escapeStringForJson(String s) {

        StringBuilder sb = new StringBuilder((int) (s.length() * 1.25));
        int length = s.length();
        for (int offset = 0; offset < length; ) {
            int ch = s.codePointAt(offset);

            int badIndex = findBad(ch);
            if (badIndex != -1)
                sb.append(GOOD[badIndex]);
            else if (ch != 0)
                sb.appendCodePoint(ch);

            offset += Character.charCount(ch);
        }

        return sb.toString();
    }

    private static int findBad(int codePoint) {
        char ch = (char) codePoint;
        for (int i = 0; i < BAD.length; i++) {
            if (ch == BAD[i])
                return i;
        }
        return -1;
    }
}
