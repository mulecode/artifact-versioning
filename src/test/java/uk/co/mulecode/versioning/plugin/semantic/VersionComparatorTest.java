package uk.co.mulecode.versioning.plugin.semantic;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static uk.co.mulecode.versioning.plugin.semantic.VersionComparator.compareTo;
import static uk.co.mulecode.versioning.plugin.semantic.VersionParser.parse;

@Slf4j
public class VersionComparatorTest {

  @Test
  public void shouldReturnLatestTag() {

    var compare = compareTo(
        parse("1.2.3.RELEASE"),
        parse("1.2.3.RELEASE")
    );

    assertThat(compare, is(0));
  }

  @Test
  public void shouldReturnLatestTag2() {

    var compare = compareTo(
        parse("1.2.3.BUILD-SNAPSHOT"),
        parse("1.2.3.RELEASE")
    );

    assertThat(compare, is(-1));
  }

  @Test
  public void shouldReturnLatestTag3() {

    var compare = compareTo(
        parse("1.2.3.RELEASE"),
        parse("1.2.3.BUILD-SNAPSHOT")
    );

    assertThat(compare, is(1));
  }

  @Test
  public void shouldReturnLatestTag4() {

    var compare = compareTo(
        parse("1.2.3.M5"),
        parse("1.2.3.RC1")
    );

    assertThat(compare, is(-1));
  }

  @Test
  public void shouldReturnLatestTag5() {

    var compare = compareTo(
        parse("1.2.3.RC1"),
        parse("1.2.3.RC5")
    );

    assertThat(compare, is(-1));
  }

  @Test
  public void shouldReturnLatestTag6() {

    var compare = compareTo(
        parse("1.2.3.M6"),
        parse("1.2.3.M5")
    );

    assertThat(compare, is(1));
  }

  @Test
  public void shouldReturnLatestTag7() {

    var compare = compareTo(
        parse("1.2.3.M6"),
        parse("1.2.3.M6")
    );

    assertThat(compare, is(0));
  }

  @Test
  public void shouldReturnLatestNumbers() {

    var compare = compareTo(
        parse("1.2.3.RELEASE"),
        parse("1.2.3.RELEASE")
    );

    assertThat(compare, is(0));
  }

  @Test
  public void shouldReturnLatestNumbers1() {

    var compare = compareTo(
        parse("1.2.4.RELEASE"),
        parse("1.2.3.RELEASE")
    );

    assertThat(compare, is(1));
  }

  @Test
  public void shouldReturnLatestNumbers2() {

    var compare = compareTo(
        parse("1.2.3.RELEASE"),
        parse("1.2.4.RELEASE")
    );

    assertThat(compare, is(-1));
  }

  @Test
  public void shouldReturnLatestNumbers3() {

    var compare = compareTo(
        parse("1.4.3.RELEASE"),
        parse("1.2.4.RELEASE")
    );

    assertThat(compare, is(1));
  }

  @Test
  public void shouldReturnLatestNumbers4() {

    var compare = compareTo(
        parse("1.4.3.RELEASE"),
        parse("1.4.4.RELEASE")
    );

    assertThat(compare, is(-1));
  }

  @Test
  public void shouldReturnLatestNumbers5() {

    var compare = compareTo(
        parse("2.4.3.RELEASE"),
        parse("1.4.4.RELEASE")
    );

    assertThat(compare, is(1));
  }

  @Test
  public void shouldReturnLatestNumbers6() {

    var compare = compareTo(
        parse("2.4.3.RELEASE"),
        parse("2.4.4.RELEASE")
    );

    assertThat(compare, is(-1));
  }

  @Test
  public void shouldReturnLatestBoth() {

    var compare = compareTo(
        parse("2.5.1.RELEASE"),
        parse("1.3.4.RC4")
    );

    assertThat(compare, is(1));
  }

  @Test
  public void shouldReturnLatestBoth1() {

    var compare = compareTo(
        parse("2.5.1.RC4"),
        parse("1.3.4.RELEASE")
    );

    assertThat(compare, is(-1));
  }

  @Test
  public void shouldReturnLatestBoth2() {

    var compare = compareTo(
        parse("2.5.1.RC4"),
        parse("1.3.4.RC5")
    );

    assertThat(compare, is(1));
  }

  @Test
  public void shouldReturnLatestBoth3() {

    var compare = compareTo(
        parse("2.5.1.RC4"),
        parse("3.3.4.RC1")
    );

    assertThat(compare, is(-1));
  }

  @Test
  public void shouldReturnLatestBoth4() {

    var compare = compareTo(
        parse("2.5.1.RC1"),
        parse("3.3.4.RC1")
    );

    assertThat(compare, is(-1));
  }
}