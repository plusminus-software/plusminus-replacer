package software.plusminus.replacer;

import lombok.experimental.UtilityClass;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import software.plusminus.util.ResourceUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@UtilityClass
public class Evaluator {

    private static final Context JS = createJsContext();
    private static final String PLAIN_PREFIX = "plain:";
    private static final String JS_PREFIX = "js:";

    public String evaluate(String code, Map<String, String> variables) {
        if (code.startsWith(PLAIN_PREFIX)) {
            return code.substring(PLAIN_PREFIX.length());
        }
        code = prepareCode(code);
        Value result = evaluateWithVariables(code, variables);
        return result.isString() ? result.asString() : result.toString();
    }

    public boolean condition(String code, Map<String, String> variables) {
        Value result = evaluateWithVariables(code, variables);
        if (!result.isBoolean()) {
            throw new IllegalArgumentException("The code " + code + " must return boolean result but returns "
                    + result + " instead");
        }
        return result.asBoolean();
    }

    private Context createJsContext() {
        Context context = Context.newBuilder("js")
                .option("engine.WarnInterpreterOnly", "false")
                .build();
        addStrman(context);
        addEnvVariables(context);
        return context;
    }

    private void addStrman(Context context) {
        String strmanCode = ResourceUtils.toString("strman.js");
        String wrappedStrmanCode = "const exports = {}; (function(exports) {"
                + strmanCode
                + "})(exports); const strman = exports;";
        context.eval("js", wrappedStrmanCode);
        context.eval("js", "Object.assign(globalThis, strman);");
    }

    private void addEnvVariables(Context context) {
        Value bindings = context.getBindings("js");
        for (Map.Entry<String, String> entry : System.getenv().entrySet()) {
            String key = prepareVariableKey(entry.getKey());
            bindings.putMember(key, entry.getValue());
        }
    }

    private Value evaluateWithVariables(String code, Map<String, String> variables) {
        Value bindings = JS.getBindings("js");
        Set<String> keys = new HashSet<>();
        try {
            for (Map.Entry<String, String> entry : variables.entrySet()) {
                String key = prepareVariableKey(entry.getKey());
                keys.add(key);
                bindings.putMember(key, entry.getValue());
            }
            return JS.eval("js", code);
        } finally {
            keys.forEach(bindings::removeMember);
        }
    }

    private String prepareCode(String code) {
        if (code.startsWith(JS_PREFIX)) {
            code = code.substring(JS_PREFIX.length());
        } else if (!code.startsWith("`") || !code.endsWith("`")) {
            code = '`' + code + '`';
        }
        return code;
    }

    private String prepareVariableKey(String key) {
        return key.replaceAll("\\W", "_");
    }
}
