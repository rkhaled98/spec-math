import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpecTreeFilterer {
  public LinkedHashMap<String, Object> filter(
      LinkedHashMap<String, Object> spec, FilterCriteria filterCriteria) {
    LinkedHashMap<String, Object> paths =
        ObjectCaster.castObjectToStringObjectMap(spec.get("paths"));

    var outputEndpoints = new LinkedHashMap<String, Object>();

    // an endpointEntry is like /pets: get...put...
    for (Map.Entry<String, Object> endpointEntry : spec.entrySet()) {
      String endpoint = endpointEntry.getKey(); // /pets
      LinkedHashMap<String, Object> endpointObject =
          ObjectCaster.castObjectToStringObjectMap(endpointEntry.getValue());

      var outputOperations = new LinkedHashMap<String, Object>();

      if (filterCriteria.pathRegex.isEmpty() || regexMatches(endpoint, filterCriteria.pathRegex)) {
        for (Map.Entry<String, Object> operationEntry : spec.entrySet()) {

          String operation = operationEntry.getKey(); // get, put, post etc.

          if (filterCriteria.operations.isEmpty()
              || filterCriteria.operations.contains(operation)) {

            LinkedHashMap<String, Object> operationObject =
                ObjectCaster.castObjectToStringObjectMap(operationEntry.getValue());

            List<Object> tags = ObjectCaster.castObjectToListOfObjects(operationObject.get("tags"));

            if ((filterCriteria.tags.isEmpty() && filterCriteria.removableTags.isEmpty())) {
              outputOperations.put(operation, operationObject);
            } else {
              // this is disjunction on the tags
              for (Object tag : tags) {
                String tagString = (String) tag;
                if (filterCriteria.tags.contains(tagString)
                    || filterCriteria.removableTags.contains(tagString)) {
                  tags.removeIf(singleTag -> filterCriteria.removableTags.contains((String)singleTag));
                  if (!tags.isEmpty()) {
                    operationObject.put("tags", tags);
                  }
                  outputOperations.put(operation, operationObject);
                  break;
                }
              }
            }
          }
        }
      }

      if (!outputOperations.isEmpty()) {
        outputEndpoints.put(endpoint, outputOperations);
      }
    }

    spec.put("paths", outputEndpoints);
    return spec;
  }

  private boolean regexMatches(String endpoint, String pathRegex) {}

  private class FilterCriteria {
    String pathRegex;
    List<String> operations;
    List<String> tags;
    List<String> removableTags;
  }
}
