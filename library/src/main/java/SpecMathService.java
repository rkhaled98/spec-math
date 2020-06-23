import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SpecMathService {
  public String unionWithDefaults(String spec1, String spec2, String defaults) throws IOException {
    return unionHelper(spec1, spec2, defaults, "");
  }

  public String unionWithConflictResolutions(String spec1, String specc2, String conflictResolutions){
    return unionHelper(spec1, sec2, "", conflictResolutions);
  }

  public String unionWithDefaultsAndConflictResolutions(String spec1, String spec2, String defaults, String conflictResolutions){
    return unionHelper(spec1, spec2, defaults, conflictResolutions);
  }

  public String union(String spec1, String spec2){
    return unionHelper(spec1, spec2, "", "")
  }

  private String unionHelper(String spec1, String spec2, String defaults, String conflictResolutions)
      throws IOException, UnableToUnionException {
    // conflictResolution is a JSON string which we need to decode.
    var mapper = new ObjectMapper();
    HashMap<String, String> conflictMap = new HashMap<>();

    if (!conflictResolutions.isEmpty()) {
      List<Conflict> conflictObjs =
          mapper.readValue(conflictResolutions, new TypeReference<List<Conflict>>() {});
      for (Conflict conflictObj : conflictObjs) {
        String keypath = conflictObj.getKeypath();
        String resolvedValue = conflictObj.getResolvedValue();
        conflictMap.put(keypath, resolvedValue);
      }
    }

    YamlStringToSpecTreeConverter yamlStringToSpecTreeConverter =
        new YamlStringToSpecTreeConverter();
    Map<String, Object> spec1map = yamlStringToSpecTreeConverter.convertYamlStringToSpecTree(spec1);
    Map<String, Object> spec2map = yamlStringToSpecTreeConverter.convertYamlStringToSpecTree(spec2);

    SpecTreesUnionizer specTreesUnionizer = new SpecTreesUnionizer();
    Map<String, Object> merged = null;
      merged =
          specTreesUnionizer.mergeMaps(
              spec1map, spec2map, new HashMap<String, Object>(), conflictMap);

      SpecTreeToYamlStringsConverter specTreeToYamlStringsConverter =
          new SpecTreeToYamlStringsConverter();

      return specTreeToYamlStringsConverter.convertSpecTreeToYaml(merged);

    }

    return "";
  }
}
