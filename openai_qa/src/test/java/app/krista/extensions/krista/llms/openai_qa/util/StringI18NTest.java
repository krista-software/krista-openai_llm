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

package app.krista.extensions.krista.llms.openai_qa.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive tests for StringI18N utility class.
 */
@DisplayName("StringI18N Tests")
class StringI18NTest {

    private static final Locale US = Locale.US;

    @Nested
    @DisplayName("makeArrayFromDelimits Tests")
    class MakeArrayFromDelimitsTests {

        @Test
        @DisplayName("Should split string by delimiter")
        void shouldSplitStringByDelimiter() {
            String[] result = StringI18N.makeArrayFromDelimits("a,b,c", ",");
            assertArrayEquals(new String[]{"a", "b", "c"}, result);
        }

        @Test
        @DisplayName("Should return empty array for null input")
        void shouldReturnEmptyArrayForNullInput() {
            String[] result = StringI18N.makeArrayFromDelimits(null, ",");
            assertEquals(0, result.length);
        }

        @Test
        @DisplayName("Should trim tokens")
        void shouldTrimTokens() {
            String[] result = StringI18N.makeArrayFromDelimits(" a , b , c ", ",");
            assertArrayEquals(new String[]{"a", "b", "c"}, result);
        }
    }

    @Nested
    @DisplayName("makeArrayFromCommas Tests")
    class MakeArrayFromCommasTests {

        @Test
        @DisplayName("Should split by commas")
        void shouldSplitByCommas() {
            String[] result = StringI18N.makeArrayFromCommas("one,two,three");
            assertArrayEquals(new String[]{"one", "two", "three"}, result);
        }
    }

    @Nested
    @DisplayName("makeCommasFromArray Tests")
    class MakeCommasFromArrayTests {

        @Test
        @DisplayName("Should join array with commas")
        void shouldJoinArrayWithCommas() {
            String result = StringI18N.makeCommasFromArray(new String[]{"a", "b", "c"});
            assertEquals("a,b,c", result);
        }

        @Test
        @DisplayName("Should return empty string for null array")
        void shouldReturnEmptyStringForNullArray() {
            assertEquals("", StringI18N.makeCommasFromArray(null));
        }

        @Test
        @DisplayName("Should return empty string for empty array")
        void shouldReturnEmptyStringForEmptyArray() {
            assertEquals("", StringI18N.makeCommasFromArray(new String[]{}));
        }

        @Test
        @DisplayName("Should add spaces when requested")
        void shouldAddSpacesWhenRequested() {
            String result = StringI18N.makeCommasFromArray(new String[]{"a", "b", "c"}, true, null);
            assertEquals("a, b, c", result);
        }

        @Test
        @DisplayName("Should add andOr before last element")
        void shouldAddAndOrBeforeLastElement() {
            String result = StringI18N.makeCommasFromArray(new String[]{"a", "b", "c"}, true, "and");
            assertEquals("a, b, and c", result);
        }
    }

    @Nested
    @DisplayName("safeEquals Tests")
    class SafeEqualsTests {

        @Test
        @DisplayName("Should return true for equal strings")
        void shouldReturnTrueForEqualStrings() {
            assertTrue(StringI18N.safeEquals("test", "test"));
        }

        @Test
        @DisplayName("Should return false for null first argument")
        void shouldReturnFalseForNullFirstArgument() {
            assertFalse(StringI18N.safeEquals(null, "test"));
        }

        @Test
        @DisplayName("Should return false for null second argument")
        void shouldReturnFalseForNullSecondArgument() {
            assertFalse(StringI18N.safeEquals("test", null));
        }

        @Test
        @DisplayName("Should return false for different strings")
        void shouldReturnFalseForDifferentStrings() {
            assertFalse(StringI18N.safeEquals("test1", "test2"));
        }
    }

    @Nested
    @DisplayName("safeString Tests")
    class SafeStringTests {

        @Test
        @DisplayName("Should return string if not null")
        void shouldReturnStringIfNotNull() {
            assertEquals("test", StringI18N.safeString("test"));
        }

