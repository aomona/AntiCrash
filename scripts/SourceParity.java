import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.JavacTask;
import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaFileObject;
import javax.tools.ToolProvider;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/** Compare Java syntax trees (ignoring comments/formatting) and exact resource bytes. */
public class SourceParity {
    // javac retains Javadoc. Strip comments/whitespace iteratively, preserving quoted values.
    static String canonical(String source) {
        var result = new StringBuilder();
        for (int i = 0; i < source.length();) {
            char c = source.charAt(i);
            if (c == '\"' || c == '\'') {
                int start = i;
                boolean textBlock = c == '\"' && source.startsWith("\"\"\"", i);
                i += textBlock ? 3 : 1;
                while (i < source.length()) {
                    if (source.charAt(i) == '\\') { i = Math.min(source.length(), i + 2); continue; }
                    if (textBlock ? source.startsWith("\"\"\"", i) : source.charAt(i) == c) {
                        i += textBlock ? 3 : 1;
                        break;
                    }
                    i++;
                }
                result.append(source, start, i);
                continue;
            }
            if (source.startsWith("/*", i)) {
                int end = source.indexOf("*/", i + 2);
                i = end < 0 ? source.length() : end + 2;
            } else if (source.startsWith("//", i)) {
                while (i < source.length() && source.charAt(i) != '\n' && source.charAt(i) != '\r') i++;
            } else if (Character.isWhitespace(c)) {
                i++;
            } else {
                result.append(c);
                i++;
                continue;
            }
            if (!result.isEmpty() && result.charAt(result.length() - 1) != ' ') result.append(' ');
        }
        return result.toString().trim();
    }

    static Map<String, String> read(Path root, boolean productionOnly) throws Exception {
        Map<String, String> result = new TreeMap<>();
        List<Path> javaFiles;
        try (var walk = Files.walk(root)) {
            var files = walk.filter(Files::isRegularFile)
                    .filter(p -> !productionOnly || !root.relativize(p).toString().replace(java.io.File.separatorChar, '/').startsWith("test/java/"))
                    .sorted().toList();
            javaFiles = files.stream().filter(p -> p.toString().endsWith(".java")).toList();
            for (Path path : files) {
                if (!path.toString().endsWith(".java"))
                    result.put(root.relativize(path).toString(), hash(Files.readAllBytes(path)));
            }
        }
        var compiler = ToolProvider.getSystemJavaCompiler();
        if (compiler == null) throw new IllegalStateException("A JDK is required for source parity checks");
        var diagnostics = new DiagnosticCollector<JavaFileObject>();
        try (var manager = compiler.getStandardFileManager(diagnostics, null, StandardCharsets.UTF_8)) {
            var task = (JavacTask) compiler.getTask(null, manager, diagnostics,
                    List.of("-proc:none"), null, manager.getJavaFileObjectsFromPaths(javaFiles));
            for (CompilationUnitTree unit : task.parse()) {
                // A class absent in this target becomes an empty commented-out compilation unit.
                if (unit.getTypeDecls().isEmpty() && unit.getPackageName() == null && unit.getImports().isEmpty()) continue;
                String name = root.relativize(Path.of(unit.getSourceFile().toUri())).toString();
                result.put(name, hash(canonical(unit.toString()).getBytes(StandardCharsets.UTF_8)));
            }
        }
        var errors = diagnostics.getDiagnostics().stream().filter(d -> d.getKind() == Diagnostic.Kind.ERROR).toList();
        if (!errors.isEmpty()) throw new IllegalStateException("Java parse errors: " + errors.subList(0, Math.min(5, errors.size())));
        return result;
    }

    static String hash(byte[] bytes) throws Exception {
        return HexFormat.of().formatHex(MessageDigest.getInstance("SHA-256").digest(bytes));
    }

    static void selfTest() throws Exception {
        Path root = Files.createTempDirectory("anticrash-source-parity-");
        try {
            String longLiteral = "\"\"\"\n" + "line /*literal*/ // value ".repeat(5000) + "\n\"\"\"";
            if (!canonical(longLiteral).equals(longLiteral)) throw new AssertionError("Long text block changed");
            Path a = Files.createDirectories(root.resolve("a/main/java"));
            Path b = Files.createDirectories(root.resolve("b/main/java"));
            Files.writeString(a.resolve("Example.java"), "/** Old Javadoc. */ class Example { String url = \"https://example.com/*x*/\"; int n = 1; }");
            Files.writeString(b.resolve("Example.java"), "//? if 1.21.8\n/** New Javadoc. */ class Example { /*comment*/ String url = \"https://example.com/*x*/\"; int n=1; }\n// class Other {}\n");
            Files.writeString(b.resolve("Absent.java"), "// class Absent {}\n");
            if (!read(root.resolve("a"), false).equals(read(root.resolve("b"), false))) throw new AssertionError("Comments or empty variants changed parity");
            Files.writeString(b.resolve("Example.java"), "class Example { String url = \"https://example.com/*x*/\"; int n = 2; }");
            if (read(root.resolve("a"), false).equals(read(root.resolve("b"), false))) throw new AssertionError("Code change was not detected");
            if (canonical("a++ + b").equals(canonical("a + ++b"))) throw new AssertionError("Operator boundaries were lost");
            if (canonical("\"a b\"").equals(canonical("\"ab\""))) throw new AssertionError("String whitespace was lost");
            Files.writeString(root.resolve("b/changed.json"), "{}");
            if (!read(root.resolve("b"), false).containsKey("changed.json")) throw new AssertionError("Resource membership was not checked");
            Files.writeString(b.resolve("Broken.java"), "class Broken {");
            try { read(root.resolve("b"), false); throw new AssertionError("Invalid Java was accepted"); }
            catch (IllegalStateException expected) { }
            System.out.println("Source parity self-check passed: comments, strings, absent classes, code changes, resources and syntax errors.");
        } finally {
            try (var paths = Files.walk(root)) {
                for (Path path : paths.sorted(java.util.Comparator.reverseOrder()).toList()) Files.delete(path);
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 1 && args[0].equals("--self-test")) { selfTest(); return; }
        if (args.length < 2) throw new IllegalArgumentException("SourceParity <baseline-src> <generated-src> [--production-only]");
        boolean productionOnly = args.length == 3 && args[2].equals("--production-only");
        var expected = read(Path.of(args[0]).toAbsolutePath(), productionOnly);
        var actual = read(Path.of(args[1]).toAbsolutePath(), productionOnly);
        var differences = new java.util.TreeSet<>(expected.keySet());
        differences.addAll(actual.keySet());
        differences.removeIf(name -> java.util.Objects.equals(expected.get(name), actual.get(name)));
        if (!differences.isEmpty()) throw new AssertionError("Source/resource mismatch: " + differences.stream().limit(12).toList());
        System.out.println("Matching Java syntax trees and resource bytes: " + expected.size() + " files.");
    }
}
