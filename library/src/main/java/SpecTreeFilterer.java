import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Stack;

public class SpecTreeFilterer {

  /**
   * Filters an input spec tree {@code specToFilter} based on the {@code filterCriteriaArrayList}.
   * Only the paths which match the properties in the filter will be kept. Also, only the relevant
   * component refs will be kept.
   *
   * @param specToFilter the origin spec to filter on
   * @param filterCriteriaArrayList a list of FilterCriteria objects used to filter {@code
   *     specToFilter}
   * @return a new spec which is the filtered version of {@code specToFilter} based on the {@code
   *     filterCriteriaArrayList}
   * @throws UnionConflictException if there was an issue when merging the results from each
   *     FilterCriteria
   * @throws UnexpectedTypeException if there was an issue when merging the results from each
   *     FilterCriteria
   * @throws AllUnmatchedFilterException the filtered result would have no paths
   */
  public static LinkedHashMap<String, Object> filter(
      LinkedHashMap<String, Object> specToFilter, ArrayList<FilterCriteria> filterCriteriaArrayList)
      throws UnionConflictException, UnexpectedTypeException, AllUnmatchedFilterException {
    var outputSpec = new LinkedHashMap<String, Object>();

    for (FilterCriteria filterCriteria : filterCriteriaArrayList) {
      SpecTreesUnionizer.union(
          outputSpec, filterUsingSingleFilterCriteria(specToFilter, filterCriteria));
    }

    if (!outputSpec.containsKey("paths")
        || ObjectCaster.castObjectToStringObjectMap(outputSpec.get("paths")).isEmpty()) {
      throw new AllUnmatchedFilterException("The paths would be empty");
    }

    // the component selection functionality will be added in the next PR.
    LinkedHashMap<String, Object> components = getAllComponents(outputSpec);

    if (!components.isEmpty()) {
      outputSpec.put("components", components.get("components"));
    } else outputSpec.remove("components");

    return outputSpec;
  }

  private static LinkedHashMap<String, Object> filterUsingSingleFilterCriteria(
      LinkedHashMap<String, Object> specToFilter, FilterCriteria filterCriteria) {
    LinkedHashMap<String, Object> paths =
        ObjectCaster.castObjectToStringObjectMap(specToFilter.get("paths"));

    var outputEndpoints = new LinkedHashMap<String, Object>();

    for (Map.Entry<String, Object> endpointEntry : paths.entrySet()) {
      processEndpoint(filterCriteria, outputEndpoints, endpointEntry);
    }

    LinkedHashMap<String, Object> filteredSpec = new LinkedHashMap<>(specToFilter);
    filteredSpec.put("paths", outputEndpoints);

    return filteredSpec;
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
        processTags(filterCriteria, outputOperations, operation, operationObject, tags);
      }
    }
  }

  private static void processTags(
      FilterCriteria filterCriteria,
      LinkedHashMap<String, Object> outputOperations,
      String operation,
      LinkedHashMap<String, Object> operationObject,
      List<Object> tags) {
    if (operationObject.containsKey("tags")) {
      for (Object tag : tags) {
        // this is disjunction on the tags
        if (processTagsDisjunction(
            filterCriteria, outputOperations, operation, operationObject, tags, (String) tag)) {
          break; // since its disjunction, one tag matched will be enough
        }
      }
    }
  }

  private static boolean processTagsDisjunction(
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

    var keypaths = new HashSet<String>();
    addRefsInSubtreeToKeypaths(
        ObjectCaster.castObjectToStringObjectMap(spec.get("paths")), keypaths);

    var outputComponents = new LinkedHashMap<String, Object>();

    int size = 0;

    // as soon as keypaths.size() == size, terminate because no new refs need to be looked for.
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
        String processedRefKeyPath = processRefKeyPath(value);
        if (!processedRefKeyPath.isEmpty()) {
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
