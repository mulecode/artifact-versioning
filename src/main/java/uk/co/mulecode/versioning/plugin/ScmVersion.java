package uk.co.mulecode.versioning.plugin;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import uk.co.mulecode.versioning.plugin.model.Incrementer;
import uk.co.mulecode.versioning.plugin.repository.GitRepository;
import uk.co.mulecode.versioning.plugin.semantic.Version;
import uk.co.mulecode.versioning.plugin.semantic.VersionParser;
import uk.co.mulecode.versioning.plugin.service.EnumParserService;
import uk.co.mulecode.versioning.plugin.service.OutVersionFileService;
import uk.co.mulecode.versioning.plugin.service.VersionService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

@Slf4j
public class ScmVersion extends DefaultTask {

  public static final String OUTPUT_DIR_NAME = "/versioning";

  private final GitRepository gitRepository = new GitRepository(getProject().getProjectDir().getPath());
  private final EnumParserService enumParserService = new EnumParserService();
  private final VersionService versionService = new VersionService(gitRepository);
  private final OutVersionFileService outVersionFileService = new OutVersionFileService();

  @Setter
  private String versionIncrementer;
  @Setter
  private String tagSuffix;
  @Setter
  private String initialVersion;
  @Setter
  private Boolean applyVersion;

  @OutputDirectory
  public File getVersioningFolderOut() {
    var buildDir = getProject().getBuildDir().getPath();
    Path filePath = Paths.get(buildDir + OUTPUT_DIR_NAME);
    return filePath.toFile();
  }

  @TaskAction
  public void applyNextVersion() {

    var nextTagSuffixEnum = enumParserService.validateSuffix(tagSuffix);
    var incrementerEnum = enumParserService.validateIncrementer(versionIncrementer);
    var tagLatest = gitRepository.getAllTags()
        .stream()
        .filter(VersionParser::isValidVersionNumber)
        .map(VersionParser::parse)
        .max(Version::compareTo);

    //Project not setup
    if (tagLatest.isEmpty()) {
      // Skip project setup
      if (!Incrementer.SKIP.equals(incrementerEnum)) {

        var initialVersionNext = versionService.setupNewVersioning(
            initialVersion,
            nextTagSuffixEnum
        );

        outputNextVersion(initialVersionNext.toString());

        log.warn(
            "Project successfully initialised\n" +
                "Tags Created:\n" +
                "- '{}'", initialVersionNext
        );

        logAdviceToPush();

      } else {

        log.warn(
            "Project is not setup and versionIncrementer set to 'SKIP'\n" +
                "Task will resume.\n" +
                "No version will be generated"
        );

      }
      return;
    }

    Version currentVersion = tagLatest.orElseThrow();

    log.warn(" Current version: {}", currentVersion);

    if (Incrementer.SKIP.equals(incrementerEnum)) {
      log.warn(
          "versionIncrementer 'SKIP' detected.\n" +
              "Task will resume.\n" +
              "No version will be generated"
      );
      outputNextVersion(currentVersion.toString());
      return;
    }

    if (gitRepository.isTagInHeadCommit(currentVersion.toTagString())) {
      log.warn(
          "[ATTENTION] Tag 'latest' already on HEAD commit. \n" +
              "-> Task will resume."
      );
      outputNextVersion(currentVersion.toString());
      return;
    }

    Version nextVersion = versionService.applyNextVersion(
        nextTagSuffixEnum,
        incrementerEnum,
        currentVersion
    );

    outputNextVersion(nextVersion.toString());
    logAdviceToPush();
  }

  private void logAdviceToPush() {
    log.warn(
        "\n[ACTION] Push the tags to remote branch with command:\n" +
            "'scmVersionPush'"
    );
  }

  private void outputNextVersion(String version) {
    outVersionFileService.write(
        getProject(),
        getVersioningFolderOut().toPath(),
        version,
        applyVersion
    );
  }

  public String nextVersion() {
    return outVersionFileService.read(
        getVersioningFolderOut().toPath()
    );
  }
}