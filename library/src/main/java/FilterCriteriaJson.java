import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.ArrayList;
import java.util.List;

public class FilterCriteriaJson {
  @JsonProperty("pathRegex")
  public String pathRegex = "";

  @JsonSetter("pathRegex")
  public void setPathRegex(String s) {
    if (s != null) {
      pathRegex = s;
    }
  }

  @JsonProperty("tags")
  public List<String> tags = new ArrayList<String>();

  @JsonSetter("tags")
  public void setTags(List<String> s) {
    if (s != null) {
      tags = s;
    }
  }

  @JsonProperty("operations")
  public List<String> operations = new ArrayList<String>();

  @JsonSetter("operations")
  public void setOperations(List<String> s) {
    if (s != null) {
      operations = s;
    }
  }

  @JsonProperty("removableTags")
  public List<String> removableTags = new ArrayList<String>();

  @JsonSetter("removableTags")
  public void setRemovableTags(List<String> s) {
    if (s != null) {
      removableTags = s;
    }
  }
}
