package com.example.seleniumspringbootjava.dataloader;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

/**
 * JSON implementation of {@link DataLoader}.
 *
 * <p>
 * Convention:
 * <ul>
 * <li>Per test class common data:
 * {@code testdata/<TestClassSimpleName>/common.json}</li>
 * <li>Per test case data:
 * {@code testdata/<TestClassSimpleName>/<testCaseId>.json}</li>
 * </ul>
 *
 * <p>
 * Note: JSON files typically live under
 * {@code src/test/resources/testdata/...}.
 * When running tests, Maven Surefire adds {@code target/test-classes} to the
 * classpath,
 * so these resources are accessible from this main-sources class.
 */
public final class JsonDataLoader implements DataLoader {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public Map<String, Object> loadCommon(Class<?> testClass) {
        String path = "testdata/" + testClass.getSimpleName() + "/common.json";
        return load(path);
    }

    @Override
    public Map<String, Object> loadTestCase(Class<?> testClass, String testCaseId) {
        String path = "testdata/" + testClass.getSimpleName() + "/" + testCaseId + ".json";
        return load(path);
    }

    @Override
    public Map<String, Object> load(String classpathResource) {
        try (InputStream is = JsonDataLoader.class.getClassLoader().getResourceAsStream(classpathResource)) {
            if (is == null) {
                throw new IllegalStateException("Test data not found on classpath: " + classpathResource);
            }
            return MAPPER.readValue(is, new TypeReference<>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException("Failed reading test data: " + classpathResource, e);
        }
    }
}
