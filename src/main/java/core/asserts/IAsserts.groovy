package core.asserts

import org.hamcrest.Matcher

/**
 * Provides various assertion methods.<br>
 * There are two types of methods declared:<br>
 * - Usual methods that perform assertion and throw {@link AssertionError} if it is failed.<br>
 * - Methods which names start with '_'. They wrap usual methods into {@link Closure} and return the result.
 * Thus an assertion can be performed later.
 * Such methods can be used to write 'soft' assertion using {@link IAsserts#assertAll(groovy.lang.Closure[])} method.
 */
@SuppressWarnings('MethodName')
interface IAsserts {

    /**
     * Asserts that a condition is true. If it isn't, an {@link AssertionError} will be thrown.
     * @param description The assertion description.
     * @param condition Condition to be checked.
     */
    void assertTrue(final String description, final boolean condition)

    /**
     * Wraps {@link IAsserts#assertTrue(java.lang.String, boolean)} into a {@link Closure}.
     * @param description The assertion description.
     * @param condition Condition to be checked.
     * @return Prepared assertion that can be executed later.
     */
    Closure _assertTrue(final String description, final boolean condition)

    /**
     * Asserts that a condition is false. If it isn't an {@link AssertionError} will be thrown.
     * @param description The assertion description.
     * @param condition Condition to be checked.
     */
    void assertFalse(final String description, final boolean condition)

    /**
     * Wraps {@link IAsserts#assertFalse(java.lang.String, boolean)} into a {@link Closure}.
     * @param description The assertion description.
     * @param condition Condition to be checked.
     * @return Prepared assertion that can be executed later.
     */
    Closure _assertFalse(final String description, final boolean condition)

    /**
     * Asserts that two string are equal. If they are not, an {@link AssertionError} will be thrown.
     * If <code>expected</code> and <code>actual</code> are <code>null</code> or <code>empty</code>,
     * they are considered equal.
     * @param description The assertion description.
     * @param expected Expected value.
     * @param actual Actual value.
     */
    void assertEquals(final String description, final String expected, final String actual)

    /**
     * Wraps {@link IAsserts#assertEquals(java.lang.String, java.lang.String, java.lang.String)} into a {@link Closure}.
     * @param description The assertion description.
     * @param expected Expected value.
     * @param actual Actual value.
     * @return Prepared assertion that can be executed later.
     */
    Closure _assertEquals(final String description, final String expected, final String actual)

    /**
     * Asserts that two objects are equal. If they are not, an {@link AssertionError} will be thrown.
     * If <code>expected</code> and <code>actual</code> are <code>null</code>, they are considered equal.
     * @param description The assertion description.
     * @param expected Expected value.
     * @param actual Actual value.
     */
    void assertEquals(final String description, final Object expected, final Object actual)

    /**
     * Wraps {@link IAsserts#assertEquals(java.lang.String, java.lang.Object, java.lang.Object)} into a {@link Closure}.
     * @param description The assertion description.
     * @param expected Expected value.
     * @param actual Actual value.
     * @return Prepared assertion that can be executed later.
     */
    Closure _assertEquals(final String description, final Object expected, final Object actual)

    /**
     * Asserts that two string are <b>not</b> equal. If they are, an {@link AssertionError} will be thrown.
     * If <code>expected</code> and <code>actual</code> are <code>null</code> or <code>empty</code>,
     * they are considered equal.
     * @param description The assertion description.
     * @param expected Expected value.
     * @param actual Actual value.
     */
    void assertNotEquals(final String description, final String expected, final String actual)

    /**
     * Wraps {@link IAsserts#assertNotEquals(java.lang.String, java.lang.String, java.lang.String)} into a
     * {@link Closure}.
     * @param description The assertion description.
     * @param expected Expected value.
     * @param actual Actual value.
     * @return Prepared assertion that can be executed later.
     */
    Closure _assertNotEquals(final String description, final String expected, final String actual)

    /**
     * Asserts that two objects are <b>not</b> equal. If they are, an {@link AssertionError} will be thrown.
     * If <code>expected</code> and <code>actual</code> are <code>null</code>, they are considered equal.
     * @param description The assertion description.
     * @param expected Expected value.
     * @param actual Actual value.
     */
    void assertNotEquals(final String description, final Object expected, final Object actual)

    /**
     * Wraps {@link IAsserts#assertNotEquals(java.lang.String, java.lang.Object, java.lang.Object)} into a
     * {@link Closure}.
     * @param description The assertion description.
     * @param expected Expected value.
     * @param actual Actual value.
     * @return Prepared assertion that can be executed later.
     */
    Closure _assertNotEquals(final String description, final Object expected, final Object actual)

    /**
     * Asserts that two collections are of the same size and contain the same elements in any order.
     * If they are not, an {@link AssertionError} will be thrown.
     * If <code>expected</code> and <code>actual</code> are <code>null</code>, they are considered equal.
     * @param description The assertion description.
     * @param expected Expected collection.
     * @param actual Actual collection.
     */
    void assertEqualCollections(final String description, final Collection expected, final Collection actual)

