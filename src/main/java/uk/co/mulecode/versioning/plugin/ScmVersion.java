package uk.co.mulecode.versioning.plugin;

import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import uk.co.mulecode.versioning.plugin.model.Incrementer;
import uk.co.mulecode.versioning.plugin.model.Tag;
import uk.co.mulecode.versioning.plugin.repository.GitRepository;
import uk.co.mulecode.versioning.plugin.semantic.Version;
import uk.co.mulecode.versioning.plugin.semantic.VersionParser;
import uk.co.mulecode.versioning.plugin.service.EnumParserService;
import uk.co.mulecode.versioning.plugin.service.OutVersionFileService;
import uk.co.mulecode.versioning.plugin.service.ShellService;
import uk.co.mulecode.versioning.plugin.service.VersionService;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static uk.co.mulecode.versioning.plugin.model.Incrementer.SKIP;

@Slf4j
public class ScmVersion extends DefaultTask {

  public static final String OUTPUT_DIR_NAME = "/versioning";

  private final ShellService shellService = new ShellService();
  private final GitRepository gitRepository = new GitRepository(getProject().getProjectDir().getPath(), shellService);
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
  @Setter
  private Boolean tagLatest;

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

    gitRepository.getAllTags()
        .stream()
        .filter(VersionParser::isValidVersionNumber)
        .map(VersionParser::parse)
        .max(Version::compareTo)
        .ifPresentOrElse(
            currentVersion -> nextVersion(nextTagSuffixEnum, incrementerEnum, currentVersion),
            () -> setupNewVersion(nextTagSuffixEnum, incrementerEnum)
        );
  }

  private void nextVersion(Tag nextTagSuffixEnum, Incrementer incrementerEnum, Version currentVersion) {
    log.warn(" Current version: {}", currentVersion);

    if (SKIP.equals(incrementerEnum)) {
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
          "[ATTENTION] Tag '{}' already on HEAD commit. \n" +
              "-> Task will resume.",
          currentVersion.toString()
      );
      outputNextVersion(currentVersion.toString());
      return;
    }

    Version nextVersion = versionService.applyNextVersion(
        nextTagSuffixEnum,
        incrementerEnum,
        currentVersion,
        tagLatest
    );

    outputNextVersion(nextVersion.toString());
    logAdviceToPush();
  }

  private void setupNewVersion(Tag nextTagSuffixEnum, Incrementer incrementer) {

    if (SKIP.equals(incrementer)) {
      log.warn(
          "Project is not setup and versionIncrementer set to 'SKIP'\n" +
              "Task will resume.\n" +
              "No version will be generated"
      );
      return;
    }

    var initialVersionNext = versionService.setupNewVersioning(
        initialVersion,
        nextTagSuffixEnum,
        tagLatest
    );

    outputNextVersion(initialVersionNext.toString());

    log.warn(
        "Project successfully initialised\n" +
            "Tags Created:\n" +
            "- '{}'", initialVersionNext
    );

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