package emanondev.itemtag.activity.arguments;

import lombok.Setter;

@Setter
public class BooleanArgument extends Argument {
    private boolean value;

    public BooleanArgument(boolean info) {
        this.value = info;
    }


    public boolean getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
