public class Conflict {
  private String keypath;
  private String optionA;
  private String optionB;
  private String resolvedValue;

  public Conflict(String keypath, String optionA, String optionB) {
    this.keypath = keypath;
    this.optionA = optionA;
    this.optionB = optionB;
  }

  public String getKeypath() {
    return keypath;
  }

  public String getResolvedValue() {
    return resolvedValue;
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
