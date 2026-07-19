package software.plusminus.replacer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.tuple;

class ConfigurationTest {

    @Test
    void buildReplaces() {
        Path config = Paths.get("src/test/resources/replacer.yml");
        List<Replace> replaces = Configuration.buildReplaces(config);
        assertThat(replaces)
                .hasSize(3)
                .extracting(Replace::getFrom, Replace::getTo, Replace::getScopes)
                .containsExactly(
                        tuple("plusminus-lorem", "plusminus-replaced\nline 2", scopes(ReplaceScope.CONTENT)),
                        tuple("a", "b", scopes(ReplaceScope.CONTENT)),
                        tuple("x", "z", scopes(ReplaceScope.FOLDER_NAME)));
    }

    @Test
    void buildReplacesFailsWhenFileIsMissing() {
        Path config = Paths.get("src/test/resources/does-not-exist.yml");
        assertThatThrownBy(() -> Configuration.buildReplaces(config))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("is missed");
    }

    @Test
    void buildReplacesFailsWhenFromIsEmpty(@TempDir Path dir) throws IOException {
        Path config = dir.resolve("replacer.yml");
        Files.write(config, "- from: \"\"\n  to: bar\n".getBytes(StandardCharsets.UTF_8));
        assertThatThrownBy(() -> Configuration.buildReplaces(config))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must not be empty");
    }

    @Test
    void buildReplacesFailsWhenFromIsMissing(@TempDir Path dir) throws IOException {
        Path config = dir.resolve("replacer.yml");
        Files.write(config, "- to: bar\n".getBytes(StandardCharsets.UTF_8));
        assertThatThrownBy(() -> Configuration.buildReplaces(config))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("must not be empty");
    }

    private Set<ReplaceScope> scopes(ReplaceScope... scopes) {
        return new HashSet<>(Arrays.asList(scopes));
    }
}