        @Test
        @DisplayName("Should return empty string for null")
        void shouldReturnEmptyStringForNull() {
            assertEquals("", StringI18N.safeString(null));
        }

        @Test
        @DisplayName("Should return default for null")
        void shouldReturnDefaultForNull() {
            assertEquals("default", StringI18N.safeString(null, "default"));
        }
    }

    @Nested
    @DisplayName("startsWithIgnoreCase Tests")
    class StartsWithIgnoreCaseTests {

        @Test
        @DisplayName("Should return true for matching prefix")
        void shouldReturnTrueForMatchingPrefix() {
            assertTrue(StringI18N.startsWithIgnoreCase("Hello World", "hello"));
        }

        @Test
        @DisplayName("Should return false for null string")
        void shouldReturnFalseForNullString() {
            assertFalse(StringI18N.startsWithIgnoreCase(null, "test"));
        }

        @Test
        @DisplayName("Should return true for null prefix")
        void shouldReturnTrueForNullPrefix() {
            assertTrue(StringI18N.startsWithIgnoreCase("test", null));
        }

        @Test
        @DisplayName("Should return false when prefix longer than string")
        void shouldReturnFalseWhenPrefixLongerThanString() {
            assertFalse(StringI18N.startsWithIgnoreCase("hi", "hello"));
        }
    }

    @Nested
    @DisplayName("isEmpty Tests")
    class IsEmptyTests {

        @Test
        @DisplayName("Should return true for null")
        void shouldReturnTrueForNull() {
            assertTrue(StringI18N.isEmpty(null));
        }

        @Test
        @DisplayName("Should return true for empty string")
        void shouldReturnTrueForEmptyString() {
            assertTrue(StringI18N.isEmpty(""));
        }

        @Test
        @DisplayName("Should return true for whitespace only")
        void shouldReturnTrueForWhitespaceOnly() {
            assertTrue(StringI18N.isEmpty("   "));
        }

        @Test
        @DisplayName("Should return false for non-empty string")
        void shouldReturnFalseForNonEmptyString() {
            assertFalse(StringI18N.isEmpty("test"));
        }
    }

    @Nested
    @DisplayName("isNotEmpty Tests")
    class IsNotEmptyTests {

        @Test
        @DisplayName("Should return false for null")
        void shouldReturnFalseForNull() {
            assertFalse(StringI18N.isNotEmpty(null));
        }

        @Test
        @DisplayName("Should return true for non-empty string")
        void shouldReturnTrueForNonEmptyString() {
            assertTrue(StringI18N.isNotEmpty("test"));
        }
    }

    @Nested
    @DisplayName("isStringInStrings Tests")
    class IsStringInStringsTests {

        @Test
        @DisplayName("Should find string in array case sensitive")
        void shouldFindStringInArrayCaseSensitive() {
            assertTrue(StringI18N.isStringInStrings("test", new String[]{"one", "test", "three"}, true));
        }

        @Test
        @DisplayName("Should not find string with wrong case when case sensitive")
        void shouldNotFindStringWithWrongCaseWhenCaseSensitive() {
            assertFalse(StringI18N.isStringInStrings("TEST", new String[]{"one", "test", "three"}, true));
        }

        @Test
        @DisplayName("Should find string ignoring case")
        void shouldFindStringIgnoringCase() {
            assertTrue(StringI18N.isStringInStrings("TEST", new String[]{"one", "test", "three"}, false));
        }

        @Test
        @DisplayName("Should return false for null string")
        void shouldReturnFalseForNullString() {
            assertFalse(StringI18N.isStringInStrings(null, new String[]{"test"}, true));
        }

        @Test
        @DisplayName("Should return false for null array")
        void shouldReturnFalseForNullArray() {
            assertFalse(StringI18N.isStringInStrings("test", null, true));
        }
    }

    @Nested
    @DisplayName("doesStringHaveSubString Tests")
    class DoesStringHaveSubStringTests {

        @Test
        @DisplayName("Should find substring")
        void shouldFindSubstring() {
            assertTrue(StringI18N.doesStringHaveSubString("This is a test", new String[]{"nope", "test"}));
        }

