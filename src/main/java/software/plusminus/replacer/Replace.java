package software.plusminus.replacer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Collections;
import java.util.Set;

@Data
public class Replace {

    private String from;
    private String to;
    @JsonProperty("if")
    private String condition;
    private Set<ReplaceScope> scopes = Collections.singleton(ReplaceScope.CONTENT);

    public static Replace of(String from, String to) {
        Replace replace = new Replace();
        replace.setFrom(from);
        replace.setTo(to);
        return replace;
    }
}
