package bzh.ya2o.funcjava.monads;

import bzh.ya2o.funcjava.functions.Function0;
import bzh.ya2o.funcjava.functions.Function1;

import com.google.common.base.Predicate;
import org.junit.Test;

import java.util.NoSuchElementException;

import static bzh.ya2o.funcjava.monads.Option.None.none;
import static bzh.ya2o.funcjava.monads.Option.option;
import static org.junit.Assert.*;

public class OptionTest {

    @Test
    public void option_None() {
        final Option<String> option = option(null);
        assertEquals(none(), option);
    }

    @Test
    public void option_Some() {
        final Option<String> option = option("A");
        assertEquals(option("A"), option);
    }

    @Test
    public void isDefined_None() {
        final Option<String> option = option(null);
        assertFalse(option.isDefined());
    }

    @Test
    public void isDefined_Some() {
        final Option<String> option = option("A");
        assertTrue(option.isDefined());
    }

    @Test
    public void get_None() {
        final Option<String> option = option(null);
        try {
            option.get();
            fail();
        } catch (NoSuchElementException nsee) {
            // OK
        }
    }

    @Test
    public void get_Some() {
        final Option<String> option = option("A");
        assertEquals("A", option.get());
    }

    @Test
    public void flatMap_None() {
        final Option<String> option = none();
        final Option<Integer> integerOption = option.flatMap(new Function1<String, Option<Integer>>() {
            @Override
            public Option<Integer> apply(final String s) {
                return option(s.length());
            }
        });
        assertEquals(none(), integerOption);
    }

    @Test
    public void flatMap_Some() {
        final Option<String> option = option("A");
        final Option<Integer> integerOption = option.flatMap(new Function1<String, Option<Integer>>() {
            @Override
            public Option<Integer> apply(final String s) {
                return option(s.length());
            }
        });
        assertEquals(option(1), integerOption);
    }

    @Test
    public void map_None() {
        final Option<String> option = none();
        final Option<Integer> integerOption = option.map(new Function1<String, Integer>() {
            @Override
            public Integer apply(final String s) {
                return s.length();
            }
        });
        assertEquals(none(), integerOption);
    }

    @Test
    public void map_Some() {
        final Option<String> option = option("A");
        final Option<Integer> integerOption = option.map(new Function1<String, Integer>() {
            @Override
            public Integer apply(final String s) {
                return s.length();
            }
        });
        assertEquals(option(1), integerOption);
    }

    @Test
    public void filter_None() {
        final Option<String> option = none();
        final Option<String> filtered = option.filter(new Predicate<String>() {
            @Override
            public boolean apply(final String s) {
                return true;
            }
        });
        assertEquals(none(), filtered);
    }

    @Test
    public void filter_Some_pass() {
        final Option<String> option = option("A");
        final Option<String> filtered = option.filter(new Predicate<String>() {
            @Override
            public boolean apply(final String s) {
                return s.equals("A");
            }
        });
        assertEquals(option, filtered);
    }

    @Test
    public void filter_Some_fail() {
        final Option<String> option = option("A");
        final Option<String> filtered = option.filter(new Predicate<String>() {
            @Override
            public boolean apply(final String s) {
                return s.equals("B");
            }
        });
        assertEquals(none(), filtered);
    }

    @Test
    public void match_None() {
        final Option<String> option = none();

        Integer i = option.match(new Function1<String, Integer>() {
            @Override
            public Integer apply(final String s) {
                return 1;
            }
        }, new Function0<Integer>() {
            @Override
            public Integer apply() {
                return 0;
            }
        });

        assertEquals(Integer.valueOf(0), i);
    }

    @Test
    public void match_Some() {
        final Option<String> option = option("A");

        Integer i = option.match(new Function1<String, Integer>() {
            @Override
            public Integer apply(final String s) {
                return 1;
            }
        }, new Function0<Integer>() {
            @Override
            public Integer apply() {
                return 0;
            }
        });

        assertEquals(Integer.valueOf(1), i);
    }

    @Test
    public void equals_Some() {
        final Option<String> option = option("A");
        assertEquals(option(new String("A")), option);
    }

    @Test
    public void equals_Some_negative() {
        final Option<String> option = option("B");
        assertNotEquals(option(new String("A")), option);
    }

    @Test
    public void equals_Some_notSameType() {
        assertEquals(Option.<Integer>option(1), Option.<Number>option(1));
        assertEquals(Option.<Number>option(1), Option.<Integer>option(1));
    }

    @Test
    public void equals_None() {
        final Option<String> option = none();
        assertEquals(none(), option);
    }

}
