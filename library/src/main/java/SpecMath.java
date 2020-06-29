import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.*;

public class SpecMath {
  /**
   * Performs the union operation on two specs represented as strings.
   * This operation will attempt to combine {@code spec1} and {@code spec2} using the logic
   * provided in the SpecTreeUnionizer class. Since no arguments are provided, it will attempt the
   * union and if any conflicts are found an {@code UnableToUnionException} will be thrown.
   *
   * @param spec1
   * @param spec2
   * @return the result of the union on spec1 and spec2, as a YAML string
   * @throws IOException if there was a parsing issue
   * @throws UnableToUnionException if there was a conflict in the union process, i.e. when two
   *     keypaths have the same value
   */
  public static String union(String spec1, String spec2)
      throws IOException, UnableToUnionException {
    UnionOptions params = UnionOptions.builder().build();

    return union(spec1, spec2, params);
  }

  /**
   * Performs the union operation on two specs represented as strings.
   * This operation will attempt to combine {@code spec1} and {@code spec2} using the logic
   * provided in the SpecTreeUnionizer class. If {@code UnionOptions} are provided,
   * then it will apply them as is appropriate based on the logic in the @code SpecTreeUnionizer}
   * class. If {@code UnionOptions} cannot resolve the conflict then an {@code UnableToUnionException}
   * will be thrown.
   *
   *
   * @param spec1
   * @param spec2
   * @param unionOptions
   * @return the result of the union on spec1 and spec2, as a YAML string
   * @throws IOException if there was a parsing issue
   * @throws UnableToUnionException if there was a conflict in the union process, i.e. when two
   *     keypaths have the same value
   */
  public static String union(String spec1, String spec2, UnionOptions unionOptions)
      throws IOException, UnableToUnionException {
    var mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    HashMap<String, Object> conflictMap = new HashMap<>();

    // conflictResolutions is a json string
    // might be its own function
    String conflictResolutions = unionOptions.conflictResolutions();
    if (!conflictResolutions.isEmpty()) {
      List<Conflict> conflictObjs =
          mapper.readValue(conflictResolutions, new TypeReference<List<Conflict>>() {});
      for (Conflict conflictObj : conflictObjs) {
        String keypath = conflictObj.getKeypath();
        Object resolvedValue = conflictObj.getResolvedValue();
        conflictMap.put(keypath, resolvedValue);
      }
    }

    var yamlStringToSpecTreeConverter = new YamlStringToSpecTreeConverter();
    LinkedHashMap<String, Object> spec1map =
        yamlStringToSpecTreeConverter.convertYamlStringToSpecTree(spec1);
    LinkedHashMap<String, Object> spec2map =
        yamlStringToSpecTreeConverter.convertYamlStringToSpecTree(spec2);
    LinkedHashMap<String, Object> defaults =
        yamlStringToSpecTreeConverter.convertYamlStringToSpecTree(unionOptions.defaults());

    var specTreesUnionizer = new SpecTreesUnionizer();
    UnionizerUnionParams unionizerUnionParams =
        UnionizerUnionParams.builder().defaults(defaults).conflictResolutions(conflictMap).build();
    LinkedHashMap<String, Object> mergedMap =
        specTreesUnionizer.union(spec1map, spec2map, unionizerUnionParams);

    //    var specTreeOrderer = new SpecTreeOrderer();
    //    LinkedHashMap<String, Object> orderedMap = specTreeOrderer.applyOrder(mergedMap);

    var specTreeToYamlStringsConverter = new SpecTreeToYamlStringsConverter();

    return specTreeToYamlStringsConverter.convertSpecTreeToYamlString(mergedMap);
  }

  /**
   * Performs the overlay operation on two specs represented as strings by calling the
   * {@code applyOverlay} function of the {@code SpecTreeUnionizer} class.
   *
   * @param spec1
   * @param overlay
   * @return
   */
  public static String applyOverlay(String spec1, String overlay) throws IOException {
    var yamlStringToSpecTreeConverter = new YamlStringToSpecTreeConverter();
    LinkedHashMap<String, Object> spec1map =
        yamlStringToSpecTreeConverter.convertYamlStringToSpecTree(spec1);
    LinkedHashMap<String, Object> overlayMap =
        yamlStringToSpecTreeConverter.convertYamlStringToSpecTree(overlay);

    var specTreesUnionizer = new SpecTreesUnionizer();
    LinkedHashMap<String, Object> overlayedMap =
        specTreesUnionizer.applyOverlay(overlayMap, spec1map);

    var specTreeToYamlStringsConverter = new SpecTreeToYamlStringsConverter();
    return specTreeToYamlStringsConverter.convertSpecTreeToYamlString(overlayedMap);
  }
}