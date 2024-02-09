package emanondev.itemtag.activity.arguments;

public class TargetArgument {

    private String value;

    public TargetArgument(String info) {
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
