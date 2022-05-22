package impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public final class Processor {
    public record ProcessResult(boolean successful) {
    }

    public ProcessResult process(Scanner.ScanResult scanResult) {
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
                }

                method.setAccessible(methodAccessible);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException(e);
            }
        }

        return new ProcessResult(scanResult.successful());
    }
}
