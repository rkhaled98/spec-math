package poc;

public class Conflict {
    private String keypath;
    private String optionA;
    private String optionB;

    public Conflict(String attribute, String optionA, String optionB) {
        this.keypath = attribute;
        this.optionA = optionA;
        this.optionB = optionB;
    }

    public String getAttribute() {
        return keypath;
    }

    public void setAttribute(String attribute) {
        this.keypath = attribute;
    }

    public String getOptionA() {
        return optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    @Override
    public String toString() {
        return
                "keypath='" + keypath + '\'' +
                ", optionA='" + optionA + '\'' +
                ", optionB='" + optionB + '\'';
    }
}
