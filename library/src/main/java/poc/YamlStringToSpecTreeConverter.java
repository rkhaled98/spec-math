package poc;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.util.Map;

public class YamlStringToSpecTreeConverter {
  public Map<String, Object> convertYamlFileToMap(String pathname) throws FileNotFoundException {
    Yaml yaml = new Yaml();

    File file = new File(pathname);
    InputStream stream = new FileInputStream(file);

    Map<String, Object> yamlMap = yaml.load(stream);

    return yamlMap;
  }

  public Map<String, Object> convertYamlStringToMap(String yamlString) {
    Yaml yaml = new Yaml();

    Map<String, Object> yamlMap = yaml.load(yamlString);

    return yamlMap;
  }
}
