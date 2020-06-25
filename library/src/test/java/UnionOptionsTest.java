import org.junit.jupiter.api.Test;
import static com.google.common.truth.Truth.*;

class UnionParametersTest {
  @Test
  void testUseUnionParametersBuilderBuilds() {
    UnionOptions up =
        UnionOptions.builder()
            .conflictResolutions("testing auto value")
            .defaults("testing auto value")
            .build();

    assertThat(up.defaults()).isEqualTo("testing auto value");
    assertThat(up.conflictResolutions()).isEqualTo("testing auto value");
  }

  @Test
  void testUseUnionParametersBuilderDefaultsEmpty() {
    UnionOptions up =
        UnionOptions.builder().conflictResolutions("testing auto value").build();

    assertThat(up.defaults()).isEqualTo("");
    assertThat(up.conflictResolutions()).isEqualTo("testing auto value");
  }

  @Test
  void testUseUnionParametersBuilderEmpty() {
    UnionOptions up = UnionOptions.builder().build();

    assertThat(up.defaults()).isEqualTo("");
    assertThat(up.conflictResolutions()).isEqualTo("");
  }
}
