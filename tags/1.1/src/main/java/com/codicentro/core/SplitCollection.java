/**
 * @author: Alexander Villalobos Yadró
 * @user: avillalobos
 * @email: avyadro@yahoo.com.mx
 * @created: Oct 12, 2011 at 3:51:09 PM
 * @place: Toluca, Estado de México, México
 * @company: AdeA México S.A. de C.V.
 * @web: http://www.adea.com.mx
 * @className: SplitCollection.java
 * @purpose:
 * Revisions:
 * Ver        Date               Author                                      Description
 * ---------  ---------------  -----------------------------------  ------------------------------------
 **/
package com.codicentro.core;

import java.util.AbstractList;
import java.util.List;

public class SplitCollection {

    public static <T> List<List<T>> split(List<T> list, int numberOfElements) {

        if (list == null) {
            throw new NullPointerException(
                    "'list' must not be null");
        }
        if (!(numberOfElements > 0)) {
            throw new IllegalArgumentException(
                    "'size' must be greater than 0");
        }

        return new Split<T>(list, numberOfElements);
    }

    private static class Split<T> extends AbstractList<List<T>> {

        final List<T> list;
        final int numberOfElements;

        Split(List<T> list, int numberOfElements) {
            this.list = list;
            this.numberOfElements = numberOfElements;
        }

        @Override
        public List<T> get(int index) {
            int listSize = size();
            if (listSize < 0) {
                throw new IllegalArgumentException("negative size: " + listSize);
            }
            if (index < 0) {
                throw new IndexOutOfBoundsException(
                        "index " + index + " must not be negative");
            }
            if (index >= listSize) {
                throw new IndexOutOfBoundsException(
                        "index " + index + " must be less than size " + listSize);
            }
            int start = index * numberOfElements;
            int end = Math.min(start + numberOfElements, list.size());
            return list.subList(start, end);
        }

        @Override
        public int size() {
            return (list.size() + numberOfElements - 1) / numberOfElements;
        }

        @Override
        public boolean isEmpty() {
            return list.isEmpty();
        }
    }
}
