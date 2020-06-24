import java.util.ArrayList;
import java.util.List;

public class ListUtils {
  public static List<Object> listUnion(List<Object> list1, List<Object> list2) {
    List<Object> output = new ArrayList<>(list2);
    output.addAll(list1);

    return output;
  }
}
