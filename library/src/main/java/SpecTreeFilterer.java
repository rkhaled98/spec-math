import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

public class SpecTreeFilterer {

  public static LinkedHashMap<String, Object> filter(
      LinkedHashMap<String, Object> spec, ArrayList<FilterCriteria> filterCriteriaArrayList)
      throws UnionConflictException, UnexpectedTypeException, AllUnmatchedFilterException {
    var output = new LinkedHashMap<String, Object>();

    for (FilterCriteria filterCriteria : filterCriteriaArrayList) {
      SpecTreesUnionizer.union(output, filterUsingSingleFilterCriteria(spec, filterCriteria));
    }

    if (!output.containsKey("paths")
        || ObjectCaster.castObjectToStringObjectMap(output.get("paths")).isEmpty()) {
      throw new AllUnmatchedFilterException("The paths would be empty");
    }

    LinkedHashMap<String, Object> components = getAllComponents(output);

    if (!components.isEmpty()) {
      output.put("components", components.get("components"));
    } else if (output.containsKey("components")) {
      // not covered. this is a situation in which no refs are needed in the path. create this test!
      output.remove("components");
    }

    return output;
  }

  private static LinkedHashMap<String, Object> filterUsingSingleFilterCriteria(
      LinkedHashMap<String, Object> spec, FilterCriteria filterCriteria) {
    LinkedHashMap<String, Object> paths =
        ObjectCaster.castObjectToStringObjectMap(spec.get("paths"));

    var outputEndpoints = new LinkedHashMap<String, Object>();

    for (Map.Entry<String, Object> endpointEntry : paths.entrySet()) {
      processEndpoint(filterCriteria, outputEndpoints, endpointEntry);
    }

//    if (!outputEndpoints.isEmpty()){
    LinkedHashMap<String, Object> newSpec = new LinkedHashMap<>(spec);
    newSpec.put("paths", outputEndpoints);
//    spec.put("paths", outputEndpoints);
//    }
    return newSpec;
  }

  private static void processEndpoint(
      FilterCriteria filterCriteria,
      LinkedHashMap<String, Object> outputEndpoints,
      Entry<String, Object> endpointEntry) {
    String endpoint = endpointEntry.getKey(); // /pets

    LinkedHashMap<String, Object> endpointObject =
        ObjectCaster.castObjectToStringObjectMap(endpointEntry.getValue());

    var outputOperations = new LinkedHashMap<String, Object>();

    if (filterCriteria.pathRegex().isEmpty() || endpoint.matches(filterCriteria.pathRegex())) {
      for (Entry<String, Object> operationEntry : endpointObject.entrySet()) {

        processOperation(filterCriteria, outputOperations, operationEntry);
      }
    }

    if (!outputOperations.isEmpty()) {
      outputEndpoints.put(endpoint, outputOperations);
    }
  }

  private static void processOperation(
      FilterCriteria filterCriteria,
      LinkedHashMap<String, Object> outputOperations,
      Entry<String, Object> operationEntry) {
    String operation = operationEntry.getKey(); // get, put, post etc.

    if (filterCriteria.operations().isEmpty() || filterCriteria.operations().contains(operation)) {
      LinkedHashMap<String, Object> operationObject =
          ObjectCaster.castObjectToStringObjectMap(operationEntry.getValue());

      List<Object> tags = ObjectCaster.castObjectToListOfObjects(operationObject.get("tags"));

      if ((filterCriteria.tags().isEmpty() && filterCriteria.removableTags().isEmpty())) {
        outputOperations.put(operation, operationObject);
      } else {
        // this is disjunction on the tags
        for (Object tag : tags) {
          if (processTag(
              filterCriteria, outputOperations, operation, operationObject, tags, (String) tag)) {
            break; // since its disjunction, one tag matched will be enough
          }
        }
      }
    }
  }

  private static boolean processTag(
      FilterCriteria filterCriteria,
      LinkedHashMap<String, Object> outputOperations,
      String operation,
      LinkedHashMap<String, Object> operationObject,
      List<Object> tags,
      String tag) {
    String tagString = tag.toLowerCase();
    if (filterCriteria.tags().contains(tagString)
        || filterCriteria.removableTags().contains(tagString)) {
      tags.removeIf(singleTag -> filterCriteria.removableTags().contains((String) singleTag));
      if (!tags.isEmpty()) {
        operationObject.put("tags", tags);
      }
      outputOperations.put(operation, operationObject);
      return true;
    }
    return false;
  }

  private static LinkedHashMap<String, Object> getAllComponents(LinkedHashMap<String, Object> spec)
      throws UnionConflictException, UnexpectedTypeException {
    int size = 0;

    // get the initial hashset

    var keypaths = new HashSet<String>();
    addRefsInSubtreeToKeypaths(
        ObjectCaster.castObjectToStringObjectMap(spec.get("paths")), keypaths);

    var outputComponents = new LinkedHashMap<String, Object>();

    while (keypaths.size() != size) {
      size = keypaths.size();

      LinkedHashMap<String, Object> relevantComponents =
          expandComponentTree(spec, keypaths, new Stack<String>());
      SpecTreesUnionizer.union(outputComponents, relevantComponents);
    }

    return outputComponents;
  }

  private static void addRefsInSubtreeToKeypaths(
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

  private static String processRefKeyPath(Object value) {
    String refToProcess = (String) value;
    if (refToProcess.substring(0, 1).equals("#")) {
      // ignore the first two characters (#/)
      return Arrays.asList(refToProcess.substring(2).split("/")).toString();
    } else {
      return "";
    }
  }

  private static LinkedHashMap<String, Object> expandComponentTree(
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

          if (!outputForSubtree.isEmpty()) {
            outputForThisLevel.put(key, outputForSubtree);
          }
        }
      }

      keypath.pop();
    }

    return outputForThisLevel;
  }
}
