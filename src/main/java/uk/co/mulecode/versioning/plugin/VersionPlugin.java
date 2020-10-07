package uk.co.mulecode.versioning.plugin;

import lombok.extern.slf4j.Slf4j;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import uk.co.mulecode.versioning.plugin.model.VersionConfig;

@Slf4j
public class VersionPlugin implements Plugin<Project> {

  public static final String GROUP = "versioning";
  public static final String TASK_NAME = "scmVersion";
  public static final String TASK_NAME_PUSH = "scmVersionPush";
  public static final String TASK_CONFIG_NAME = "versionConfig";

  @Override
  public void apply(Project target) {

    var config = target.getExtensions().create(
        TASK_CONFIG_NAME,
        VersionConfig.class
    );

    target.getTasks().register(
        TASK_NAME_PUSH,
        ScmVersionPush.class,
        task -> task.setGroup(GROUP)
    );

    target.getTasks().register(
        TASK_NAME,
        ScmVersion.class,
        task -> {
          task.setGroup(GROUP);
          task.setTagSuffix(config.getTagSuffix());
          task.setInitialVersion(config.getInitialVersion());
          task.setVersionIncrementer(config.getVersionIncrementer());
          task.setApplyVersion(config.getApplyVersion());
          task.setTagLatest(config.getTagLatest());
        }
    );

  }

}
