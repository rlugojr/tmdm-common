package org.talend.mdm.commmon.metadata.builder;

import org.apache.commons.lang.StringUtils;

/**
 *
 */
public abstract class Builder<T> {

    public static TypeBuilder type(String name) {
        return type(StringUtils.EMPTY, name);
    }

    public static TypeBuilder type(String namespace, String name) {
        return new TypeBuilder(namespace, name);
    }

    public abstract T build();

}
