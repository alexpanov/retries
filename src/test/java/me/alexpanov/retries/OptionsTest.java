package me.alexpanov.retries;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;

import org.junit.Test;

import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.fest.assertions.Assertions.assertThat;

public class OptionsTest {

    private final WorkHistory<String> emptyWorkHistory = new WorkHistory<String>();

    private Options<String> options = new Options<String>();

    @Test
    public void defaultNumberOfRetriesIsTwo() throws Exception {
        WorkHistory<String> oneTry = emptyWorkHistory.tryEndedIn(Optional.<String>absent());
        assertThat(options.isSatisfiedBy(oneTry)).isFalse();
        WorkHistory<String> twoTries = oneTry.tryEndedIn(Optional.<String>absent());
        assertThat(options.isSatisfiedBy(twoTries)).isTrue();
    }

    @Test
    public void usesPredicateToDetermineSatisfiedResult() throws Exception {
        Options<String> options = this.options.ignoreIfResult(isEmpty());
        WorkHistory<String> historyWithEmptyLastResult = emptyWorkHistory.tryEndedIn(Optional.of(" "));
        assertThat(options.isSatisfiedBy(historyWithEmptyLastResult)).isFalse();
    }

    @Test
    public void usesPredicateToDetermineSatisfiedResult1() throws Exception {
        Options<String> options = this.options.ignoreIfResult(isEmpty());
        WorkHistory<String> historyWithEmptyLastResult = emptyWorkHistory.tryEndedIn(Optional.of("a"));
        assertThat(options.isSatisfiedBy(historyWithEmptyLastResult)).isTrue();
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
