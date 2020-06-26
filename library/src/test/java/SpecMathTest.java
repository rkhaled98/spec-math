import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import org.junit.jupiter.api.Test;

class SpecMathTest {
  @Test
  void testUnionTwoStrings() throws IOException, UnableToUnionException {
    String spec1String = Files.readString(Path.of("src/test/resources/noConflict1.yaml"));
    String spec2String = Files.readString(Path.of("src/test/resources/noConflict2.yaml"));

    String actual = SpecMath.union(spec1String, spec2String);

    String expectedString = Files.readString(Path.of("src/test/resources/noConflictMerged.yaml"));

    assertThat(actual).isEqualTo(expectedString);
  }
}