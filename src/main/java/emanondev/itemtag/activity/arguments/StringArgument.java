package emanondev.itemtag.activity.arguments;

public class StringArgument extends Argument {
    private String value;

    public StringArgument(String info) {
        this.value = info;
    }


    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }
}
