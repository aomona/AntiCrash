package com.atsukimc.anticrash.audit;

import com.sun.source.util.JavacTask;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import javax.tools.ToolProvider;

/** Source-scanning audits must ignore inactive Stonecutter branches. */
public final class SourceText {
    private SourceText() { }
    public static String active(Path path) throws IOException {
        var compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) throw new IllegalStateException("Source audits require a JDK");
        try (var manager = compiler.getStandardFileManager(null, null, java.nio.charset.StandardCharsets.UTF_8)) {
            var task = (JavacTask) compiler.getTask(null, manager, null, List.of("-proc:none"), null,
                    manager.getJavaFileObjectsFromPaths(List.of(path)));
            return task.parse().iterator().next().toString();
        }
    }
}
