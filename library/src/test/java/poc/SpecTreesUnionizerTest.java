package poc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

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
    Map<String, Object> map1 =
        yamlStringToSpecTreeConverter.convertYamlFileToMap(
            "src/test/resources/simplepetstore.yaml");
    Map<String, Object> map2 =
        yamlStringToSpecTreeConverter.convertYamlFileToMap(
            "src/test/resources/simplepetstore2.yaml");
    Map<String, Object> defaults = new LinkedHashMap<>();
    HashMap<String, String> conflictResolutions = new HashMap<String, String>();
    Map<String, Object> expected =
        yamlStringToSpecTreeConverter.convertYamlFileToMap(
            "src/test/resources/simplepetstoremerged.yaml");

    assertThrows(
        UnableToUnionException.class,
        () -> specTreesUnionizer.mergeMaps(map1, map2, defaults, conflictResolutions));
    //    assertEquals(expected, mapUtils.mergeMaps(map1, map2));
  }

  @Test
  void testMergeMapsWithConflictsFixedByDefaults()
      throws FileNotFoundException, UnableToUnionException {
    Map<String, Object> map1 =
        yamlStringToSpecTreeConverter.convertYamlFileToMap(
            "src/test/resources/simplepetstore.yaml");
    Map<String, Object> map2 =
        yamlStringToSpecTreeConverter.convertYamlFileToMap(
            "src/test/resources/simplepetstore2.yaml");
    Map<String, Object> defaults =
        yamlStringToSpecTreeConverter.convertYamlFileToMap(
            "src/test/resources/simplepetstoredefaults.yaml");
    HashMap<String, String> conflictResolutions = new HashMap<String, String>();

    Map<String, Object> expected =
        yamlStringToSpecTreeConverter.convertYamlFileToMap(
            "src/test/resources/simplepetstoremerged.yaml");

    // assertThrows(UnableToUnionException.class, () -> mapUtils.mergeMaps(map1, map2, defaults));
    assertEquals(expected, specTreesUnionizer.mergeMaps(map1, map2, defaults, conflictResolutions));
  }

  @Test
  void testMergeMapsWithConflictsFixedByConflictResolutions()
          throws FileNotFoundException, UnableToUnionException {
    Map<String, Object> map1 =
            yamlStringToSpecTreeConverter.convertYamlFileToMap(
                    "src/test/resources/conflict1.yaml");
    Map<String, Object> map2 =
            yamlStringToSpecTreeConverter.convertYamlFileToMap(
                    "src/test/resources/conflict2.yaml");
    Map<String, Object> defaults = new LinkedHashMap<>();

    HashMap<String, String> conflictResolutions = new HashMap<String, String>();
    conflictResolutions.put("[paths, /pets, get, summary]", "CONFLICT RESOLVED");

    Map<String, Object> expected =
            yamlStringToSpecTreeConverter.convertYamlFileToMap(
                    "src/test/resources/conflictMerged.yaml");

    // assertThrows(UnableToUnionException.class, () -> mapUtils.mergeMaps(map1, map2, defaults));
    assertEquals(expected, specTreesUnionizer.mergeMaps(map1, map2, defaults, conflictResolutions));
  }

  @Test
  void testMergeMapsWithConflictsNotFixedByInvalidConflictResolutions()
          throws FileNotFoundException, UnableToUnionException {
    Map<String, Object> map1 =
            yamlStringToSpecTreeConverter.convertYamlFileToMap(
                    "src/test/resources/conflict1.yaml");
    Map<String, Object> map2 =
            yamlStringToSpecTreeConverter.convertYamlFileToMap(
                    "src/test/resources/conflict2.yaml");
    Map<String, Object> defaults = new LinkedHashMap<>();

    HashMap<String, String> conflictResolutions = new HashMap<String, String>();
    conflictResolutions.put("[this, /isnt, a, path]", "CONFLICT RESOLVED");

    assertThrows(UnableToUnionException.class, () -> specTreesUnionizer.mergeMaps(map1, map2, defaults, conflictResolutions));
//    assertEquals(expected, specTreesUnionizer.mergeMaps(map1, map2, defaults, conflictResolutions));
  }

  @Test
  void testMergeMapsWithoutConflicts() throws FileNotFoundException, UnableToUnionException {
    Map<String, Object> map1 =
        yamlStringToSpecTreeConverter.convertYamlFileToMap("src/test/resources/noConflict1.yaml");
    Map<String, Object> map2 =
        yamlStringToSpecTreeConverter.convertYamlFileToMap("src/test/resources/noConflict2.yaml");
    Map<String, Object> defaults = new LinkedHashMap<>();
    HashMap<String, String> conflictResolutions = new HashMap<String, String>();

    Map<String, Object> expected =
        yamlStringToSpecTreeConverter.convertYamlFileToMap(
            "src/test/resources/noConflictMerged.yaml");

    assertEquals(expected, specTreesUnionizer.mergeMaps(map1, map2, defaults, conflictResolutions));
  }
}
