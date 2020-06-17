/*
Copyright 2020 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package poc;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

import static org.junit.jupiter.api.Assertions.*;

class MapUtilsTest {
  YamlUtils yamlUtils;
  MapUtils mapUtils;

  @BeforeEach
  void init() {
    yamlUtils = new YamlUtils();
    mapUtils = new MapUtils();
  } 

  @Test
  void testGetKeypathsFromMap() throws FileNotFoundException, UnableToMergeException {
    Map<String, Object> map1 =
        yamlUtils.convertYamlFileToMap("src/test/resources/simplepetstore.yaml");
    HashSet<String> actual = new HashSet<String>();

    // TODO FILL THIS EXPECTED IN
    HashSet<String> expected = new HashSet<String>();

    mapUtils.getKeypathsFromMap(map1, new Stack<String>(), actual);

    assertEquals(expected, actual);
  }

  @Test
  void testMergeMapsWithConflictsThrows() throws FileNotFoundException {
    Map<String, Object> map1 =
        yamlUtils.convertYamlFileToMap("src/test/resources/simplepetstore.yaml");
    Map<String, Object> map2 =
        yamlUtils.convertYamlFileToMap("src/test/resources/simplepetstore2.yaml");
    Map<String, Object> defaults = new LinkedHashMap<>();
    Map<String, Object> expected =
        yamlUtils.convertYamlFileToMap("src/test/resources/simplepetstoremerged.yaml");

    assertThrows(UnableToMergeException.class, () -> mapUtils.mergeMaps(map1, map2, defaults));
    //    assertEquals(expected, mapUtils.mergeMaps(map1, map2));
  }

  @Test
  void testMergeMapsWithConflictsFixedByDefaults()
      throws FileNotFoundException, UnableToMergeException {
    Map<String, Object> map1 =
        yamlUtils.convertYamlFileToMap("src/test/resources/simplepetstore.yaml");
    Map<String, Object> map2 =
        yamlUtils.convertYamlFileToMap("src/test/resources/simplepetstore2.yaml");
    Map<String, Object> defaults =
        yamlUtils.convertYamlFileToMap("src/test/resources/simplepetstoredefaults.yaml");
    Map<String, Object> expected =
        yamlUtils.convertYamlFileToMap("src/test/resources/simplepetstoremerged.yaml");

    // assertThrows(UnableToMergeException.class, () -> mapUtils.mergeMaps(map1, map2, defaults));
    assertEquals(expected, mapUtils.mergeMaps(map1, map2, defaults));
  }

  @Test
  void testMergeMapsWithoutConflicts() throws FileNotFoundException, UnableToMergeException {
    Map<String, Object> map1 =
        yamlUtils.convertYamlFileToMap("src/test/resources/noConflict1.yaml");
    Map<String, Object> map2 =
        yamlUtils.convertYamlFileToMap("src/test/resources/noConflict2.yaml");
    Map<String, Object> defaults = new LinkedHashMap<>();

    Map<String, Object> expected =
        yamlUtils.convertYamlFileToMap("src/test/resources/noConflictMerged.yaml");

    assertEquals(expected, mapUtils.mergeMaps(map1, map2, defaults));
  }

  /*
  @Ignore("not ready yet, need to sort the keys by original order")
  @Test
  void testMergeMapsToYAML() throws IOException {
    Map<String, Object> map1 =
        yamlUtils.convertYamlFileToMap("src/test/resources/simplepetstore.yaml");
    Map<String, Object> map2 =
        yamlUtils.convertYamlFileToMap("src/test/resources/simplepetstore2.yaml");

    String expected =
        new String(Files.readAllBytes(Paths.get("src/test/resources/simplepetstoremerged.yaml")));
    assertEquals(expected, mapUtils.convertMapToYaml(mapUtils.mergeMaps(map1, map2)));
  }

  @Ignore("not ready yet, need to work on indentation in map->YAML output")
  @Test
  void testConvertMapToYaml() throws IOException {
    Map<String, Object> map1 =
        yamlUtils.convertYamlFileToMap("src/test/resources/simplepetstoremerged.yaml");

    String expected =
        new String(Files.readAllBytes(Paths.get("src/test/resources/simplepetstoremerged.yaml")));

    assertEquals(expected, mapUtils.convertMapToYaml(map1));
  }
  */
}
