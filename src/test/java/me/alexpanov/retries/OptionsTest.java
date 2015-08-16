package me.alexpanov.retries;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import org.junit.Test;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.fest.assertions.Assertions.assertThat;

public class OptionsTest {

    private final PerformedWork<String> emptyPerformedWork = new PerformedWork<String>();

    private Options<String> options = new Options<String>();

    @Test
    public void defaultNumberOfRetriesIsTwo() throws Exception {
        PerformedWork<String> oneTry = emptyPerformedWork.tryEndedIn(Optional.<String>absent());
        assertThat(options.isSatisfiedBy(oneTry)).isFalse();
        PerformedWork<String> twoTries = oneTry.tryEndedIn(Optional.<String>absent());
        assertThat(options.isSatisfiedBy(twoTries)).isTrue();
    }

    @Test
    public void usesPredicateToDetermineSatisfiedResult() throws Exception {
        Options<String> options = this.options.ignoreIfResult(isEmpty());
        PerformedWork<String> historyWithEmptyLastResult = emptyPerformedWork.tryEndedIn(Optional.of(" "));
        assertThat(options.isSatisfiedBy(historyWithEmptyLastResult)).isFalse();
    }

    @Test
    public void resultSatisfiesPredicate() throws Exception {
        Options<String> options = this.options.ignoreIfResult(isEmpty());
        PerformedWork<String> historyWithNonEmptyLastResult = emptyPerformedWork.tryEndedIn(Optional.of("a"));
        assertThat(options.isSatisfiedBy(historyWithNonEmptyLastResult)).isTrue();
    }

    @Test
    public void multiplePredicatesAreUsed() throws Exception {
        Options<String> options = this.options.ignoreIfResult(isEmpty()).ignoreIfResult(startsWith("a"));
        PerformedWork<String> historyWithEmptyLastResult = emptyPerformedWork.tryEndedIn(Optional.of("a"));
        assertThat(options.isSatisfiedBy(historyWithEmptyLastResult)).isFalse();
    }

    private Predicate<? super String> startsWith(final String start) {
        return new Predicate<String>() {
            @Override
            public boolean apply(String input) {
                return input.startsWith(start);
            }
        };
    }

    private Predicate<CharSequence> isEmpty() {
        return new Predicate<CharSequence>() {
            @Override
            public boolean apply(CharSequence input) {
                return isBlank(input);
            }
        };
    }

}
