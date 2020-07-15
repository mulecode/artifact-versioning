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

  private static final Predicate<Tag> isSnapShot = s -> s.equals(Tag.SNAPSHOT);
  private static final Predicate<Tag> isMilestone = s -> s.equals(Tag.MILESTONE);
  private static final Predicate<Tag> isRC = s -> s.equals(Tag.RELEASE_CANDIDATE);
  private static final Predicate<Tag> isRelease = s -> s.equals(Tag.RELEASE);

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

    if (isSnapShot.test(currentVersion.getTagType())) {
      if (isSnapShot.test(nextTagSuffixEnum)) {
        gitRepository.tagDelete(currentVersion.toTagString());
      } else {
        nextVersion.setTagType(nextTagSuffixEnum);
      }
    }

    if (isMilestone.test(currentVersion.getTagType())) {
      if (isMilestone.test(nextTagSuffixEnum)) {
        nextVersion.incrementSeq();
      } else {
        nextVersion.setTagType(nextTagSuffixEnum);
        nextVersion.resetSeq();
      }
    }

    if (isRC.test(currentVersion.getTagType())) {
      if (isRC.test(nextTagSuffixEnum)) {
        nextVersion.incrementSeq();
      } else {
        nextVersion.setTagType(nextTagSuffixEnum);
        nextVersion.resetSeq();
      }
    }

    if (isRelease.test(currentVersion.getTagType())) {
      nextVersion.setTagType(nextTagSuffixEnum);
      nextVersion.resetSeq();
      nextVersion.increment(incrementerEnum);
    }

    gitRepository.tag(nextVersion.toTagString());

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