        @Test
        @DisplayName("Should return false when no match")
        void shouldReturnFalseWhenNoMatch() {
            assertFalse(StringI18N.doesStringHaveSubString("Hello", new String[]{"nope", "test"}));
        }

        @Test
        @DisplayName("Should return false for null string")
        void shouldReturnFalseForNullString() {
            assertFalse(StringI18N.doesStringHaveSubString(null, new String[]{"test"}));
        }
    }

    @Nested
    @DisplayName("safeIntValue Tests")
    class SafeIntValueTests {

        @Test
        @DisplayName("Should parse valid integer")
        void shouldParseValidInteger() {
            assertEquals(42, StringI18N.safeIntValue("42", 0, US));
        }

        @Test
        @DisplayName("Should return default for invalid input")
        void shouldReturnDefaultForInvalidInput() {
            assertEquals(0, StringI18N.safeIntValue("abc", 0, US));
        }
    }

    @Nested
    @DisplayName("safeDoubleValue Tests")
    class SafeDoubleValueTests {

        @Test
        @DisplayName("Should parse valid double")
        void shouldParseValidDouble() {
            assertEquals(3.14, StringI18N.safeDoubleValue("3.14", 0.0, US), 0.01);
        }

        @Test
        @DisplayName("Should return default for invalid input")
        void shouldReturnDefaultForInvalidInput() {
            assertEquals(0.0, StringI18N.safeDoubleValue("abc", 0.0, US), 0.01);
        }
    }

    @Nested
    @DisplayName("safeLongValue Tests")
    class SafeLongValueTests {

        @Test
        @DisplayName("Should parse valid long")
        void shouldParseValidLong() {
            assertEquals(1234567890L, StringI18N.safeLongValue("1234567890", 0L, US));
        }
    }

    @Nested
    @DisplayName("safeEnumValue Tests")
    class SafeEnumValueTests {

        enum TestEnum { VALUE1, VALUE2 }

        @Test
        @DisplayName("Should parse valid enum value")
        void shouldParseValidEnumValue() {
            assertEquals(TestEnum.VALUE1, StringI18N.safeEnumValue("VALUE1", TestEnum.class, TestEnum.VALUE2));
        }

        @Test
        @DisplayName("Should return default for invalid enum value")
        void shouldReturnDefaultForInvalidEnumValue() {
            assertEquals(TestEnum.VALUE2, StringI18N.safeEnumValue("INVALID", TestEnum.class, TestEnum.VALUE2));
        }

        @Test
        @DisplayName("Should return default for null")
        void shouldReturnDefaultForNull() {
            assertEquals(TestEnum.VALUE2, StringI18N.safeEnumValue(null, TestEnum.class, TestEnum.VALUE2));
        }
    }

    @Nested
    @DisplayName("pad Tests")
    class PadTests {

        @Test
        @DisplayName("Should pad string to length with spaces")
        void shouldPadStringToLengthWithSpaces() {
            assertEquals("test  ", StringI18N.pad("test", 6));
        }

        @Test
        @DisplayName("Should pad string with custom character")
        void shouldPadStringWithCustomCharacter() {
            assertEquals("test00", StringI18N.pad("test", 6, '0'));
        }

        @Test
        @DisplayName("Should return string unchanged if already at length")
        void shouldReturnStringUnchangedIfAlreadyAtLength() {
            assertEquals("test", StringI18N.pad("test", 4));
        }

        @Test
        @DisplayName("Should handle null string")
        void shouldHandleNullString() {
            assertEquals("    ", StringI18N.pad(null, 4));
        }
    }

    @Nested
    @DisplayName("maxLength Tests")
    class MaxLengthTests {

        @Test
        @DisplayName("Should truncate long string")
        void shouldTruncateLongString() {
            assertEquals("test", StringI18N.maxLength("testing", 4));
        }

        @Test
        @DisplayName("Should return string unchanged if shorter")
        void shouldReturnStringUnchangedIfShorter() {
            assertEquals("hi", StringI18N.maxLength("hi", 10));
        }

        @Test
        @DisplayName("Should return empty string for null")
        void shouldReturnEmptyStringForNull() {
            assertEquals("", StringI18N.maxLength(null, 10));
        }
    }

