package software.plusminus.replacer;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class EnvReplacerTest {

    @Test
    void singleVariable() {
        String input = "User: ${USER}";
        Map<String, String> env = new HashMap<>();
        env.put("USER", "taras");

        String result = EnvReplacer.replaceEnvVariables(input, env);

        assertEquals("User: taras", result);
    }

    @Test
    void multipleVariables() {
        String input = "Home: ${HOME:default}, Missing: ${MISSING:default}, Empty: ${EMPTY:emptyVal}, None: ${NONE}";
        Map<String, String> env = new HashMap<>();
        env.put("HOME", "/home/test");
        env.put("EMPTY", "");

        String result = EnvReplacer.replaceEnvVariables(input, env);

        assertEquals("Home: /home/test, Missing: default, Empty: , None: ${NONE}", result);
    }

    @Test
    void defaultValue() {
        String input = "Path: ${NOT_SET:default/path}";
        Map<String, String> env = new HashMap<>();

        String result = EnvReplacer.replaceEnvVariables(input, env);

        assertEquals("Path: default/path", result);
    }
}