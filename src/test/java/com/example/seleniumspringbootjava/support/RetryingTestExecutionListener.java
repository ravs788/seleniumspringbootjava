package com.example.seleniumspringbootjava.support;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

/**
 * JUnit Platform listener used to enable retries in a Maven/Surefire run.
 *
 * <p>
 * Important: JUnit Platform listeners cannot "re-run" a test within the same
 * JVM execution by themselves. Retrying is typically implemented using:
 * <ul>
 * <li>JUnit Jupiter extensions (per test) OR</li>
 * <li>Surefire rerunFailingTestsCount (JUnit4/TestNG) OR</li>
 * <li>Separate re-invocation of Maven with a failed-tests selector</li>
 * </ul>
 *
 * <p>
 * This class is intentionally not used. Kept as a placeholder to avoid
 * confusion.
 * </p>
 */
public final class RetryingTestExecutionListener implements TestExecutionListener {

    @Override
    public void testPlanExecutionStarted(TestPlan testPlan) {
        // no-op
    }

    @Override
    public void executionFinished(TestIdentifier testIdentifier, TestExecutionResult testExecutionResult) {
        // no-op
    }

    @Override
    public void testPlanExecutionFinished(TestPlan testPlan) {
        // no-op
    }
}
