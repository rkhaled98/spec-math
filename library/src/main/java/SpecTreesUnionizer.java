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

  public Map<String, Object> union(
      Map<String, Object> map1,
      Map<String, Object> map2,
      Map<String, Object> defaults,
      HashMap<String, String> conflictResolutions)
      throws UnableToUnionException {
    var keypath = new Stack<String>();
    var conflicts = new ArrayList<Conflict>();
    Map<String, Object> mergedMap =
        mergeMapsHelper(map1, map2, false, keypath, conflicts, conflictResolutions);
    Map<String, Object> resolvedMap = applyDefaults(defaults, mergedMap, conflicts);

    if (conflicts.isEmpty()) {
      return resolvedMap;
    } else {
      throw new UnableToUnionException(conflicts);
    }
  }

  public Map<String, Object> applyOverlay(Map<String, Object> defaults, Map<String, Object> map2) {
    return mergeMapsHelper(
        defaults,
        map2,
        true,
        new Stack<String>(),
        new ArrayList<>(),
        new HashMap<String, String>());
  }

  public Map<String, Object> applyDefaults(
      Map<String, Object> defaults, Map<String, Object> map2, ArrayList<Conflict> conflicts) {
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

    return mergeMapsHelper(defaults, map2, true, new Stack<>(), new ArrayList<>(), new HashMap<>());
    // not all conflicts could be resolved
  }

  @SuppressWarnings("unchecked")
  private Map<String, Object> mergeMapsHelper(
      Map<String, Object> map1,
      Map<String, Object> map2,
      boolean map1IsDefault,
      Stack<String> keypath,
      ArrayList<Conflict> conflicts,
      HashMap<String, String> conflictResolutions) {

    // traverse map2
    for (Map.Entry<String, Object> entry : map2.entrySet()) {
      String key = entry.getKey();
      Object value2 = entry.getValue();
      keypath.push(key);
      if (map1.containsKey(entry.getKey())) { // make each case its own function
        // they could both be values, they could both be maps,
        Object value1 = map1.get(key);
        if (value1 instanceof Map && value2 instanceof Map) { // do we need the second conjunct??
          // need to process further
          Map<String, Object> value1Map = (Map<String, Object>) value1;
          Map<String, Object> value2Map = (Map<String, Object>) value2;
          if (!value1Map.equals(value2Map)) {
            map1.put(
                key,
                mergeMapsHelper(
                    value1Map, value2Map, map1IsDefault, keypath, conflicts, conflictResolutions));
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
        } else if (!value1.equals(value2)){
          System.out.println("some unexpected case...");
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
      Map<String, Object> map1,
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