    /**
     * Wraps {@link IAsserts#assertEqualCollections(java.lang.String, java.util.Collection, java.util.Collection)} into
     * a {@link Closure}.
     * @param description The assertion description.
     * @param expected Expected collection.
     * @param actual Actual collection.
     * @return Prepared assertion that can be executed later.
     */
    Closure _assertEqualCollections(final String description, final Collection expected, final Collection actual)

    /**
     * Asserts that two collections have different size or contain different elements.
     * If they are not, an {@link AssertionError} will be thrown.
     * If <code>expected</code> and <code>actual</code> are <code>null</code>, they are considered equal.
     * @param description The assertion description.
     * @param expected Expected collection.
     * @param actual Actual collection.
     */
    void assertNotEqualCollections(final String description, final Collection expected, final Collection actual)

    /**
     * Wraps {@link IAsserts#assertNotEqualCollections(java.lang.String, java.util.Collection, java.util.Collection)}
     * into a {@link Closure}.
     * @param description The assertion description.
     * @param expected Expected collection.
     * @param actual Actual collection.
     * @return Prepared assertion that can be executed later.
     */
    Closure _assertNotEqualCollections(final String description, final Collection expected, final Collection actual)

    /**
     * Asserts that an object isn't null. If it is, an {@link AssertionError} will be thrown.
     * @param description The assertion description.
     * @param object Object to check or <code>null</code>.
     */
    void assertNotNull(final String description, final Object object)

    /**
     * Wraps {@link IAsserts#assertNotNull(java.lang.String, java.lang.Object)} into a {@link Closure}.
     * @param description The assertion description.
     * @param object Object to check or <code>null</code>.
     * @return Prepared assertion that can be executed later.
     */
    Closure _assertNotNull(final String description, final Object object)

    /**
     * Asserts that an object is null. If it is not, an {@link AssertionError} will be thrown.
     * @param description The assertion description.
     * @param object Object to check or <code>null</code>.
     */
    void assertNull(final String description, final Object object)

    /**
     * Wraps {@link IAsserts#assertNull(java.lang.String, java.lang.Object)} into a {@link Closure}.
     * @param description The assertion description.
     * @param object Object to check or <code>null</code>.
     * @return Prepared assertion that can be executed later.
     */
    Closure _assertNull(final String description, final Object object)

    /**
     * Asserts that <code>actual</code> satisfies the condition specified by <code>matcher</code>.
     * If it is not, an {@link AssertionError} will be thrown.
     * @param description The assertion description.
     * @param actual A value being compared.
     * @param matcher An expression, built of {@link Matcher}s, specifying allowed values.
     */
    public <T> void assertThat(final String description, final T actual, final Matcher<? super T> matcher)

    /**
     * Wraps {@link IAsserts#assertThat(java.lang.String, java.lang.Object, org.hamcrest.Matcher)} into a
     * {@link Closure}.
     * @param description The assertion description.
     * @param actual A value being compared.
     * @param matcher An expression, built of {@link Matcher}s, specifying allowed values.
     * @return Prepared assertion that can be executed later.
     */
    public <T> Closure _assertThat(final String description, final T actual, final Matcher<? super T> matcher)

    /**
     * Asserts that <code>actual</code> satisfies the condition specified by <code>matcher</code>.
     * If it is not, an {@link AssertionError} will be thrown.
     * @param description The assertion description.
     * @param actual A value being compared.
     * @param matcher An expression, built of {@link Matcher}s, specifying allowed values.
     */
    public <T> void assertThat(final GString description, final T actual, final Matcher<? super T> matcher)

    /**
     * Wraps {@link IAsserts#assertThat(java.lang.String, java.lang.Object, org.hamcrest.Matcher)} into a
     * {@link Closure}.
     * @param description The assertion description.
     * @param actual A value being compared.
     * @param matcher An expression, built of {@link Matcher}s, specifying allowed values.
     * @return Prepared assertion that can be executed later.
     */
    public <T> Closure _assertThat(final GString description, final T actual, final Matcher<? super T> matcher)

    /**
     * Logs information about passed assertion.
     * @param description The assertion description.
     */
    void assertPassed(final String description)

    /**
     * Logs information about failed assertion and trows an {@link AssertionError}.
     * @param description The assertion description.
     */
    void assertFailed(final String description)

    /**
     * Logs information about failed assertion and trows an {@link AssertionError}.
     * @param cause Cause of the failed assertion.
     */
    void assertFailed(final Throwable cause)

    /**
     * Logs information about failed assertion and trows an {@link AssertionError}.
     * @param description The assertion description.
     * @param cause Cause of the failed assertion.
     */
    void assertFailed(final String description, final Throwable cause)

    /**
     * Accepts an array of {@link Closure}s which perform some assertions and throw an {@link AssertionError} in case
     * of failure.<br>
     * All input assertions will be performed sequentially, all {@link AssertionError}s will be collected and summary
     * result will be logged.<br>
     * In case if any assertion throws an {@link AssertionError} other assertions will be executed any way. But at the
     * end an {@link AssertionError} which aggregates all errors from previously executed assertions will be thrown.
     * @param asserts An array of assertions.
     */
    void assertAll(Closure... asserts)
}
