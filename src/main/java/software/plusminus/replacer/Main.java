package software.plusminus.replacer;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@SpringBootApplication
public class Main implements ApplicationRunner {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @Override
    public void run(ApplicationArguments args) {
        Path sourceFolder = getSourceFolder(args);
        Path configFile = getConfigFile(args);
        List<Replace> replaces = Configuration.buildReplaces(configFile);
        new Replacer(sourceFolder, replaces).run();
    }

    private Path getConfigFile(ApplicationArguments args) {
        String configFilePath;
        if (args.containsOption("config")) {
            configFilePath = args.getOptionValues("config").get(0);
        } else {
            configFilePath = "replacer.yml";
        }
        return Paths.get(configFilePath);
    }

    private Path getSourceFolder(ApplicationArguments args) {
        String sourceFolderPath;
        if (args.containsOption("sourceFolder")) {
            sourceFolderPath = args.getOptionValues("sourceFolder").get(0);
        } else {
            sourceFolderPath = "";
        }
        return Paths.get(sourceFolderPath);
    }
}
