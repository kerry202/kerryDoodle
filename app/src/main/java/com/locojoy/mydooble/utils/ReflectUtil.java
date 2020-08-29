package com.locojoy.mydooble.utils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author: kerry
 * date: On $ {DATE}
 */
public class ReflectUtil {
    public ReflectUtil() {
    }

    public static Field getField(Class<?> thisClass, String fieldName) {
        if (thisClass == null) {
            return null;
        } else {
            try {
                return thisClass.getDeclaredField(fieldName);
            } catch (Throwable var3) {
                return null;
            }
        }
    }

    public static Object getValue(Object instance, String fieldName) {
        Field field = getField(instance.getClass(), fieldName);
        if (field == null) {
            return null;
        } else {
            field.setAccessible(true);

            try {
                return field.get(instance);
            } catch (Throwable var4) {
                return null;
            }
        }
    }

    public static Object getValue(Class clazz, String fieldName) {
        Field field = getField(clazz, fieldName);
        if (field == null) {
            return null;
        } else {
            field.setAccessible(true);

            try {
                return field.get((Object)null);
            } catch (Throwable var4) {
                return null;
            }
        }
    }

    public static Method getMethod(Class<?> thisClass, String methodName, Class<?>[] parameterTypes) {
        if (thisClass == null) {
            return null;
        } else {
            try {
                Method method = thisClass.getDeclaredMethod(methodName, parameterTypes);
                if (method == null) {
                    return null;
                } else {
                    method.setAccessible(true);
                    return method;
                }
            } catch (Throwable var4) {
                return null;
            }
        }
    }

    public static Object invokeMethod(Object instance, String methodName, Object... args) throws Throwable {
        Class<?>[] parameterTypes = null;
        if (args != null) {
            parameterTypes = new Class[args.length];

            for(int i = 0; i < args.length; ++i) {
                if (args[i] != null) {
                    parameterTypes[i] = args[i].getClass();
                }
            }
        }

        Method method = getMethod(instance.getClass(), methodName, parameterTypes);
        return method.invoke(instance, args);
    }

    public static Object invokeMethod(Class clazz, String methodName, Object... args) throws Throwable {
        Class<?>[] parameterTypes = null;
        if (args != null) {
            parameterTypes = new Class[args.length];

            for(int i = 0; i < args.length; ++i) {
                if (args[i] != null) {
                    parameterTypes[i] = args[i].getClass();
                }
            }
        }

        Method method = getMethod(clazz, methodName, parameterTypes);
        return method.invoke(clazz, args);
    }
}
