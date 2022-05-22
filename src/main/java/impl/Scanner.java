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

    public ScanResult scan() {
        final Set<Method> beans = new HashSet<>();
        final List<Field> injects = new ArrayList<>();

        try {
            final File workdir = new File(System.getenv("GRADLE_OPTS").contains("--cfg-workdir") ? Objects.requireNonNull(RunnerImpl.class.getResource("/")).toURI() : new URI("."));

            // Scan entire classpath for Bean & Inject annotations.
            for (File file : searchFilesRecursively(workdir, new ArrayList<>())) {
                final String path = file.getAbsolutePath();

                if (path.endsWith(".class")) {
                    final Class<?> clazz = Class.forName(path
                            .replace(workdir.getAbsolutePath(), "")
                            .replace(".class", "")
                            .substring(1));

                    beans.addAll(scanBeans(clazz));
                    injects.addAll(scanInjects(clazz));
                }
            }

            return new ScanResult(!beans.isEmpty() && !injects.isEmpty(), beans, injects);
        } catch (URISyntaxException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private Set<Method> scanBeans(Class<?> target) {
        final Set<Method> beans = new HashSet<>();

        // Search for beans.
        for (Method method : target.getDeclaredMethods()) {
            if (!method.isAnnotationPresent(Bean.class)) {
                continue;
            }

            final boolean accessible = method.isAccessible();
            method.setAccessible(true);

            beans.add(method);
            method.setAccessible(accessible);
        }

        return beans;
    }

    private List<Field> scanInjects(Class<?> target) {
        final List<Field> injects = new ArrayList<>();

        // Search for injects.
        for (Field field : target.getDeclaredFields()) {
            if (!field.isAnnotationPresent(Inject.class)) {
                continue;
            }

            final boolean accessible = field.isAccessible();
            field.setAccessible(true);

            injects.add(field);
            field.setAccessible(accessible);
        }

        return injects;
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
