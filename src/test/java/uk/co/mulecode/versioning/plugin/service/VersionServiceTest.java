package uk.co.mulecode.versioning.plugin.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.co.mulecode.versioning.plugin.repository.GitRepository;
import uk.co.mulecode.versioning.plugin.semantic.VersionParser;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.co.mulecode.versioning.plugin.model.Incrementer.MAJOR;
import static uk.co.mulecode.versioning.plugin.model.Incrementer.MINOR;
import static uk.co.mulecode.versioning.plugin.model.Incrementer.PATCH;
import static uk.co.mulecode.versioning.plugin.model.Tag.MILESTONE;
import static uk.co.mulecode.versioning.plugin.model.Tag.RELEASE;
import static uk.co.mulecode.versioning.plugin.model.Tag.RELEASE_CANDIDATE;
import static uk.co.mulecode.versioning.plugin.model.Tag.SNAPSHOT;
import static uk.co.mulecode.versioning.plugin.semantic.VersionParser.parse;

@RunWith(MockitoJUnitRunner.class)
public class VersionServiceTest {

  @Mock
  private GitRepository gitRepository;

  @InjectMocks
  private VersionService versionService;

  @Test
  public void applyNextVersion_snapshotToSnapshot() {

    var currentVersion = parse("v1.0.2.BUILD-SNAPSHOT");

    var nextVersion = versionService.applyNextVersion(SNAPSHOT, MINOR, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v1.0.2.BUILD-SNAPSHOT");

    verify(gitRepository, times(1)).tagDelete(currentVersion.toTagString());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_snapshotToMilestone() {

    var currentVersion = parse("v1.0.2.BUILD-SNAPSHOT");

    var nextVersion = versionService.applyNextVersion(MILESTONE, MINOR, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v1.0.2.M1");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_snapshotToRc() {

    var currentVersion = parse("v1.0.2.BUILD-SNAPSHOT");

    var nextVersion = versionService.applyNextVersion(RELEASE_CANDIDATE, MINOR, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v1.0.2.RC1");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_snapshotToRelease() {

    var currentVersion = parse("v1.0.2.BUILD-SNAPSHOT");

    var nextVersion = versionService.applyNextVersion(RELEASE, MINOR, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v1.0.2.RELEASE");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_milestoneToSnapshot_shouldFail() {

    var currentVersion = parse("v1.0.2.M1");

    assertThatThrownBy(() ->
        versionService.applyNextVersion(SNAPSHOT, MINOR, currentVersion))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("[ERROR] Invalid version flow.\n Cannot decrease a 'M' to 'SNAPSHOT.");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository, never()).tag(any());
  }

  @Test
  public void applyNextVersion_milestoneToMilestone() {

    var currentVersion = parse("v1.0.2.M1");

    var nextVersion = versionService.applyNextVersion(MILESTONE, MINOR, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v1.0.2.M2");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_milestoneToRc() {

    var currentVersion = parse("v1.0.2.M2");

    var nextVersion = versionService.applyNextVersion(RELEASE_CANDIDATE, MINOR, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v1.0.2.RC1");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_milestoneToRelease() {

    var currentVersion = parse("v1.0.2.M2");

    var nextVersion = versionService.applyNextVersion(RELEASE, MINOR, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v1.0.2.RELEASE");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_rcToSnapshot_shouldFail() {

    var currentVersion = parse("v1.0.2.RC3");

    assertThatThrownBy(() ->
        versionService.applyNextVersion(SNAPSHOT, MINOR, currentVersion))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("[ERROR] Invalid version flow.\n Cannot decrease a 'RC' to 'SNAPSHOT.");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository, never()).tag(any());
  }

  @Test
  public void applyNextVersion_rcToMilestone_shouldFail() {

    var currentVersion = parse("v1.0.2.RC3");

    assertThatThrownBy(() ->
        versionService.applyNextVersion(MILESTONE, MINOR, currentVersion))
        .isInstanceOf(IllegalStateException.class)
        .hasMessage("[ERROR] Invalid version flow.\n Cannot decrease a 'RC' to 'M.");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository, never()).tag(any());
  }

  @Test
  public void applyNextVersion_rcToRc() {

    var currentVersion = parse("v1.0.2.RC3");

    var nextVersion = versionService.applyNextVersion(RELEASE_CANDIDATE, MINOR, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v1.0.2.RC4");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_rcToRelease() {

    var currentVersion = parse("v1.0.2.RC3");

    var nextVersion = versionService.applyNextVersion(RELEASE, MINOR, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v1.0.2.RELEASE");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_releaseToSnapshotPatch() {

    var currentVersion = parse("v1.0.2.RELEASE");

    var nextVersion = versionService.applyNextVersion(SNAPSHOT, PATCH, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v1.0.3.BUILD-SNAPSHOT");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_releaseToSnapshotMinor() {

    var currentVersion = parse("v1.0.2.RELEASE");

    var nextVersion = versionService.applyNextVersion(SNAPSHOT, MINOR, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v1.1.0.BUILD-SNAPSHOT");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_releaseToSnapshotMajor() {

    var currentVersion = parse("v1.0.2.RELEASE");

    var nextVersion = versionService.applyNextVersion(SNAPSHOT, MAJOR, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v2.0.0.BUILD-SNAPSHOT");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_releaseToMilestonePatch() {

    var currentVersion = parse("v1.0.2.RELEASE");

    var nextVersion = versionService.applyNextVersion(MILESTONE, PATCH, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v1.0.3.M1");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_releaseToMilestoneMinor() {

    var currentVersion = parse("v1.0.2.RELEASE");

    var nextVersion = versionService.applyNextVersion(MILESTONE, MINOR, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v1.1.0.M1");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_releaseToMilestoneMajor() {

    var currentVersion = parse("v1.0.2.RELEASE");

    var nextVersion = versionService.applyNextVersion(MILESTONE, MAJOR, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v2.0.0.M1");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_releaseToRcPatch() {

    var currentVersion = parse("v1.0.2.RELEASE");

    var nextVersion = versionService.applyNextVersion(RELEASE_CANDIDATE, PATCH, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v1.0.3.RC1");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_releaseToRcMinor() {

    var currentVersion = parse("v1.0.2.RELEASE");

    var nextVersion = versionService.applyNextVersion(RELEASE_CANDIDATE, MINOR, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v1.1.0.RC1");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_releaseToRcMajor() {

    var currentVersion = parse("v1.0.2.RELEASE");

    var nextVersion = versionService.applyNextVersion(RELEASE_CANDIDATE, MAJOR, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v2.0.0.RC1");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_releaseToReleasePatch() {

    var currentVersion = parse("v1.0.2.RELEASE");

    var nextVersion = versionService.applyNextVersion(RELEASE, PATCH, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v1.0.3.RELEASE");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_releaseToReleaseMinor() {

    var currentVersion = parse("v1.0.2.RELEASE");

    var nextVersion = versionService.applyNextVersion(RELEASE, MINOR, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v1.1.0.RELEASE");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

  @Test
  public void applyNextVersion_releaseToReleaseMajor() {

    var currentVersion = parse("v1.0.2.RELEASE");

    var nextVersion = versionService.applyNextVersion(RELEASE, MAJOR, currentVersion);

    assertThat(nextVersion.toTagString()).isEqualTo("v2.0.0.RELEASE");

    verify(gitRepository, never()).tagDelete(any());
    verify(gitRepository).tag(nextVersion.toTagString());
  }

}