package uk.co.mulecode.versioning.plugin.semantic;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VersionNumber {
  Integer patch;
  Integer minor;
  Integer major;
}
