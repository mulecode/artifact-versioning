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
  public void shouldCompare_case1() {

    var compare = compareTo(
        parse("1.2.3.BUILD-SNAPSHOT"),
        parse("1.2.3.BUILD-SNAPSHOT")
    );

    assertThat(compare, is(0));
  }

  @Test
  public void shouldCompare_case2() {

    var compare = compareTo(
        parse("1.2.4.BUILD-SNAPSHOT"),
        parse("1.2.3.BUILD-SNAPSHOT")
    );

    assertThat(compare, is(1));
  }

  @Test
  public void shouldCompare_case3() {

    var compare = compareTo(
        parse("1.2.3.BUILD-SNAPSHOT"),
        parse("1.2.4.BUILD-SNAPSHOT")
    );

    assertThat(compare, is(-1));
  }

  @Test
  public void shouldCompare_case4() {

    var compare = compareTo(
        parse("1.3.3.BUILD-SNAPSHOT"),
        parse("1.2.4.BUILD-SNAPSHOT")
    );

    assertThat(compare, is(1));
  }

  @Test
  public void shouldCompare_case5() {

    var compare = compareTo(
        parse("1.3.3.BUILD-SNAPSHOT"),
        parse("1.3.4.BUILD-SNAPSHOT")
    );

    assertThat(compare, is(-1));
  }

  @Test
  public void shouldCompare_case6() {

    var compare = compareTo(
        parse("2.3.3.BUILD-SNAPSHOT"),
        parse("1.3.4.BUILD-SNAPSHOT")
    );

    assertThat(compare, is(1));
  }

  @Test
  public void shouldCompare_case7() {

    var compare = compareTo(
        parse("2.3.3.BUILD-SNAPSHOT"),
        parse("3.3.4.BUILD-SNAPSHOT")
    );

    assertThat(compare, is(-1));
  }

  @Test
  public void shouldCompare_case8() {

    var compare = compareTo(
        parse("1.0.1.M1"),
        parse("1.0.0.BUILD-SNAPSHOT")
    );

    assertThat(compare, is(1));
  }

  @Test
  public void shouldCompare_case9() {

    var compare = compareTo(
        parse("1.0.1.M1"),
        parse("1.1.0.BUILD-SNAPSHOT")
    );

    assertThat(compare, is(-1));
  }

  @Test
  public void shouldCompare_case10() {

    var compare = compareTo(
        parse("1.1.0.M1"),
        parse("1.1.0.M1")
    );

    assertThat(compare, is(0));
  }

  @Test
  public void shouldCompare_case11() {

    var compare = compareTo(
        parse("1.2.0.M1"),
        parse("1.1.0.M1")
    );

    assertThat(compare, is(1));
  }

  @Test
  public void shouldCompare_case12() {

    var compare = compareTo(
        parse("1.2.0.M1"),
        parse("1.1.0.M2")
    );

    assertThat(compare, is(1));
  }

  @Test
  public void shouldCompare_case13() {

    var compare = compareTo(
        parse("1.2.0.M3"),
        parse("1.3.0.M2")
    );

    assertThat(compare, is(-1));
  }

  @Test
  public void shouldCompare_case14() {

    var compare = compareTo(
        parse("1.3.0.M3"),
        parse("1.3.0.M2")
    );

    assertThat(compare, is(1));
  }

  @Test
  public void shouldCompare_case15() {

    var compare = compareTo(
        parse("1.3.0.M3"),
        parse("1.3.0.M4")
    );

    assertThat(compare, is(-1));
  }

  @Test
  public void shouldCompare_case16() {

    var compare = compareTo(
        parse("1.3.0.RC1"),
        parse("1.3.0.M3")
    );

    assertThat(compare, is(1));
  }

  @Test
  public void shouldCompare_case17() {

    var compare = compareTo(
        parse("1.3.0.RC1"),
        parse("1.3.0.RC2")
    );

    assertThat(compare, is(-1));
  }

  @Test
  public void shouldCompare_case18() {

    var compare = compareTo(
        parse("1.4.0.RC1"),
        parse("1.3.0.RC2")
    );

    assertThat(compare, is(1));
  }

  @Test
  public void shouldCompare_case19() {

    var compare = compareTo(
        parse("1.4.0.RC2"),
        parse("1.4.0.RC2")
    );

    assertThat(compare, is(0));
  }

  @Test
  public void shouldCompare_case20() {

    var compare = compareTo(
        parse("1.4.0.RC2"),
        parse("1.4.0.BUILD-SNAPSHOT")
    );

    assertThat(compare, is(1));
  }

  @Test
  public void shouldCompare_case21() {

    var compare = compareTo(
        parse("1.4.0.RC2"),
        parse("1.4.0.RELEASE")
    );

    assertThat(compare, is(-1));
  }

  @Test
  public void shouldCompare_case22() {

    var compare = compareTo(
        parse("1.4.0.RELEASE"),
        parse("1.4.0.RELEASE")
    );

    assertThat(compare, is(0));
  }
}
