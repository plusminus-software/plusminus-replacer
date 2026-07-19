package software.plusminus.replacer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

class TextDetectorTest {

    private static final int MAX_BYTES = 1024 * 5;
    private static final double THRESHOLD = 0.8d;

    @Test
    void plainTextIsTextual(@TempDir Path dir) throws IOException {
        Path file = write(dir, "text.txt", "This is foo!\nSecond line.\n");
        assertThat(TextDetector.isMostlyText(file, MAX_BYTES, THRESHOLD)).isTrue();
    }

    @Test
    void punctuationDenseFileIsTextual(@TempDir Path dir) throws IOException {
        String minified = "{\"a\":[1,2,3],\"b\":{\"c\":\"d\"},\"e\":true};"
                + "function(){return a&&b||c?(x):(y);}<xml attr=\"v\"/>";
        Path file = write(dir, "min.json", minified);
        assertThat(TextDetector.isMostlyText(file, MAX_BYTES, THRESHOLD)).isTrue();
    }

    @Test
    void binaryFileIsNotTextual() {
        Path jpg = Paths.get("src/test/resources/files/foo/foo.jpg");
        assertThat(TextDetector.isMostlyText(jpg, MAX_BYTES, THRESHOLD)).isFalse();
    }

    @Test
    void fileWithNulBytesIsNotTextual(@TempDir Path dir) throws IOException {
        byte[] bytes = new byte[256];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) (i % 2 == 0 ? 0 : 'a');
        }
        Path file = dir.resolve("binary.dat");
        Files.write(file, bytes);
        assertThat(TextDetector.isMostlyText(file, MAX_BYTES, THRESHOLD)).isFalse();
    }

    @Test
    void emptyFileIsNotTextual(@TempDir Path dir) throws IOException {
        Path file = dir.resolve("empty.txt");
        Files.createFile(file);
        assertThat(TextDetector.isMostlyText(file, MAX_BYTES, THRESHOLD)).isFalse();
    }

    private Path write(Path dir, String name, String content) throws IOException {
        Path file = dir.resolve(name);
        Files.write(file, content.getBytes(StandardCharsets.UTF_8));
        return file;
    }
}
