package uk.co.mulecode.versioning.plugin.semantic;

import groovy.lang.Tuple3;
import org.gradle.internal.impldep.org.apache.ivy.plugins.version.VersionMatcher;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;
import static uk.co.mulecode.versioning.plugin.model.Tag.MILESTONE;
import static uk.co.mulecode.versioning.plugin.model.Tag.RELEASE;
import static uk.co.mulecode.versioning.plugin.model.Tag.RELEASE_CANDIDATE;
import static uk.co.mulecode.versioning.plugin.model.Tag.SNAPSHOT;

public class VersionFlowTest {

  @Test
  public void givenValidFlowTest() {
    var sequences = List.of(
        new Tuple3<>(SNAPSHOT, SNAPSHOT, true),
        new Tuple3<>(SNAPSHOT, MILESTONE, true),
        new Tuple3<>(SNAPSHOT, RELEASE_CANDIDATE, true),
        new Tuple3<>(SNAPSHOT, RELEASE, true),

        new Tuple3<>(MILESTONE, MILESTONE, true),
        new Tuple3<>(MILESTONE, RELEASE_CANDIDATE, true),
        new Tuple3<>(MILESTONE, RELEASE, true),

        new Tuple3<>(RELEASE_CANDIDATE, RELEASE_CANDIDATE, true),
        new Tuple3<>(RELEASE_CANDIDATE, RELEASE, true),

        new Tuple3<>(RELEASE, RELEASE, true),
        new Tuple3<>(RELEASE, RELEASE_CANDIDATE, true),
        new Tuple3<>(RELEASE, MILESTONE, true),
        new Tuple3<>(RELEASE, SNAPSHOT, true)
    );

    sequences.forEach(t -> {
      var isValid = VersionFlow.isValidVersionFlow(t.getFirst(), t.getSecond());
      assertEquals(isValid, t.getThird());
    });
  }

  @Test
  public void givenInvalidFlowTest() {
    var sequences = List.of(
        new Tuple3<>(MILESTONE, SNAPSHOT, false),
        new Tuple3<>(RELEASE_CANDIDATE, SNAPSHOT, false),
        new Tuple3<>(RELEASE_CANDIDATE, MILESTONE, false)
    );

    sequences.forEach(t -> {
      var isValid = VersionFlow.isValidVersionFlow(t.getFirst(), t.getSecond());
      assertEquals(isValid, t.getThird());
    });
  }
}