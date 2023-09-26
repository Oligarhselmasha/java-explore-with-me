package variables;

import java.time.LocalDateTime;

public class Constants {
    public static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    public static final LocalDateTime MIN_DATE = LocalDateTime.of(1900, 1, 1, 0, 0, 0);
    public static final LocalDateTime MAX_DATE = LocalDateTime.of(2100, 1, 1, 0, 0, 0);

}
