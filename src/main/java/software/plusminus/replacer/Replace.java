package software.plusminus.replacer;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Replace {

    private String from;
    private String to;
    @JsonProperty("if")
    private String ifExpression;
    private boolean replaceFileContent = true;
    private boolean replaceFileName = true;
    private boolean replaceFolderName = false;
    private boolean useEnvVariables = true;

    public static Replace of(String from, String to) {
        Replace replace = new Replace();
        replace.setFrom(from);
        replace.setTo(to);
        return replace;
    }
}
