import static com.google.common.truth.Truth.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;

class ListUtilsTest {
  @Test
  void testUnionTwoListsWithoutDuplicates() {
    List<Object> list1 = new ArrayList<>();
    List<Object> list2 = new ArrayList<>();

    list1.add("World");
    list2.add("Hello");

    List<Object> expected = new ArrayList<>();
    expected.add("Hello");
    expected.add("World");

    assertThat(ListUtils.listUnion(list1, list2)).isEqualTo(expected);
  }

  @Test
  void testUnionTwoListsWithDuplicates() {
    List<Object> list1 = new ArrayList<>();
    List<Object> list2 = new ArrayList<>();

    list1.add("World");
    list2.add("World");

    List<Object> expected = new ArrayList<>();
    expected.add("World");

    assertThat(ListUtils.listUnion(list1, list2)).isEqualTo(expected);
  }
}
