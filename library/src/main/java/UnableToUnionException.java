import java.util.ArrayList;

public class UnableToUnionException extends Exception {
  private ArrayList<Conflict> conflicts;

  public UnableToUnionException(ArrayList<Conflict> conflicts) {
    this.conflicts = conflicts;
  }

  public ArrayList<Conflict> getConflicts() {
    return conflicts;
  }
}
