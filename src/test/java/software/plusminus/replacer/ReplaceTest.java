package software.plusminus.replacer;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;

class ReplaceTest {

    @Test
    void ofSetsFromAndTo() {
        Replace replace = Replace.of("a", "b");
        assertThat(replace.getFrom()).isEqualTo("a");
        assertThat(replace.getTo()).isEqualTo("b");
        assertThat(replace.getCondition()).isNull();
        assertThat(replace.getScopes()).containsExactly(ReplaceScope.CONTENT);
    }

    @Test
    void settersAndGetters() {
        Replace replace = new Replace();
        replace.setFrom("x");
        replace.setTo("y");
        replace.setCondition("true");
        replace.setScopes(new HashSet<>(Collections.singletonList(ReplaceScope.FILE_NAME)));
        assertThat(replace.getFrom()).isEqualTo("x");
        assertThat(replace.getTo()).isEqualTo("y");
        assertThat(replace.getCondition()).isEqualTo("true");
        assertThat(replace.getScopes()).containsExactly(ReplaceScope.FILE_NAME);
    }

    @Test
    void equalsSameInstance() {
        Replace replace = populated();
        assertThat(replace.equals(replace)).isTrue();
    }

    @Test
    void equalsWhenAllFieldsEqual() {
        assertThat(populated()).isEqualTo(populated());
        assertThat(populated().hashCode()).isEqualTo(populated().hashCode());
    }

    @Test
    void equalsWhenAllFieldsNull() {
        Replace one = new Replace();
        Replace two = new Replace();
        one.setScopes(null);
        two.setScopes(null);
        assertThat(one).isEqualTo(two);
        assertThat(one.hashCode()).isEqualTo(two.hashCode());
    }

    @Test
    void notEqualsDifferentFrom() {
        Replace other = populated();
        other.setFrom("other");
        assertThat(populated()).isNotEqualTo(other);
    }

    @Test
    void notEqualsDifferentTo() {
        Replace other = populated();
        other.setTo("other");
        assertThat(populated()).isNotEqualTo(other);
    }

    @Test
    void notEqualsDifferentCondition() {
        Replace other = populated();
        other.setCondition("other");
        assertThat(populated()).isNotEqualTo(other);
    }

    @Test
    void notEqualsDifferentScopes() {
        Replace other = populated();
        other.setScopes(new HashSet<>(Collections.singletonList(ReplaceScope.FOLDER_NAME)));
        assertThat(populated()).isNotEqualTo(other);
    }

    @Test
    void notEqualsNullFromAgainstSet() {
        Replace withNull = populated();
        withNull.setFrom(null);
        assertThat(withNull).isNotEqualTo(populated());
        assertThat(populated()).isNotEqualTo(withNull);
    }

    @Test
    void notEqualsNullToAgainstSet() {
        Replace withNull = populated();
        withNull.setTo(null);
        assertThat(withNull).isNotEqualTo(populated());
        assertThat(populated()).isNotEqualTo(withNull);
    }

    @Test
    void notEqualsNullConditionAgainstSet() {
        Replace withNull = populated();
        withNull.setCondition(null);
        assertThat(withNull).isNotEqualTo(populated());
        assertThat(populated()).isNotEqualTo(withNull);
    }

    @Test
    void notEqualsNullScopesAgainstSet() {
        Replace withNull = populated();
        withNull.setScopes(null);
        assertThat(withNull).isNotEqualTo(populated());
        assertThat(populated()).isNotEqualTo(withNull);
    }

    @Test
    void toStringContainsFields() {
        assertThat(populated().toString())
                .contains("from")
                .contains("to")
                .contains("condition")
                .contains("scopes");
    }

    private Replace populated() {
        Replace replace = new Replace();
        replace.setFrom("from");
        replace.setTo("to");
        replace.setCondition("true");
        replace.setScopes(new HashSet<>(Collections.singletonList(ReplaceScope.CONTENT)));
        return replace;
    }
}
