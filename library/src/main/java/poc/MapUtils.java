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

import java.util.ArrayList;
import java.util.List;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.StringWriter;
import java.util.Map;
import java.util.Stack;

public class MapUtils {

  public String convertMapToYaml(Map<String, Object> yamlMap) {
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

    options.setIndent(4);
    options.setIndicatorIndent(2);
    Yaml yaml = new Yaml(options);

    StringWriter writer = new StringWriter();
    yaml.dump(yamlMap, writer);

    return writer.toString();
  }

  public Map<String, Object> mergeMaps(Map<String, Object> map1, Map<String, Object> map2) {
    Stack<String> keypath = new Stack<String>();
    ArrayList<Conflict> conflicts = new ArrayList<Conflict>();
    return mergeMapsHelper(map1, map2, false, keypath, conflicts);
  }

  public Map<String, Object> applyOverlay(Map<String, Object> defaults, Map<String, Object> map2) {
    return mergeMapsHelper(defaults, map2, true, new Stack<String>(), new ArrayList<>());
  }

  public Map<String, Object> resolveConflictsWithDefaults(
      Map<String, Object> defaults, Map<String, Object> map2, ArrayList<Conflict> conflicts) {
    // TODO FIRST STEP: REMOVE ALL CONFLICTS FROM conflicts ARRAY WHICH HAVE SAME KEY PATH AS SOMETHING IN DEFAULTS.

    return mergeMapsHelper(defaults, map2, true, new Stack<String>(), new ArrayList<>());
  }



  public Map<String, Object> mergeMapsHelper(
      Map<String, Object> map1,
      Map<String, Object> map2,
      boolean map1IsDefault,
      Stack<String> keypath,
      ArrayList<Conflict> conflicts) {

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
                key, mergeMapsHelper(nmap1, nmap2, map1IsDefault, keypath, conflicts));
            keypath.pop();
          }
        } else if (value1 instanceof List && value2 instanceof List) { // do we need the second conjunct??
          List<Object> output = new ArrayList<Object>((List<Object>) value2);
          output.addAll((List<Object>) value1);
          map1.put(key, output);
        } else {
          // ASSUMPTION: both are primitive values, WITH THE SAME KEY PATHS IN BOTH MAPS, so choose one of them

          // THIS IS A CONFLICT, report it back somehow.
          if (!value1.equals(value2) && !map1IsDefault) {
            // conflict, unless map1IsDefault.
            Conflict conflict = new Conflict(keypath.toString(), (String) value1, (String) value2);
            conflicts.add(conflict);
          }
          // just keep the value from value1, this is not needed ---> map1.put(key, value1);
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
