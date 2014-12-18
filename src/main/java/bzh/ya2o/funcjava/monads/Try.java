package bzh.ya2o.funcjava.monads;

import bzh.ya2o.funcjava.functions.Function0;
import bzh.ya2o.funcjava.functions.Function1;
import bzh.ya2o.funcjava.functions.Predicate;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;

public abstract class Try<T extends Serializable> implements Serializable {

    private static final long serialVersionUID = 681837274300734520L;

    private Try() {}

    public static final <T extends Serializable> Try<T> newTry(final Function0<T> provider) {
        try {
            return Try.success(provider.apply());
        } catch (final Throwable error) {
            return Try.failure(error);
        }
    }

    public static <A extends Serializable> Try<A> success(final A a) {
        return new Success<>(a);
    }

    public static <A extends Serializable> Try<A> failure(Throwable error) {
        return new Failure<>(error);
    }

    public abstract Boolean isSuccess();

    public Boolean isFailure() {
        return !isSuccess();
    }

    public abstract T get();

    public abstract Throwable getError();

    public abstract <U extends Serializable> Try<U> flatMap(Function1<? super T, ? extends Try<U>> f);

    public <U extends Serializable> Try<U> map(final Function1<? super T, ? extends U> f) {
        return flatMap(new Function1<T, Try<U>>() {
            @Override
            public Try<U> apply(final T t) {
                try {
                    U u = f.apply(t);
                    return Try.success(u);
                } catch (final Throwable error) {
                    return Try.failure(error);
                }
            }
        });
    }

    public abstract Try<T> filter(Predicate<? super T> p);

    public abstract <U> U match(Function1<? super T, ? extends U> onSuccess, Function1<Throwable, ? extends U> onFailure);


    public static class Success<T extends Serializable> extends Try<T> {
        public final T value;

        Success(final T t) {
            value = t;
        }

        @Override
        public Boolean isSuccess() {
            return true;
        }

        @Override
        public T get() {
            return value;
        }

        @Override
        public Throwable getError() {
            throw new NoSuchElementException("Calling getError() on an instance of Try.Success!");
        }

        @Override
        public <U extends Serializable> Try<U> flatMap(final Function1<? super T, ? extends Try<U>> f) {
            return f.apply(value);
        }

        @Override
        public Try<T> filter(final Predicate<? super T> p) {
            return p.apply(value) ? this : Failure.<T>failure(new IllegalStateException("Predicate not satisfied in Try.filter()"));
        }

        @Override
        public <U> U match(final Function1<? super T, ? extends U> onSuccess, final Function1<Throwable, ? extends U> onFailure) {
            return onSuccess.apply(value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.value);
        }

        /**
         * NB: Try<T> is covariant
         */
        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || other.getClass() != Success.class) {
                return false;
            }
            return valueEquals(((Success) other).get());
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



    public static class Failure<T extends Serializable> extends Try<T> {
        private final Throwable throwable;

        Failure(final Throwable throwable) {
            this.throwable = throwable;
        }

        @Override
        public Boolean isSuccess() {
            return false;
        }

        @Override
        public T get() {
            throw new NoSuchElementException("Calling get() on an instance of Try.Failure!");
        }

        public Throwable getError() {
            return throwable;
        }

        @Override
        public <U extends Serializable> Try<U> flatMap(final Function1<? super T, ? extends Try<U>> f) {
            return Try.failure(throwable);
        }

        @Override
        public Try<T> filter(final Predicate<? super T> p) {
            return this;
        }

        @Override
        public <U> U match(final Function1<? super T, ? extends U> onSuccess, final Function1<Throwable, ? extends U> onFailure) {
            return onFailure.apply(throwable);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.throwable);
        }

        @Override
        public boolean equals(final Object other) {
            if (this == other) {
                return true;
            }
            if (other == null || other.getClass() != Failure.class) {
                return false;
            }
            return Objects.equals(throwable, (((Failure) other)).throwable);
        }
    }
}

