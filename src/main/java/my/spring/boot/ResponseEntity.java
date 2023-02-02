package my.spring.boot;

import java.util.Map;

public class ResponseEntity<T> {
    public static <T> ResponseEntity<T> ok(T t) {
        return null;
    }

    public static ResponseEntity<Map<String, Boolean>> ok(Map<String, Boolean> response) {
        return null;
    }
}
