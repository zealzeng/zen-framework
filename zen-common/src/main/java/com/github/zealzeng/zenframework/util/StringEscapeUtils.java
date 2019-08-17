/*
 * Copyright (c) 2016, All rights reserved.
 */
package com.github.zealzeng.zenframework.util;

/**
 * Escapes and unescapes string, 
 * try to implement safe method to escapse sql while having time 
 * @author Zeal 2016年4月26日
 */
public class StringEscapeUtils extends org.apache.commons.text.StringEscapeUtils {
	
//	// Java and JavaScript
//    //--------------------------------------------------------------------------
//    /**
//     * <p>Escapes the characters in a {@code String} using Java String rules.</p>
//     *
//     * <p>Deals correctly with quotes and control-chars (tab, backslash, cr, ff, etc.) </p>
//     *
//     * <p>So a tab becomes the characters {@code '\\'} and
//     * {@code 't'}.</p>
//     *
//     * <p>The only difference between Java strings and JavaScript strings
//     * is that in JavaScript, a single quote and forward-slash (/) are escaped.</p>
//     *
//     * <p>Example:</p>
//     * <pre>
//     * input string: He didn't say, "Stop!"
//     * output string: He didn't say, \"Stop!\"
//     * </pre>
//     *
//     * @param input  String to escape values in, may be null
//     * @return String with escaped values, {@code null} if null string input
//     */
//    public static final String escapeJava(final String input) {
//        return org.apache.commons.lang3.StringEscapeUtils.escapeJava(input);
//    }
//
//    /**
//     * <p>Escapes the characters in a {@code String} using EcmaScript String rules.</p>
//     * <p>Escapes any values it finds into their EcmaScript String form.
//     * Deals correctly with quotes and control-chars (tab, backslash, cr, ff, etc.) </p>
//     *
//     * <p>So a tab becomes the characters {@code '\\'} and
//     * {@code 't'}.</p>
//     *
//     * <p>The only difference between Java strings and EcmaScript strings
//     * is that in EcmaScript, a single quote and forward-slash (/) are escaped.</p>
//     *
//     * <p>Note that EcmaScript is best known by the JavaScript and ActionScript dialects. </p>
//     *
//     * <p>Example:</p>
//     * <pre>
//     * input string: He didn't say, "Stop!"
//     * output string: He didn\'t say, \"Stop!\"
//     * </pre>
//     *
//     * @param input  String to escape values in, may be null
//     * @return String with escaped values, {@code null} if null string input
//     *
//     * @since 3.0
//     */
//    public static final String escapeEcmaScript(final String input) {
//        return org.apache.commons.lang3.StringEscapeUtils.escapeEcmaScript(input);
//    }
//
//    /**
//     * <p>Escapes the characters in a {@code String} using Json String rules.</p>
//     * <p>Escapes any values it finds into their Json String form.
//     * Deals correctly with quotes and control-chars (tab, backslash, cr, ff, etc.) </p>
//     *
//     * <p>So a tab becomes the characters {@code '\\'} and
//     * {@code 't'}.</p>
//     *
//     * <p>The only difference between Java strings and Json strings
//     * is that in Json, forward-slash (/) is escaped.</p>
//     *
//     * <p>See http://www.ietf.org/rfc/rfc4627.txt for further details. </p>
//     *
//     * <p>Example:</p>
//     * <pre>
//     * input string: He didn't say, "Stop!"
//     * output string: He didn't say, \"Stop!\"
//     * </pre>
//     *
//     * @param input  String to escape values in, may be null
//     * @return String with escaped values, {@code null} if null string input
//     *
//     * @since 3.2
//     */
//    public static final String escapeJson(final String input) {
//        return org.apache.commons.lang3.StringEscapeUtils.escapeJson(input);
//    }
//
//    /**
//     * <p>Unescapes any Java literals found in the {@code String}.
//     * For example, it will turn a sequence of {@code '\'} and
//     * {@code 'n'} into a newline character, unless the {@code '\'}
//     * is preceded by another {@code '\'}.</p>
//     *
//     * @param input  the {@code String} to unescape, may be null
//     * @return a new unescaped {@code String}, {@code null} if null string input
//     */
//    public static final String unescapeJava(final String input) {
//        return org.apache.commons.lang3.StringEscapeUtils.unescapeJava(input);
//    }
//
//    /**
//     * <p>Unescapes any EcmaScript literals found in the {@code String}.</p>
//     *
//     * <p>For example, it will turn a sequence of {@code '\'} and {@code 'n'}
//     * into a newline character, unless the {@code '\'} is preceded by another
//     * {@code '\'}.</p>
//     *
//     * @see #unescapeJava(String)
//     * @param input  the {@code String} to unescape, may be null
//     * @return A new unescaped {@code String}, {@code null} if null string input
//     *
//     * @since 3.0
//     */
//    public static final String unescapeEcmaScript(final String input) {
//        return org.apache.commons.lang3.StringEscapeUtils.unescapeEcmaScript(input);
//    }
//
//    /**
//     * <p>Unescapes any Json literals found in the {@code String}.</p>
//     *
//     * <p>For example, it will turn a sequence of {@code '\'} and {@code 'n'}
//     * into a newline character, unless the {@code '\'} is preceded by another
//     * {@code '\'}.</p>
//     *
//     * @see #unescapeJava(String)
//     * @param input  the {@code String} to unescape, may be null
//     * @return A new unescaped {@code String}, {@code null} if null string input
//     *
//     * @since 3.2
//     */
//    public static final String unescapeJson(final String input) {
//        return org.apache.commons.lang3.StringEscapeUtils.unescapeJson(input);
//    }
//
//    // HTML and XML
//    //--------------------------------------------------------------------------
//    /**
//     * <p>Escapes the characters in a {@code String} using HTML entities.</p>
//     *
//     * <p>
//     * For example:
//     * </p>
//     * <p><code>"bread" &amp; "butter"</code></p>
//     * becomes:
//     * <p>
//     * <code>&amp;quot;bread&amp;quot; &amp;amp; &amp;quot;butter&amp;quot;</code>.
//     * </p>
//     *
//     * <p>Supports all known HTML 4.0 entities, including funky accents.
//     * Note that the commonly used apostrophe escape character (&amp;apos;)
//     * is not a legal entity and so is not supported). </p>
//     *
//     * @param input  the {@code String} to escape, may be null
//     * @return a new escaped {@code String}, {@code null} if null string input
//     *
//     * @see <a href="http://hotwired.lycos.com/webmonkey/reference/special_characters/">ISO Entities</a>
//     * @see <a href="http://www.w3.org/TR/REC-html32#latin1">HTML 3.2 Character Entities for ISO Latin-1</a>
//     * @see <a href="http://www.w3.org/TR/REC-html40/sgml/entities.html">HTML 4.0 Character entity references</a>
//     * @see <a href="http://www.w3.org/TR/html401/charset.html#h-5.3">HTML 4.01 Character References</a>
//     * @see <a href="http://www.w3.org/TR/html401/charset.html#code-position">HTML 4.01 Code positions</a>
//     *
//     * @since 3.0
//     */
//    public static final String escapeHtml4(final String input) {
//        return org.apache.commons.lang3.StringEscapeUtils.escapeHtml4(input);
//    }
//
//    /**
//     * <p>Escapes the characters in a {@code String} using HTML entities.</p>
//     * <p>Supports only the HTML 3.0 entities. </p>
//     *
//     * @param input  the {@code String} to escape, may be null
//     * @return a new escaped {@code String}, {@code null} if null string input
//     *
//     * @since 3.0
//     */
//    public static final String escapeHtml3(final String input) {
//        return org.apache.commons.lang3.StringEscapeUtils.escapeHtml3(input);
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * <p>Unescapes a string containing entity escapes to a string
//     * containing the actual Unicode characters corresponding to the
//     * escapes. Supports HTML 4.0 entities.</p>
//     *
//     * <p>For example, the string {@code "&lt;Fran&ccedil;ais&gt;"}
//     * will become {@code "<Fran鏰is>"}</p>
//     *
//     * <p>If an entity is unrecognized, it is left alone, and inserted
//     * verbatim into the result string. e.g. {@code "&gt;&zzzz;x"} will
//     * become {@code ">&zzzz;x"}.</p>
//     *
//     * @param input  the {@code String} to unescape, may be null
//     * @return a new unescaped {@code String}, {@code null} if null string input
//     *
//     * @since 3.0
//     */
//    public static final String unescapeHtml4(final String input) {
//        return org.apache.commons.lang3.StringEscapeUtils.unescapeHtml4(input);
//    }
//
//    /**
//     * <p>Unescapes a string containing entity escapes to a string
//     * containing the actual Unicode characters corresponding to the
//     * escapes. Supports only HTML 3.0 entities.</p>
//     *
//     * @param input  the {@code String} to unescape, may be null
//     * @return a new unescaped {@code String}, {@code null} if null string input
//     *
//     * @since 3.0
//     */
//    public static final String unescapeHtml3(final String input) {
//        return org.apache.commons.lang3.StringEscapeUtils.unescapeHtml3(input);
//    }
//
//    /**
//     * <p>Escapes the characters in a {@code String} using XML entities.</p>
//     *
//     * <p>For example: {@code "bread" & "butter"} =&gt;
//     * {@code &quot;bread&quot; &amp; &quot;butter&quot;}.
//     * </p>
//     *
//     * <p>Note that XML 1.0 is a text-only format: it cannot represent control
//     * characters or unpaired Unicode surrogate codepoints, even after escaping.
//     * {@code escapeXml10} will remove characters that do not fit in the
//     * following ranges:</p>
//     *
//     * <p>{@code #x9 | #xA | #xD | [#x20-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]}</p>
//     *
//     * <p>Though not strictly necessary, {@code escapeXml10} will escape
//     * characters in the following ranges:</p>
//     *
//     * <p>{@code [#x7F-#x84] | [#x86-#x9F]}</p>
//     *
//     * <p>The returned string can be inserted into a valid XML 1.0 or XML 1.1
//     * document. If you want to allow more non-text characters in an XML 1.1
//     * document, use {@link #escapeXml11(String)}.</p>
//     *
//     * @param input  the {@code String} to escape, may be null
//     * @return a new escaped {@code String}, {@code null} if null string input
//     * @see #unescapeXml(java.lang.String)
//     * @since 3.3
//     */
//    public static String escapeXml10(final String input) {
//        return org.apache.commons.lang3.StringEscapeUtils.escapeXml10(input);
//    }
//
//    /**
//     * <p>Escapes the characters in a {@code String} using XML entities.</p>
//     *
//     * <p>For example: {@code "bread" & "butter"} =&gt;
//     * {@code &quot;bread&quot; &amp; &quot;butter&quot;}.
//     * </p>
//     *
//     * <p>XML 1.1 can represent certain control characters, but it cannot represent
//     * the null byte or unpaired Unicode surrogate codepoints, even after escaping.
//     * {@code escapeXml11} will remove characters that do not fit in the following
//     * ranges:</p>
//     *
//     * <p>{@code [#x1-#xD7FF] | [#xE000-#xFFFD] | [#x10000-#x10FFFF]}</p>
//     *
//     * <p>{@code escapeXml11} will escape characters in the following ranges:</p>
//     *
//     * <p>{@code [#x1-#x8] | [#xB-#xC] | [#xE-#x1F] | [#x7F-#x84] | [#x86-#x9F]}</p>
//     *
//     * <p>The returned string can be inserted into a valid XML 1.1 document. Do not
//     * use it for XML 1.0 documents.</p>
//     *
//     * @param input  the {@code String} to escape, may be null
//     * @return a new escaped {@code String}, {@code null} if null string input
//     * @see #unescapeXml(java.lang.String)
//     * @since 3.3
//     */
//    public static String escapeXml11(final String input) {
//        return org.apache.commons.lang3.StringEscapeUtils.escapeXml11(input);
//    }
//
//    //-----------------------------------------------------------------------
//    /**
//     * <p>Unescapes a string containing XML entity escapes to a string
//     * containing the actual Unicode characters corresponding to the
//     * escapes.</p>
//     *
//     * <p>Supports only the five basic XML entities (gt, lt, quot, amp, apos).
//     * Does not support DTDs or external entities.</p>
//     *
//     * <p>Note that numerical \\u Unicode codes are unescaped to their respective
//     *    Unicode characters. This may change in future releases. </p>
//     *
//     * @param input  the {@code String} to unescape, may be null
//     * @return a new unescaped {@code String}, {@code null} if null string input
//     * @see #escapeXml(String)
//     * @see #escapeXml10(String)
//     * @see #escapeXml11(String)
//     */
//    public static final String unescapeXml(final String input) {
//        return org.apache.commons.lang3.StringEscapeUtils.unescapeXml(input);
//    }
//
//    //-----------------------------------------------------------------------
//
//    /**
//     * <p>Returns a {@code String} value for a CSV column enclosed in double quotes,
//     * if required.</p>
//     *
//     * <p>If the value contains a comma, newline or double quote, then the
//     *    String value is returned enclosed in double quotes.</p>
//     *
//     * <p>Any double quote characters in the value are escaped with another double quote.</p>
//     *
//     * <p>If the value does not contain a comma, newline or double quote, then the
//     *    String value is returned unchanged.</p>
//     *
//     * see <a href="http://en.wikipedia.org/wiki/Comma-separated_values">Wikipedia</a> and
//     * <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a>.
//     *
//     * @param input the input CSV column String, may be null
//     * @return the input String, enclosed in double quotes if the value contains a comma,
//     * newline or double quote, {@code null} if null string input
//     * @since 2.4
//     */
//    public static final String escapeCsv(final String input) {
//        return org.apache.commons.lang3.StringEscapeUtils.escapeCsv(input);
//    }
//
//    /**
//     * <p>Returns a {@code String} value for an unescaped CSV column. </p>
//     *
//     * <p>If the value is enclosed in double quotes, and contains a comma, newline
//     *    or double quote, then quotes are removed.
//     * </p>
//     *
//     * <p>Any double quote escaped characters (a pair of double quotes) are unescaped
//     *    to just one double quote. </p>
//     *
//     * <p>If the value is not enclosed in double quotes, or is and does not contain a
//     *    comma, newline or double quote, then the String value is returned unchanged.</p>
//     *
//     * see <a href="http://en.wikipedia.org/wiki/Comma-separated_values">Wikipedia</a> and
//     * <a href="http://tools.ietf.org/html/rfc4180">RFC 4180</a>.
//     *
//     * @param input the input CSV column String, may be null
//     * @return the input String, with enclosing double quotes removed and embedded double
//     * quotes unescaped, {@code null} if null string input
//     * @since 2.4
//     */
//    public static final String unescapeCsv(final String input) {
//        return org.apache.commons.lang3.StringEscapeUtils.unescapeCsv(input);
//    }
//
//    public static void main(String[] args) throws Exception {
//    	String str = "{'replace':function(){alert(/xss/);}}";
//    	System.out.println(StringEscapeUtils.escapeJson(str));
//    }


}
