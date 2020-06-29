import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ConflictStringToConflictMapConverter {

  /**
   * Takes a JSON string {@code conflictResolutions} which contains an array of {@code Conflict}
   * objects and converts it into a HashMap in which the key is the keypath of a conflict and the
   * value is the resolved value
   *
   * @param conflictResolutions a JSON string which contains an array of {@code Conflict} objects
   * @return a HashMap which contains a mapping of keypath->value to resolve during union
   * @throws IOException if there was a parsing issue
   */
  public HashMap<String, Object> convertConflictResolutionsStringToConflictMap(
      String conflictResolutions) throws IOException {
    var mapper = new ObjectMapper();
    mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    HashMap<String, Object> conflictMap = new HashMap<>();

    if (!conflictResolutions.isEmpty()) {
      List<Conflict> conflictObjs =
          mapper.readValue(conflictResolutions, new TypeReference<List<Conflict>>() {});
      for (Conflict conflictObj : conflictObjs) {
        String keypath = conflictObj.getKeypath();
        Object resolvedValue = conflictObj.getResolvedValue();
        conflictMap.put(keypath, resolvedValue);
      }
    }

    return conflictMap;
  }
}
