package uk.co.mulecode.versioning.plugin.semantic;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public final class VersionComparator {

  public static int compareTo(Version one, Version two) {

    if (Objects.isNull(two)) {
      return 1;
    }

    if (Objects.isNull(one)) {
      return -1;
    }

    if (one.equals(two)) {
      return 0;
    }

    Integer tagCompare = compareTag(one, two);
    Integer numberCompare = compareNumber(one, two);
    Integer seqCompare = compareSeq(one, two);

    // 1.0.0-RC1 == 1.1.0-RC1
    if (numberCompare != 0 && tagCompare == 0 && seqCompare == 0) {
      return numberCompare;
    }

    // 1.0.0-RC1 == 1.0.0-RC2
    if (numberCompare == 0 && tagCompare == 0 && seqCompare != 0) {
      return seqCompare;
    }

    // 3.3.4.RC1 == 2.5.1.RC4
    if (numberCompare != 0 && tagCompare == 0) {
      return numberCompare;
    }

    // 3.3.3.RELEASE == 3.3.3.RC1
    if (numberCompare == 0 && tagCompare != 0) {
      return tagCompare;
    }

    // 3.3.3.RELEASE == 1.0.0.RC1
    if (numberCompare == 1 && tagCompare == -1) {
      return -1;
    }

    return 1;
  }

  private static Integer compareNumber(Version one, Version two) {

    if (one.getMajor() > two.getMajor()) {
      return 1;
    }

    if (one.getMajor() < two.getMajor()) {
      return -1;
    }

    if (one.getMinor() > two.getMinor()) {
      return 1;
    }

    if (one.getMinor() < two.getMinor()) {
      return -1;
    }

    if (one.getPatch() > two.getPatch()) {
      return 1;
    }

    if (one.getPatch() < two.getPatch()) {
      return -1;
    }

    if (one.getMajor().equals(two.getMajor()) &&
        one.getMinor().equals(two.getMinor()) &&
        one.getPatch().equals(two.getPatch())) {
      return 0;
    }

    return -1;
  }

  private static Integer compareTag(Version one, Version two) {

    if (one.getTagType().getWeight() > two.getTagType().getWeight()) {
      return 1;
    }

    if (one.getTagType().getWeight() < two.getTagType().getWeight()) {
      return -1;
    }

    return 0;
  }

  private static Integer compareSeq(Version one, Version two) {

    return one.getSeq().compareTo(two.getSeq());
  }
}
