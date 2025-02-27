package com.github.kingschan1204.easycrawl.helper.ref.lambda;

import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Lambda Ref
 *
 * @author kingschan 2025-02-27
 */
public class LambdaRefHelper {

  public static <T> String ref(SerializableFunction<T, ?> getter) {
    try {
      Method writeReplace = getter.getClass().getDeclaredMethod("writeReplace");
      writeReplace.setAccessible(true);
      SerializedLambda serializedLambda = (SerializedLambda) writeReplace.invoke(getter);
      String methodName = serializedLambda.getImplMethodName();
      if (methodName.startsWith("get")) {
        methodName = methodName.substring(3);
      } else if (methodName.startsWith("is")) {
        methodName = methodName.substring(2);
      }
      return Character.toLowerCase(methodName.charAt(0)) + methodName.substring(1);
    } catch (Exception e) {
      throw new RuntimeException("error", e);
    }
  }

  public static <T> Object set(Object obj, String name, Object val) {
    try {
      Class<?> clazz = obj.getClass();
      Field field = clazz.getDeclaredField(name);
      field.setAccessible(true);
      field.set(obj, val);
    } catch (Exception e) {
      throw new RuntimeException("error", e);
    }
    return obj;
  }

  public static <T> Object get(Object obj, String name) {
    Object val = null;
    try {
      Class<?> clazz = obj.getClass();
      Field field = clazz.getDeclaredField(name);
      field.setAccessible(true);
      val = field.get(obj);
    } catch (Exception e) {
      throw new RuntimeException("error", e);
    }
    return val;
  }

  public static <T> Object set(Object obj, SerializableFunction<T, ?> getter, Object val) {
    return set(obj, ref(getter), val);
  }

  public static <T> Object get(Object obj, SerializableFunction<T, ?> getter) {
    return get(obj, ref(getter));
  }
}
