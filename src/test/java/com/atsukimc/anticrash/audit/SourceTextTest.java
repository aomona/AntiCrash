package com.atsukimc.anticrash.audit;

import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class SourceTextTest {
    @TempDir Path directory;
    @Test void inactiveBranchesAreIgnoredAndStringContentsRemainIntact() throws Exception {
        Path file = directory.resolve("Example.java");
        Files.writeString(file, "// @Inject(method = \"wrong\")\nclass Example { String url = \"https://example.com/*literal*/\"; }\n");
        String active = SourceText.active(file);
        assertFalse(active.contains("@Inject"));
        assertTrue(active.contains("https://example.com/*literal*/"));
    }
}
