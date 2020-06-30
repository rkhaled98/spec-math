import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SpecTreeToYamlStringsConverterTest {
  @Test
  void testConvertMapToYamlString() throws IOException {
    var yamlStringToSpecTreeConverter = new YamlStringToSpecTreeConverter();
    LinkedHashMap<String, Object> map1 =
        yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/serializationSample.yaml");

    var specTreeToYamlStringsConverter = new SpecTreeToYamlStringsConverter();

    assertThat(specTreeToYamlStringsConverter.convertSpecTreeToYamlString(map1))
        .isEqualTo(Files.readString(Path.of("src/test/resources/serializationSample.yaml")));
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
