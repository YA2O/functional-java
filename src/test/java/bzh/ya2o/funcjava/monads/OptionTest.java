package bzh.ya2o.funcjava.monads;

import bzh.ya2o.funcjava.functions.Function0;
import bzh.ya2o.funcjava.functions.Function1;
import bzh.ya2o.funcjava.functions.Predicate;

import org.junit.Test;

import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class OptionTest {

    @Test
    public void some() {
        final Option<String> option = Option.newOption("A");
        assertEquals(Option.some("A"), option);
    }

    @Test
    public void none() {
        final Option<String> option = Option.newOption(null);
        assertEquals(Option.none(), option);
    }

    @Test
    public void isDefined_Some() {
        final Option<String> option = Option.newOption("A");
        assertTrue(option.isDefined());
    }

    @Test
    public void isDefined_None() {
        final Option<String> option = Option.newOption(null);
        assertFalse(option.isDefined());
    }

    @Test
    public void get_Some() {
        final Option<String> option = Option.newOption("A");
        assertEquals("A", option.get());
    }

    @Test
    public void get_None() {
        final Option<String> option = Option.newOption(null);
        try {
            option.get();
            fail();
        } catch (NoSuchElementException nsee) {
            // OK
        }
    }

    @Test
    public void flatMap_Some() {
        final Option<String> option = Option.newOption("A");
        final Option<Integer> integerOption = option.flatMap(
                new Function1<String, Option<Integer>>() {
                    @Override
                    public Option<Integer> apply(final String s) {
                        return Option.newOption(s.length());
                    }
                });
        assertEquals(Option.newOption(1), integerOption);
    }

    @Test
    public void flatMap_None() {
        final Option<String> option = Option.none();
        final Option<Integer> integerOption = option.flatMap(
                new Function1<String, Option<Integer>>() {
                    @Override
                    public Option<Integer> apply(final String s) {
                        return Option.newOption(s.length());
                    }
                });
        assertEquals(Option.none(), integerOption);
    }

    @Test
    public void map_Some() {
        final Option<String> option = Option.newOption("A");
        final Option<Integer> integerOption = option.map(
                new Function1<String, Integer>() {
                    @Override
                    public Integer apply(final String s) {
                        return s.length();
                    }
                });
        assertEquals(Option.newOption(1), integerOption);
    }

    @Test
    public void map_None() {
        final Option<String> option = Option.none();
        final Option<Integer> integerOption = option.map(
                new Function1<String, Integer>() {
                    @Override
                    public Integer apply(final String s) {
                        return s.length();
                    }
                });
        assertEquals(Option.none(), integerOption);
    }

    @Test
    public void filter_Some_pass() {
        final Option<String> option = Option.newOption("A");
        final Option<String> filtered = option.filter(
                new Predicate<String>() {
                    @Override
                    public Boolean apply(final String s) {
                        return s.equals("A");
                    }
                });
        assertEquals(option, filtered);
    }

    @Test
    public void filter_Some_fail() {
        final Option<String> option = Option.newOption("A");
        final Option<String> filtered = option.filter(
                new Predicate<String>() {
                    @Override
                    public Boolean apply(final String s) {
                        return s.equals("B");
                    }
                });
        assertEquals(Option.none(), filtered);
    }

    @Test
    public void filter_None() {
        final Option<String> option = Option.none();
        final Option<String> filtered = option.filter(
                new Predicate<String>() {
                    @Override
                    public Boolean apply(final String s) {
                        return true;
                    }
                });
        assertEquals(Option.none(), filtered);
    }

    @Test
    public void match_Some() {
        final Option<String> option = Option.newOption("A");

        Integer i = option.match(
                new Function1<String, Integer>() {
                    @Override
                    public Integer apply(final String s) {
                        return 1;
                    }
                },
                new Function0<Integer>() {
                    @Override
                    public Integer apply() {
                        return 0;
                    }
                });
        assertEquals(Integer.valueOf(1), i);
    }

    @Test
    public void match_None() {
        final Option<String> option = Option.none();

        Integer i = option.match(
                new Function1<String, Integer>() {
                    @Override
                    public Integer apply(final String s) {
                        return 1;
                    }
                },
                new Function0<Integer>() {
                    @Override
                    public Integer apply() {
                        return 0;
                    }
                });
        assertEquals(Integer.valueOf(0), i);
    }

    @Test
    public void equals_Some() {
        final Option<String> option = Option.newOption("A");
        assertEquals(Option.some(new String("A")), option);
    }

    @Test
    public void equals_Some_negativeTest() {
        final Option<String> option = Option.newOption("B");
        assertNotEquals(Option.some(new String("A")), option);
    }

    @Test
    public void equals_Some_covariance() {
        assertEquals(Option.<Integer>newOption(1), Option.<Number>newOption(1));
        assertEquals(Option.<Number>newOption(1), Option.<Integer>newOption(1));
    }

    @Test
    public void equals_None() {
        final Option<String> option = Option.newOption(null);
        assertEquals(Option.<Integer>none(), option);
    }

}
