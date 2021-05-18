package core.asserts

import org.apache.commons.collections.CollectionUtils
import org.hamcrest.Matcher
import org.hamcrest.StringDescription
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component

import static org.hamcrest.Matchers.notNullValue
import static org.hamcrest.Matchers.nullValue

@SuppressWarnings(['MethodName', 'MethodCount'])
@Lazy
@Component
class Asserts implements IAsserts {

    private static final Logger LOGGER = LoggerFactory.getLogger(Asserts)

    private static final String IS_TRUE = 'The condition is true'
    private static final String IS_FALSE = 'The condition is false'
    private static final String EXPECTED = 'Expected: '
    private static final String ACTUAL = ' Actual: '
    private static final String WHITESPACE = ' '

    @Override
    void assertTrue(final String description, final boolean condition) {
        recordAssertion(description, condition ? IS_TRUE : IS_FALSE, condition)
    }

    @Override
    Closure _assertTrue(final String description, final boolean condition) {
        return { assertTrue(description, condition) }
    }

    @Override
    void assertFalse(final String description, final boolean condition) {
        recordAssertion(description, condition ? IS_TRUE : IS_FALSE, !condition)
    }

    @Override
    Closure _assertFalse(final String description, final boolean condition) {
        return { assertFalse(description, condition) }
    }

    @Override
    void assertEquals(final String description, final String expected, final String actual) {
        assertEquality(description, true, expected, actual)
    }

    @Override
    Closure _assertEquals(final String description, final String expected, final String actual) {
        return { assertEquals(description, expected, actual) }
    }

    @Override
    void assertEquals(final String description, final Object expected, final Object actual) {
        assertEquality(description, true, expected, actual)
    }

    @Override
    Closure _assertEquals(final String description, final Object expected, final Object actual) {
        return { assertEquals(description, expected, actual) }
    }

    @Override
    void assertNotEquals(final String description, final String expected, final String actual) {
        assertEquality(description, false, expected, actual)
    }

    @Override
    Closure _assertNotEquals(final String description, final String expected, final String actual) {
        return { assertNotEquals(description, expected, actual) }
    }

    @Override
    void assertNotEquals(final String description, final Object expected, final Object actual) {
        assertEquality(description, false, expected, actual)
    }

    @Override
    Closure _assertNotEquals(final String description, final Object expected, final Object actual) {
        return { assertNotEquals(description, expected, actual) }
    }

    @Override
    void assertEqualCollections(String description, Collection expected, Collection actual) {
        assertCollectionEquality(description, true, expected, actual)
    }

    @Override
    Closure _assertEqualCollections(String description, Collection expected, Collection actual) {
        return { assertEqualCollections(description, expected, actual) }
    }

    @Override
    void assertNotEqualCollections(String description, Collection expected, Collection actual) {
        assertCollectionEquality(description, false, expected, actual)
    }

    @Override
    Closure _assertNotEqualCollections(String description, Collection expected, Collection actual) {
        return { assertNotEqualCollections(description, expected, actual) }
    }

    @Override
    void assertNotNull(final String description, final Object object) {
        assertThat(description, object, notNullValue())
    }

    @Override
    Closure _assertNotNull(final String description, final Object object) {
        return { assertNotNull(description, object) }
    }

    @Override
    void assertNull(final String description, final Object object) {
        assertThat(description, object, nullValue())
    }

    @Override
    Closure _assertNull(final String description, final Object object) {
        return { assertNull(description, object) }
    }

    @Override
    <T> void assertThat(final String description, final T actual, final Matcher<? super T> matcher) {
        boolean matches = matcher.matches(actual)
        String assertionDescription = getAssertionDescription(actual, matcher)
        recordAssertion(description, assertionDescription, matches)
    }

    @Override
    <T> Closure _assertThat(final String description, final T actual, final Matcher<? super T> matcher) {
        return { assertThat(description, actual, matcher) }
    }

    @Override
    <T> void assertThat(final GString description, final T actual, final Matcher<? super T> matcher) {
        assertThat(description.toString(), actual, matcher)
    }

    @Override
    <T> Closure _assertThat(final GString description, final T actual, final Matcher<? super T> matcher) {
        return { assertThat(description, actual, matcher) }
    }

    @Override
    void assertPassed(final String description) {
        recordAssertion(description, true)
    }

    @Override
    void assertFailed(final String description) {
        recordAssertion(description, false)
    }

    @Override
    void assertFailed(final Throwable cause) {
        recordAssertion(throwableToDescription(cause), false, cause)
    }

    @Override
    void assertFailed(final String description, Throwable cause) {
        recordAssertion(description, false, cause)
    }

