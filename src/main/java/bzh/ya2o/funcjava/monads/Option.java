package bzh.ya2o.funcjava.monads;

import bzh.ya2o.funcjava.functions.Function0;
import bzh.ya2o.funcjava.functions.Function1;
import bzh.ya2o.funcjava.functions.Predicate;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;

public abstract class Option<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 8151914158643749006L;

    private Option() {}

    public static final <T extends Serializable> Option<T> newOption(final T t) {
        return (t == null) ? Option.<T>none() : Option.some(t);
    }

    public static <A extends Serializable> Option<A> some(final A a) {
        return new Some<>(a);
    }

    public static <A extends Serializable> Option<A> none() {
        return new None<>();
    }

    public abstract Boolean isDefined();

    public abstract T get();

    public abstract <U extends Serializable> Option<U> flatMap(Function1<? super T, ? extends Option<U>> f);

    public <U extends Serializable> Option<U> map(final Function1<? super T, ? extends U> f) {
        return flatMap(new Function1<T, Option<U>>() {
            @Override
            public Option<U> apply(final T t) {
                return newOption(f.apply(t));
            }
        });
    }

    public abstract Option<T> filter(Predicate<? super T> p);

    public abstract <U> U match(Function1<? super T, ? extends U> onPresent, Function0<? extends U> onAbsent);


    public static final class Some<T extends Serializable> extends Option<T> {
        private final T value;

        Some(final T t) {
            if (t == null) {
                throw new NullPointerException("Option.Some can not contain a null value");
            }
            value = t;
        }

        @Override
        public Boolean isDefined() {
            return true;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public <U extends Serializable> Option<U> flatMap(final Function1<? super T, ? extends Option<U>> f) {
            return f.apply(value);
        }

        @Override
        public Option<T> filter(final Predicate<? super T> p) {
            return p.apply(value) ? this : Option.<T>none();
        }

        @Override
        public <U> U match(final Function1<? super T, ? extends U> onPresent, final Function0<? extends U> onAbsent) {
            return onPresent.apply(value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(value);
        }

        /**
         * NB: Option<T> is covariant
         */
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (value == null || other.getClass() != Some.class) {
                return false;
            }
            return valueEquals(((Some) other).get());
        }

        private <U> Boolean valueEquals(U u) {
            if (u == null) {
                return false;
            }
            if (u.getClass().isAssignableFrom(value.getClass())) {
                return u.equals((U) value);
            }
            if (value.getClass().isAssignableFrom(u.getClass())) {
                return value.equals((T) u);
            }
            return false;
        }
    }



    public static final class None<T extends Serializable> extends Option<T> {
        None() {}

        @Override
        public Boolean isDefined() {
            return false;
        }

        @Override
        public T get() {
            throw new NoSuchElementException("Calling get() on an instance of Option.None!");
        }

        @Override
        public <U extends Serializable> Option<U> flatMap(final Function1<? super T, ? extends Option<U>> f) {
            return Option.none();
        }

        @Override
        public Option<T> filter(final Predicate<? super T> p) {
            return Option.none();
        }

        @Override
        public <U> U match(final Function1<? super T, ? extends U> onPresent, final Function0<? extends U> onAbsent) {
            return onAbsent.apply();
        }

        @Override
        public int hashCode() {
            return super.hashCode();
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            return other != null && other.getClass() == None.class;
        }
    }


    //	/**
    //	 * Like in Guava's Optional
    //	 */
    //	public final Boolean isPresent() {
    //		return isDefined();
    //	}
    //
    //	/**
    //	 * Like in Guava's Optional
    //	 */
    //	public static final <T> Option<T> fromNullable(final T t) {
    //		return Option.option(t);
    //	}
    //
    //	/**
    //	 * Like in Guava's Optional
    //	 */
    //	public static final <T> Option<T> of(final T t) {
    //		if (t == null) {
    //			throw new NullPointerException("");
    //		}
    //		return Some.some(t);
    //	}
}
