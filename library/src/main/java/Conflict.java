public class Conflict {
  private String keypath;
  private String optionA;
  private String optionB;
  private String resolvedValue;

  public Conflict() {
    this.keypath = "";
    this.optionA = "";
    this.optionB = "";
    this.resolvedValue = "";
  }

  public Conflict(String keypath, String optionA, String optionB, String resolvedValue) {
    this.keypath = keypath;
    this.optionA = optionA;
    this.optionB = optionB;
    this.resolvedValue = resolvedValue;
  }

  public Conflict(String keypath, String optionA, String optionB) {
    this.keypath = keypath;
    this.optionA = optionA;
    this.optionB = optionB;
  }

  public Conflict(String keypath, String resolvedValue) {
    this.keypath = keypath;
    this.resolvedValue = resolvedValue;
  }

  public String getKeypath() {
    return keypath;
  }

  public void setKeypath(String keypath) {
    this.keypath = keypath;
  }

  public String getOptionA() {
    return optionA;
  }

  public String getOptionB() {
    return optionB;
  }

  public void setOptionB(String optionB) {
    this.optionB = optionB;
  }

  public String getResolvedValue() {
    return resolvedValue;
  }

  public void setResolvedValue(String resolvedValue) {
    this.resolvedValue = resolvedValue;
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

  //  @Override
  //  public String toString() {
  //    return "keypath='"
  //        + keypath
  //        + '\''
  //        + ", optionA='"
  //        + optionA
  //        + '\''
  //        + ", optionB='"
  //        + optionB
  //        + '\'';
  //  }
}
