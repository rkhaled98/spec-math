import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SpecTreesUnionizerTest {
  YamlStringToSpecTreeConverter yamlStringToSpecTreeConverter;

  @BeforeEach
  void init() {
    yamlStringToSpecTreeConverter = new YamlStringToSpecTreeConverter();
  }

  @Test
  void testUnionMapsWithoutConflicts()
      throws FileNotFoundException, UnionConflictException, UnexpectedDataException {
    LinkedHashMap<String, Object> map1 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/noConflict1.yaml");
    LinkedHashMap<String, Object> map2 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/noConflict2.yaml");

    LinkedHashMap<String, Object> expected =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/noConflictMerged.yaml");

    assertThat(SpecTreesUnionizer.union(map1, map2)).isEqualTo(expected);
  }

  @Test
  void testUnionMapsWithConflictResolutionsAndDefaults()
      throws FileNotFoundException, UnionConflictException, UnexpectedDataException {
    LinkedHashMap<String, Object> map1 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/conflict1.yaml");
    LinkedHashMap<String, Object> map2 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/conflict2.yaml");
    LinkedHashMap<String, Object> defaults =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/conflictDefaults.yaml");

    HashMap<String, Object> conflictResolutions = new HashMap<>();
    conflictResolutions.put("[paths, /pets, get, summary]", "CONFLICT RESOLVED");

    UnionizerUnionParams unionizerUnionParams =
        UnionizerUnionParams.builder().defaults(defaults).conflictResolutions(conflictResolutions).build();

    LinkedHashMap<String, Object> expected =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/conflictsMergedWithDefaults.yaml");

    assertThat(SpecTreesUnionizer.union(map1, map2, unionizerUnionParams)).isEqualTo(expected);
  }

  @Test
  void testUnionMapsDoesNotApplyUnnecessaryConflictResolutions()
      throws FileNotFoundException, UnionConflictException, UnexpectedDataException {
    LinkedHashMap<String, Object> map1 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/noConflict1.yaml");
    LinkedHashMap<String, Object> map2 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/noConflict2.yaml");

    HashMap<String, Object> conflictResolutions = new HashMap<>();
    conflictResolutions.put("[info, title]", "CONFLICT RESOLUTION TITLE");

    UnionizerUnionParams unionizerUnionParams =
        UnionizerUnionParams.builder().conflictResolutions(conflictResolutions).build();

    LinkedHashMap<String, Object> expected =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/noConflictMerged.yaml");

    var actual = SpecTreesUnionizer.union(map1, map2, unionizerUnionParams);

    assertThat(ObjectCaster.castObjectToStringObjectMap(actual.get("info")).get("title"))
        .isNotEqualTo("CONFLICT RESOLUTION TITLE");

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void testUnionMapsWithConflictsThrows() throws FileNotFoundException {
    LinkedHashMap<String, Object> map1 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/simplepetstore.yaml");
    LinkedHashMap<String, Object> map2 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/simplepetstore2.yaml");
    UnionizerUnionParams unionizerUnionParams = UnionizerUnionParams.builder().build();

    UnionConflictException e =
        assertThrows(
            UnionConflictException.class,
            () -> SpecTreesUnionizer.union(map1, map2, unionizerUnionParams));

    ArrayList<Conflict> expectedConflicts = new ArrayList<>();
    expectedConflicts.add(
        new Conflict("[info, title]", "Swagger Petstore Platform", "Swagger Petstore Marketing"));
    expectedConflicts.add(
        new Conflict("[paths, /pets, get, summary]", "List all pets", "List every pet"));

    assertThat(e.getConflicts()).isEqualTo(expectedConflicts);
  }

  @Test
  void testUnionMapsWithConflictsFixedByDefaults()
      throws FileNotFoundException, UnionConflictException, UnexpectedDataException {
    LinkedHashMap<String, Object> map1 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/simplepetstore.yaml");
    LinkedHashMap<String, Object> map2 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/simplepetstore2.yaml");
    LinkedHashMap<String, Object> defaults =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/simplepetstoredefaults.yaml");

    UnionizerUnionParams unionizerUnionParams =
        UnionizerUnionParams.builder().defaults(defaults).build();

    LinkedHashMap<String, Object> expected =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/simplepetstoremerged.yaml");

    assertThat(SpecTreesUnionizer.union(map1, map2, unionizerUnionParams)).isEqualTo(expected);
  }

  @Test
  void testUnionMapsWithConflictsFixedByConflictResolutions()
      throws FileNotFoundException, UnionConflictException, UnexpectedDataException {
    LinkedHashMap<String, Object> map1 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/conflict1.yaml");
    LinkedHashMap<String, Object> map2 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/conflict2.yaml");

    HashMap<String, Object> conflictResolutions = new HashMap<>();
    conflictResolutions.put("[paths, /pets, get, summary]", "CONFLICT RESOLVED");

    UnionizerUnionParams unionizerUnionParams =
        UnionizerUnionParams.builder().conflictResolutions(conflictResolutions).build();

    LinkedHashMap<String, Object> expected =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/conflictMerged.yaml");

    assertThat(SpecTreesUnionizer.union(map1, map2, unionizerUnionParams)).isEqualTo(expected);
  }

  @Test
  void testUnionMapsWithConflictsNotFixedByInvalidConflictResolutions()
      throws FileNotFoundException, UnionConflictException {
    LinkedHashMap<String, Object> map1 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/conflict1.yaml");
    LinkedHashMap<String, Object> map2 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/conflict2.yaml");

    HashMap<String, Object> conflictResolutions = new HashMap<>();
    conflictResolutions.put("[this, /isnt, a, path]", "CONFLICT RESOLVED");

    UnionizerUnionParams unionizerUnionParams =
        UnionizerUnionParams.builder().conflictResolutions(conflictResolutions).build();

    assertThrows(
        UnionConflictException.class,
        () -> SpecTreesUnionizer.union(map1, map2, unionizerUnionParams));
  }

  @Test
  void testUnionMapsWithUnexpectedDataThrows() {
    LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();
    LinkedHashMap<String, Object> map2 = new LinkedHashMap<>();

    map1.put("samekey", new LinkedHashMap<String, Object>());
    map2.put("samekey", "not a map");

    assertThrows(UnexpectedDataException.class, () -> SpecTreesUnionizer.union(map1, map2));
  }
}
