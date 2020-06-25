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

import java.util.*;

public class SpecTreesUnionizer {

  // TODO would it be useful to have a conflictFinder to preprocess conflicts?

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
            false,
            new Stack<String>(),
            conflicts,
            unionizerUnionParams.conflictResolutions());

    LinkedHashMap<String, Object> resolvedMap =
        applyDefaults(unionizerUnionParams.defaults(), mergedMap, conflicts);

    // todo consider adding the ordering step here as part of the unionizer parameters.

    if (conflicts.isEmpty()) {
      return resolvedMap;
    } else {
      throw new UnableToUnionException(conflicts);
    }
  }

  public LinkedHashMap<String, Object> applyOverlay(
      LinkedHashMap<String, Object> defaults, LinkedHashMap<String, Object> map2) {
    return union(
        defaults,
        map2,
        true,
        false,
        new Stack<String>(),
        new ArrayList<>(),
        new HashMap<String, String>());
  }

  public LinkedHashMap<String, Object> applyDefaults(
      LinkedHashMap<String, Object> defaults,
      LinkedHashMap<String, Object> map2,
      ArrayList<Conflict> conflicts) {
    // TWO USES FOR THIS FUNCTION:
    // 1: IF DEFAULTS FILE IS SPECIFIED BY USER THEN WE WILL USE IT TO TRY TO RESOLVE.
    // 2: IF SOME KIND OF CONFLICT RESOLUTION FILE IS PROVIDED THEN WE CAN CONVERT IT IN THE
    // CONTROLLER TO A DEFAULTS MAP, WHICH IS EASIER TO USE IN THIS FUNCTION.
    // WE MAY NEED A CLASS FOR CONVERTING WHATEVER AGREED UPON FORMAT IS GIVEN FROM THE FRONTEND
    // INTO THE DEFAULTS MAP WHICH IS USED HERE. THE EASIEST SITUATION WOULD BE IF THE FRONTEND
    // CREATES THIS NEW DEFAULTS YAML MAP. AND WE MAY ALSO NEED TO MERGE THAT WITH THE ORIGINAL
    // DEFAULTS.

    // REMOVE ALL CONFLICTS FROM conflicts ARRAY WHICH HAVE SAME KEY PATH AS
    // SOMETHING IN DEFAULTS.
    var defaultKeypaths = new HashSet<String>();

    MapUtils mapUtils = new MapUtils();
    mapUtils.getKeypathsFromMap(defaults, new Stack<>(), defaultKeypaths);

    conflicts.removeIf(conflict -> defaultKeypaths.contains(conflict.getKeypath()));

    LinkedHashMap<String, Object> newMap = new LinkedHashMap<>(defaults);

    return union(
        newMap, map2, true, false, new Stack<String>(), new ArrayList<>(), new HashMap<>());
    // not all conflicts could be resolved
  }

  static final String ORDER_VALUE = null;

  @SuppressWarnings("unchecked")
  private LinkedHashMap<String, Object> union(
      LinkedHashMap<String, Object> map1,
      LinkedHashMap<String, Object> map2,
      boolean map1IsDefault,
      boolean map1IsOrderer,
      Stack<String> keypath,
      ArrayList<Conflict> conflicts,
      HashMap<String, String> conflictResolutions) {

    for (Map.Entry<String, Object> entry : map2.entrySet()) {
      String key = entry.getKey();
      Object value2 = entry.getValue();

      keypath.push(key);

      if (map1.containsKey(entry.getKey())) { // make each case its own function
        Object value1 = map1.get(key);
        if (value1 instanceof LinkedHashMap
            && value2 instanceof LinkedHashMap) { // do we need the second conjunct??
          // need to process further
          LinkedHashMap<String, Object> value1Map = (LinkedHashMap<String, Object>) value1;
          LinkedHashMap<String, Object> value2Map = (LinkedHashMap<String, Object>) value2;
          if (!value1Map.equals(value2Map)) {
            map1.put(
                key,
                union(
                    value1Map,
                    value2Map,
                    map1IsDefault,
                    map1IsOrderer,
                    keypath,
                    conflicts,
                    conflictResolutions));
          }
        } else if (value1 instanceof List && value2 instanceof List) {
          List<Object> output = ListUtils.listUnion((List<Object>) value1, (List<Object>) value2);
          map1.put(key, output);
        } else if (value1 instanceof String && value2 instanceof String) {
          processUnequalLeafNodes(
              map1,
              key,
              (String) value1,
              (String) value2,
              map1IsDefault,
              keypath,
              conflicts,
              conflictResolutions);
        } else if (value1 == ORDER_VALUE && map1IsOrderer) {
          map1.put(key, value2);
        }
      } else {
        // its a new key so add to tree
        map1.put(key, value2);
      }

      keypath.pop();
    }

    return map1;
  }

  private void processUnequalLeafNodes(
      LinkedHashMap<String, Object> map1,
      String key,
      String value1,
      String value2,
      boolean map1IsDefault,
      Stack<String> keypath,
      ArrayList<Conflict> conflicts,
      HashMap<String, String> conflictResolutions) {
    if (!value1.equals(value2)) {
      String keypathString = keypath.toString();
      if (!map1IsDefault) {
        if (conflictResolutions.containsKey(keypathString)) {
          // can be resolved by a conflictResolution
          map1.put(key, conflictResolutions.get(keypathString));
        } else {
          // THIS IS A CONFLICT, add it as a new Conflict in the conflicts array list.
          Conflict conflict =
              new Conflict(keypathString, value1, value2); // can ccheck if value1 is string
          conflicts.add(conflict);
        }
      }
      // just keep the value from value1, this is not needed ---> map1.put(key, value1);
    }
  }
}
