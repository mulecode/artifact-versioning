package uk.co.mulecode.versioning.plugin.semantic;

import org.junit.Test;
import uk.co.mulecode.versioning.plugin.model.Tag;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

public class VersionTest {

  @Test
  public void shouldCreateWithDefaultValues() {

    Version version = Version.builder().build();

    assertThat(version.getMajor(), is(1));
    assertThat(version.getMinor(), is(0));
    assertThat(version.getPatch(), is(0));
    assertThat(version.getTagType(), is(Tag.SNAPSHOT));
    assertThat(version.getSeq(), is(1));
    assertThat(version.toString(), is("1.0.0.BUILD-SNAPSHOT"));
  }

  @Test
  public void shouldCreateOverridesTagValue() {

    Version version = Version.builder()
        .tagType(Tag.RELEASE_CANDIDATE)
        .build();

    assertThat(version.getMajor(), is(1));
    assertThat(version.getMinor(), is(0));
    assertThat(version.getPatch(), is(0));
    assertThat(version.getTagType(), is(Tag.RELEASE_CANDIDATE));
    assertThat(version.getSeq(), is(1));
    assertThat(version.toString(), is("1.0.0.RC1"));
  }
}