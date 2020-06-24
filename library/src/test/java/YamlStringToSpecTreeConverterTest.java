import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class YamlStringToSpecTreeConverterTest {
  YamlStringToSpecTreeConverter yamlStringToSpecTreeConverter;

  @BeforeEach
  void init() {
    yamlStringToSpecTreeConverter = new YamlStringToSpecTreeConverter();
  }

  @Test
  void testThrowFileNotFoundException() {
    assertThrows(
        FileNotFoundException.class,
        () -> yamlStringToSpecTreeConverter.convertYamlFileToSpecTree("src/test/resources/fakepath.yaml"));
  }

  @Test
  void testConvertYamlFileToMap() throws FileNotFoundException {
    Map<String, Object> expected = new LinkedHashMap<String, Object>();

    Map<String, Object> license = new LinkedHashMap<String, Object>();
    license.put("name", "MIT");

    Map<String, Object> info = new LinkedHashMap<String, Object>();
    info.put("license", license);
    info.put("title", "Swagger Petstore");

    expected.put("openapi", "3.0.0");
    expected.put("info", info);

    assertEquals(
        expected, yamlStringToSpecTreeConverter.convertYamlFileToSpecTree("src/test/resources/simplepetstore3.yaml"));
  }

  @Test
  void testConvertEmptyStringToEmptyMap() throws FileNotFoundException {
    Map<String, Object> expected = new LinkedHashMap<>();

    assertEquals(
            expected, yamlStringToSpecTreeConverter.convertYamlStringToSpecTree(""));
  }

  @Test
  void testConvertOrderFileToYaml() throws FileNotFoundException {
    Map<String, Object> expected = new LinkedHashMap<>();

    Map<String, Object> license = new LinkedHashMap<>();
    license.put("name", "MIT");

    Map<String, Object> paths = new LinkedHashMap<>();
    Map<String, Object> pets = new LinkedHashMap<>();

    pets.put("get", null);
    paths.put("/pets", pets);

    expected.put("paths", paths);
    expected.put("openapi", null);
    expected.put("info", null);

    assertEquals(
        expected, yamlStringToSpecTreeConverter.convertYamlFileToSpecTree("src/test/resources/order.yaml"));
  }
}
