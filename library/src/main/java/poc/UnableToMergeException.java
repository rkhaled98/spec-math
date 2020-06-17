package poc;

import java.util.ArrayList;

public class UnableToMergeException extends Exception {
  private ArrayList<Conflict> conflicts;

  public UnableToMergeException(ArrayList<Conflict> conflicts) {
    this.conflicts = conflicts;
  }

  public ArrayList<Conflict> getConflicts() {
    return conflicts;
  }
}
