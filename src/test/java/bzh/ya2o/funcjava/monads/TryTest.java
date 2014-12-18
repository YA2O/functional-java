package bzh.ya2o.funcjava.monads;

import bzh.ya2o.funcjava.functions.Function0;
import bzh.ya2o.funcjava.functions.Function1;
import bzh.ya2o.funcjava.functions.Predicate;

import org.junit.Test;

import java.util.IllegalFormatCodePointException;
import java.util.NoSuchElementException;

import static org.junit.Assert.*;

public class TryTest {

    @Test
    public void success() {
        final Try<String> tryObj = Try.newTry(
                new Function0<String>() {
                    @Override
                    public String apply() {
                        return "A";
                    }
                });
        assertEquals(Try.success("A"), tryObj);
    }

    @Test
    public void failure() {
        final Error error = new NoSuchFieldError("FAIL!");
        final Try<String> tryObj = Try.newTry(
                new Function0<String>() {
                    @Override
                    public String apply() {
                        throw error;
                    }
                });
        assertEquals(error, tryObj.getError());
    }

    @Test
    public void isSuccess_success() {
        final Try<String> tryObj = Try.success("A");
        assertTrue(tryObj.isSuccess());
    }

    @Test
    public void isSuccess_failure() {
        final Try<String> tryObj = Try.failure(new Throwable());
        assertFalse(tryObj.isSuccess());
    }

    @Test
    public void isFailure_success() {
        final Try<String> tryObj = Try.success("A");
        assertFalse(tryObj.isFailure());
    }

    @Test
    public void isFailure_failure() {
        final Try<String> tryObj = Try.failure(new Throwable());
        assertTrue(tryObj.isFailure());
    }

    @Test
    public void get_success() {
        final Try<String> tryObj = Try.success("A");
        assertEquals("A", tryObj.get());
    }

    @Test
    public void get_failure() {
        final Throwable throwable = new IllegalStateException("Fail!");
        final Try<String> tryObj = Try.failure(throwable);
        try {
            tryObj.get();
            fail();
        } catch (final NoSuchElementException e) {
            // OK
        }
    }

    @Test
    public void getError_failure() {
        final Throwable throwable = new IllegalStateException("Fail!");
        final Try<String> tryObj = Try.failure(throwable);
        assertEquals(throwable, tryObj.getError());
    }

    @Test
    public void getError_success() {
        final Try<String> tryObj = Try.success("A");
        try {
            tryObj.getError();
            fail();
        } catch (final NoSuchElementException e) {
            // OK
        }
    }

    @Test
    public void flatMap_Success() {
        final Try<String> tryObj = Try.newTry(
                new Function0<String>() {
                    @Override
                    public String apply() {
                        return "A";
                    }
                });
        final Try<Integer> integerTry = tryObj.flatMap(
                new Function1<String, Try<Integer>>() {
                    @Override
                    public Try<Integer> apply(final String s) {
                        return Try.newTry(
                                new Function0<Integer>() {
                                    @Override
                                    public Integer apply() {
                                        return s.length();
                                    }
                                });
                    }
                });
        assertEquals(Try.success(1), integerTry);
    }

    @Test
    public void flatMap_Failure() {
        final RuntimeException error = new IllegalFormatCodePointException(0);
        final Try<String> tryObj = Try.newTry(
                new Function0<String>() {
                    @Override
                    public String apply() {
                        throw error;
                    }
                });
        final Try<Integer> integerTry = tryObj.flatMap(
                new Function1<String, Try<Integer>>() {
                    @Override
                    public Try<Integer> apply(final String s) {
                        return Try.newTry(new Function0<Integer>() {
                            @Override
                            public Integer apply() {
                                return s.length();
                            }
                        });
                    }
                });
        assertEquals(Try.failure(error), integerTry);
    }

    @Test
    public void map_Success() {
        final Try<String> tryObj = Try.newTry(
                new Function0<String>() {
                    @Override
                    public String apply() {
                        return "AA";
                    }
                });
        final Try<Integer> integerTry = tryObj.map(
                new Function1<String, Integer>() {
                    @Override
                    public Integer apply(final String s) {
                        return s.length();
                    }
                });
        assertEquals(Try.success(2), integerTry);
    }

