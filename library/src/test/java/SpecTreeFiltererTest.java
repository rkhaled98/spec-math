import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import org.junit.jupiter.api.Test;

class SpecTreeFiltererTest {
  @Test
  void filter_withSpecificPath_succeeds()
      throws FileNotFoundException, UnionConflictException, UnexpectedTypeException,
          AllUnmatchedFilterException {
    var specTreeFilterer = new SpecTreeFilterer();

    LinkedHashMap<String, Object> map1 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/filtering/filteringMonolithicSpec.yaml");

    FilterCriteria filterCriteria = FilterCriteria.builder().pathRegex("/pets/\\{petId\\}").build();

    var listOfFilterCriteria = new ArrayList<FilterCriteria>();
    listOfFilterCriteria.add(filterCriteria);

    LinkedHashMap<String, Object> expected =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/filtering/filteredMonolithicSpecWithSpecificPath.yaml");
    var actual = specTreeFilterer.filter(map1, listOfFilterCriteria);
    assertThat(actual).isEqualTo(expected);
    assertEquals(expected.get("paths"), actual.get("paths"));
    assertEquals(expected.get("components"), actual.get("components"));
  }

  @Test
  void filter_withSpecificOperations_succeeds()
      throws FileNotFoundException, UnionConflictException, UnexpectedTypeException,
          AllUnmatchedFilterException {
    var specTreeFilterer = new SpecTreeFilterer();

    LinkedHashMap<String, Object> map1 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/filtering/filteringMonolithicSpec.yaml");

    var operations = new ArrayList<String>();
    operations.add("post");
    operations.add("PATCH");

    FilterCriteria filterCriteria = FilterCriteria.builder().operations(operations).build();

    var listOfFilterCriteria = new ArrayList<FilterCriteria>();
    listOfFilterCriteria.add(filterCriteria);

    LinkedHashMap<String, Object> expected =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/filtering/filteredMonolithicSpecWithSpecificOperations.yaml");
    var actual = specTreeFilterer.filter(map1, listOfFilterCriteria);
    assertThat(actual).isEqualTo(expected);
    assertEquals(expected.get("paths"), actual.get("paths"));
    assertEquals(expected.get("components"), actual.get("components"));
  }

  @Test
  void filter_withRemovableTags_succeeds()
      throws FileNotFoundException, UnionConflictException, UnexpectedTypeException,
          AllUnmatchedFilterException {
    var specTreeFilterer = new SpecTreeFilterer();

    LinkedHashMap<String, Object> map1 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/filtering/filteringMonolithicSpec.yaml");

    var removableTags = new ArrayList<String>();
    removableTags.add("public");

    FilterCriteria filterCriteria = FilterCriteria.builder().removableTags(removableTags).build();

    var listOfFilterCriteria = new ArrayList<FilterCriteria>();
    listOfFilterCriteria.add(filterCriteria);

    LinkedHashMap<String, Object> expected =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/filtering/filteredMonolithicSpecWithPublicTags.yaml");

    var actual = specTreeFilterer.filter(map1, listOfFilterCriteria);
    assertThat(actual).isEqualTo(expected);
    assertEquals(expected.get("paths"), actual.get("paths"));
    assertEquals(expected.get("components"), actual.get("components"));
  }

  @Test
  void filter_withMultipleFilterCriteria_succeeds()
      throws FileNotFoundException, UnionConflictException, AllUnmatchedFilterException,
          UnexpectedTypeException {
    var specTreeFilterer = new SpecTreeFilterer();

    LinkedHashMap<String, Object> map1 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/filtering/filteringMonolithicSpec.yaml");

    var tags = new ArrayList<String>();
    tags.add("pets");

    FilterCriteria filterCriteria1 = FilterCriteria.builder().tags(tags).build();

    var operations = new ArrayList<String>();
    operations.add("get");

    FilterCriteria filterCriteria2 =
        FilterCriteria.builder().operations(operations).pathRegex("/pets.*").build();

    var listOfFilterCriteria = new ArrayList<FilterCriteria>();
    listOfFilterCriteria.add(filterCriteria1);
    listOfFilterCriteria.add(filterCriteria2);

    LinkedHashMap<String, Object> expected =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/filtering/filteredMonolithicSpecWithAllFilterCriteria.yaml");
    var actual = specTreeFilterer.filter(map1, listOfFilterCriteria);
    assertThat(actual).isEqualTo(expected);
    assertEquals(expected.get("paths"), actual.get("paths"));
    assertEquals(expected.get("components"), actual.get("components"));
  }

  @Test
  void filter_withUnselectedFilterCriteria_returnsOriginalSpec()
      throws FileNotFoundException, UnionConflictException, AllUnmatchedFilterException,
          UnexpectedTypeException {
    var specTreeFilterer = new SpecTreeFilterer();

    LinkedHashMap<String, Object> map1 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/filtering/filteringMonolithicSpec.yaml");

    FilterCriteria filterCriteria = FilterCriteria.builder().build();

    var listOfFilterCriteria = new ArrayList<FilterCriteria>();
    listOfFilterCriteria.add(filterCriteria);

    LinkedHashMap<String, Object> expected =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/filtering/filteringMonolithicSpec.yaml");
    var actual = specTreeFilterer.filter(map1, listOfFilterCriteria);
    assertThat(actual).isEqualTo(expected);
    assertEquals(expected.get("paths"), actual.get("paths"));
    assertEquals(expected.get("components"), actual.get("components"));
  }

  @Test
  void filter_withPathsThatWouldHaveNestedComponents_returnsComponentDependencyTree()
      throws FileNotFoundException, UnionConflictException, AllUnmatchedFilterException,
          UnexpectedTypeException {
    var specTreeFilterer = new SpecTreeFilterer();

    LinkedHashMap<String, Object> map1 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/filtering/filteringMonolithicSpec.yaml");

    var operations = new ArrayList<String>();
    operations.add("get");

    FilterCriteria filterCriteria =
        FilterCriteria.builder().operations(operations).pathRegex("/pets").build();

    var listOfFilterCriteria = new ArrayList<FilterCriteria>();
    listOfFilterCriteria.add(filterCriteria);

    LinkedHashMap<String, Object> expected =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/filtering/filteredMonolithicSpecWithOnlyPetsGet.yaml");

    var actual = specTreeFilterer.filter(map1, listOfFilterCriteria);
    assertThat(actual).isEqualTo(expected);
    assertEquals(expected.get("paths"), actual.get("paths"));
    assertEquals(expected.get("components"), actual.get("components"));
  }

  @Test
  void filter_withAllUnmatchedOrEmptyFilterCriteriaList_throws() throws FileNotFoundException {
    var specTreeFilterer = new SpecTreeFilterer();

    LinkedHashMap<String, Object> map1 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/filtering/filteringMonolithicSpec.yaml");

    var filterCriteriaEmptyList = new ArrayList<FilterCriteria>();

    LinkedHashMap<String, Object> expected =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/filtering/filteredMonolithicSpecWithPublicTags.yaml");

    assertThrows(
        AllUnmatchedFilterException.class,
        () -> specTreeFilterer.filter(map1, filterCriteriaEmptyList));
  }
}
