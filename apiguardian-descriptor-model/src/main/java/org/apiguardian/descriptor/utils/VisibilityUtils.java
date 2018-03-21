package org.apiguardian.descriptor.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class VisibilityUtils {
    private static Visibility getVisibility(int modifiersMask){
        if ((modifiersMask & Modifier.PUBLIC) != 0)
            return Visibility.PUBLIC;
        if ((modifiersMask & Modifier.PROTECTED) != 0)
            return Visibility.PROTECTED;
        if ((modifiersMask & Modifier.PRIVATE) != 0)
            return Visibility.PRIVATE;
        return Visibility.PKG;
    }

    public static Visibility getVisibility(Method method){
        return getVisibility(method.getModifiers());
    }

    public static Visibility getVisibility(Field field){
        return getVisibility(field.getModifiers());
    }

    public static Visibility getVisibility(Constructor constructor){
        return getVisibility(constructor.getModifiers());
    }

    public static Visibility getVisibility(Class clazz){
        return getVisibility(clazz.getModifiers());
    }
}
