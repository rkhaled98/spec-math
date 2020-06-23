import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class SpecMathService {
  public String union(String spec1, String spec2, String defaults, String conflictResolutions)
      throws IOException {
    // conflictResolution is a JSON object which we need to decode.
    var objectMapper = new ObjectMapper();

    YamlStringToSpecTreeConverter yamlStringToSpecTreeConverter =
        new YamlStringToSpecTreeConverter();

    ObjectMapper mapper = new ObjectMapper();
    //    String content = new String(Files.readAllBytes(Paths.get(conflictResolutions)));

    List<Conflict> conflictObjs =
        mapper.readValue(new File(conflictResolutions), new TypeReference<List<Conflict>>() {});

    HashMap<String, String> conflictMap = new HashMap<>();

    for (Conflict conflictObj : conflictObjs) {
      String keypath = conflictObj.getKeypath();
      String resolvedValue = conflictObj.getResolvedValue();
      conflictMap.put(keypath, resolvedValue);
    }

    Map<String, Object> spec1map = yamlStringToSpecTreeConverter.convertYamlFileToMap(spec1);
    Map<String, Object> spec2map = yamlStringToSpecTreeConverter.convertYamlFileToMap(spec2);

    SpecTreesUnionizer specTreesUnionizer = new SpecTreesUnionizer();
    Map<String, Object> merged = null;
    try {
      merged =
          specTreesUnionizer.mergeMaps(
              spec1map, spec2map, new HashMap<String, Object>(), conflictMap);
      SpecTreeToYamlStringsConverter specTreeToYamlStringsConverter =
          new SpecTreeToYamlStringsConverter();
      return specTreeToYamlStringsConverter.convertMapToYaml(merged);
    } catch (UnableToUnionException e) {
      e.printStackTrace();
      ArrayList<Conflict> conflicts = e.getConflicts();

      // Set pretty printing of json
      //      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
      //      objectMapper.writ
      //      String arrayToJson = objectMapper.writeValueAsString(conflicts);
      //      System.out.println(arrayToJson);

      ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
      writer.writeValue(new File("./CONFLICTS.json"), conflicts);
    }

    //    //JSON file to Java object
    //    ArrayList<Conflict> conflictRes = mapper.readValue(new File(conflictResolutions),
    // Conflict.class);

    //    Map<String, Object> conflictResMap =
    //        yamlStringToSpecTreeConverter.convertYamlFileToMap(conflictResolutions);

    //    System.out.println(conflictResMap);

    //		ArrayList<Conflict> conflictResolutionObjects = new ArrayList<Conflict>();

    //		for (String conflictResolution: conflictResolutions){
    //			Conflict conflict = objectMapper.readValue(conflictResolution, Conflict.class);
    //			conflictResolutionObjects.add(conflict);
    //		}
    // now we need to add these conflicts to the defaults file.
    return "";
  }

  //	public String union(String spec1, String spec2, String defaults) throws UnableToUnionException,
  // IOException {
  //		// now we need to add these conflicts to the defaults file.
  //		YamlStringToSpecTreeConverter yamlStringToSpecTreeConverter = new
  // YamlStringToSpecTreeConverter();
  //
  //		Map<String, Object> spec1map = yamlStringToSpecTreeConverter.convertYamlFileToMap(spec1);
  //		Map<String, Object> spec2map = yamlStringToSpecTreeConverter.convertYamlFileToMap(spec2);
  //
  //
  //		SpecTreesUnionizer	specTreesUnionizer = new SpecTreesUnionizer();
  //		Map<String, Object> merged = specTreesUnionizer.mergeMaps(spec1map,spec2map,defaults, new
  // HashMap<String, String>());
  //
  //		SpecTreeToYamlStringsConverter specTreeToYamlStringsConverter = new
  // SpecTreeToYamlStringsConverter();
  //		String output = specTreeToYamlStringsConverter.convertMapToYaml(merged);
  //
  //		return output;
  //	}

  public String union(String spec1, String spec2) throws IOException {
    // now we need to add these conflicts to the defaults file.
    YamlStringToSpecTreeConverter yamlStringToSpecTreeConverter =
        new YamlStringToSpecTreeConverter();

    Map<String, Object> spec1map = yamlStringToSpecTreeConverter.convertYamlFileToMap(spec1);
    Map<String, Object> spec2map = yamlStringToSpecTreeConverter.convertYamlFileToMap(spec2);

    SpecTreesUnionizer specTreesUnionizer = new SpecTreesUnionizer();
    Map<String, Object> merged = null;
    try {
      merged =
          specTreesUnionizer.mergeMaps(
              spec1map, spec2map, new HashMap<String, Object>(), new HashMap<String, String>());
      SpecTreeToYamlStringsConverter specTreeToYamlStringsConverter =
          new SpecTreeToYamlStringsConverter();
      return specTreeToYamlStringsConverter.convertMapToYaml(merged);
    } catch (UnableToUnionException e) {
      e.printStackTrace();
      ArrayList<Conflict> conflicts = e.getConflicts();

      ObjectMapper objectMapper = new ObjectMapper();
      // Set pretty printing of json
      //      objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
      //      objectMapper.writ
      //      String arrayToJson = objectMapper.writeValueAsString(conflicts);
      //      System.out.println(arrayToJson);

      ObjectWriter writer = objectMapper.writer(new DefaultPrettyPrinter());
      writer.writeValue(new File("./CONFLICTS.json"), conflicts);
    }

    return "";
  }

  public String unionWithDefaults() throws UnableToUnionException {
    return "";
  }
}
