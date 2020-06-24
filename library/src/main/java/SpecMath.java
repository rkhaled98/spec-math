import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

public class SpecMath {
  public static String union(String spec1, String spec2)
      throws IOException, UnableToUnionException {
    UnionParameters params = UnionParameters.builder().build();

    return union(spec1, spec2, params);
  }

  public static String union(String spec1, String spec2, UnionParameters unionParameters)
      throws IOException, UnableToUnionException {
    var mapper = new ObjectMapper();
    HashMap<String, String> conflictMap = new HashMap<>();

    // conflictResolutions is a json string
    if (!unionParameters.conflictResolutions().isEmpty()) {
      List<Conflict> conflictObjs =
          mapper.readValue(
              unionParameters.conflictResolutions(), new TypeReference<List<Conflict>>() {});
      for (Conflict conflictObj : conflictObjs) {
        String keypath = conflictObj.getKeypath();
        String resolvedValue = conflictObj.getResolvedValue();
        conflictMap.put(keypath, resolvedValue);
      }
    }

    var yamlStringToSpecTreeConverter = new YamlStringToSpecTreeConverter();
    Map<String, Object> spec1map = yamlStringToSpecTreeConverter.convertYamlStringToSpecTree(spec1);
    Map<String, Object> spec2map = yamlStringToSpecTreeConverter.convertYamlStringToSpecTree(spec2);
    Map<String, Object> defaults =
        yamlStringToSpecTreeConverter.convertYamlFileToSpecTree(unionParameters.defaults());

    var specTreesUnionizer = new SpecTreesUnionizer();
    Map<String, Object> mergedMap =
        specTreesUnionizer.union(spec1map, spec2map, defaults, conflictMap);

    var specTreeToYamlStringsConverter = new SpecTreeToYamlStringsConverter();

    return specTreeToYamlStringsConverter.convertSpecTreeToYaml(mergedMap);
  }

  public static String applyOverlay(String spec1, String overlay) {
    var yamlStringToSpecTreeConverter = new YamlStringToSpecTreeConverter();
    Map<String, Object> spec1map = yamlStringToSpecTreeConverter.convertYamlStringToSpecTree(spec1);
    Map<String, Object> overlayMap =
        yamlStringToSpecTreeConverter.convertYamlStringToSpecTree(overlay);

    var specTreesUnionizer = new SpecTreesUnionizer();
    Map<String, Object> overlayedMap = specTreesUnionizer.applyOverlay(overlayMap, spec1map);

    var specTreeToYamlStringsConverter = new SpecTreeToYamlStringsConverter();
    return specTreeToYamlStringsConverter.convertSpecTreeToYaml(overlayedMap);
  }
}
