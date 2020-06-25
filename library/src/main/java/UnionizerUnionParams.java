import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Stack;

@AutoValue
public abstract class UnionizerUnionParams {
  public abstract LinkedHashMap<String, Object> defaults();
  public abstract HashMap<String, String> conflictResolutions();

  public static Builder builder() {
    return new AutoValue_UnionizerUnionParams.Builder()
        .defaults(new LinkedHashMap<String, Object>())
        .conflictResolutions(new HashMap<String, String>());
  }

  @AutoValue.Builder
  public abstract static class Builder {

    public abstract Builder defaults(LinkedHashMap<String, Object> defaults);

    public abstract Builder conflictResolutions(HashMap<String, String> conflictResolutions);

    public abstract UnionizerUnionParams build();
  }

//  @AutoValue.Builder
//  public abstract static class Builder {
//
//    public abstract Builder map1IsDefault(boolean mapIsDefault);
//
//    public abstract Builder map1IsOrderer(boolean mapIsOrderer);
//
//    public abstract Builder conflictResolutions(HashMap<String, String> conflictResolutions);
//
//    public abstract UnionizerUnionParams build();
//  }
}

