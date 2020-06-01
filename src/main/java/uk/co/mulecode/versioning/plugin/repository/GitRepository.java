package uk.co.mulecode.versioning.plugin.repository;

import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.RepositoryBuilder;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

@Slf4j
public class GitRepository {

  private static final String REFS_TAGS_PREFIX = "refs/tags/";
  private static final UnaryOperator<String> stripPrefix = s -> s.replace(REFS_TAGS_PREFIX, "");
  private final String basePath;

  public GitRepository(String basePath) {
    this.basePath = basePath;
  }

  private Repository getRepository() {

    var gitPath = String.format("%s/.git", basePath);
    var gitDir = new File(gitPath);

    if (!gitDir.exists()) {
      throw new IllegalStateException("Provided git path does not exists: " + basePath);
    }
    try {
      return new RepositoryBuilder()
          .setMustExist(true)
          .setGitDir(gitDir)
          .readEnvironment()
          .build();
    } catch (Exception e) {
      throw new IllegalStateException("Could not initiate git repository", e);
    }
  }

  public Boolean isTagExists(String tagName) {
    var repo = getRepository();

    try {

      ObjectId tagId = repo.resolve(REFS_TAGS_PREFIX + tagName);

      return Objects.nonNull(tagId);

    } catch (Exception e) {
      throw new IllegalStateException("could not locate tag existence", e);
    }
  }

  public Set<String> getLatestVersionTag(ObjectId tagId) {

    log.info("getLatestVersionTag: {}", tagId);
    var repo = getRepository();

    try (RevWalk walk = new RevWalk(repo)) {

      RevCommit commit = walk.parseCommit(tagId);
      return getTagsForCommit(commit);

    } catch (Exception e) {
      throw new IllegalStateException("could not execute getLatestVersionTag", e);
    }
  }

  public RevCommit getHeadCommit() {
    var repo = getRepository();

    try {
      ObjectId tagId = repo.resolve(Constants.HEAD);

      RevWalk walk = new RevWalk(repo);
      RevCommit headCommit = walk.parseCommit(tagId);

      log.info("getHeadCommit {}", headCommit);
      return headCommit;

    } catch (Exception e) {
      throw new IllegalStateException("could get head commit", e);
    }
  }

  public Set<String> getAllTags() {

    var repo = getRepository();

    try {
      return repo.getRefDatabase().getRefsByPrefix(REFS_TAGS_PREFIX)
          .stream()
          .map(Ref::getName)
          .map(stripPrefix)
          .collect(Collectors.toSet());
    } catch (IOException e) {
      throw new IllegalStateException("could not execute getLatestVersionTag", e);
    }
  }

  public void tag(final String name) {
    var repo = getRepository();
    var git = new Git(repo);
    try {

      Ref tagRef = git.tag()
          .setName(name)
          .setAnnotated(true)
          .call();
      log.info("tag: {}", tagRef);

    } catch (Exception e) {
      throw new IllegalStateException("could not tag" + e.getMessage(), e);
    }
  }

  public void tagDelete(final String name) {
    var repo = getRepository();
    var git = new Git(repo);
    try {
      git.tagDelete().setTags(name).call();
    } catch (GitAPIException e) {
      throw new IllegalStateException("could not delete tag", e);
    }
  }

  public void pushTags() {
    var repo = getRepository();
    var git = new Git(repo);
    try {

      PushCommand pushCommand1 = git.push()
          .setPushTags();

      pushCommand1.call();

    } catch (GitAPIException e) {
      throw new RuntimeException(e);
    }
  }

  private Set<String> getTagsForCommit(RevCommit latestCommit) {

    var repo = getRepository();
    try {
      log.info("Finding tags for: {}\n", latestCommit);
      var git = new Git(repo);
      RevWalk walk = new RevWalk(repo);

      var tags = new ArrayList<Ref>();
      List<Ref> refs = git.tagList().call();
      log.info("TAGS: {}", refs.size());
      for (Ref tagRef : refs) {
        final RevCommit tagCommit = walk.parseCommit(tagRef.getObjectId());
        final RevCommit objectCommit = walk.parseCommit(latestCommit);
        if (walk.isMergedInto(objectCommit, tagCommit)) {
          log.info("TAGS ADDED: {}", tagRef);
          tags.add(tagRef);
        } else {
          log.info("TAGS: {}", tagRef);
        }
      }

      return tags.stream()
          .map(Ref::getName)
          .map(stripPrefix)
          .collect(Collectors.toSet());

    } catch (Exception e) {
      throw new IllegalStateException("could not execute getTagsForCommit" + e.getMessage(), e);
    }
  }

  public Boolean isTagInHeadCommit(String tagValue) {
    RevCommit headCommit = getHeadCommit();
    Set<String> tagsForCommit = getTagsForCommit(headCommit);

    log.info("tags found for commit: {}", headCommit);
    boolean anyMatch = tagsForCommit.stream()
        .peek(log::info)
        .anyMatch(s -> s.equals(tagValue));
    log.info("{} tag is HEAD: {}", tagValue, anyMatch);

    return anyMatch;
  }
}
