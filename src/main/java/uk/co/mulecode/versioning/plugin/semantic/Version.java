package uk.co.mulecode.versioning.plugin.semantic;

import lombok.Builder;
import lombok.Data;
import uk.co.mulecode.versioning.plugin.model.Incrementer;
import uk.co.mulecode.versioning.plugin.model.Tag;

import javax.annotation.Nonnull;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;

@Data
@Builder
public class Version implements Comparable<Version> {

  @Builder.Default
  private Integer patch = 0;
  @Builder.Default
  private Integer minor = 0;
  @Builder.Default
  private Integer major = 1;
  @Builder.Default
  private Tag tagType = Tag.SNAPSHOT;
  @Builder.Default
  private Integer seq = 1;

  public void tag(Tag nextTag) {
    if (!VersionFlow.isValidVersionFlow(this.getTagType(), nextTag)) {
      throw new IllegalStateException(
          "[ERROR] Invalid version flow.\n" +
              " Cannot decrease a '" + this.tagType.getInputName() + "' to '" + nextTag.getInputName() + "."
      );
    }
    this.tagType = nextTag;
  }

  public void incrementPatch() {
    this.patch++;
  }

  public void incrementMinor() {
    this.patch = 0;
    this.minor++;
  }

  public void incrementMajor() {
    this.patch = 0;
    this.minor = 0;
    this.major++;
  }

  public void resetSeq() {
    this.seq = 1;
  }

  public void incrementSeq() {
    this.seq++;
  }

  public void increment(Incrementer increment) {

    requireNonNull(increment);

    switch (increment) {
      case PATCH:
        incrementPatch();
        break;
      case MINOR:
        incrementMinor();
        break;
      case MAJOR:
        incrementMajor();
        break;
      default:
        throw new IllegalArgumentException(
            format("Invalid increment '%s' value", increment)
        );
    }
  }

  @Override
  public String toString() {

    StringBuilder append = new StringBuilder()
        .append(major)
        .append(".")
        .append(minor)
        .append(".")
        .append(patch)
        .append(".")
        .append(tagType.getOutputName());

    if (tagType.equals(Tag.RELEASE_CANDIDATE) || tagType.equals(Tag.MILESTONE)) {
      append.append(seq);
    }

    return append.toString();
  }

  public Version duplicate() {
    return Version.builder()
        .patch(patch)
        .minor(minor)
        .major(major)
        .tagType(tagType)
        .seq(seq)
        .build();
  }

  @Override
  public int compareTo(@Nonnull Version o) {
    return VersionComparator.compareTo(this, o);
  }
}
