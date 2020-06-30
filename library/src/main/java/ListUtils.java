import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Provides static functions for operations on Lists
 */
public class ListUtils {

  /**
   * Performs the union operation on two lists. Will add elements from list2 and then list1,
   * avoiding duplicates
   *
   * @param list1
   * @param list2
   * @return a new list containing list2 and list1
   */
  public static List<Object> listUnion(List<Object> list1, List<Object> list2) {
    HashSet<Object> seenElements = new HashSet<>();
    List<Object> output = new ArrayList<>();

    for (Object element : list2) {
      if (!seenElements.contains(element)) {
        output.add(element);
        seenElements.add(element);
      }
    }

    for (Object element : list1) {
      if (!seenElements.contains(element)) {
        output.add(element);
        seenElements.add(element);
      }
    }

    return output;
  }
}
