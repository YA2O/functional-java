package bzh.ya2o.funcjava;

import com.google.common.base.Function;

import java.io.Serializable;
import java.util.NoSuchElementException;

public abstract class Try<A extends Serializable> implements Serializable {

    private Try() {}

    //    public abstract <U> U match(Function<T, U> onSuccess, Function<E, R> onFailure);

    public abstract <B extends Serializable> Try<B> map(final Function<A, B> f);

    public abstract <B extends Serializable> Try<B> flatMap(final Function<A, Try<B>> f);

    public abstract Try<A> filter(final Function<A, Boolean> predicate);

    public static class Success<A extends Serializable> extends Try<A> {
        public final A value;

        public Success(final A value) {
            this.value = value;
        }

        //        @Override
        //        public <R> R match(final Function<S, R> onSuccess, final Function<E, R> onFailure) {
        //            return onSuccess.apply(value);
        //        }

        @Override
        public <B extends Serializable> Try<B> map(final Function<A, B> f) {
            return new Success<>(f.apply(value));
        }

        @Override
        public <B extends Serializable> Try<B> flatMap(final Function<A, Try<B>> f) {
            return f.apply(value);
        }

        @Override
        public Try<A> filter(final Function<A, Boolean> predicate) {
            try {
                return predicate.apply(value) ? this : new Failure<A>(new NoSuchElementException("Predicate unfulfilled for " + value));
            }catch (Throwable t) {
                return new Failure<A>(t);
            }
        }
    }


    public static class Failure<A extends Serializable> extends Try<A> {
        public final Throwable throwable;

        public Failure(final Throwable throwable) {
            this.throwable = throwable;
        }

        //        @Override
        //        public <R> R match(final Function<S, R> onSuccess, final Function<E, R> onFailure) {
        //            return onFailure.apply(exception);
        //        }

        @Override
        public <B extends Serializable> Try<B> map(final Function<A, B> f) {
            return new Failure<>(throwable);
        }

        @Override
        public <B extends Serializable> Try<B> flatMap(final Function<A, Try<B>> f) {
            return new Failure<>(throwable);
        }

        @Override
        public Try<A> filter(final Function<A, Boolean> predicate) {
            return this;
        }
    }
}

