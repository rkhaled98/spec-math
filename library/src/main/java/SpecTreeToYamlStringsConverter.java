import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpecTreeToYamlStringsConverter {

  int indent;

  public SpecTreeToYamlStringsConverter() {
    this.indent = 2;
  }

  /**
   * Converts a spec tree represented as a {@code LinkedHashMap} to a YAML string
   *
   * @param yamlMap a spec tree
   * @return a YAML string which represents {@code yamlMap}
   * @throws IOException if there is a parsing issue
   */
  public String convertSpecTreeToYamlString(LinkedHashMap<String, Object> yamlMap) {
    return convertSpecTreeToYamlString(yamlMap, 0, false);
  }

  @SuppressWarnings("unchecked")
  private String convertSpecTreeToYamlString(
      LinkedHashMap<String, Object> map, int level, boolean firstListElement) {
    StringBuilder str = new StringBuilder();
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      if (!firstListElement) {
        str.append(spaces(level));
      } else {
        firstListElement = false;
      }

      // the key is a digit, which is surrounded by single quotes
      if ((key.chars().allMatch(Character::isDigit))) {
        str.append(String.format("'%s':", key));
      } else {
        str.append(String.format("%s:", key));
      }

      if (value instanceof LinkedHashMap) {
        LinkedHashMap<String, Object> valueMap = (LinkedHashMap<String, Object>) value;

        str.append("\n");
        str.append(convertSpecTreeToYamlString(valueMap, level + 1, false));

      } else if (value instanceof List) {
        List<Object> valueList = (List<Object>) value;
        int listLevel = level + 1;

        str.append("\n");
        for (int i = 0; i < valueList.size(); i++) {
          str.append(spaces(listLevel));
          str.append("- ");

          Object element = valueList.get(i);

          if (element instanceof String
              || element instanceof Boolean
              || element instanceof Integer) {
            str.append(String.format("%s\n", element));
          } else if (element instanceof LinkedHashMap) {
            LinkedHashMap<String, Object> elementMap = (LinkedHashMap<String, Object>) element;
            str.append(convertSpecTreeToYamlString(elementMap, listLevel + 1, true));
          }
        }

      } else if (value instanceof String || value instanceof Boolean || value instanceof Integer) {
        //        str.append(spaces(level));
        if (key.equals("$ref")) {
          str.append(String.format(" \"%s\"\n", value));
        } else {
          str.append(String.format(" %s\n", value));
        }
      }
    }

    return str.toString();
  }

  public String spaces(int level) {
    StringBuilder str = new StringBuilder();
    for (int i = 0; i < level * this.indent; i++) {
      str.append(" ");
    }
    return str.toString();
  }

  //  public String convertSpecTreeToYamlString(LinkedHashMap<String, Object> yamlMap)
  //      throws IOException {
  //    //The representer allows us to ignore null properties, and to leave off the class
  // definitions
  ////    Representer representer = new Representer() {
  ////      //ignore null properties
  ////      @Override
  ////      protected NodeTuple representJavaBeanProperty(Object javaBean, Property property, Object
  // propertyValue, Tag customTag) {
  ////        // if value of property is null, ignore it.
  ////        if (propertyValue == null) {
  ////          return null;
  ////        }
  ////        else {
  ////          return super.representJavaBeanProperty(javaBean, property, propertyValue,
  // customTag);
  ////        }
  ////      }
  ////
  ////      //Don't print the class definition
  ////      @Override
  ////      protected MappingNode representJavaBean(Set<Property> properties, Object javaBean) {
  ////        if (!classTags.containsKey(javaBean.getClass())){
  ////          addClassTag(javaBean.getClass(), Tag.MAP);
  ////        }
  ////
  ////        return super.representJavaBean(properties, javaBean);
  ////      }
  ////    };
  ////
  //    DumperOptions options = new DumperOptions();
  //    options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
  //
  //    YAMLFactory yamlFactory = new YAMLFactory();
  //
  //    options.setIndent(4);
  //    options.setIndicatorIndent(2);
  //
  //    StringWriter writer = new StringWriter();
  //
  //    Yaml yaml = new Yaml(options);
  //    yaml.dump(yamlMap, writer);
  //
  //    return writer.toString();
  ////
  ////    Writer swriter = new StringWriter();
  ////    yaml.dump(yamlMap, writer);
  //
  ////    YamlWriter writer = new YamlWriter(swriter);
  ////    writer.getConfig().writeConfig.setIndentSize(2);
  ////    writer.getConfig().writeConfig.setAutoAnchor(false);
  //////    writer.getConfig().writeConfig.setWriteRootTags(false);
  //////    writer.getConfig().writeConfig.setWriteDefaultValues(false);
  ////    writer.getConfig().writeConfig.setKeepBeanPropertyOrder(true);
  //////    writer.getConfig().writeConfig.setUseVerbatimTags(false);
  //////    writer.getConfig().writeConfig.setWriteRootElementTags(false);
  ////    writer.getConfig().writeConfig.setWriteClassname(YamlConfig.WriteClassName.NEVER);
  //////    writer.getConfig().setClassTag("", LinkedHashMap.class);
  //////    writer.getConfig().writeConfig.setWriteClassname(YamlConfig.WriteClassName.NEVER);
  ////
  ////
  //////    writer.getConfig().writeConfig.setWriteRootTags(false);
  //////    writer.getConfig().writeConfig.
  ////    writer.write(yamlMap);
  ////
  ////
  ////    return swriter.toString();
  //  }
}
