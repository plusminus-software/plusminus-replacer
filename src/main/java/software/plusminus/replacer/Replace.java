package software.plusminus.replacer;

import lombok.Data;

@Data
public class Replace {

    private String from;
    private String to;
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
