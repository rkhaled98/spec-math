import com.google.auto.value.AutoValue;
import java.util.HashMap;
import java.util.LinkedHashMap;

@AutoValue
public abstract class UnionizerUnionParams {
  public static Builder builder() {
    return new AutoValue_UnionizerUnionParams.Builder()
        .defaults(new LinkedHashMap<String, Object>())
        .conflictResolutions(new HashMap<String, Object>());
  }

  public abstract LinkedHashMap<String, Object> defaults();

  public abstract HashMap<String, Object> conflictResolutions();

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder defaults(LinkedHashMap<String, Object> defaults);

    public abstract Builder conflictResolutions(HashMap<String, Object> conflictResolutions);

    public abstract UnionizerUnionParams build();
  }
}
