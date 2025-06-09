package software.plusminus.replacer;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

class ConfigurationTest {

    @Test
    void buildReplaces() {
        Path config = Paths.get("src/test/resources/replacer.yml");
        List<Replace> replaces = Configuration.buildReplaces(config);
        assertThat(replaces)
                .hasSize(3)
                .extracting(Replace::getFrom, Replace::getTo)
                .containsExactly(
                        tuple("plusminus-lorem", "plusminus-replaced\nline 2"),
                        tuple("a", "b"),
                        tuple("x", "z"));
        assertThat(replaces.get(2).isReplaceFileName()).isFalse();
    }
}
