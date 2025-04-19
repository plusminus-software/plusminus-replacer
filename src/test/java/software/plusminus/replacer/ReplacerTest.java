package software.plusminus.replacer;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import software.plusminus.util.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static software.plusminus.check.Checks.check;

@RunWith(MockitoJUnitRunner.class)
public class ReplacerTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();
    private Path sourceFolder = Paths.get("src/test/resources/files");
    private Map<String, String> replaces;
    private Replacer replacer;


    @Before
    public void before() {
        replaces = new HashMap<>();
        replaces.put("foo", "bar");
        replacer = new Replacer(temporaryFolder.getRoot().toPath(), replaces);
    }

    @After
    public void after() {
        temporaryFolder.delete();
    }

    @Test
    public void run() {
        populateTemporaryFolder();

        replacer.run();

        checkSourceFolder();
        checkTargetFolder();
    }

    private void populateTemporaryFolder() {
        List<Path> paths = walk(sourceFolder);
        paths.forEach(source -> {
            String subpath = source.toString().substring(sourceFolder.toString().length());
            if (subpath.startsWith(File.separator)) {
                subpath = subpath.substring(1);
            }
            if (subpath.isEmpty()) {
                return;
            }
            Path destination = temporaryFolder.getRoot().toPath().resolve(subpath);
            try {
                Files.copy(source, destination);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    private List<Path> walk(Path folder) {
        try (Stream<Path> stream = Files.walk(folder)) {
            return stream.collect(Collectors.toList());
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private void checkSourceFolder() {
        List<Path> sourcePaths = walk(sourceFolder);
        check(sourcePaths).hasSize(5);
        check(sourcePaths).contains(sourceFolder);
        check(sourcePaths).contains(sourceFolder.resolve("foo"));
        check(sourcePaths).contains(sourceFolder.resolve("foo/foo.txt"));
        check(sourcePaths).contains(sourceFolder.resolve("foo/foo.jpg"));
        check(sourcePaths).contains(sourceFolder.resolve("foo2.txt"));
        check(FileUtils.readString(sourceFolder.resolve("foo/foo.txt")))
                .is("This is foo!");
        check(FileUtils.readString(sourceFolder.resolve("foo2.txt")))
                .is("This is foo2!");
    }

    private void checkTargetFolder() {
        Path targetFolder = temporaryFolder.getRoot().toPath();
        List<Path> sourcePaths = walk(targetFolder);
        check(sourcePaths).hasSize(5);
        check(sourcePaths).contains(targetFolder);
        check(sourcePaths).contains(targetFolder.resolve("bar"));
        check(sourcePaths).contains(targetFolder.resolve("bar/bar.txt"));
        check(sourcePaths).contains(targetFolder.resolve("bar/bar.jpg"));
        check(sourcePaths).contains(targetFolder.resolve("bar2.txt"));
        check(FileUtils.readString(targetFolder.resolve("bar/bar.txt")))
                .is("This is bar!");
        check(FileUtils.readString(targetFolder.resolve("bar2.txt")))
                .is("This is bar2!");
    }
}
