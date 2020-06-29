import java.util.ArrayList;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static com.google.common.truth.Truth.*;

import java.io.*;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class SpecTreesUnionizerTest {
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

    UnableToUnionException e = assertThrows(
        UnableToUnionException.class,
        () -> specTreesUnionizer.union(map1, map2, unionizerUnionParams));

    ArrayList<Conflict> expected = new ArrayList<>();
    expected.add(new Conflict("[info, title]", "Swagger Petstore Platform",  "Swagger Petstore Marketing"));
    expected.add(new Conflict("[paths, /pets, get, summary]", "List all pets", "List every pet"));

    assertThat(e.getConflicts()).isEqualTo(expected);
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

    UnionizerUnionParams unionizerUnionParams =
        UnionizerUnionParams.builder().defaults(defaults).build();

    LinkedHashMap<String, Object> expected =
        yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/simplepetstoremerged.yaml");

    assertThat(specTreesUnionizer.union(map1, map2, unionizerUnionParams)).isEqualTo(expected);
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

    HashMap<String, Object> conflictResolutions = new HashMap<>();
    conflictResolutions.put("[paths, /pets, get, summary]", "CONFLICT RESOLVED");

    UnionizerUnionParams unionizerUnionParams =
        UnionizerUnionParams.builder().conflictResolutions(conflictResolutions).build();

    LinkedHashMap<String, Object> expected =
        yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/conflictMerged.yaml");

    assertThat(specTreesUnionizer.union(map1, map2, unionizerUnionParams)).isEqualTo(expected);
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

    HashMap<String, Object> conflictResolutions = new HashMap<>();
    conflictResolutions.put("[this, /isnt, a, path]", "CONFLICT RESOLVED");

    UnionizerUnionParams unionizerUnionParams =
        UnionizerUnionParams.builder().conflictResolutions(conflictResolutions).build();

    assertThrows(
        UnableToUnionException.class,
        () -> specTreesUnionizer.union(map1, map2, unionizerUnionParams));
  }

  @Test
  void testMergeMapsWithoutConflicts() throws FileNotFoundException, UnableToUnionException {
    LinkedHashMap<String, Object> map1 =
        yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/noConflict1.yaml");
    LinkedHashMap<String, Object> map2 =
        yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/noConflict2.yaml");

    LinkedHashMap<String, Object> expected =
        yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/noConflictMerged.yaml");

    Map<String, Object> actual = specTreesUnionizer.union(map1, map2);

    assertThat(specTreesUnionizer.union(map1, map2)).isEqualTo(expected);
  }
}
