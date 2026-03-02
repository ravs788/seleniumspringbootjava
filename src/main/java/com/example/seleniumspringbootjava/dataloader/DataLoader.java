package com.example.seleniumspringbootjava.dataloader;

import java.util.Map;

/**
 * Abstraction for loading test/support data from the classpath.
 *
 * <p>
 * Typical convention used by implementations:
 * <ul>
 * <li>Per test class common data:
 * {@code testdata/<TestClassSimpleName>/common.*}</li>
 * <li>Per test case data:
 * {@code testdata/<TestClassSimpleName>/<testCaseId>.*}</li>
 * </ul>
 */
public interface DataLoader {

    Map<String, Object> loadCommon(Class<?> testClass);

    Map<String, Object> loadTestCase(Class<?> testClass, String testCaseId);

    Map<String, Object> load(String classpathResource);
}
