package impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;

public final class Processor {
    public record ProcessResult(boolean successful, Map<String, Object> values) {
    }

    public ProcessResult process(Scanner.ScanResult scanResult) {
        final Map<String, Object> values = new HashMap<>();

        for (Method method : scanResult.beans()) {
            try {
                final boolean methodAccessible = method.isAccessible();
                final Object value = method.invoke(!Modifier.isStatic(method.getModifiers())
                        ? method.getDeclaringClass().newInstance()
                        : null
                );

                method.setAccessible(true);

                for (Field field : scanResult.injects()) {
                    final boolean fieldAccessible = field.isAccessible();
                    field.setAccessible(true);

                    field.set((!Modifier.isStatic(field.getModifiers()) ? field.getDeclaringClass().newInstance() : null), value);
                    field.setAccessible(fieldAccessible);
                    values.put(field.getDeclaringClass().getName() + ":" + field.getName(), value);
                }

                method.setAccessible(methodAccessible);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        return new ProcessResult(scanResult.successful(), values);
    }
}
