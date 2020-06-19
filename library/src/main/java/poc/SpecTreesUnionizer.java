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

package poc;

import java.util.*;

public class SpecTreesUnionizer {

  public Map<String, Object> mergeMaps(
      Map<String, Object> map1,
      Map<String, Object> map2,
      Map<String, Object> defaults,
      HashMap<String, String> conflictResolutions)
      throws UnableToUnionException {
    Stack<String> keypath = new Stack<String>();
    ArrayList<Conflict> conflicts = new ArrayList<Conflict>();
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

  //  public Map<String, Object> applyConflictResolutions(
  //      Map<String, Object> map, HashSet<Conflict> conflictResolutions) {}

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
    HashSet<String> defaultKeypaths = new HashSet<String>();

    MapUtils mapUtils = new MapUtils();
    mapUtils.getKeypathsFromMap(defaults, new Stack<String>(), defaultKeypaths);

    conflicts.removeIf(conflict -> defaultKeypaths.contains(conflict.getKeypath()));

    return mergeMapsHelper(
        defaults,
        map2,
        true,
        new Stack<String>(),
        new ArrayList<>(),
        new HashMap<String, String>());
    // not all conflicts could be resolved
  }

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
      if (map1.containsKey(entry.getKey())) {
        // they could both be values, they could both be maps,
        Object value1 = map1.get(key);
        if (value1 instanceof Map && value2 instanceof Map) { // do we need the second conjunct??
          // need to process further
          Map<String, Object> nmap1 = (Map<String, Object>) value1;
          Map<String, Object> nmap2 = (Map<String, Object>) value2;
          if (!nmap1.equals(nmap2)) {
            // there is some diff in value1 and value2
            keypath.push(key);
            map1.put(
                key,
                mergeMapsHelper(
                    nmap1, nmap2, map1IsDefault, keypath, conflicts, conflictResolutions));
            keypath.pop();
          }
        } else if (value1 instanceof List
            && value2 instanceof List) { // do we need the second conjunct??
          List<Object> output = new ArrayList<Object>((List<Object>) value2);
          output.addAll((List<Object>) value1);
          map1.put(key, output);
        } else {
          // ASSUMPTION: both are primitive values, WITH THE SAME KEY PATHS IN BOTH MAPS, so choose
          // one of them

          // THIS IS A CONFLICT, add it as a new Conflict in the conflicts array list.
          if (!value1.equals(value2)) {
            keypath.push(key);
            String keypathString = keypath.toString();
            if (!map1IsDefault) {
              if (conflictResolutions.containsKey(keypathString)) {
                // can be resolved
                map1.put(key, conflictResolutions.get(keypathString));
              } else {
                Conflict conflict =
                    new Conflict(keypathString, (String) value1, (String) value2);
                conflicts.add(conflict);
              }
              keypath.pop();
            }
            // just keep the value from value1, this is not needed ---> map1.put(key, value1);
          }
        }
      } else {
        // the original map didn't contain this key, its a new key so add to tree
        map1.put(key, value2);
      }
    }

    System.out.println(conflicts);
    return map1;
  }
}

//  public ArrayList<Conflict> conflictFinder(
//          Map<String, Object> map1, Map<String, Object> map2, Map<String, Object> defaults) {
//    ArrayList<Conflict> conflicts = new ArrayList<Conflict>();
//
//    conflictFinderHelper(map1, map2, defaults, conflicts);
//
//    return conflicts;
//  }
//
//  public void conflictFinderHelper(
//          Map<String, Object> map1,
//          Map<String, Object> map2,
//          Map<String, Object> defaults,
//          ArrayList<Conflict> conflicts) {}
