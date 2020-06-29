import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlWriter;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.LinkedHashMap;
import java.util.Set;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.StringWriter;
import java.util.Map;
import org.yaml.snakeyaml.introspector.Property;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.NodeTuple;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

public class SpecTreeToYamlStringsConverter {

  /**
   * Converts a spec tree represented as a {@code LinkedHashMap} to a YAML string
   *
   * @param yamlMap a spec tree
   * @return a YAML string which represents {@code yamlMap}
   * @throws IOException if there is a parsing issue
   */
  public String convertSpecTreeToYamlString(LinkedHashMap<String, Object> yamlMap)
      throws IOException {
    //The representer allows us to ignore null properties, and to leave off the class definitions
//    Representer representer = new Representer() {
//      //ignore null properties
//      @Override
//      protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object propertyValue, Tag customTag) {
//        // if value of property is null, ignore it.
//        if (propertyValue == null) {
//          return null;
//        }
//        else {
//          return super.representJavaBeanProperty(javaBean, property, propertyValue, customTag);
//        }
//      }
//
//      //Don't print the class definition
//      @Override
//      protected MappingNode representJavaBean(Set<Property> properties, Object javaBean) {
//        if (!classTags.containsKey(javaBean.getClass())){
//          addClassTag(javaBean.getClass(), Tag.MAP);
//        }
//
//        return super.representJavaBean(properties, javaBean);
//      }
//    };
//
    DumperOptions options = new DumperOptions();
    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);

    YAMLFactory yamlFactory = new YAMLFactory();

    options.setIndent(4);
    options.setIndicatorIndent(2);

    StringWriter writer = new StringWriter();

    Yaml yaml = new Yaml(options);
    yaml.dump(yamlMap, writer);

    return writer.toString();
//
//    Writer swriter = new StringWriter();
//    yaml.dump(yamlMap, writer);

//    YamlWriter writer = new YamlWriter(swriter);
//    writer.getConfig().writeConfig.setIndentSize(2);
//    writer.getConfig().writeConfig.setAutoAnchor(false);
////    writer.getConfig().writeConfig.setWriteRootTags(false);
////    writer.getConfig().writeConfig.setWriteDefaultValues(false);
//    writer.getConfig().writeConfig.setKeepBeanPropertyOrder(true);
////    writer.getConfig().writeConfig.setUseVerbatimTags(false);
////    writer.getConfig().writeConfig.setWriteRootElementTags(false);
//    writer.getConfig().writeConfig.setWriteClassname(YamlConfig.WriteClassName.NEVER);
////    writer.getConfig().setClassTag("", LinkedHashMap.class);
////    writer.getConfig().writeConfig.setWriteClassname(YamlConfig.WriteClassName.NEVER);
//
//
////    writer.getConfig().writeConfig.setWriteRootTags(false);
////    writer.getConfig().writeConfig.
//    writer.write(yamlMap);
//
//
//    return swriter.toString();
  }
}
