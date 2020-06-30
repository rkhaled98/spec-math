import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConflictStringToConflictMapConverterTest {
  ConflictStringToConflictMapConverter conflictStringToConflictMapConverter;

  @BeforeEach
  void init() {
    conflictStringToConflictMapConverter = new ConflictStringToConflictMapConverter();
  }

  @Test
  void testConvertEmptyConflictResolutionsString() throws IOException {
    HashMap<String, Object> expected = new HashMap<>();

    assertThat(
            conflictStringToConflictMapConverter.convertConflictResolutionsStringToConflictMap(""))
        .isEqualTo(expected);
  }

  @Test
  void testConvertConflictResolutionsString() throws IOException {
    HashMap<String, Object> expected = new HashMap<>();
    expected.put("[paths, /pets/{petId}, get, summary]", "get the specified pets");

    String conflictResolutions =
        Files.readString(Path.of("src/test/resources/conflictResolutions.json"));

    assertThat(
            conflictStringToConflictMapConverter.convertConflictResolutionsStringToConflictMap(
                conflictResolutions))
        .isEqualTo(expected);
  }
}
