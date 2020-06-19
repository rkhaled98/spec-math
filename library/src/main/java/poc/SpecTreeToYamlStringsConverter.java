package poc;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.StringWriter;
import java.util.Map;

public class SpecTreeToYamlStringsConverter {
  public String convertMapToYaml(Map<String, Object> yamlMap) {
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

    options.setIndent(4);
    options.setIndicatorIndent(2);
    Yaml yaml = new Yaml(options);

    StringWriter writer = new StringWriter();
    yaml.dump(yamlMap, writer);

    return writer.toString();
  }
}
