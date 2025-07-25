package software.plusminus.replacer;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import software.plusminus.util.FileUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Replacer {

    private Path sourceFolder;
    private List<Replace> replaces;

    public Replacer(Path sourceFolder, List<Replace> replaces) {
        this.sourceFolder = sourceFolder;
        this.replaces = replaces;
    }

    public void run() {
        List<Path> filesAndDirectories = collectFilesAndDirectories();
        filesAndDirectories.stream()
                .filter(Files::isRegularFile)
                .forEach(file -> {
                    replaceContent(file);
                    renameFile(file);
                });
        filesAndDirectories.stream()
                .filter(Files::isDirectory)
                .sorted(Comparator.comparingInt(Path::getNameCount).reversed())
                .forEach(this::renameFolder);
    }

    private List<Path> collectFilesAndDirectories() {
        try (Stream<Path> stream = Files.walk(sourceFolder)) {
            return stream.filter(path -> !path.equals(sourceFolder))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void replaceContent(Path file) {
        if (!TextDetector.isMostlyText(file, 1024 * 5, 0.8d)) {
            return;
        }
        String originalContent = FileUtils.readString(file);
        String replacedContent = replace(originalContent, file, ReplaceScope.CONTENT);
        if (originalContent.equals(replacedContent)) {
            return;
        }
        FileUtils.write(file, replacedContent);
    }

    private void renameFile(Path file) {
        Path fileName = file.getFileName();
        if (fileName == null) {
            return;
        }
        String originalFileName = fileName.toString();
        String replacedFileName = replace(originalFileName, file, ReplaceScope.FILE_NAME);
        if (originalFileName.equals(replacedFileName)) {
            return;
        }
        Path targetFile = getTarget(file, replacedFileName);
        try {
            Files.move(file, targetFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void renameFolder(Path folder) {
        String originalFolderName = folder.getFileName().toString();
        String replacedFolderName = replace(originalFolderName, folder, ReplaceScope.FOLDER_NAME);
        if (originalFolderName.equals(replacedFolderName)) {
            return;
        }
        Path targetFolder = getTarget(folder, replacedFolderName);
        try {
            Files.move(folder, targetFolder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @SuppressFBWarnings("PATH_TRAVERSAL_IN")
    private Path getTarget(Path source, String targetName) {
        Path parent = source.getParent();
        return parent != null
                ? parent.resolve(targetName)
                : Paths.get(targetName);
    }

    private String replace(String original, Path path, ReplaceScope scope) {
        StringBuilder replacedBuilder = new StringBuilder(original);
        Map<String, String> variables = Collections.singletonMap("PATH", path.toString());
        replaces.stream()
                .filter(r -> r.getScopes().contains(scope))
                .filter(r -> r.getCondition() == null || Evaluator.condition(r.getCondition(), variables))
                .forEach(r -> replace(replacedBuilder,
                        Evaluator.evaluate(r.getFrom(), variables),
                        Evaluator.evaluate(r.getTo(), variables)));
        return replacedBuilder.toString();
    }

    private void replace(StringBuilder sb, String from, String to) {
        int index = 0;
        while ((index = sb.indexOf(from, index)) != -1) {
            sb.replace(index, index + from.length(), to);
            index += to.length();
        }
    }
}
