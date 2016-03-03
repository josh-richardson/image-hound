package hound.components;

import org.apache.commons.lang3.tuple.MutablePair;

/**
 * Created by Joshua on 15/02/2016.
 */
public class ViewableMutablePair<L, R> extends MutablePair<L, R> {
    @Override
    public String toString() {
        return String.valueOf(getLeft());
    }

    public ViewableMutablePair(L left, R right) {
        this.left = left;
        this.right = right;
    }

}
