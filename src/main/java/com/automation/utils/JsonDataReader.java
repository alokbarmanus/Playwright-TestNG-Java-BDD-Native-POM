package com.automation.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Reads a JSON test-data file from the classpath and returns its contents
 * as a flat {@code Map<String, String>}.
 *
 * <h3>Supported JSON shapes</h3>
 * <ul>
 *   <li><b>Array of objects</b> — the <em>first</em> element's scalar fields
 *       are flattened into the map:
 *       <pre>[{"username":"Admin","password":"admin123"}]</pre>
 *       → {@code {username=Admin, password=admin123}}</li>
 *   <li><b>Object</b> — scalar fields at the top level are mapped directly;
 *       nested arrays of objects are flattened from their first element:
 *       <pre>{"validCredentials":[{"username":"Admin"}]}</pre>
 *       → {@code {username=Admin}}</li>
 * </ul>
 */
public class JsonDataReader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonDataReader() {
        // utility class — no instantiation
    }

    /**
     * Loads the JSON file at {@code classpathPath} and returns a flat map of
     * scalar values.
     *
     * @param classpathPath classpath-relative path, e.g.
     *                      {@code environments/dev/data/loginData.json}
     * @return flat key→value map of the first data record
     * @throws RuntimeException if the file is missing or cannot be parsed
     */
    public static Map<String, String> load(String classpathPath) {
        try (InputStream is = JsonDataReader.class
                .getClassLoader()
                .getResourceAsStream(classpathPath)) {

            if (is == null) {
                throw new RuntimeException(
                        "Test-data file not found on classpath: " + classpathPath);
            }

            JsonNode root = MAPPER.readTree(is);
            return flatten(root);

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to parse test-data file: " + classpathPath, e);
        }
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private static Map<String, String> flatten(JsonNode root) {
        Map<String, String> result = new HashMap<>();

        if (root.isArray() && root.size() > 0) {
            // Array of objects — take the first element
            flattenObject(root.get(0), result);

        } else if (root.isObject()) {
            Iterator<Map.Entry<String, JsonNode>> fields = root.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                JsonNode value = entry.getValue();

                if (isScalar(value)) {
                    result.put(entry.getKey(), value.asText());
                } else if (value.isArray() && value.size() > 0 && value.get(0).isObject()) {
                    // Nested array — flatten first element's scalar fields
                    flattenObject(value.get(0), result);
                }
            }
        }

        return result;
    }

    private static void flattenObject(JsonNode node, Map<String, String> target) {
        if (!node.isObject()) return;
        Iterator<Map.Entry<String, JsonNode>> fields = node.fields();
        while (fields.hasNext()) {
            Map.Entry<String, JsonNode> entry = fields.next();
            if (isScalar(entry.getValue())) {
                target.put(entry.getKey(), entry.getValue().asText());
            }
        }
    }

    /**
     * Loads the JSON file at {@code classpathPath} and returns a map of named
     * sub-maps for each top-level key whose value is a JSON object.
     *
     * <h3>Supported shape</h3>
     * <pre>
     * [{"loginData":{"username":"Admin","password":"admin123"},
     *   "addressData":{"street":"123 Main St",...}}]
     * </pre>
     * If the root is an array, the <em>first</em> element is used.
     *
     * @param classpathPath classpath-relative path
     * @return map of groupKey → flat key→value map
     * @throws RuntimeException if the file is missing or cannot be parsed
     */
    public static Map<String, Map<String, String>> loadNestedMap(String classpathPath) {
        try (InputStream is = JsonDataReader.class
                .getClassLoader()
                .getResourceAsStream(classpathPath)) {

            if (is == null) {
                throw new RuntimeException(
                        "Test-data file not found on classpath: " + classpathPath);
            }

            JsonNode root = MAPPER.readTree(is);
            // If array, use first element; otherwise use the object directly
            JsonNode target = (root.isArray() && root.size() > 0) ? root.get(0) : root;

            Map<String, Map<String, String>> result = new HashMap<>();
            if (target.isObject()) {
                Iterator<Map.Entry<String, JsonNode>> fields = target.fields();
                while (fields.hasNext()) {
                    Map.Entry<String, JsonNode> entry = fields.next();
                    if (entry.getValue().isObject()) {
                        Map<String, String> inner = new HashMap<>();
                        flattenObject(entry.getValue(), inner);
                        result.put(entry.getKey(), inner);
                    }
                }
            }
            return result;

        } catch (IOException e) {
            throw new RuntimeException(
                    "Failed to parse test-data file: " + classpathPath, e);
        }
    }

    private static boolean isScalar(JsonNode node) {
        return node.isTextual() || node.isNumber() || node.isBoolean();
    }
}
