package com.colak.jet.jdbc;

import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;

public class DynamicCompiler {

    private DynamicCompiler() {
    }

    public static Class<?> compileForJava11(String className, String code)
            throws ClassNotFoundException, MalformedURLException {

        Class<?> clazz = null;

        JavaFileObject sourceObject = new JavaSourceFromString(className, code);

        // Get the system Java compiler
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

        // Get the standard file manager
        StandardJavaFileManager fileManager = compiler.getStandardFileManager(null, null,
                null);

        // Create a temporary directory for the compiled classes
        File outputDir = new File(System.getProperty("java.io.tmpdir"));
        outputDir.mkdirs();

        // Set the compiler options
        List<String> compilerOptions = List.of("-source", "11", "-target", "11", "-d", outputDir.getAbsolutePath());

        Iterable<? extends JavaFileObject> compilationUnits = List.of(sourceObject);
        JavaCompiler.CompilationTask task = compiler.getTask(null, fileManager, null, compilerOptions,
                null, compilationUnits);
        boolean success = task.call();
        if (success) {
            // Load the dynamically compiled class
            URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{outputDir.toURI().toURL()});
            clazz = Class.forName(className, true, classLoader);
        }
        return clazz;
    }

    static class JavaSourceFromString extends SimpleJavaFileObject {
        private final String code;

        public JavaSourceFromString(String name, String code) {
            super(URI.create("string:///" + name.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.code = code;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return code;
        }
    }
}
