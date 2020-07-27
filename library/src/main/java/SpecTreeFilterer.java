import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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

    LinkedHashMap<String, Object> components = getAllComponents(output);

    if (!components.isEmpty()){
      output.put("components", components.get("components"));
    } else if (output.containsKey("components")) {
      // not covered. this is a situation in which no refs are needed in the path. create this test!
      output.remove("components");
    }
//    SpecTreesUnionizer.union(output, components);

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
//      System.out.println(endpoint);
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

  public LinkedHashMap<String, Object> getAllComponents(LinkedHashMap<String, Object> spec)
      throws UnionConflictException, UnexpectedTypeException {
    int size = 0;

    // get the initial hashset

    var keypaths = new HashSet<String>();
    addRefsInSubtreeToKeypaths(
        ObjectCaster.castObjectToStringObjectMap(spec.get("paths")), keypaths);

    var outputComponents = new LinkedHashMap<String, Object>();

    while (keypaths.size() != size) {
      size = keypaths.size();

      LinkedHashMap<String, Object> relevantComponents = expandComponentTree(spec, keypaths, new Stack<String>());
      SpecTreesUnionizer.union(outputComponents, relevantComponents);
    }

    return outputComponents;
  }

  private void addRefsInSubtreeToKeypaths(
      LinkedHashMap<String, Object> paths, HashSet<String> refKeypaths) {
    for (Map.Entry<String, Object> entry : paths.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (key.equals("$ref")) {
        System.out.println("FOUND!S");
        String processedRefKeyPath = processRefKeyPath(value);
        if (!processedRefKeyPath.isEmpty()) {
          System.out.println(processedRefKeyPath);
          refKeypaths.add(processedRefKeyPath);
        }
      } else if (TypeChecker.isObjectMap(value)) {
        LinkedHashMap<String, Object> subtreeMap = ObjectCaster.castObjectToStringObjectMap(value);
        addRefsInSubtreeToKeypaths(subtreeMap, refKeypaths);
      }
    }
  }

  private String processRefKeyPath(Object value) {
    String refToProcess = (String) value;
    if (refToProcess.substring(0, 1).equals("#")) {

      // ignore the first two characters (#/)
      String x = Arrays.asList(refToProcess.substring(2).split("/")).toString();
      return x;
    } else {
      return "";
    }
  }

  public LinkedHashMap<String, Object> expandComponentTree(
      LinkedHashMap<String, Object> components, HashSet<String> keypaths, Stack<String> keypath) {

    var outputForThisLevel = new LinkedHashMap<String, Object>();

    for (Map.Entry<String, Object> entry : components.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      keypath.push(key);

      if (keypaths.contains(keypath.toString())) {
        // add this subtree
        if (TypeChecker.isObjectMap(value)) {
          LinkedHashMap<String, Object> subtreeMap =
              ObjectCaster.castObjectToStringObjectMap(value);
          addRefsInSubtreeToKeypaths(subtreeMap, keypaths);
          outputForThisLevel.put(key, subtreeMap);
        }
      } else {
        if (TypeChecker.isObjectMap(value)) {
          LinkedHashMap<String, Object> subtreeMap =
              ObjectCaster.castObjectToStringObjectMap(value);
          LinkedHashMap<String, Object> outputForSubtree =
              expandComponentTree(subtreeMap, keypaths, keypath);

          if (!outputForSubtree.isEmpty()){
            outputForThisLevel.put(key, outputForSubtree);
          }
        }
      }

      keypath.pop();
    }

    return outputForThisLevel;
  }
}
