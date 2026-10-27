package emanondev.itemtag.activity.arguments;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class StringArgument extends Argument {
    private String value;

    public StringArgument(String info) {
        this.value = info;
    }

    @Override
    public String toString() {
        return value;
    }
}
