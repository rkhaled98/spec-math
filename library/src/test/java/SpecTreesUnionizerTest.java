import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static com.google.common.truth.Truth.*;

class SpecTreesUnionizerTest {
  MapUtils mapUtils;
  SpecTreesUnionizer specTreesUnionizer;
  YamlStringToSpecTreeConverter yamlStringToSpecTreeConverter;

  @BeforeEach
  void init() {
    specTreesUnionizer = new SpecTreesUnionizer();
    yamlStringToSpecTreeConverter = new YamlStringToSpecTreeConverter();
  }

  @Test
  void testMergeMapsWithConflictsThrows() throws FileNotFoundException {
    LinkedHashMap<String, Object> map1 =
        yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/simplepetstore.yaml");
    LinkedHashMap<String, Object> map2 =
        yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/simplepetstore2.yaml");
    UnionizerUnionParams unionizerUnionParams = UnionizerUnionParams.builder().build();
    LinkedHashMap<String, Object> expected =
        yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/simplepetstoremerged.yaml");


    assertThrows(
        UnableToUnionException.class,
        () -> specTreesUnionizer.union(map1, map2, unionizerUnionParams));
  }

  @Test
  void testMergeMapsWithConflictsFixedByDefaults()
      throws FileNotFoundException, UnableToUnionException {
    LinkedHashMap<String, Object> map1 =
        yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/simplepetstore.yaml");
    LinkedHashMap<String, Object> map2 =
        yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/simplepetstore2.yaml");
    LinkedHashMap<String, Object> defaults =
        yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/simplepetstoredefaults.yaml");

    UnionizerUnionParams unionizerUnionParams = UnionizerUnionParams.builder().defaults(defaults).build();

    LinkedHashMap<String, Object> expected =
        yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/simplepetstoremerged.yaml");

    assertEquals(expected, specTreesUnionizer.union(map1, map2, unionizerUnionParams));
  }

  @Test
  void testMergeMapsWithConflictsFixedByConflictResolutions()
          throws FileNotFoundException, UnableToUnionException {
    LinkedHashMap<String, Object> map1 =
            yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
                    "src/test/resources/conflict1.yaml");
    LinkedHashMap<String, Object> map2 =
            yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
                    "src/test/resources/conflict2.yaml");

    HashMap<String, String> conflictResolutions = new HashMap<>();
    conflictResolutions.put("[paths, /pets, get, summary]", "CONFLICT RESOLVED");

    UnionizerUnionParams unionizerUnionParams = UnionizerUnionParams.builder().conflictResolutions(conflictResolutions).build();

    LinkedHashMap<String, Object> expected =
            yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
                    "src/test/resources/conflictMerged.yaml");

    assertEquals(expected, specTreesUnionizer.union(map1, map2, unionizerUnionParams));
  }

  @Test
  void testMergeMapsWithConflictsNotFixedByInvalidConflictResolutions()
          throws FileNotFoundException, UnableToUnionException {
    LinkedHashMap<String, Object> map1 =
            yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
                    "src/test/resources/conflict1.yaml");
    LinkedHashMap<String, Object> map2 =
            yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
                    "src/test/resources/conflict2.yaml");

    HashMap<String, String> conflictResolutions = new HashMap<>();
    conflictResolutions.put("[this, /isnt, a, path]", "CONFLICT RESOLVED");

    UnionizerUnionParams unionizerUnionParams = UnionizerUnionParams.builder().conflictResolutions(conflictResolutions).build();

    assertThrows(UnableToUnionException.class, () -> specTreesUnionizer.union(map1, map2, unionizerUnionParams));
  }

  @Test
  void testMergeMapsWithoutConflicts() throws FileNotFoundException, UnableToUnionException {
    LinkedHashMap<String, Object> map1 =
        yamlStringToSpecTreeConverter.convertYamlFileToSpecTree("src/test/resources/noConflict1.yaml");
    LinkedHashMap<String, Object> map2 =
        yamlStringToSpecTreeConverter.convertYamlFileToSpecTree("src/test/resources/noConflict2.yaml");

    UnionizerUnionParams unionizerUnionParams = UnionizerUnionParams.builder().build();

    LinkedHashMap<String, Object> expected =
        yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/noConflictMerged.yaml");

    Map<String, Object> actual =specTreesUnionizer.union(map1, map2, unionizerUnionParams);

    assertEquals(expected, actual);
  }
}
