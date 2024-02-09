package emanondev.itemtag.activity.arguments;

public class BooleanArgument extends Argument {
    private boolean value;

    public BooleanArgument(boolean info) {
        this.value = info;
    }


    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
