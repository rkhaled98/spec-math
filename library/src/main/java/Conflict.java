public class Conflict {
  private String keypath;
  private Object optionA;
  private Object optionB;
  private Object resolvedValue;

  public Conflict(){
    this.keypath = "";
    this.optionA = "";
    this.optionB = "";
    this.resolvedValue = "";
  }

  public Conflict(String keypath, Object optionA, Object optionB, Object resolvedValue){
    this.keypath = keypath;
    this.optionA = optionA;
    this.optionB = optionB;
    this.resolvedValue = resolvedValue;
  }

  public Conflict(String keypath, Object optionA, Object optionB) {
    this.keypath = keypath;
    this.optionA = optionA;
    this.optionB = optionB;
  }

  public String getKeypath() {
    return keypath;
  }

  public Object getResolvedValue() {
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
