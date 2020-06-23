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

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.StringWriter;

public class MapUtils {
  public void getKeypathsFromMap(
      Map<String, Object> map, Stack<String> keypath, HashSet<String> result) {
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (value instanceof Map) {
        Map<String, Object> nmap = (Map<String, Object>) value;

        keypath.push(key);
        getKeypathsFromMap(nmap, keypath, result);
      } else {
        keypath.push(key);
        result.add(keypath.toString());
      }

      keypath.pop(); // backtrack
    }
  }
}


