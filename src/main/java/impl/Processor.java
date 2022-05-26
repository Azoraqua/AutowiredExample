package impl;

import api.Bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public final class Processor {
    public record ProcessResult(boolean successful, Map<Class<?>, Object> values) {
    }

    private Map<Class<?>, Object> beanCache = new HashMap<>();

    public ProcessResult process(Scanner.ScanResult scanResult) {
        for (Method method : scanResult.beans()) {
            try {
                final boolean methodAccessible = method.isAccessible();
                final Bean bean = method.getDeclaredAnnotation(Bean.class);

                if (bean.cached() && beanCache.containsKey(method.getReturnType())) {
                    continue;
                }

                method.setAccessible(true);
                final Object value = method.invoke(!Modifier.isStatic(method.getModifiers())
                        ? method.getDeclaringClass().newInstance()
                        : null
                );

                beanCache.put(method.getReturnType(), value);
                method.setAccessible(methodAccessible);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        for (Field field : scanResult.injects()) {
            try {
                final boolean fieldAccessible = field.isAccessible();
                field.setAccessible(true);

                if (!beanCache.containsKey(field.getType())) {
                    throw new IllegalStateException("Field " + field.getName() + " has @Inject annotation yet no bean registered with type " + field.getType());
                }

                field.set(
                        (!Modifier.isStatic(field.getModifiers()) ? field.getDeclaringClass().newInstance() : null),
                        beanCache.get(field.getType())
                );
                field.setAccessible(fieldAccessible);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }


        return new ProcessResult(scanResult.successful(), beanCache);
    }
}
