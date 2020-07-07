import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Stack;
import org.junit.jupiter.api.Test;

class SpecTreeToYamlStringsConverterTest {
  @Test
  void testConvertMapToYamlString() throws IOException, UnexpectedDataException {
    LinkedHashMap<String, Object> map1 =
        YamlStringToSpecTreeConverter.convertYamlFileToSpecTree(
            "src/test/resources/serializationSample.yaml");

    var specTreeToYamlStringsConverter = new SpecTreeToYamlStringsConverter();

    assertThat(specTreeToYamlStringsConverter.convertSpecTreeToYamlString(map1))
        .isEqualTo(Files.readString(Path.of("src/test/resources/serializationSample.yaml")));
  }

  @Test
  void testSerializeMapWithUnexpectedDataThrows() {
    LinkedHashMap<String, Object> map1 = new LinkedHashMap<>();

    map1.put("key", (LinkedHashMap) null);

    var specTreeToYamlStringsConverter = new SpecTreeToYamlStringsConverter();

    assertThrows(
        UnexpectedDataException.class,
        () -> specTreeToYamlStringsConverter.convertSpecTreeToYamlString(map1));
  }
}
