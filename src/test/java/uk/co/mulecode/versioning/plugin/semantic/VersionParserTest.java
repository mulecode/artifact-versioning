package uk.co.mulecode.versioning.plugin.semantic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import uk.co.mulecode.versioning.plugin.model.Tag;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

@Slf4j
public class VersionParserTest {

  @Test
  public void shouldParseReleaseVersion() {

    var version = "1.2.3.RELEASE";

    Version parse = VersionParser.parse(version);

    assertThat(parse.getMajor(), is(1));
    assertThat(parse.getMinor(), is(2));
    assertThat(parse.getPatch(), is(3));
    assertThat(parse.getTagType(), is(Tag.RELEASE));
    assertThat(parse.getSeq(), is(1));
    assertThat(parse.toString(), is(version));
  }

  @Test
  public void shouldParseReleaseVersionStartingWithV() {

    var version = "v1.2.3.RELEASE";
    var expected = "1.2.3.RELEASE";

    Version parse = VersionParser.parse(version);

    assertThat(parse.getMajor(), is(1));
    assertThat(parse.getMinor(), is(2));
    assertThat(parse.getPatch(), is(3));
    assertThat(parse.getTagType(), is(Tag.RELEASE));
    assertThat(parse.getSeq(), is(1));
    assertThat(parse.toString(), is(expected));
  }

  @Test
  public void shouldParseSnapShotVersion() {

    var version = "1.2.3.BUILD-SNAPSHOT";

    Version parse = VersionParser.parse(version);

    assertThat(parse.getMajor(), is(1));
    assertThat(parse.getMinor(), is(2));
    assertThat(parse.getPatch(), is(3));
    assertThat(parse.getTagType(), is(Tag.SNAPSHOT));
    assertThat(parse.getSeq(), is(1));
    assertThat(parse.toString(), is(version));
  }

  @Test
  public void shouldParseRCVersion() {

    var version = "1.2.3.RC4";

    Version parse = VersionParser.parse(version);

    assertThat(parse.getMajor(), is(1));
    assertThat(parse.getMinor(), is(2));
    assertThat(parse.getPatch(), is(3));
    assertThat(parse.getTagType(), is(Tag.RELEASE_CANDIDATE));
    assertThat(parse.getSeq(), is(4));
    assertThat(parse.toString(), is(version));
  }

  @Test
  public void shouldParseMilestoneVersion() {

    var version = "1.2.3.M4";

    Version parse = VersionParser.parse(version);

    assertThat(parse.getMajor(), is(1));
    assertThat(parse.getMinor(), is(2));
    assertThat(parse.getPatch(), is(3));
    assertThat(parse.getTagType(), is(Tag.MILESTONE));
    assertThat(parse.getSeq(), is(4));
    assertThat(parse.toString(), is(version));
  }

  @Test
  public void shouldIncrementPatch() {

    var version = "1.2.3.RELEASE";
    var expected = "1.2.4.RELEASE";

    Version parse = VersionParser.parse(version);
    parse.incrementPatch();

    assertThat(parse.getMajor(), is(1));
    assertThat(parse.getMinor(), is(2));
    assertThat(parse.getPatch(), is(4));
    assertThat(parse.getTagType(), is(Tag.RELEASE));
    assertThat(parse.getSeq(), is(1));
    assertThat(parse.toString(), is(expected));
  }

  @Test
  public void shouldIncrementMinor() {

    var version = "1.2.3.RELEASE";
    var expected = "1.3.0.RELEASE";

    Version parse = VersionParser.parse(version);
    parse.incrementMinor();

    assertThat(parse.getMajor(), is(1));
    assertThat(parse.getMinor(), is(3));
    assertThat(parse.getPatch(), is(0));
    assertThat(parse.getTagType(), is(Tag.RELEASE));
    assertThat(parse.getSeq(), is(1));
    assertThat(parse.toString(), is(expected));
  }

  @Test
  public void shouldIncrementMajor() {

    var version = "1.2.3.RELEASE";
    var expected = "2.0.0.RELEASE";

    Version parse = VersionParser.parse(version);
    parse.incrementMajor();

    assertThat(parse.getMajor(), is(2));
    assertThat(parse.getMinor(), is(0));
    assertThat(parse.getPatch(), is(0));
    assertThat(parse.getTagType(), is(Tag.RELEASE));
    assertThat(parse.getSeq(), is(1));
    assertThat(parse.toString(), is(expected));
  }

  @Test
  public void shouldIncrementSeq() {

    var version = "1.2.3.RC1";
    var expected = "1.2.3.RC2";

    Version parse = VersionParser.parse(version);
    parse.incrementSeq();

    assertThat(parse.getMajor(), is(1));
    assertThat(parse.getMinor(), is(2));
    assertThat(parse.getPatch(), is(3));
    assertThat(parse.getTagType(), is(Tag.RELEASE_CANDIDATE));
    assertThat(parse.getSeq(), is(2));
    assertThat(parse.toString(), is(expected));
  }

}