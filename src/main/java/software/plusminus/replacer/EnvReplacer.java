package software.plusminus.replacer;

import lombok.experimental.UtilityClass;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@UtilityClass
public class EnvReplacer {

    private static final Pattern ENV_PATTERN = Pattern.compile("\\$\\{([^}:]+)(?::([^}]*))?}");

    public String replaceEnvVariables(String input) {
        return replaceEnvVariables(input, System.getenv());
    }

    String replaceEnvVariables(String input, Map<String, String> env) {
        Matcher matcher = ENV_PATTERN.matcher(input);
        StringBuffer result = new StringBuffer();

        while (matcher.find()) {
            String varName = matcher.group(1);
            String defaultValue = matcher.group(2);

            if (env.containsKey(varName)) {
                String envValue = env.get(varName);
                matcher.appendReplacement(result, Matcher.quoteReplacement(envValue != null ? envValue : ""));
            } else if (defaultValue != null) {
                matcher.appendReplacement(result, Matcher.quoteReplacement(defaultValue));
            }
        }

        matcher.appendTail(result);
        return result.toString();
    }
}
