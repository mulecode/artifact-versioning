package uk.co.mulecode.versioning.plugin.model;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class VersionConfig {

  private String versionIncrementer = "patch";
  private String tagSuffix = "SNAPSHOT";
  private String initialVersion = "1.0.0";
  private Boolean applyVersion = true;
  private Boolean tagLatest = true;

}
