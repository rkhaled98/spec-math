import java.util.LinkedHashMap;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

public class YamlStringToSpecTreeConverter {
  public LinkedHashMap<String, Object> convertYamlFileToSpecTree(String pathname)
      throws FileNotFoundException {
    Yaml yaml = new Yaml();

    File file = new File(pathname);
    InputStream stream = new FileInputStream(file);

    LinkedHashMap<String, Object> yamlMap = yaml.load(stream);

    return yamlMap;
  }

  public LinkedHashMap<String, Object> convertYamlStringToSpecTree(String yamlString) {
    Yaml yaml = new Yaml();

    LinkedHashMap<String, Object> yamlMap;

    if (yamlString.isEmpty()) {
      yamlMap = new LinkedHashMap<>();
    } else {
      yamlMap = yaml.load(yamlString);
    }

    return yamlMap;
  }
}
