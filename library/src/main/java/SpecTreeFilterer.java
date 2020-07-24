import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpecTreeFilterer {
  public LinkedHashMap<String, Object> filter(
      LinkedHashMap<String, Object> spec, ArrayList<FilterCriteria> filterCriteriaArrayList)
      throws UnionConflictException, UnexpectedTypeException, AllUnmatchedFilterException {
    var output = new LinkedHashMap<String, Object>();

    for (FilterCriteria filterCriteria : filterCriteriaArrayList) {
      SpecTreesUnionizer.union(output, filterSingleCriteria(spec, filterCriteria));
    }

    if (!output.containsKey("paths")
        || ObjectCaster.castObjectToStringObjectMap(output.get("paths")).isEmpty()) {
      throw new AllUnmatchedFilterException("The paths would be empty");
    }

    return output;
  }

  public LinkedHashMap<String, Object> filterSingleCriteria(
      LinkedHashMap<String, Object> spec, FilterCriteria filterCriteria) {
    LinkedHashMap<String, Object> paths =
        ObjectCaster.castObjectToStringObjectMap(spec.get("paths"));

    var outputEndpoints = new LinkedHashMap<String, Object>();

    // an endpointEntry is like /pets: get...put...
    for (Map.Entry<String, Object> endpointEntry : paths.entrySet()) {
      String endpoint = endpointEntry.getKey(); // /pets
      System.out.println(endpoint);
      //      System.out.println(endpointEntry.getValue());
      LinkedHashMap<String, Object> endpointObject =
          ObjectCaster.castObjectToStringObjectMap(endpointEntry.getValue());

      var outputOperations = new LinkedHashMap<String, Object>();

      if (filterCriteria.pathRegex().isEmpty() || endpoint.matches(filterCriteria.pathRegex())) {
        for (Map.Entry<String, Object> operationEntry : endpointObject.entrySet()) {

          String operation = operationEntry.getKey(); // get, put, post etc.

          if (filterCriteria.operations().isEmpty()
              || filterCriteria.operations().contains(operation)) {

            //            System.out.println(operationEntry.getValue());
            LinkedHashMap<String, Object> operationObject =
                ObjectCaster.castObjectToStringObjectMap(operationEntry.getValue());

            List<Object> tags = ObjectCaster.castObjectToListOfObjects(operationObject.get("tags"));

            if ((filterCriteria.tags().isEmpty() && filterCriteria.removableTags().isEmpty())) {
              outputOperations.put(operation, operationObject);
            } else {
              // this is disjunction on the tags
              for (Object tag : tags) {
                String tagString = ((String) tag).toLowerCase();
                if (filterCriteria.tags().contains(tagString)
                    || filterCriteria.removableTags().contains(tagString)) {
                  tags.removeIf(
                      singleTag -> filterCriteria.removableTags().contains((String) singleTag));
                  if (!tags.isEmpty()) {
                    operationObject.put("tags", tags);
                  }
                  outputOperations.put(operation, operationObject);
                  break; // since its disjunction, one tag matched will be enough
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
  //  public LinkedHashMap<String, Object> getAllComponents(LinkedHashMap<String, Object> spec)
  //      throws UnionConflictException, UnexpectedTypeException {
  //    int size=0;
  //
  //    // get the initial hashset
  //
  //    var keypaths = new HashSet<String>();
  //    loadInitialKeypaths(spec, keypaths);
  //
  //    var outputComponents = new LinkedHashMap<String, Object>();
  //    LinkedHashMap<String, Object> components =
  // ObjectCaster.castObjectToStringObjectMap(spec.get("components"));
  //
  //    while (keypaths.size()!=size){
  //      size=keypaths.size();
  //
  //      LinkedHashMap<String, Object> relevantComponents = expandComponentTree(components,
  // keypaths);
  //      SpecTreesUnionizer.union(outputComponents, relevantComponents);
  //    }
  //  }
  //
  //  private void loadInitialKeypaths(LinkedHashMap<String, Object> spec, HashSet<String> keypaths)
  // {
  //  }
  //
  //  public LinkedHashMap<String, Object> expandComponentTree(
  //      LinkedHashMap<String, Object> components, HashSet<String> keypaths) {
  //
  //  }

}
