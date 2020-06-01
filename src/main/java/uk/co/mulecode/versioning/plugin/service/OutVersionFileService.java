package uk.co.mulecode.versioning.plugin.service;

import lombok.extern.slf4j.Slf4j;
import org.gradle.api.Project;

import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class OutVersionFileService {

  public static final String VERSION_OUT_FILENAME = "next.txt";

  public String read(Path versioningFolderOut) {

    try {
      var filePath = versioningFolderOut.resolve(VERSION_OUT_FILENAME);

      if (filePath.toFile().exists()) {
        return Files.readString(filePath);
      }
      log.warn("SCMVersion file not found");
    } catch (Exception e) {
      log.error("Error reading file version, {}", e.getMessage(), e);
    }
    return "";
  }

  public void write(Project project, Path versioningFolderOut, String version, Boolean applyVersion) {

    try {
      var filePath = versioningFolderOut.resolve(VERSION_OUT_FILENAME);

      log.warn(" Next version: {}\n", version);
      log.warn(" Outputting version: {}\n", filePath.toString());

      Files.write(filePath, version.getBytes());

      if (applyVersion) {
        log.warn(" Applying version to project and sub-projects");
        project.setVersion(version);
        project.getAllprojects().forEach(p ->
            p.setVersion(version)
        );
      }

    } catch (Exception e) {
      log.error("Error writing version to file, {}", e.getMessage(), e);
    }

  }
}
