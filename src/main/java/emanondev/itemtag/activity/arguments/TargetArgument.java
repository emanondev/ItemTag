package emanondev.itemtag.activity.arguments;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TargetArgument {

    private String value;

    public TargetArgument(String info) {
        this.value = info;
    }

    @Override
    public String toString() {
        return value;
    }
}
