package uk.co.mulecode.versioning.plugin.service;

import uk.co.mulecode.versioning.plugin.model.Incrementer;
import uk.co.mulecode.versioning.plugin.model.Tag;

import java.util.Arrays;
import java.util.stream.Collectors;

public class EnumParserService {

  public Incrementer validateIncrementer(String value) {
    try {

      return Incrementer.valueOf(value.toUpperCase());

    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Invalid versionIncrementer '" + value + "'. Please provide any of: \n" +
              "[" +
              Arrays.stream(Incrementer.values())
                  .map(Incrementer::name)
                  .collect(Collectors.joining(", "))
              + "]\n"
      );
    }
  }

  public Tag validateSuffix(String value) {
    try {

      return Tag.valueOfName(value.toUpperCase());

    } catch (Exception e) {
      throw new IllegalArgumentException(
          "Invalid tagSuffix '" + value + "'. Please provide any of: \n" +
              "[" +
              Arrays.stream(Tag.values())
                  .map(Tag::getInputName)
                  .collect(Collectors.joining(", "))
              + "]\n"
      );
    }
  }
}