    @Nested
    @DisplayName("trimBoth Tests")
    class TrimBothTests {

        @Test
        @DisplayName("Should trim leading and trailing whitespace")
        void shouldTrimLeadingAndTrailingWhitespace() {
            assertEquals("test", StringI18N.trimBoth("  test  "));
        }

        @Test
        @DisplayName("Should return empty string for null")
        void shouldReturnEmptyStringForNull() {
            assertEquals("", StringI18N.trimBoth(null));
        }
    }

    @Nested
    @DisplayName("trimLeading Tests")
    class TrimLeadingTests {

        @Test
        @DisplayName("Should trim leading whitespace")
        void shouldTrimLeadingWhitespace() {
            assertEquals("test  ", StringI18N.trimLeading("  test  "));
        }

        @Test
        @DisplayName("Should return null for null input")
        void shouldReturnNullForNullInput() {
            assertNull(StringI18N.trimLeading(null));
        }
    }

    @Nested
    @DisplayName("trimTrailing Tests")
    class TrimTrailingTests {

        @Test
        @DisplayName("Should trim trailing whitespace")
        void shouldTrimTrailingWhitespace() {
            assertEquals("test", StringI18N.trimTrailing("test  "));
        }

        @Test
        @DisplayName("Should return null for null input")
        void shouldReturnNullForNullInput() {
            assertNull(StringI18N.trimTrailing(null));
        }
    }

    @Nested
    @DisplayName("removeNonDigits Tests")
    class RemoveNonDigitsTests {

        @Test
        @DisplayName("Should remove non-digit characters")
        void shouldRemoveNonDigitCharacters() {
            assertEquals("123", StringI18N.removeNonDigits("a1b2c3"));
        }

        @Test
        @DisplayName("Should return null for null input")
        void shouldReturnNullForNullInput() {
            assertNull(StringI18N.removeNonDigits(null));
        }

        @Test
        @DisplayName("Should return empty string for no digits")
        void shouldReturnEmptyStringForNoDigits() {
            assertEquals("", StringI18N.removeNonDigits("abc"));
        }
    }

    @Nested
    @DisplayName("isAllAlpha Tests")
    class IsAllAlphaTests {

        @Test
        @DisplayName("Should return true for all alpha")
        void shouldReturnTrueForAllAlpha() {
            assertTrue(StringI18N.isAllAlpha("Hello", US));
        }

        @Test
        @DisplayName("Should return false for alphanumeric")
        void shouldReturnFalseForAlphanumeric() {
            assertFalse(StringI18N.isAllAlpha("Hello123", US));
        }

        @Test
        @DisplayName("Should return false for null")
        void shouldReturnFalseForNull() {
            assertFalse(StringI18N.isAllAlpha(null, US));
        }
    }

    @Nested
    @DisplayName("isAllNumbers Tests")
    class IsAllNumbersTests {

        @Test
        @DisplayName("Should return true for all numbers")
        void shouldReturnTrueForAllNumbers() {
            assertTrue(StringI18N.isAllNumbers("12345", US));
        }

        @Test
        @DisplayName("Should return false for alphanumeric")
        void shouldReturnFalseForAlphanumeric() {
            assertFalse(StringI18N.isAllNumbers("123abc", US));
        }
    }

    @Nested
    @DisplayName("isAllAlphaNum Tests")
    class IsAllAlphaNumTests {

        @Test
        @DisplayName("Should return true for alphanumeric")
        void shouldReturnTrueForAlphanumeric() {
            assertTrue(StringI18N.isAllAlphaNum("Hello123", US));
        }

        @Test
        @DisplayName("Should return false for special characters")
        void shouldReturnFalseForSpecialCharacters() {
            assertFalse(StringI18N.isAllAlphaNum("Hello!", US));
        }
    }

    @Nested
    @DisplayName("hasAlphas Tests")
    class HasAlphasTests {

        @Test
        @DisplayName("Should return true when has letters")
        void shouldReturnTrueWhenHasLetters() {
            assertTrue(StringI18N.hasAlphas("123abc", US));
        }

