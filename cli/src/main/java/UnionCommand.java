import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.specmath.library.SpecMath;
import org.specmath.library.UnionConflictException;
import org.specmath.library.UnionOptions;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(
    name = "union",
    description = "Perform the union operation.",
    mixinStandardHelpOptions = true)
class UnionCommand implements Callable<Integer> {
  @Option(
      names = {"-o", "--output"},
      required = true,
      description = "name of the output file")
  String outputFilename;

  @Option(
      names = {"-d", "--defaults"},
      required = false,
      description = "path to defaults file")
  Path defaultsPath;

  @Option(
      names = {"-c", "--conflictResolutions"},
      required = false,
      description = "path to conflict resolutions file")
  Path conflictResolutionsPath;

  @Parameters(arity = "2..*")
  private List<Path> filesToMerge;

  public static void main(String[] args) {
    int exitCode =
        new CommandLine(new UnionCommand())
            .setExecutionExceptionHandler(new PrintExceptionMessageHandler())
            .execute(args);
    System.exit(exitCode);
  }

  @Override
  public Integer call() throws Exception {
    List<String> specStrings = new ArrayList<String>();

    String defaults = "";
    String conflictResolutions = "";

    if (defaultsPath != null) {
      defaults = Files.readString(defaultsPath);
    }

    if (conflictResolutionsPath != null) {
      conflictResolutions = Files.readString(conflictResolutionsPath);
    }

    UnionOptions unionOptions =
        UnionOptions.builder().defaults(defaults).conflictResolutions(conflictResolutions).build();

    for (Path path : filesToMerge) {
      specStrings.add(Files.readString(path));
    }

    try {
      String result = SpecMath.union(specStrings, unionOptions);
      Files.writeString(Paths.get(outputFilename), result);
      System.out.printf(
          "The union operation succeeded. Result file written to %s.\n", outputFilename);
    } catch (UnionConflictException e) {
      System.out.println("There were conflicts in the union process\n");

      String conflictsFilename = "CONFLICTS_" + outputFilename;

      ObjectMapper mapper = new ObjectMapper();

      mapper
          .writerWithDefaultPrettyPrinter()
          .writeValue(new File(conflictsFilename), e.getConflicts());

      System.out.println("Please see the file " + conflictsFilename + " for more information\n");

      System.out.println("To resolve the conflicts you have three choices:");
      System.out.printf(
          "1: Automatically resolving the conflicts by updating the \"resolvedValue\" property in %s"
              + " with your desired option, and then passing that file into the union as an"
              + " additional parameter using the -c flag\n\n",
          conflictsFilename);
      System.out.printf(
          "2: Manually resolve the conflicts by looking at %s and changing the input YAML files\n\n",
          conflictsFilename);
      System.out.printf(
          "3: Resolve the conflicts by updating/creating a defaults file with "
              + "overrides for the conflicting keypaths in %s, and pass in the new defaults file"
              + " into the union as an additional parameter using the -d flags\n\n",
          conflictsFilename);
    }

    return 0;
  }
}