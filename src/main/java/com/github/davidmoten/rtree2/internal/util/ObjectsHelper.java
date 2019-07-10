package com.github.davidmoten.rtree2.internal.util;

import com.github.davidmoten.guavamini.annotations.VisibleForTesting;

public final class ObjectsHelper {

    private ObjectsHelper() {
        // prevent instantiation
    }

    @VisibleForTesting
    static void instantiateForTestCoveragePurposesOnly() {
        new ObjectsHelper();
    }

    @SuppressWarnings("unchecked")
    public static <T> T asClass(Object object, Class<T> cls) {
        if (object == null)
            return null;
        else if (object.getClass() != cls)
            return null;
        else
            return (T) object;
    }

}