    @Test
    public void map_Failure() {
        final Error error = new NoSuchMethodError();
        final Try<String> tryObj = Try.newTry(
                new Function0<String>() {
                    @Override
                    public String apply() {
                        throw error;
                    }
                });
        final Try<Integer> integerTry = tryObj.map(
                new Function1<String, Integer>() {
                    @Override
                    public Integer apply(final String s) {
                        return s.length();
                    }
                });
        assertEquals(Try.failure(error), integerTry);
    }

    @Test
    public void filter_Success_pass() {
        final Try<String> tryObj = Try.newTry(
                new Function0<String>() {
                    @Override
                    public String apply() {
                        return "AA";
                    }
                });
        final Try<String> filtered = tryObj.filter(
                new Predicate<String>() {
                    @Override
                    public Boolean apply(final String s) {
                        return s.contains("A");
                    }
                });
        assertEquals(tryObj, filtered);
    }

    @Test
    public void filter_Success_fail() {
        final Try<String> tryObj = Try.newTry(
                new Function0<String>() {
                    @Override
                    public String apply() {
                        return "AA";
                    }
                });
        final Try<String> filtered = tryObj.filter(
                new Predicate<String>() {
                    @Override
                    public Boolean apply(final String s) {
                        return s.contains("B");
                    }
                });
        assertTrue(filtered.isFailure());
        assertTrue(filtered.getError() instanceof IllegalStateException);
    }

    @Test
    public void filter_Failure() {
        final Error error = new NoSuchMethodError();
        final Try<String> tryObj = Try.newTry(
                new Function0<String>() {
                    @Override
                    public String apply() {
                        throw error;
                    }
                });
        final Try<String> filtered = tryObj.filter(
                new Predicate<String>() {
                    @Override
                    public Boolean apply(final String s) {
                        return s.contains("B");
                    }
                });
        assertTrue(filtered.isFailure());
        assertEquals(error, filtered.getError());
    }

    @Test
    public void match_Success() {
        final Try<String> tryObj = Try.success("AAA");
        Integer i = tryObj.match(
                new Function1<String, Integer>() {
                    @Override
                    public Integer apply(final String s) {
                        return 1;
                    }
                },
                new Function1<Throwable, Integer>() {
                    @Override
                    public Integer apply(final Throwable throwable) {
                        return 0;
                    }
                }
        );
        assertEquals(Integer.valueOf(1), i);
    }

    @Test
    public void match_Failure() {
        final Try<String> tryObj = Try.failure(new Throwable());
        Integer i = tryObj.match(
                new Function1<String, Integer>() {
                    @Override
                    public Integer apply(final String s) {
                        return 1;
                    }
                },
                new Function1<Throwable, Integer>() {
                    @Override
                    public Integer apply(final Throwable throwable) {
                        return 0;
                    }
                }
        );
        assertEquals(Integer.valueOf(0), i);
    }

    @Test
    public void equals_Success() {
        final Try<String> tryObj = Try.newTry(
                new Function0<String>() {
                    @Override
                    public String apply() {
                        return "A";
                    }
                });
        assertEquals(Try.success(new String("A")), tryObj);
    }

    @Test
    public void equals_Success_negativeTest() {
        final Try<String> tryObj = Try.newTry(
                new Function0<String>() {
                    @Override
                    public String apply() {
                        return "A";
                    }
                });
        assertNotEquals(Try.success(new String("B")), tryObj);
    }

    @Test
    public void equals_Success_covariance() {
        assertEquals(
                Try.newTry(
                        new Function0<Integer>() {
                            @Override
                            public Integer apply() {
                                return 1;
                            }
                        }),
                Try.newTry(
                        new Function0<Number>() {
                            @Override
                            public Number apply() {
                                return 1;
                            }
                        }));
        assertEquals(
                Try.newTry(
                        new Function0<Number>() {
                            @Override
                            public Number apply() {
                                return 1;
                            }
                        }),
                Try.newTry(
                        new Function0<Integer>() {
                            @Override
                            public Integer apply() {
                                return 1;
                            }
                        }));
    }

    @Test
    public void equals_Failure() {
        final Error error = new NoSuchFieldError("FAIL!");
        final Try<String> tryObj = Try.newTry(
                new Function0<String>() {
                    @Override
                    public String apply() {
                        throw error;
                    }
                });
        assertEquals(Try.failure(error), tryObj);
    }
}
