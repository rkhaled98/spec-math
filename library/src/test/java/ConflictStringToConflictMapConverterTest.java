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

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ConflictStringToConflictMapConverterTest {
  ConflictStringToConflictMapConverter conflictStringToConflictMapConverter;

  @BeforeEach
  void init() {
    conflictStringToConflictMapConverter = new ConflictStringToConflictMapConverter();
  }

  @Test
  void convertConflictResolutionsStringToConflictMap_emptyConflictResolutions_returnsEmptyMap() throws IOException {
    HashMap<String, Object> expected = new HashMap<>();

    assertThat(
            conflictStringToConflictMapConverter.convertConflictResolutionsStringToConflictMap(""))
        .isEqualTo(expected);
  }

  @Test
  void convertConflictResolutionsStringToConflictMap_withConflictResolution_succeeds() throws IOException {
    HashMap<String, Object> expected = new HashMap<>();
    expected.put("[paths, /pets/{petId}, get, summary]", "get the specified pets");

    String conflictResolutions =
        Files.readString(Path.of("src/test/resources/conflictResolutions.json"));

    assertThat(
            conflictStringToConflictMapConverter.convertConflictResolutionsStringToConflictMap(
                conflictResolutions))
        .isEqualTo(expected);
  }
}
