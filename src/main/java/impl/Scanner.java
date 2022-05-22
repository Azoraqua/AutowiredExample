package impl;

import api.Bean;
import api.Inject;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public final class Scanner {
    public record ScanResult(boolean successful, Collection<Method> beans, Collection<Field> injects) {
    }

    private Set<Method> beans = new HashSet<>();
    private List<Field> injects = new ArrayList<>();

    public ScanResult scan() {
        // Scan entire classpath for Bean & Inject annotations.

        try {
            final File workdir = new File(System.getenv("GRADLE_OPTS").contains("--cfg-workdir") ? Objects.requireNonNull(RunnerImpl.class.getResource("/")).toURI() : new URI("."));

            for (File file : searchFilesRecursively(workdir, new ArrayList<>())) {
                final String path = file.getAbsolutePath();

                if (path.endsWith(".class")) {
                    final Class<?> clazz = Class.forName(path
                            .replace(workdir.getAbsolutePath(), "")
                            .replace(".class", "")
                            .substring(1));

                    scanBeans(clazz);
                    scanInjects(clazz);
                }
            }

            return new ScanResult(!beans.isEmpty() && !injects.isEmpty(), beans, injects);
        } catch (URISyntaxException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void scanBeans(Class<?> target) {
        // Search for beans.
        for (Method method : target.getDeclaredMethods()) {
            final boolean accessible = method.isAccessible();
            method.setAccessible(true);

            if (!method.isAnnotationPresent(Bean.class)) {
                continue;
            }

            beans.add(method);
            method.setAccessible(accessible);
        }
    }

    private void scanInjects(Class<?> target) {
        // Search for injects.
        for (Field field : target.getDeclaredFields()) {
            final boolean accessible = field.isAccessible();
            field.setAccessible(true);

            if (!field.isAnnotationPresent(Inject.class)) {
                continue;
            }

            injects.add(field);
            field.setAccessible(accessible);
        }
    }

    private List<File> searchFilesRecursively(File root, List<File> foundFiles) {
        for (File file : Objects.requireNonNull(root.listFiles())) {
            if (file.isDirectory()) {
                return searchFilesRecursively(file, foundFiles);
            }

            foundFiles.add(file);
        }

        return foundFiles;
    }
}
