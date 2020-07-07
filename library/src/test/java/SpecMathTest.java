import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class SpecMathTest {
  @Test
  void testUnionTwoStringsNoConflicts()
      throws IOException, UnionConflictException, UnexpectedDataException {
    String spec1String = Files.readString(Path.of("src/test/resources/noConflict1.yaml"));
    String spec2String = Files.readString(Path.of("src/test/resources/noConflict2.yaml"));

    String actual = SpecMath.union(spec1String, spec2String);

    String expectedString = Files.readString(Path.of("src/test/resources/noConflictMerged.yaml"));

    assertThat(actual).isEqualTo(expectedString);
  }

  @Test
  void testUnionTwoStringsWithConflictsThrows() throws IOException {
    String spec1String = Files.readString(Path.of("src/test/resources/elgoogMarketing.yaml"));
    String spec2String = Files.readString(Path.of("src/test/resources/elgoogBilling.yaml"));

    UnionConflictException e =
        assertThrows(UnionConflictException.class, () -> SpecMath.union(spec1String, spec2String));

    ArrayList<Conflict> expected = new ArrayList<>();
    expected.add(
        new Conflict("[info, title]", "Elgoog Marketing Team API", "Elgoog Billing Team API"));
    expected.add(
        new Conflict(
            "[info, description]",
            "An API for Elgoog's marketing team",
            "An API for Elgoog's billing team"));

    assertThat(e.getConflicts()).isEqualTo(expected);
  }

  @Test
  void testUnionTwoStringsWithDefaults()
      throws IOException, UnionConflictException, UnexpectedDataException {
    String spec1String = Files.readString(Path.of("src/test/resources/elgoogMarketing.yaml"));
    String spec2String = Files.readString(Path.of("src/test/resources/elgoogBilling.yaml"));
    String defaults = Files.readString(Path.of("src/test/resources/elgoogMetadata.yaml"));

    UnionOptions unionOptions = UnionOptions.builder().defaults(defaults).build();
    String actual = SpecMath.union(spec1String, spec2String, unionOptions);

    String expected =
        Files.readString(Path.of("src/test/resources/elgoogBillingAndMarketingMetadataUnion.yaml"));

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void testUnionTwoEqualStringsEqualsOriginalString()
      throws IOException, UnionConflictException, UnexpectedDataException {
    String spec1String = Files.readString(Path.of("src/test/resources/elgoogMarketing.yaml"));
    String spec2String = Files.readString(Path.of("src/test/resources/elgoogMarketing.yaml"));
    String actual = SpecMath.union(spec1String, spec2String);
    String expected = Files.readString(Path.of("src/test/resources/elgoogMarketing.yaml"));

    assertThat(actual).isEqualTo(expected);
  }

  @Test
  void testApplyOverlay() throws IOException, UnionConflictException, UnexpectedDataException {
    String spec1String = Files.readString(Path.of("src/test/resources/elgoogMarketing.yaml"));
    String overlay = Files.readString(Path.of("src/test/resources/elgoogMetadata.yaml"));
    String actual = SpecMath.applyOverlay(overlay, spec1String);
    String expected =
        Files.readString(Path.of("src/test/resources/elgoogBillingOverlayedWithMetadata.yaml"));

    assertThat(actual).isEqualTo(expected);
  }
}
