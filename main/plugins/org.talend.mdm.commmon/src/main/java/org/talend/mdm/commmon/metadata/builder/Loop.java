package org.talend.mdm.commmon.metadata.builder;

import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

abstract class Loop<T> implements Function<T, T> {

    protected final List<Predicate<T>> predicates = new LinkedList<Predicate<T>>();

    protected abstract T getInput();

    @Override
    public T apply(T f) {
        T input = getInput();
        for (Predicate<T> predicate : predicates) {
            predicate.apply(input);
        }
        return input;
    }
}
