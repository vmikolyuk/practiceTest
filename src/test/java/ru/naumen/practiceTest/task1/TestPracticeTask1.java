package ru.naumen.practiceTest.task1;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevObject;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.List;
import java.util.stream.StreamSupport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Тестирование практического задания по git
 *
 * @author skokurin
 * @since 30.06.2023
 */
class TestPracticeTask1 {
    private static final String REPO_PATH = System.getProperty("app.git.repo");
    private static final File REPO_FILE = Path.of(REPO_PATH).toFile();

    private static List<String> getCommitHashes(Git git, String branch) throws GitAPIException {
        return getCommits(git, branch).stream()
                .map(RevObject::name)
                .toList();
    }

    private static List<RevCommit> getCommits(Git git, String branch) throws GitAPIException {
        git.checkout().setName(branch).call();
        var iterator = git.log().call();
        return StreamSupport.stream(iterator.spliterator(), false).toList();
    }

    private static List<DiffEntry> getCommitDiff(Git git, RevCommit commit) throws IOException, GitAPIException {
        ObjectId oldTreeId = commit.getParent(0).getTree().getId();
        ObjectId newTreeId = commit.getTree().getId();

        try (ObjectReader reader = git.getRepository().newObjectReader()) {
            CanonicalTreeParser oldTreeIter = new CanonicalTreeParser();
            oldTreeIter.reset(reader, oldTreeId);
            CanonicalTreeParser newTreeIter = new CanonicalTreeParser();
            newTreeIter.reset(reader, newTreeId);

            return git.diff()
                    .setNewTree(newTreeIter)
                    .setOldTree(oldTreeIter)
                    .call();
        }
    }

    private static void assertFileTimes(Path path, long expectedCreation, long expectedModified) throws IOException {
        BasicFileAttributes attributes = Files.readAttributes(path, BasicFileAttributes.class);
        assertThat(attributes.creationTime().toMillis(), equalTo(expectedCreation));
        assertThat(attributes.lastModifiedTime().toMillis(), equalTo(expectedModified));
    }

    @Test
    void testBranches() throws Exception {
        try (Git git = Git.open(REPO_FILE)) {
            var branches = git.branchList().call()
                    .stream()
                    .map(Ref::getName)
                    .toList();

            assertThat("В репозитории должны остаться только ветки main и feature3",
                    branches, containsInAnyOrder("refs/heads/main", "refs/heads/feature3"));
        }
    }

    @Test
    void testRepoLayout() throws Exception {
        try (Git git = Git.open(REPO_FILE)) {
            var mainHashes = getCommitHashes(git, "main");

            assertThat("В ветке main должно быть 5 коммитов", mainHashes, hasSize(5));

            assertThat("Хэш первых трех коммитов изменился",
                    mainHashes.subList(2, 5), equalTo(List.of(
                            "f8f2634ce33a84f58ae67aca6a8df2a2e11221ef",
                            "748f0396eafc5e9d8844196e44a89db057647d08",
                            "8275be35211646901fc263eac2fbce8b810286ae"
                    )));

            var featureHashes = getCommitHashes(git, "feature3");

            assertThat("В ветке feature3 должно быть 5 коммитов", featureHashes, hasSize(5));

            assertThat("Хэш первых трех коммитов изменился",
                    featureHashes.subList(2, 5), equalTo(List.of(
                            "f8f2634ce33a84f58ae67aca6a8df2a2e11221ef",
                            "748f0396eafc5e9d8844196e44a89db057647d08",
                            "8275be35211646901fc263eac2fbce8b810286ae"
                    )));

            assertThat("Ветки main и feature3 должны иметь общий коммит G",
                    featureHashes.get(1), equalTo(mainHashes.get(1)));

            var mainCommits = getCommits(git, "main");

            List<DiffEntry> diffG = getCommitDiff(git, mainCommits.get(1));

            String commitGMessage = "В коммите G должен быть добавлен 1 файл: file4";

            assertThat(commitGMessage, diffG, hasSize(1));
            assertThat(commitGMessage, diffG.get(0).getChangeType(), equalTo(DiffEntry.ChangeType.ADD));
            assertThat(commitGMessage, diffG.get(0).getNewPath(), equalTo("file4"));

            List<DiffEntry> diffH = getCommitDiff(git, mainCommits.get(0))
                    .stream()
                    .sorted(Comparator.comparing(DiffEntry::getNewPath))
                    .toList();

            String commitHMessage = "В коммите H должно быть добавлено 2 файла: file1, file2";

            assertThat(commitHMessage, diffH, hasSize(2));
            assertThat(commitHMessage, diffH.get(0).getChangeType(), equalTo(DiffEntry.ChangeType.ADD));
            assertThat(commitHMessage, diffH.get(0).getNewPath(), equalTo("file1"));
            assertThat(commitHMessage, diffH.get(1).getChangeType(), equalTo(DiffEntry.ChangeType.ADD));
            assertThat(commitHMessage, diffH.get(1).getNewPath(), equalTo("file2"));

            var featureCommits = getCommits(git, "feature3");

            List<DiffEntry> diffI = getCommitDiff(git, featureCommits.get(0));

            String commitIMessage = "В коммите I должен быть добавлен 1 файл: file6";

            assertThat(commitIMessage, diffI, hasSize(1));
            assertThat(commitIMessage, diffI.get(0).getChangeType(), equalTo(DiffEntry.ChangeType.ADD));
            assertThat(commitIMessage, diffI.get(0).getNewPath(), equalTo("file6"));
        }
    }

    @Test
    @Disabled
    void testFileTimes() throws Exception {
        try (Git git = Git.open(REPO_FILE)) {
            List<RevCommit> mainCommits = getCommits(git, "main");

            git.checkout().setName(mainCommits.get(1).name()).call();

            assertFileTimes(Path.of(REPO_FILE.toString(), "file4"), 1688144800053L, 1688144800053L);

            git.checkout().setName(mainCommits.get(0).name()).call();

            assertFileTimes(Path.of(REPO_FILE.toString(), "file1"), 1688154739850L, 1688154739850L);
            assertFileTimes(Path.of(REPO_FILE.toString(), "file2"), 1688154739850L, 1688154739850L);
        }
    }
}
