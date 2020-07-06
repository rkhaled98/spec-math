import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SpecTreeToYamlStringsConverter {

  int indent;

  public SpecTreeToYamlStringsConverter() {
    this.indent = 2;
  }

  /**
   * Converts a spec tree represented as a {@code LinkedHashMap} to a YAML string.
   *
   * @param yamlMap a spec tree which is a LinkedHashMap which String keys and Object values
   * @return a YAML string which represents {@code yamlMap}
   */
  public String convertSpecTreeToYamlString(LinkedHashMap<String, Object> yamlMap) {
    return convertSpecTreeToYamlString(yamlMap, 0, false);
  }

  private String convertSpecTreeToYamlString(
      LinkedHashMap<String, Object> map, int level, boolean firstListElement) {
    StringBuilder str = new StringBuilder();
    for (Map.Entry<String, Object> entry : map.entrySet()) {
      String key = entry.getKey();
      Object value = entry.getValue();

      firstListElement = handleFirstElementSpacing(level, firstListElement, str);

      handleKey(str, key);

      if (TypeChecker.isObjectMap(value)) {
        handleMapValue(level, str, ObjectCaster.castObjectToStringObjectMap(value));
      } else if (TypeChecker.isObjectList(value)) {
        handleListValues(level, str, ObjectCaster.castObjectToListOfObjects(value));
      } else if (TypeChecker.isObjectPrimitive(value)) {
        handlePrimitiveValue(str, key, value);
      }
    }

    return str.toString();
  }

  /**
   * Processes and appends the elements in a list to the StringBuilder.
   *
   * @param level the current traversal level, which indicates how many spaces to print out
   * @param str the StringBuilder which we append to during the traversal
   * @param value the list of values to process
   */
  private void handleListValues(int level, StringBuilder str, List<Object> value) {
    int listLevel = level + 1;

    str.append("\n");
    for (int i = 0; i < value.size(); i++) {
      str.append(spaces(listLevel));
      str.append("- ");

      Object element = value.get(i);

      if (TypeChecker.isObjectPrimitive(element)) {
        str.append(String.format("%s\n", element));
      } else if (TypeChecker.isObjectMap(element)) {
        LinkedHashMap<String, Object> elementMap = ObjectCaster.castObjectToStringObjectMap(element);
        str.append(convertSpecTreeToYamlString(elementMap, listLevel + 1, true));
      }
    }
  }

  /**
   * @param level the current traversal level, which indicates how many spaces to print out
   * @param firstListElement a boolean which indicates whether or not current element in the loop
   *                         is the first element of a list, which needs to be processed in a
   *                         special way
   * @param str the StringBuilder which we append to during the traversal
   * @return {@code false if {@code firstListElement} was initially true. Otherwise it
   *     returns true
   */
  private boolean handleFirstElementSpacing(
      int level, boolean firstListElement, StringBuilder str) {
    if (!firstListElement) {
      str.append(spaces(level));
    } else {
      firstListElement = false;
    }
    return firstListElement;
  }

  /**
   * Appends the a key to the StringBuilder.
   *
   * @param str the StringBuilder which we append to during the traversal
   * @param key the key which we want to append to the StringBuilder, which is handled in special
   *     ways depending on the key
   */
  private void handleKey(StringBuilder str, String key) {
    // the key is a digit, which is surrounded by single quotes
    if ((key.chars().allMatch(Character::isDigit))) {
      str.append(String.format("'%s':", key));
    } else {
      str.append(String.format("%s:", key));
    }
  }

  /**
   * Appends a primitive value to the StringBuilder.
   *
   * @param str the StringBuilder which we append to during the traversal
   * @param key the key in the map which corresponds to the value, used for special handling of
   *     certain keys
   * @param value the primitive value to add to the StringBuilder
   */
  private void handlePrimitiveValue(StringBuilder str, String key, Object value) {
    if (key.equals("$ref")) {
      // a "$ref" tag should be handled in a special way, with the value in double quotes.
      str.append(String.format(" \"%s\"\n", value));
    } else {
      str.append(String.format(" %s\n", value));
    }
  }

  /**
   * Appends the result of processing a map value to the StringBuilder.
   *
   * @param level the current traversal level
   * @param str the StringBuilder which we append to during the traversal
   * @param value the map value to process further
   */
  private void handleMapValue(int level, StringBuilder str, LinkedHashMap<String, Object> value) {
    LinkedHashMap<String, Object> valueMap = value;

    str.append("\n");
    str.append(convertSpecTreeToYamlString(valueMap, level + 1, false));
  }

  /** Returns a string with {@code level} * {@code this.indent} spaces. */
  private String spaces(int level) {
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
