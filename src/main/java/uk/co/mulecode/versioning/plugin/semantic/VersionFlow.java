package uk.co.mulecode.versioning.plugin.semantic;

import lombok.extern.slf4j.Slf4j;
import uk.co.mulecode.versioning.plugin.model.Tag;

@Slf4j
public class VersionFlow {

  public static boolean isValidVersionFlow(Tag prev, Tag next) {

    if (prev.equals(Tag.MILESTONE) || prev.equals(Tag.RELEASE_CANDIDATE)) {
      return next.getWeight() >= prev.getWeight();
    }

    return true;
  }

}
