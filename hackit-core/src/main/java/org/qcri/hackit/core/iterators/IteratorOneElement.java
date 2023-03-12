package org.qcri.hackit.core.iterators;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class IteratorOneElement<T> implements Iterator<T>, Iterable<T>{

    private final boolean removeAllowed;
    private boolean beforeFirst;
    private boolean removed;
    private T object;

    public IteratorOneElement(T object) {
        this(object, true);
    }

    public IteratorOneElement(T object, boolean removeAllowed) {
        this.beforeFirst = true;
        this.removed = false;
        this.object = object;
        this.removeAllowed = removeAllowed;
    }

    public boolean hasNext() {
        return this.beforeFirst && !this.removed;
    }

    public T next() {
        if (this.beforeFirst && !this.removed) {
            this.beforeFirst = false;
            return this.object;
        } else {
            throw new NoSuchElementException();
        }
    }

    public void remove() {
        if (this.removeAllowed) {
            if (!this.removed && !this.beforeFirst) {
                this.object = null;
                this.removed = true;
            } else {
                throw new IllegalStateException();
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    public void reset() {
        this.beforeFirst = true;
    }

    @Override
    public Iterator<T> iterator() {
        return this;
    }
}
