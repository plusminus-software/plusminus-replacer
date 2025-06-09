package software.plusminus.replacer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class Configuration {

    public List<Replace> buildReplaces(Path config) {
        if (!Files.exists(config) || !Files.isRegularFile(config)) {
            throw new IllegalStateException("The file " + config + " is missed");
        }
        List<Replace> replaces = readReplaces(config);
        replaces.stream()
                .filter(Replace::isReplaceEnvVariables)
                .forEach(replace -> {
                    replace.setFrom(EnvReplacer.replaceEnvVariables(replace.getFrom()));
                    replace.setTo(EnvReplacer.replaceEnvVariables(replace.getTo()));
                });
        return replaces;
    }

    private List<Replace> readReplaces(Path config) {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try (InputStream inputStream = Files.newInputStream(config)) {
            return Arrays.asList(mapper.readValue(inputStream, Replace[].class));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
