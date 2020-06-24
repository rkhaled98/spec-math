import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ListUtils {
  public static List<Object> listUnion(List<Object> list1, List<Object> list2) {
    List<Object> output = new ArrayList<Object>(list2);
    output.addAll(list1);

    return output;
  }
}
