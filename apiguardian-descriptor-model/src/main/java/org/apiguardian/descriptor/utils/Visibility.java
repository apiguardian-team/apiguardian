package org.apiguardian.descriptor.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static org.apiguardian.descriptor.utils.VisibilityUtils.getVisibility;

public enum Visibility {
    PKG,
    PRIVATE,
    PROTECTED,
    PUBLIC;

    public static Visibility of(Class clazz){
        return getVisibility(clazz);
    }

    public static Visibility of(Method method){
        return getVisibility(method);
    }

    public static Visibility of(Constructor constructor){
        return getVisibility(constructor);
    }

    public static Visibility of(Field field){
        return getVisibility(field);
    }
}
