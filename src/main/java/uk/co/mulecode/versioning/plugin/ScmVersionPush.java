package uk.co.mulecode.versioning.plugin;

import lombok.extern.slf4j.Slf4j;
import org.gradle.api.DefaultTask;
import org.gradle.api.tasks.TaskAction;
import uk.co.mulecode.versioning.plugin.repository.GitRepository;
import uk.co.mulecode.versioning.plugin.service.ShellService;

@Slf4j
public class ScmVersionPush extends DefaultTask {

  private final ShellService shellService = new ShellService();
  private final GitRepository gitRepository = new GitRepository(
      getProject().getProjectDir().getPath(),
      shellService
  );

  @TaskAction
  public void pushTags() {

    log.warn("Pushing tags");
    gitRepository.pushTags();
  }

}