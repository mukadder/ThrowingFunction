package pl.touk.throwing;

import org.junit.Test;

import java.net.URI;
import java.util.Optional;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static pl.touk.throwing.TestCommons.givenThrowingFunction;
import static pl.touk.throwing.ThrowingFunction.lifted;
import static pl.touk.throwing.ThrowingFunction.unchecked;

public class ThrowingFunctionTest {

    @Test
    public void shouldCompose() throws Exception {
        // given
        final ThrowingFunction<Integer, Integer, Exception> f1 = i -> 2 * i;
        final ThrowingFunction<Integer, Integer, Exception> f2 = i -> i + 10;

        // when
        final Integer result = f1.compose(f2).apply(3);

        // then
        assertThat(result).isEqualTo(26);
    }


    @Test
    public void shouldAndThen() throws Exception {
        // given
        final ThrowingFunction<Integer, Integer, Exception> f1 = i -> 2 * i;
        final ThrowingFunction<Integer, Integer, Exception> f2 = i -> i + 10;

        // when
        final Integer result = f1.andThen(f2).apply(3);

        // then
        assertThat(result).isEqualTo(16);
    }

    @Test
    public void shouldReturnOptional() throws Exception {
        // given
        final ThrowingFunction<Integer, Integer, Exception> f1 = ThrowingFunction.identity();

        // when
        final Optional<Integer> result = f1.lift().apply(42);

        // then
        assertThat(result.isPresent()).isTrue();
    }

    @Test
    public void shouldReturnEmptyOptional() throws Exception {
        // given
        final ThrowingFunction<Integer, Integer, Exception> f1 = givenThrowingFunction();

        // when
        final Optional<Integer> result = f1.lift().apply(42);

        // then
        assertThat(result.isPresent()).isFalse();
    }

    @Test(expected = Exception.class)
    public void shouldThrowEx() throws Exception {
        // given
        final ThrowingFunction<Integer, Integer, Exception> f1 = givenThrowingFunction();

        // when
        f1.apply(42);

        // then RuntimeException is thrown
    }

    @Test(expected = RuntimeException.class)
    public void shouldWrapInRuntimeEx() throws Exception {
        // given
        final ThrowingFunction<Integer, Integer, Exception> f1 = givenThrowingFunction();

        // when
        f1.uncheck().apply(42);

        // then RuntimeException is thrown
    }

    @Test(expected = RuntimeException.class)
    public void shouldWrapInRuntimeExWhenUsingUnchecked() throws Exception {

        // when
        Stream.of(". .").map(unchecked(URI::new)).collect(toList());

        // then RuntimeException is thrown
    }

    @Test
    public void shouldApplyWhenNoExceptionThrown() throws Exception {
        //given
        ThrowingFunction<String, String, Exception> f = String::toUpperCase;

        // when
        Stream.of(". .").map(f.uncheck()).collect(toList());

        // then no exception thrown
    }

    @Test
    public void shouldWrapInOptionalWhenUsingStandardUtilsFunctions() throws Exception {

        // when
        final Long result = Stream.of(". .")
                .map(lifted(URI::new))
                .filter(Optional::isPresent)
                .count();

        //then
        assertThat(result).isZero();
    }
}