    @Override
    void assertAll(Closure... asserts) {
        List<AssertionError> assertionErrors = executeAsserts(asserts)
        int assertionsCount = asserts.length

        if (assertionErrors.isEmpty()) {
            LOGGER.info(getPassedVerificationMessage(assertionsCount))
        } else {
            String verificationMessage = getFailedVerificationMessage(assertionErrors, assertionsCount)
            LOGGER.error(verificationMessage)
            throw new AssertionError(verificationMessage)
        }
    }

    private static void append(StringBuilder builder, String prompt, boolean valuesEqual, Object obj,
                               String objValue) {
        builder.append(prompt)
        if (valuesEqual) {
            builder.append(obj.getClass().name).append(WHITESPACE)
        }
        builder.append('<').append(objValue).append('>')
    }

    private static void recordAssertion(String description, String assertionDescription, boolean passed) {
        recordAssertion(format(description, assertionDescription), passed)
    }

    private static void assertEquality(String description, boolean equals, Object expected, Object actual) {
        assertEquality(description, equals, expected, actual) {
            expected == null && actual == null || expected != null && expected == actual
        }
    }

    private static void assertCollectionEquality(String description, boolean equals, Collection expected,
                                                 Collection actual) {
        assertEquality(description, equals, expected, actual) {
            expected == null && actual == null ||
                    expected != null && actual != null && CollectionUtils.isEqualCollection(expected, actual)
        }
    }

    private static void assertEquality(String description, boolean equals, Object expected, Object actual,
                                       Closure<Boolean> equalityCondition) {
        if (equalityCondition()) {
            recordAssertion(description, getAssertionDescription(expected, actual, equals), equals)
        } else {
            String expectedString = String.valueOf(expected)
            String actualString = String.valueOf(actual)
            boolean areValuesEqual = expectedString == actualString
            StringBuilder assertionDescription = new StringBuilder()
            append(assertionDescription, EXPECTED, areValuesEqual, expected, expectedString)
            assertionDescription.append(WHITESPACE).append(getEqualityVerb(equals))
            append(assertionDescription, ACTUAL, areValuesEqual, actual, actualString)
            recordAssertion(description, assertionDescription.toString(), !equals)
        }
    }

    private static void recordAssertion(String description, boolean passed) {
        recordAssertion(description, passed, null)
    }

    private static void recordAssertion(String description, boolean passed, Throwable cause) {
        if (passed) {
            LOGGER.info('Pass: {}', description)
        } else {
            LOGGER.error('Fail: {}', description)
            throw new AssertionError(description, cause)
        }
    }

    private static List<AssertionError> executeAsserts(Closure... asserts) {
        List<AssertionError> assertionErrors = []
        asserts.each {
            try {
                it()
            }
            catch (AssertionError assertionError) {
                assertionErrors << assertionError
            }
        }
        assertionErrors
    }

    private static String format(String description, String assertionDescription) {
        StringBuilder fullDescription = new StringBuilder()
        if (description != null) {
            fullDescription.append(description)
        }
        if (assertionDescription != null && !assertionDescription.empty) {
            if (fullDescription.length() != 0) {
                fullDescription.append(' [').append(assertionDescription).append(']')
            } else {
                return assertionDescription
            }
        }
        fullDescription.toString()
    }

    private static String getAssertionDescription(Object expected, Object actual, boolean equals) {
        "${EXPECTED}<${expected}> ${getEqualityVerb(equals)} ${ACTUAL}<${actual}>"
    }

    private static String getEqualityVerb(boolean equals) {
        equals ? 'equals to' : 'not equals to'
    }

    private static <T> String getAssertionDescription(T actual, Matcher<? super T> matcher) {
        StringDescription description = new StringDescription()
        description.appendText(EXPECTED).appendDescriptionOf(matcher)

        StringDescription mismatchDescription = new StringDescription()
        matcher.describeMismatch(actual, mismatchDescription)

        String mismatch = mismatchDescription
        if (!mismatch.isEmpty()) {
            description.appendText(ACTUAL).appendText(mismatch)
        }
        description.toString()
    }

    private static String getPassedVerificationMessage(int assertionsCount) {
        "Passed verification: ${assertionsCount} of ${assertionsCount} assertions passed."
    }

    private static String getFailedVerificationMessage(List<AssertionError> errors, int assertionsCount) {
        StringBuilder message = new StringBuilder('Failed verification: ')
        String assertionInfo = "${errors.size()} of ${assertionsCount} assertions failed."
        message.append(assertionInfo)
        String errorsMessage = getErrorsMessage(errors)
        message.append(errorsMessage)
        message.toString()
    }

    private static String getErrorsMessage(List<AssertionError> errors) {
        StringBuilder errorsMessage = new StringBuilder()
        int index = 1
        errors.forEach {
            errorsMessage
                    .append(System.lineSeparator())
                    .append(index++).append('). ')
                    .append(it.message)
        }
        errorsMessage.toString()
    }

    protected static String throwableToDescription(Throwable cause) {
        cause.message ?: cause.toString()
    }
}
