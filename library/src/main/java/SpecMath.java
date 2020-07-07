import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class SpecMath {
  /**
   * Performs the union operation on two specs represented as strings.
   *
   * <p>This operation will attempt to combine {@code spec1} and {@code spec2} using the logic
   * provided in the SpecTreeUnionizer class. Since no arguments are provided, it will attempt the
   * union and if any conflicts are found an {@code UnionConflictException} will be thrown.
   *
   * @param spec1 the first spec to be merged.
   * @param spec2 the second spec to be merged.
   * @return the result of the union on spec1 and spec2, as a YAML string.
   * @throws IOException if there was a parsing issue.
   * @throws UnionConflictException if there was a conflict in the union process, i.e. when two
   *     keypaths have the same value.
   * @throws UnexpectedDataException either an unexpected type was met, or one map had a different
   *     type (primitive, map, list) as a value compared to the other map.
   */
  public static String union(String spec1, String spec2)
      throws IOException, UnionConflictException, UnexpectedDataException {
    UnionOptions params = UnionOptions.builder().build();

    return union(spec1, spec2, params);
  }

  /**
   * Performs the union operation on two specs represented as strings. If {@code UnionOptions} are
   * provided, then it will apply them as is appropriate based on the logic in the {@code
   * SpecTreeUnionizer} class. If {@code UnionOptions} cannot resolve the conflict then an {@code
   * UnionConflictException} will be thrown.
   *
   * @param spec1
   * @param spec2
   * @param unionOptions
   * @return the result of the union on spec1 and spec2, as a YAML string
   * @throws IOException if there was a parsing issue
   * @throws UnionConflictException if there was a conflict in the union process, i.e. when two
   *     keypaths have the same value
   * @throws UnexpectedDataException either an unexpected type was met, or one map had a different
   *     type (primitive, map, list) as a value compared to the other map.
   */
  public static String union(String spec1, String spec2, UnionOptions unionOptions)
      throws IOException, UnionConflictException, UnexpectedDataException {
    var conflictStringToConflictMapConverter = new ConflictStringToConflictMapConverter();
    HashMap<String, Object> conflictResolutionsMap =
        conflictStringToConflictMapConverter.convertConflictResolutionsStringToConflictMap(
            unionOptions.conflictResolutions());

    LinkedHashMap<String, Object> spec1map =
        YamlStringToSpecTreeConverter.convertYamlStringToSpecTree(spec1);
    LinkedHashMap<String, Object> spec2map =
        YamlStringToSpecTreeConverter.convertYamlStringToSpecTree(spec2);
    LinkedHashMap<String, Object> defaults =
        YamlStringToSpecTreeConverter.convertYamlStringToSpecTree(unionOptions.defaults());

    var specTreesUnionizer = new SpecTreesUnionizer();
    UnionizerUnionParams unionizerUnionParams =
        UnionizerUnionParams.builder()
            .defaults(defaults)
            .conflictResolutions(conflictResolutionsMap)
            .build();
    LinkedHashMap<String, Object> mergedMap =
        specTreesUnionizer.union(spec1map, spec2map, unionizerUnionParams);

    var specTreeToYamlStringsConverter = new SpecTreeToYamlStringConverter();

    return specTreeToYamlStringsConverter.convertSpecTreeToYamlString(mergedMap);
  }

  /**
   * Performs the overlay operation on two specs represented as strings by calling the {@code
   * applyOverlay} function of the {@code SpecTreeUnionizer} class.
   *
   * @param spec1
   * @param overlay
   * @return the result of applying {@code overlay} to {@code spec1}, as a YAML string
   * @throws UnexpectedDataException either an unexpected type was met, or one map had a different
   *     type (primitive, map, list) as a value compared to the other map.
   */
  public static String applyOverlay(String overlay, String spec1) throws UnexpectedDataException {
    LinkedHashMap<String, Object> spec1map =
        YamlStringToSpecTreeConverter.convertYamlStringToSpecTree(spec1);
    LinkedHashMap<String, Object> overlayMap =
        YamlStringToSpecTreeConverter.convertYamlStringToSpecTree(overlay);

    var specTreesUnionizer = new SpecTreesUnionizer();
    LinkedHashMap<String, Object> overlayedMap =
        specTreesUnionizer.applyOverlay(overlayMap, spec1map);

    var specTreeToYamlStringsConverter = new SpecTreeToYamlStringConverter();
    return specTreeToYamlStringsConverter.convertSpecTreeToYamlString(overlayedMap);
  }
}