        @Test
        @DisplayName("Should return false for numbers only")
        void shouldReturnFalseForNumbersOnly() {
            assertFalse(StringI18N.hasAlphas("12345", US));
        }

        @Test
        @DisplayName("Should return false for null")
        void shouldReturnFalseForNull() {
            assertFalse(StringI18N.hasAlphas(null, US));
        }
    }

    @Nested
    @DisplayName("escapeStringForJson Tests")
    class EscapeStringForJsonTests {

        @Test
        @DisplayName("Should escape quotes")
        void shouldEscapeQuotes() {
            assertEquals("Hello \\\"World\\\"", StringI18N.escapeStringForJson("Hello \"World\""));
        }

        @Test
        @DisplayName("Should escape newlines")
        void shouldEscapeNewlines() {
            assertEquals("Line1\\nLine2", StringI18N.escapeStringForJson("Line1\nLine2"));
        }

        @Test
        @DisplayName("Should escape tabs")
        void shouldEscapeTabs() {
            assertEquals("Col1\\tCol2", StringI18N.escapeStringForJson("Col1\tCol2"));
        }

        @Test
        @DisplayName("Should escape backslashes")
        void shouldEscapeBackslashes() {
            assertEquals("path\\\\to\\\\file", StringI18N.escapeStringForJson("path\\to\\file"));
        }
    }

    @Nested
    @DisplayName("getSimilarityScore Tests")
    class GetSimilarityScoreTests {

        @Test
        @DisplayName("Should return 1.0 for identical strings")
        void shouldReturnOneForIdenticalStrings() {
            assertEquals(1.0, StringI18N.getSimilarityScore("test", "test"), 0.01);
        }

        @Test
        @DisplayName("Should return high score for similar strings")
        void shouldReturnHighScoreForSimilarStrings() {
            double score = StringI18N.getSimilarityScore("testing", "tested");
            assertTrue(score > 0.7);
        }

        @Test
        @DisplayName("Should return low score for different strings")
        void shouldReturnLowScoreForDifferentStrings() {
            double score = StringI18N.getSimilarityScore("abc", "xyz");
            assertTrue(score < 0.5);
        }
    }

    @Nested
    @DisplayName("parseSentenceIntoWords Tests")
    class ParseSentenceIntoWordsTests {

        @Test
        @DisplayName("Should parse sentence into words")
        void shouldParseSentenceIntoWords() {
            String[] words = StringI18N.parseSentenceIntoWords("Hello world test", US);
            assertEquals(3, words.length);
            assertEquals("Hello", words[0]);
            assertEquals("world", words[1]);
            assertEquals("test", words[2]);
        }
    }

    @Nested
    @DisplayName("parseBufferIntoSentences Tests")
    class ParseBufferIntoSentencesTests {

        @Test
        @DisplayName("Should parse buffer into sentences")
        void shouldParseBufferIntoSentences() {
            String[] sentences = StringI18N.parseBufferIntoSentences("Hello. World. Test.", US);
            assertEquals(3, sentences.length);
        }
    }

    @Nested
    @DisplayName("buildSpaceSeparatedString Tests")
    class BuildSpaceSeparatedStringTests {

        @Test
        @DisplayName("Should build space separated string")
        void shouldBuildSpaceSeparatedString() {
            List<String> parts = Arrays.asList("one", "two", "three");
            assertEquals("one two three", StringI18N.buildSpaceSeparatedString(parts));
        }
    }

    @Nested
    @DisplayName("normalizeNewLines Tests")
    class NormalizeNewLinesTests {

        @Test
        @DisplayName("Should normalize CRLF to LF")
        void shouldNormalizeCrlfToLf() {
            String result = StringI18N.normalizeNewLines("line1\r\nline2", US);
            assertEquals("line1\nline2", result);
        }

        @Test
        @DisplayName("Should normalize CR to LF")
        void shouldNormalizeCrToLf() {
            String result = StringI18N.normalizeNewLines("line1\rline2", US);
            assertEquals("line1\nline2", result);
        }

        @Test
        @DisplayName("Should return null for null input")
        void shouldReturnNullForNullInput() {
            assertNull(StringI18N.normalizeNewLines(null, US));
        }
    }
}
