package uk.co.mulecode.versioning.plugin.model;

import lombok.Getter;

public enum Tag {
  SNAPSHOT("SNAPSHOT", "BUILD-SNAPSHOT", 1),
  MILESTONE("M", "M", 2),
  RELEASE_CANDIDATE("RC", "RC", 3),
  RELEASE("RELEASE", "RELEASE", 4);

  @Getter
  private final String inputName;
  @Getter
  private final String outputName;
  @Getter
  private final Integer weight;

  Tag(String inputName, String outputName, Integer weight) {
    this.inputName = inputName;
    this.outputName = outputName;
    this.weight = weight;
  }

  public static Tag valueOfName(String name) {
    for (Tag e : values()) {

      if (e.name().equalsIgnoreCase(name) ||
          e.outputName.equalsIgnoreCase(name) ||
          e.inputName.equalsIgnoreCase(name)) {

        return e;
      }
    }
    throw new IllegalArgumentException(
        "Invalid value '" + name + "' for name lookup"
    );
  }


}
