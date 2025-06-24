package software.plusminus.replacer;

import software.plusminus.util.FileUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
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
        String replacedContent = replace(originalContent,
                replaces.stream().filter(Replace::isReplaceFileContent));
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
        String replacedFileName = replace(originalFileName,
                replaces.stream().filter(Replace::isReplaceFileName));
        if (originalFileName.equals(replacedFileName)) {
            return;
        }
        Path targetFile = file.getParent() != null
                ? file.getParent().resolve(replacedFileName)
                : Paths.get(replacedFileName);
        try {
            Files.move(file, targetFile);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void renameFolder(Path folder) {
        String originalFolderName = folder.getFileName().toString();
        String replacedFolderName = replace(originalFolderName,
                replaces.stream().filter(Replace::isReplaceFolderName));
        if (originalFolderName.equals(replacedFolderName)) {
            return;
        }
        Path targetFolder = folder.getParent() != null
                ? folder.getParent().resolve(replacedFolderName)
                : Paths.get(replacedFolderName);
        try {
            Files.move(folder, targetFolder);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private String replace(String original, Stream<Replace> replacesStream) {
        StringBuilder replacedBuilder = new StringBuilder(original);
        replacesStream.forEach(r -> replace(replacedBuilder, r.getFrom(), r.getTo()));
        return replacedBuilder.toString();
    }

    private static void replace(StringBuilder sb, String from, String to) {
        int index = 0;
        while ((index = sb.indexOf(from, index)) != -1) {
            sb.replace(index, index + from.length(), to);
            index += to.length();
        }
    }
}
