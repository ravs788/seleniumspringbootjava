package com.example.seleniumspringbootjava.support;

/**
 * Holds the current \"testClass\" scope id for the executing test class.
 *
 * This uses JUnit's per-class lifecycle: each test class will set its own scope
 * id
 * in {@link com.example.seleniumspringbootjava.support.SpringSeleniumTestBase}
 * before accessing any @Scope(\"testClass\") beans, and clears it afterwards.
 */
public final class TestClassScopeContext {

    private static final InheritableThreadLocal<String> SCOPE_ID = new InheritableThreadLocal<>();

    private TestClassScopeContext() {
    }

    public static String getScopeId() {
        return SCOPE_ID.get();
    }

    public static void setScopeId(String scopeId) {
        SCOPE_ID.set(scopeId);
    }

    public static void clear() {
        SCOPE_ID.remove();
    }
}
