package uk.co.mulecode.versioning.plugin.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import uk.co.mulecode.versioning.plugin.model.Incrementer;
import uk.co.mulecode.versioning.plugin.model.Tag;
import uk.co.mulecode.versioning.plugin.repository.GitRepository;
import uk.co.mulecode.versioning.plugin.semantic.Version;
import uk.co.mulecode.versioning.plugin.semantic.VersionFlow;
import uk.co.mulecode.versioning.plugin.semantic.VersionParser;

import java.util.function.Predicate;

import static uk.co.mulecode.versioning.plugin.semantic.VersionParser.parseVersionNumber;

@Slf4j
@RequiredArgsConstructor
public class VersionService {

  private final static Predicate<Tag> isSnapShot = s -> s.equals(Tag.SNAPSHOT);
  private final static Predicate<Tag> isMilestone = s -> s.equals(Tag.MILESTONE);
  private final static Predicate<Tag> isRC = s -> s.equals(Tag.RELEASE_CANDIDATE);
  private final static Predicate<Tag> isRelease = s -> s.equals(Tag.RELEASE);

  private final GitRepository gitRepository;

  public Version setupNewVersioning(String initialVersion, Tag tagSuffix) {

    log.warn("[ATTENTION] Tag 'latest' not found. \n" +
        "Task will set up new versioning."
    );

    var anySemanticVersionTag = gitRepository.getAllTags()
        .stream()
        .anyMatch(VersionParser::isSemanticVersion);

    if (anySemanticVersionTag) {
      throw new IllegalStateException(
          "[ERROR] Project initialisation failed. Versions tags were detect.\n" +
              "These existing tags must be deleted to avoid future \n" +
              "conflicts with newer tags generation."
      );
    }

    if (StringUtils.isBlank(initialVersion) && VersionParser.isValidVersionNumber(initialVersion)) {
      throw new IllegalStateException(
          "[ERROR] Project initialisation failed. \n" +
              "Required property 'initialVersion' not found. \n" +
              "E.g: \n" +
              "versionConfig {\n" +
              "    versionIncrementer = \"minor\"\n" +
              "    tagSuffix = \"RELEASE\"\n" +
              "    initialVersion = \"1.0.0\"\n" +
              "}"
      );
    }

    var versionNumber = parseVersionNumber(initialVersion);

    var initialVersionNext = Version.builder()
        .tagType(tagSuffix)
        .major(versionNumber.getMajor())
        .minor(versionNumber.getMinor())
        .patch(versionNumber.getPatch())
        .build();

    if (isMilestone.or(isRC).or(isRelease).test(initialVersionNext.getTagType())) {
      gitRepository.tag(initialVersionNext.toTagString());
    }

    return initialVersionNext;
  }

  public Version applyNextVersion(Tag nextTagSuffixEnum, Incrementer incrementerEnum, Version currentVersion) {

    validateVersionFlow(currentVersion.getTagType(), nextTagSuffixEnum);

    Version nextVersion = currentVersion.duplicate();

    // FROM ( SNAPSHOT, M, RC ) TO ..., THEN KEEP VERSION
    if (isSnapShot.or(isMilestone).or(isRC).test(currentVersion.getTagType())) {

      // FROM SNAPSHOT to SNAPSHOT, THEN KEEP VERSION
      if (isSnapShot.test(nextTagSuffixEnum)) {
        gitRepository.tagDelete(currentVersion.toString());
      }

      //KEEP VERSION
      if (isRelease.test(nextTagSuffixEnum)) {
        nextVersion.setTagType(nextTagSuffixEnum);
      }

      //KEEP VERSION , TOP UP END
      if (isMilestone.or(isRC).test(currentVersion.getTagType())) {

        if (currentVersion.getTagType().equals(nextTagSuffixEnum)) {
          nextVersion.incrementSeq();
        } else {
          nextVersion.setTagType(nextTagSuffixEnum);
          nextVersion.resetSeq();
        }
      }
    }

    // FROM RELEASE TO ANY, THEN TOP UP VERSION AND UPDATE SUFFIX
    if (isRelease.test(currentVersion.getTagType())) {
      nextVersion.setTagType(nextTagSuffixEnum);
      nextVersion.increment(incrementerEnum);
    }

    if (isMilestone.or(isRC).or(isRelease).test(currentVersion.getTagType())) {
      gitRepository.tag(nextVersion.toTagString());
    }

    return nextVersion;
  }

  private void validateVersionFlow(Tag previous, Tag next) {
    if (!VersionFlow.isValidVersionFlow(previous, next)) {
      throw new IllegalStateException(
          "[ERROR] Invalid version flow.\n" +
              " Cannot decrease a '" + previous.getInputName() + "' to '" + next.getInputName() + "."
      );
    }
  }
}
