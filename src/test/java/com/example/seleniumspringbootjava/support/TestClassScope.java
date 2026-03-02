package com.example.seleniumspringbootjava.support;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Custom Spring scope with lifecycle tied to a single JUnit test class.
 *
 * Each test class will run with a unique scope id (set by
 * {@link TestClassScopeContext}).
 * All beans declared with @Scope(\"testClass\") will be singletons within that
 * test class,
 * and isolated from other test classes (even when executed in parallel).
 */
public class TestClassScope implements Scope {

    private final Map<String, Map<String, Object>> scopedObjects = new ConcurrentHashMap<>();
    private final Map<String, Map<String, Runnable>> destructionCallbacks = new ConcurrentHashMap<>();

    @Override
    public Object get(String name, ObjectFactory<?> objectFactory) {
        String scopeId = TestClassScopeContext.getScopeId();
        if (scopeId == null) {
            throw new IllegalStateException("TestClassScopeContext scopeId is not set. " +
                    "Ensure TestClassScopeContext is initialized before accessing @Scope(\"testClass\") beans.");
        }

        Map<String, Object> scope = scopedObjects.computeIfAbsent(scopeId, id -> new ConcurrentHashMap<>());
        return scope.computeIfAbsent(name, n -> objectFactory.getObject());
    }

    @Override
    public Object remove(String name) {
        String scopeId = TestClassScopeContext.getScopeId();
        if (scopeId == null) {
            return null;
        }
        Map<String, Object> scope = scopedObjects.get(scopeId);
        if (scope == null) {
            return null;
        }
        return scope.remove(name);
    }

    @Override
    public void registerDestructionCallback(String name, Runnable callback) {
        String scopeId = TestClassScopeContext.getScopeId();
        if (scopeId == null) {
            return;
        }
        destructionCallbacks
                .computeIfAbsent(scopeId, id -> new ConcurrentHashMap<>())
                .put(name, callback);
    }

    @Override
    public Object resolveContextualObject(String key) {
        return null;
    }

    @Override
    public String getConversationId() {
        return TestClassScopeContext.getScopeId();
    }

    /**
     * Destroy all scoped beans for the current scope id and run destruction
     * callbacks.
     */
    public void destroyCurrentScope() {
        String scopeId = TestClassScopeContext.getScopeId();
        if (scopeId == null) {
            return;
        }

        Map<String, Runnable> callbacks = destructionCallbacks.remove(scopeId);
        if (callbacks != null) {
            callbacks.values().forEach(r -> {
                try {
                    r.run();
                } catch (Exception ignored) {
                    // best effort
                }
            });
        }
        scopedObjects.remove(scopeId);
    }
}
