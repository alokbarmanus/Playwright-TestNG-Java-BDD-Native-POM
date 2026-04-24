package com.automation.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Per-scenario shared state injected by Cucumber PicoContainer.
 *
 * <p>Holds test data loaded from a JSON file (via the {@code @dataFile:} tag)
 * and exposes {@link #resolve(String)} to substitute {@code ${key}} placeholders
 * in Gherkin step parameters.
 *
 * <p>PicoContainer creates one instance per scenario, so no manual reset is needed.
 */
public class ScenarioContext {

    private static final Pattern PLACEHOLDER = Pattern.compile("\\$\\{([^}]+)}");

    private final Map<String, String> data = new HashMap<>();
    private final Map<String, Map<String, String>> nestedData = new HashMap<>();

    // -------------------------------------------------------------------------
    // Data population
    // -------------------------------------------------------------------------

    /** Adds all entries from {@code map} into the flat context. */
    public void putAll(Map<String, String> map) {
        data.putAll(map);
    }

    /** Stores a single key-value pair in the flat context. */
    public void put(String key, String value) {
        data.put(key, value);
    }

    /** Adds all named sub-maps for data-table / nested-object scenarios. */
    public void putNestedAll(Map<String, Map<String, String>> map) {
        nestedData.putAll(map);
    }

    // -------------------------------------------------------------------------
    // Data access
    // -------------------------------------------------------------------------

    /**
     * Resolves all {@code ${key}} placeholders in {@code value} using the
     * context data. If the value contains no placeholder it is returned as-is,
     * making this safe to call on every step parameter.
     *
     * @param value raw step parameter (may contain {@code ${key}} tokens)
     * @return fully resolved string
     */
    public String resolve(String value) {
        if (value == null || !value.contains("${")) {
            return value;
        }
        Matcher matcher = PLACEHOLDER.matcher(value);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String key         = matcher.group(1);
            String replacement = data.getOrDefault(key, matcher.group(0)); // keep original if not found
            matcher.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    /** Returns {@code true} if any test data has been loaded. */
    public boolean hasData() {
        return !data.isEmpty();
    }

    /** Returns the raw flat data map (read-only view). */
    public Map<String, String> getData() {
        return java.util.Collections.unmodifiableMap(data);
    }

    /**
     * Resolves a {@code ${key}} expression (or a bare key) to the named nested
     * sub-map loaded from a JSON data file.
     *
     * <p>Example: {@code resolveMap("${loginData}")} returns the map stored
     * under the key {@code loginData}.
     *
     * @param expression placeholder string such as {@code "${loginData}"} or bare key
     * @return the sub-map, or an empty map if the key is not found
     */
    public Map<String, String> resolveMap(String expression) {
        if (expression == null) {
            return java.util.Collections.emptyMap();
        }
        String key = (expression.startsWith("${") && expression.endsWith("}"))
                ? expression.substring(2, expression.length() - 1)
                : expression;
        return nestedData.getOrDefault(key, java.util.Collections.emptyMap());
    }
}
