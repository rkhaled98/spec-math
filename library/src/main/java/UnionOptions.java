// DumperOptions options = new DumperOptions();
//    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
//
//    options.setIndent(4);
//    options.setIndicatorIndent(2);

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class UnionOptions {
  public abstract String defaults();
  public abstract String conflictResolutions();

  public static Builder builder() {
    return new AutoValue_UnionOptions.Builder().defaults("").conflictResolutions("");
  }

  @AutoValue.Builder
  public abstract static class Builder {
    public abstract Builder defaults(String defaults);
    public abstract Builder conflictResolutions(String conflictResolutions);
    public abstract UnionOptions build();
  }
}
