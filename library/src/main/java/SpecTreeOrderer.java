import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Stack;

public class SpecTreeOrderer {
  public LinkedHashMap<String, Object> applyOrder(LinkedHashMap<String, Object> spec)
      throws FileNotFoundException, UnableToUnionException {
    var yamlStringToSpecTreeConverter = new YamlStringToSpecTreeConverter();

    LinkedHashMap<String, Object> orderMap =
        yamlStringToSpecTreeConverter.convertYamlFileToOrderedSpecTree(
            "src/main/resources/order.yaml");

    var specTreeUnionizer = new SpecTreesUnionizer();

    return specTreeUnionizer.union(
        orderMap, spec, new LinkedHashMap<String, Object>(), new HashMap<String, String>());
  }
}
