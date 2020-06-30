import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ListUtils {
  public static List<Object> listUnion(List<Object> list1, List<Object> list2) {
    HashSet<Object> seenElements = new HashSet<>();

    List<Object> output = new ArrayList<>(list2);

    seenElements.addAll(output);

    for (Object element : list1) {
      if (!seenElements.contains(element)) {
        output.add(element);
      }
    }

    return output;
  }
}
