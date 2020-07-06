/*
Copyright 2020 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class SpecTreesUnionizer {

  // TODO would it be useful to have a conflictFinder to preprocess conflicts?

  public LinkedHashMap<String, Object> union(
      LinkedHashMap<String, Object> map1, LinkedHashMap<String, Object> map2)
      throws UnableToUnionException {
    UnionizerUnionParams unionizerUnionParams = UnionizerUnionParams.builder().build();

    return union(map1, map2, unionizerUnionParams);
  }

  public LinkedHashMap<String, Object> union(
      LinkedHashMap<String, Object> map1,
      LinkedHashMap<String, Object> map2,
      UnionizerUnionParams unionizerUnionParams)
      throws UnableToUnionException {
    var conflicts = new ArrayList<Conflict>();
    LinkedHashMap<String, Object> mergedMap =
        union(
            map1,
            map2,
            false,
            new Stack<String>(),
            conflicts,
            unionizerUnionParams.conflictResolutions());

    removeConflictsFixedByDefaults(unionizerUnionParams.defaults(), conflicts);

    LinkedHashMap<String, Object> newMap = new LinkedHashMap<>(unionizerUnionParams.defaults());

    LinkedHashMap<String, Object> resolvedMap = applyOverlay(newMap, mergedMap);

    if (conflicts.isEmpty()) {
      return resolvedMap;
    } else {
      throw new UnableToUnionException(conflicts);
    }
  }

  /**
   * Returns a new map with {@code defaults} overlay applied to {@code map2}. In other words,
   * perform a union where {@code defaults} values takes priority over values with the same keypath
   * in {@code map2}
   */
  public LinkedHashMap<String, Object> applyOverlay(
      LinkedHashMap<String, Object> defaults, LinkedHashMap<String, Object> map2) {
    return union(
        defaults,
        map2,
        true,
        new Stack<String>(),
        new ArrayList<Conflict>(),
        new HashMap<String, Object>());
  }

  /**
   * Removes conflicts from {@code conflicts} parameter passed by reference if the conflicting
   * keypath is also a path in the defaults map.
   *
   * @param defaults a map which contains defaults to apply to some output of a union
   * @param conflicts an ArrayList passed by reference, which gets updated based on the conflicts
   *     which do not occur thanks to the default map
   */
  private void removeConflictsFixedByDefaults(
      LinkedHashMap<String, Object> defaults, ArrayList<Conflict> conflicts) {

    var defaultKeypaths = new HashSet<String>();

    MapUtils.getKeypathsFromMap(defaults, new Stack<>(), defaultKeypaths);

    conflicts.removeIf(conflict -> defaultKeypaths.contains(conflict.getKeypath()));
  }

  /**
   * Union function with all possible options. Other functions provide a nicer interface for
   * different use cases of union, and ultimately call this function.
   *
   * @param map1 map 1/2 to merge. This map is special because it can take priority in the union
   *     based on {@code map1IsDefault} or {@code map1IsOrderer}
   * @param map2 map 2/2 to merge
   * @param map1IsDefault if true, map1 will take priority over map2 in case of different leaf
   *     values, and no conflict will be reported
   * @param keypath the key path which the leaf nodes belong to in the current iteration of the
   *     recursive frame
   * @param conflicts appended to if there is an unresolvable conflict
   * @param conflictResolutions a map which can provide conflict resolutions based on keypaths
   * @return
   */
  private LinkedHashMap<String, Object> union(
      LinkedHashMap<String, Object> map1,
      LinkedHashMap<String, Object> map2,
      boolean map1IsDefault,
      Stack<String> keypath,
      ArrayList<Conflict> conflicts,
      HashMap<String, Object> conflictResolutions) {

    for (Map.Entry<String, Object> entry : map2.entrySet()) {
      String key = entry.getKey();
      Object value2 = entry.getValue();

      keypath.push(key);

      if (map1.containsKey(entry.getKey())) { // make each case its own function
        Object value1 = map1.get(key);

        if (!value1.equals(value2)) {
          if (TypeChecker.isObjectMap(value1)
              && TypeChecker.isObjectMap((value2))) { // do we need the second conjunct??
            // need to process further
            LinkedHashMap<String, Object> value1Map =
                ObjectCaster.castObjectToStringObjectMap(value1);
            LinkedHashMap<String, Object> value2Map =
                ObjectCaster.castObjectToStringObjectMap(value2);
            map1.put(
                key,
                union(
                    value1Map, value2Map, map1IsDefault, keypath, conflicts, conflictResolutions));

          } else if (TypeChecker.isObjectList(value1)
              && TypeChecker.isObjectList(value2)
              && !map1IsDefault) {
            List<Object> output =
                ListUtils.listUnion(
                    ObjectCaster.castObjectToListOfObjects(value1),
                    ObjectCaster.castObjectToListOfObjects(value2));
            map1.put(key, output);
          } else if (TypeChecker.isObjectPrimitive(value1)
              && TypeChecker.isObjectPrimitive(value2)) {
            processUnequalLeafNodes(
                map1, key, value1, value2, map1IsDefault, keypath, conflicts, conflictResolutions);
          } else {
            System.out.println("Unhandled Data During Union");
          }
        }

      } else {
        // its a new key so add to tree
        map1.put(key, value2);
      }

      keypath.pop();
    }

    return map1;
  }

  /**
   * Used when two leaf nodes are different. If {@code map1IsDefault} is true, then there is no
   * conflict. Otherwise, there is a conflict if there is no {@code key} in {@code
   * conflictResolutions} that matches the {@code keypath} of the current nodes. In the case of a
   * conflict, add it to the {@code conflictResolutions} array.
   *
   * @param map1 the output map, which may be added to
   * @param key the key which both leaf nodes belong to
   * @param value1 the first value to consider
   * @param value2 the second value to consider. If it is different from value2 and cannot be
   *     resolved by either defaults or conflictResolutions, then there is a conflict.
   * @param map1IsDefault if true, nothing is done since {@code value1} already contains the correct
   *     value.
   * @param keypath the key path which both leaf nodes belong to
   * @param conflicts appended to if there is an unresolvable conflict
   * @param conflictResolutions a map which can provide conflict resolutions based on keypaths
   */
  private void processUnequalLeafNodes(
      LinkedHashMap<String, Object> map1,
      String key,
      Object value1,
      Object value2,
      boolean map1IsDefault,
      Stack<String> keypath,
      ArrayList<Conflict> conflicts,
      HashMap<String, Object> conflictResolutions) {

    if (!map1IsDefault) {
      String keypathString = keypath.toString();
      if (conflictResolutions.containsKey(keypathString)) {
        // can be resolved by a conflictResolution
        map1.put(key, conflictResolutions.get(keypathString));
      } else {
        Conflict conflict = new Conflict(keypathString, value1, value2);
        conflicts.add(conflict);
      }
    }
  }
}
