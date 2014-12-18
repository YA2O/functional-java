package bzh.ya2o.funcjava.monads;

import bzh.ya2o.funcjava.functions.Function0;
import bzh.ya2o.funcjava.functions.Function1;

import com.google.common.base.Predicate;

import java.util.NoSuchElementException;
import java.util.Objects;

@SuppressWarnings({"MethodNamesDifferingOnlyByCase", "ProhibitedExceptionThrown"})
public abstract class Option<T> {

    private Option() {}

    public static final <T> Option<T> option(final T t) {
        return (t == null) ? None.<T>none() : Some.some(t);
    }

    public abstract Boolean isDefined();

    public abstract T get();

    public abstract <U> Option<U> flatMap(Function1<T, Option<U>> f);

    public <U> Option<U> map(final Function1<T, U> f) {
        return flatMap(new Function1<T, Option<U>>() {
            @SuppressWarnings("ParameterNameDiffersFromOverriddenParameter")
            @Override
            public Option<U> apply(final T t) {
                return Some.some(f.apply(t));
            }

        });
    }

    public abstract Option<T> filter(Predicate<T> p);

    public abstract <U> U match(Function1<T, U> onPresent, Function0<U> onAbsent);


    @SuppressWarnings("MethodNamesDifferingOnlyByCase")
    public static final class None<T> extends Option<T> {
        private None() {}

        public static <A> Option<A> none() {
            return new None<>();
        }

        @Override
        public Boolean isDefined() {
            return false;
        }

        @Override
        public T get() {
            throw new NoSuchElementException("Calling get() on Option=None");
        }

        @Override
        public <U> Option<U> flatMap(final Function1<T, Option<U>> f) {
            return None.none();
        }

        @Override
        public Option<T> filter(final Predicate<T> p) {
            return None.none();
        }

        @Override
        public <U> U match(final Function1<T, U> onPresent, final Function0<U> onAbsent) {
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
            return other != null && getClass() == other.getClass();
        }
    }


    public static final class Some<T> extends Option<T> {
        private final T t;

        private Some(final T t) {
            this.t = t;
        }

        static <A> Option<A> some(final A a) {
            return new Some<>(a);
        }

        @Override
        public Boolean isDefined() {
            return true;
        }

        @Override
        public T get() {
            return t;
        }

        @Override
        public <U> Option<U> flatMap(final Function1<T, Option<U>> f) {
            return f.apply(t);
        }

        @Override
        public Option<T> filter(final Predicate<T> p) {
            return p.apply(t) ? this : None.<T>none();
        }

        @Override
        public <U> U match(final Function1<T, U> onPresent, final Function0<U> onAbsent) {
            return onPresent.apply(t);
        }

        @Override
        public int hashCode() {
            return Objects.hash(t);
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {return true;}
            if (other == null || getClass() != other.getClass()) {return false;}
            return Objects.equals(t, ((Some<T>) other).get());
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
