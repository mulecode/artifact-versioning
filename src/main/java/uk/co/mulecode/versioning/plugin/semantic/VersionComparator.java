package uk.co.mulecode.versioning.plugin.semantic;

import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

@Slf4j
public final class VersionComparator {

  private VersionComparator() {
  }

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

    if (numberCompare != 0) {
      return numberCompare;
    }

    if (tagCompare != 0) {
      return tagCompare;
    }

    return seqCompare;
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
