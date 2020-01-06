package Chapter11;

import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@ThreadSafe
public class BetterAttributeStore {
    private final Map<String, String> attributes;

    public BetterAttributeStore() {
        this.attributes = new ConcurrentHashMap<>();
    }

    public boolean userLocationMatches(String name, String regexp) {
        String key = "users." + name + ".location";
        String location = attributes.get(key);

        if (location == null) {
            return false;
        } else {
            return Pattern.matches(regexp, location);
        }
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }
}
