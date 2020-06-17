package poc;

public class Conflict {
    private String keypath;
    private String optionA;
    private String optionB;

    public Conflict(String keypath, String optionA, String optionB) {
        this.keypath = keypath;
        this.optionA = optionA;
        this.optionB = optionB;
    }

    public String getKeypath() {
        return keypath;
    }

    public void setKeypath(String keypath) {
        this.keypath = keypath;
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
