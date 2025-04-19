package software.plusminus.replacer;

import software.plusminus.util.FileUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Replacer {

    private Path sourceFolder;
    private Map<String, String> replaces;

    public Replacer(Map<String, String> replaces) {
        this(Paths.get(""), replaces);
    }

    public Replacer(Path sourceFolder, Map<String, String> replaces) {
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
        String replacedContent = replace(originalContent);
        if (originalContent.equals(replacedContent)) {
            return;
        }
        FileUtils.write(file, replacedContent);
    }

    private void renameFile(Path file) {
        Path fileName = file.getFileName();
        Path parent = file.getParent();
        if (fileName == null || parent == null) {
            return;
        }
        String originalFileName = fileName.toString();
        String replacedFileName = replace(originalFileName);
        if (originalFileName.equals(replacedFileName)) {
            return;
        }
        Path targetFile = parent.resolve(replacedFileName);
        try {
            Files.move(file, targetFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void renameFolder(Path folder) {
        String originalFolderName = folder.getFileName().toString();
        String replacedFolderName = replace(originalFolderName);
        if (originalFolderName.equals(replacedFolderName)) {
            return;
        }
        Path targetFolder = folder.getParent().resolve(replacedFolderName);
        try {
            Files.move(folder, targetFolder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String replace(String original) {
        StringBuilder replacedBuilder = new StringBuilder(original);
        replaces.forEach((k, v) -> replaceAll(replacedBuilder, k, v));
        return replacedBuilder.toString();
    }

    private static void replaceAll(StringBuilder sb, String target, String replacement) {
        int index = 0;
        while ((index = sb.indexOf(target, index)) != -1) {
            sb.replace(index, index + target.length(), replacement);
            index += replacement.length(); // move past the replacement
        }
    }
}
