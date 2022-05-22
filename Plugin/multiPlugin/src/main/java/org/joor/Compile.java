/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.joor;


// ...

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.io.StringWriter;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;


/**
 * A utility that simplifies in-memory compilation of new classes.
 *
 * @author Lukas Eder
 */
class Compile {

    static Class<?> compile(String className, String content, CompileOptions compileOptions) {
        return compile(className, content, compileOptions, true);
    }

    static Class<?> compile(String className, String content, CompileOptions compileOptions, boolean expectResult) {
        Lookup lookup = MethodHandles.lookup();
        ClassLoader cl = lookup.lookupClass().getClassLoader();

        try {
            return cl.loadClass(className);
        } catch (ClassNotFoundException ignore) {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

            if (compiler == null) {
                throw new ReflectException(
                        "No compiler was provided by ToolProvider.getSystemJavaCompiler(). Make sure the jdk.compiler module is" +
                                " available.");
            }

            try {
                ClassFileManager fileManager = new ClassFileManager(compiler.getStandardFileManager(null, null, null));

                List<CharSequenceJavaFileObject> files = new ArrayList<>();
                files.add(new CharSequenceJavaFileObject(className, content));
                StringWriter out = new StringWriter();

                List<String> options = new ArrayList<>(compileOptions.options);
                if (!options.contains("-classpath")) {
                    StringBuilder classpath = new StringBuilder();
                    String separator = System.getProperty("path.separator");
                    String cp = System.getProperty("java.class.path");
                    String mp = System.getProperty("jdk.module.path");

                    if (cp != null && !"".equals(cp)) {
                        classpath.append(cp);
                    }
                    if (mp != null && !"".equals(mp)) {
                        classpath.append(mp);
                    }

                    if (cl instanceof URLClassLoader) {
                        for (URL url : ((URLClassLoader) cl).getURLs()) {
                            if (classpath.length() > 0) {
                                classpath.append(separator);
                            }

                            if ("file".equals(url.getProtocol())) {
                                classpath.append(new File(url.toURI()));
                            }
                        }
                    }

                    options.addAll(Arrays.asList("-classpath", classpath.toString()));
                }

                CompilationTask task = compiler.getTask(out, fileManager, null, options, null, files);

                if (!compileOptions.processors.isEmpty()) {
                    task.setProcessors(compileOptions.processors);
                }

                task.call();

                if (fileManager.isEmpty()) {
                    if (!expectResult) {
                        return null;
                    }

                    throw new ReflectException("Compilation error: " + out);
                }

                Class<?> result = null;

                // This works if we have private-access to the interfaces in the class hierarchy
                if (Reflect.CACHED_LOOKUP_CONSTRUCTOR != null) {
                    result = fileManager.loadAndReturnMainClass(className,
                            (name, bytes) -> Reflect.on(cl).call("defineClass", name, bytes, 0, bytes.length).get());
                }


                return result;
            } catch (ReflectException e) {
                throw e;
            } catch (Exception e) {
                throw new ReflectException("Error while compiling " + className, e);
            }
        }
    }


    static final class JavaFileObject extends SimpleJavaFileObject {
        final ByteArrayOutputStream os = new ByteArrayOutputStream();

        JavaFileObject(String name, Kind kind) {
            super(URI.create("string:///" + name.replace('.', '/') + kind.extension), kind);
        }

        byte[] getBytes() {
            return os.toByteArray();
        }

        @Override
        public OutputStream openOutputStream() {
            return os;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return new String(os.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    static final class ClassFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {
        private final Map<String, JavaFileObject> fileObjectMap;
        private Map<String, byte[]> classes;

        ClassFileManager(StandardJavaFileManager standardManager) {
            super(standardManager);

            fileObjectMap = new LinkedHashMap<>();
        }

        @Override
        public JavaFileObject getJavaFileForOutput(
                Location location,
                String className,
                JavaFileObject.Kind kind,
                FileObject sibling
        ) {
            JavaFileObject result = new JavaFileObject(className, kind);
            fileObjectMap.put(className, result);
            return result;
        }

        boolean isEmpty() {
            return fileObjectMap.isEmpty();
        }

        Map<String, byte[]> classes() {
            if (classes == null) {
                classes = new LinkedHashMap<>();

                for (Entry<String, JavaFileObject> entry : fileObjectMap.entrySet()) {
                    classes.put(entry.getKey(), entry.getValue().getBytes());
                }
            }

            return classes;
        }

        Class<?> loadAndReturnMainClass(String mainClassName, ThrowingBiFunction<String, byte[], Class<?>> definer)
                throws Exception {
            Class<?> result = null;

            // [#117] We don't know the subclass hierarchy of the top level
            //        classes in the compilation unit, and we can't find out
            //        without either:
            //
            //        - class loading them (which fails due to NoClassDefFoundError)
            //        - using a library like ASM (which is a big and painful dependency)
            //
            //        Simple workaround: try until it works, in O(n^2), where n
            //        can be reasonably expected to be small.
            Deque<Entry<String, byte[]>> queue = new ArrayDeque<>(classes().entrySet());
            int n1 = queue.size();

            // Try at most n times
            for (int i1 = 0; i1 < n1 && !queue.isEmpty(); i1++) {
                int n2 = queue.size();

                for (int i2 = 0; i2 < n2; i2++) {
                    Entry<String, byte[]> entry = queue.pop();

                    try {
                        Class<?> c = definer.apply(entry.getKey(), entry.getValue());

                        if (mainClassName.equals(entry.getKey())) {
                            result = c;
                        }
                    } catch (ReflectException e) {
                        queue.offer(entry);
                    }
                }
            }

            return result;
        }
    }

    @FunctionalInterface
    interface ThrowingBiFunction<T, U, R> {
        R apply(T t, U u) throws Exception;
    }

    static final class CharSequenceJavaFileObject extends SimpleJavaFileObject {
        final CharSequence content;

        public CharSequenceJavaFileObject(String className, CharSequence content) {
            super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
            this.content = content;
        }

        @Override
        public CharSequence getCharContent(boolean ignoreEncodingErrors) {
            return content;
        }
    }
}

