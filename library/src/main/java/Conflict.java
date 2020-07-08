import java.util.Objects;

public class Conflict {
  private String keypath;

  private Object optionA;
  private Object optionB;
  private Object resolvedValue;

  /**
   * The default constructor is needed for com.fasterxml.jackson.databind.ObjectMapper which is used
   * in the {@code ConflictStringToConflictMapConverter} class
   */
  public Conflict() {
    this.keypath = "";
    this.optionA = "";
    this.optionB = "";
    this.resolvedValue = "";
  }

  public Conflict(String keypath, Object optionA, Object optionB) {
    this.keypath = keypath;
    this.optionA = optionA;
    this.optionB = optionB;
  }

  public Object getOptionA() {
    return optionA;
  }

  public Object getOptionB() {
    return optionB;
  }

  public String getKeypath() {
    return keypath;
  }

  public Object getResolvedValue() {
    return resolvedValue;
  }

  @Override
  public boolean equals(Object o) {
    Conflict conflict = (Conflict) o;
    return keypath.equals(conflict.keypath)
        && optionA.equals(conflict.optionA)
        && optionB.equals(conflict.optionB)
        && Objects.equals(resolvedValue, conflict.resolvedValue);
  }

  @Override
  public String toString() {
    return "Conflict{"
        + "keypath='"
        + keypath
        + '\''
        + ", optionA='"
        + optionA
        + '\''
        + ", optionB='"
        + optionB
        + '\''
        + ", resolvedValue='"
        + resolvedValue
        + '\''
        + '}';
  }
}
