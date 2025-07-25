package software.plusminus.replacer;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
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

    private Set<ReplaceScope> scopes(ReplaceScope... scopes) {
        return new HashSet<>(Arrays.asList(scopes));
    }
}
