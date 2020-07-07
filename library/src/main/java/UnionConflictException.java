import java.util.ArrayList;

public class UnionConflictException extends Exception {
  private ArrayList<Conflict> conflicts;

  public UnionConflictException(ArrayList<Conflict> conflicts) {
    this.conflicts = conflicts;
  }

  public ArrayList<Conflict> getConflicts() {
    return conflicts;
  }
}
