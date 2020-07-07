package uk.co.mulecode.versioning.plugin.semantic;

import uk.co.mulecode.versioning.plugin.model.Tag;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class VersionParser {

  private VersionParser() {
  }

  public static Version parse(String value) {

    if (!isSemanticVersion(value) && !isValidVersionNumber(value)) {
      throw new IllegalArgumentException(
          "[ERROR] Value " + value + " is not a valid semantic version."
      );
    }

    var number = extractVersion(value)
        .map(VersionParser::parseVersionNumber)
        .orElseThrow();

    var suffix = extractSuffix(value).orElse(Tag.RELEASE.getOutputName());
    var tag = extractTagFromSuffix(suffix).orElseThrow();
    var seq = extractTagSeqFromSuffix(suffix).orElse(1);

    return Version.builder()
        .patch(number.getPatch())
        .minor(number.getMinor())
        .major(number.getMajor())
        .tagType(tag)
        .seq(seq)
        .build();
  }

  public static VersionNumber parseVersionNumber(String value) {
    String[] numberArr = value.split("\\.");
    var major = Integer.parseInt(numberArr[0]);
    var minor = Integer.parseInt(numberArr[1]);
    var patch = Integer.parseInt(numberArr[2]);
    return VersionNumber.builder()
        .patch(patch)
        .minor(minor)
        .major(major)
        .build();
  }

  public static Tag parseTag(String value) {
    return Tag.valueOfName(value);
  }

  public static boolean isSemanticVersion(String value) {
    String regex = "((v)?(\\d+)(\\.)(\\d+)(\\.)(\\d+)([.]+)(M\\d+|RC\\d+|BUILD-SNAPSHOT|RELEASE))";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(value);
    return matcher.find();
  }

  public static Optional<String> extractVersion(String value) {
    var regex = "((\\d+)(\\.)(\\d+)(\\.)(\\d+))";
    return extractor(regex, value);
  }

  public static Optional<String> extractSuffix(String value) {
    var regex = "(M\\d+|RC\\d+|BUILD-SNAPSHOT|RELEASE)";
    return extractor(regex, value);
  }

  public static Optional<Tag> extractTagFromSuffix(String value) {
    var regex = "(M|RC|BUILD-SNAPSHOT|RELEASE)";
    return extractor(regex, value).map(Tag::valueOfName);
  }

  public static boolean isValidVersionNumber(String value) {
    var regex = "((\\d+)(\\.)(\\d+)(\\.)(\\d+))";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(value);
    return matcher.find();
  }

  public static boolean isTagSeq(String value) {
    var regex = "(\\d+)";
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(value);
    return matcher.find();
  }

  public static Optional<Integer> extractTagSeqFromSuffix(String value) {

    if (!isTagSeq(value)) {
      return Optional.empty();
    }

    var regex = "(\\d+)";
    return extractor(regex, value).map(Integer::parseInt);
  }

  public static Optional<String> extractor(String regex, String value) {
    Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(value);
    if (matcher.find()) {
      return Optional.of(matcher.group(1));
    }
    return Optional.empty();
  }
}
