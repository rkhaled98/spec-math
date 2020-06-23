import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@CommandLine.Command(
        name = "Spec Math CLI",
        description = "Perform operations on OpenAPI Specifications"
)
public class CliEntrypoint implements Runnable {
  @CommandLine.Option(names = {"-o", "--operation"}, required = false, description = "union | merge | filter")
  String operation;

  @CommandLine.Option(names = {"-d", "--defaults"}, required = false, description = "defaults path")
  String defaults;

  @CommandLine.Option(names = {"-c", "--conflictResolutions"}, required = false, description = "conflictResolutions")
  String conflictResolutions;

  @CommandLine.Option(names = {"-s1", "--spec1"}, required = false, description = "spec1 path")
  String spec1Path;

  @CommandLine.Option(names = {"-s2", "--spec2"}, required = false, description = "spec2 path")
  String spec2Path;

  public static void main(String[] args) {
    CommandLine.run(new CliEntrypoint(), args);
  }

  @Override
  public void run() {
    SpecMathService specMathService = new SpecMathService();
    try {
      String output;
      String spec1 = Files.readString(Path.of(spec1Path));
      String spec2 = Files.readString(Path.of(spec2Path));
      if (conflictResolutions != null){
        output = specMathService.union(spec1, spec2, "", conflictResolutions);
      } else{
        output = specMathService.union(spec1, spec2, "", "");
//      System.out.println(e.getConflicts());
      }
      System.out.println(output);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
