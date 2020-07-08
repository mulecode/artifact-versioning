package uk.co.mulecode.versioning.plugin.service;

import java.nio.charset.StandardCharsets;

public class ShellService {

  public String run(String shellCommand) {
    try {
      var runtime = Runtime.getRuntime();
      var process = runtime.exec(shellCommand);
      var bytes = process.getInputStream().readAllBytes();
      return new String(bytes, StandardCharsets.UTF_8);
    } catch (Exception e) {
      throw new IllegalStateException("Could not execute shell command. " + e.getMessage(), e);
    }
  }

}
