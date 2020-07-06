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
import java.util.HashSet;
import java.util.List;

/** Provides functions for operations on Lists */
public class ListUtils {

  /**
   * Performs the union operation on two lists. Will add elements from list2 and then list1,
   * avoiding duplicates.
   *
   * @param list1 the list that is added second in the output order.
   * @param list2 the list that is added first in the output order.
   * @return a new list containing list2 and list1
   */
  public static List<Object> listUnion(List<Object> list1, List<Object> list2) {
    // use an extra hashset to avoid duplicating elements
    HashSet<Object> seenElements = new HashSet<>();
    List<Object> output = new ArrayList<>();

    addListToOutput(list2, seenElements, output);
    addListToOutput(list1, seenElements, output);

    return output;
  }

  private static void addListToOutput(
      List<Object> list, HashSet<Object> seenElements, List<Object> output) {
    for (Object element : list) {
      if (!seenElements.contains(element)) {
        output.add(element);
        seenElements.add(element);
      }
    }
  }
}
