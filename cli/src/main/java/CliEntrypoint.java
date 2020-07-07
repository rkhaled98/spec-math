import picocli.CommandLine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@CommandLine.Command(
    name = "Spec Math CLI",
    description = "Perform operations on OpenAPI Specifications")
public class CliEntrypoint implements Runnable {
  @CommandLine.Option(
      names = {"-o", "--operation"},
      required = false,
      description = "union | merge | overlay | filter")
  String operation;

  @CommandLine.Option(
      names = {"-d", "--defaults"},
      required = false,
      description = "defaults path")
  String defaultsPath;

  @CommandLine.Option(
      names = {"-c", "--conflictResolutions"},
      required = false,
      description = "conflictResolutions")
  String conflictResolutionsPath;

  @CommandLine.Option(
      names = {"-s1", "--spec1"},
      required = false,
      description = "spec1 path")
  String spec1Path;

  @CommandLine.Option(
      names = {"-s2", "--spec2"},
      required = false,
      description = "spec2 path")
  String spec2Path;

  public static void main(String[] args) {
    CommandLine.run(new CliEntrypoint(), args);
  }

  @Override
  public void run() {
    SpecMath specMath = new SpecMath();
    try {
      String output = "";
      String spec1 = Files.readString(Path.of(spec1Path));
      String spec2 = Files.readString(Path.of(spec2Path));
      String conflictResolutions = "";
      String defaults = "";
      if (conflictResolutionsPath != null) {
        conflictResolutions = Files.readString(Path.of(conflictResolutionsPath));
      }
      if (defaultsPath!= null) {
        defaults = Files.readString(Path.of(defaultsPath));
      }

      if (operation.equals("union") || operation.equals("merge")){
        UnionOptions params;
        if (!defaults.isEmpty()){
          params =
              UnionOptions.builder().defaults(defaults).conflictResolutions(conflictResolutions).build();
        } else {
          params =
              UnionOptions.builder().build();
        }

        output = SpecMath.union(spec1, spec2, params);
        System.out.println("Union Success!");
      } else if (operation.equals("overlay")){
        output = SpecMath.applyOverlay(defaults, spec1);
        System.out.println("Overlay Success!");
      }

      System.out.println(output);
    } catch (IOException e) {
      e.printStackTrace();
    } catch (UnionConflictException e){ ;
      System.out.println("couldnt do the operation, there was a conflict");
      System.out.println(e.getConflicts());
    }
  }
}