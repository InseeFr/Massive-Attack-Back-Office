package fr.insee.sabianedata.ws.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utilitary class to parse fixed and relative dates.
 * <p>
 * <b>Warning</b> : the parse method throws IllegalArgumentException, don't
 * forget to catch it
 */
public class DateParser {

    private DateParser() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    private static final String DMY_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private static final String DMY_REGEXP = "^\\d{2}\\/\\d{2}\\/\\d{4} \\d{2}:\\d{2}:\\d{2}$";
    private static final String RELATIVE_DATE_PATTERN = "^J(?<operator>[\\+\\-])(?<value>\\d+$)";
    private static final Pattern dmyPattern = Pattern.compile(DMY_REGEXP);
    private static final Pattern relativeDatePattern = Pattern.compile(RELATIVE_DATE_PATTERN);

    private static final String PATTERN_ERROR_MESSAGE = String.format(" is not parsable, right pattern is : %s",
            DMY_REGEXP);

    /**
     * @param input the string representing a fixed date
     * @return Long typed timestamp in UTC+00:00
     * @throws IllegalArgumentException if input doesn't respect expected format :
     *                                  "dd/MM/yyyy HH:mm:ss"
     */
    public static Long fixedDateParse(String input) throws IllegalArgumentException {
        if (isRelativeDateParsable(input)) {
            return relativeDateParse(input, new Date().getTime());
        }
        checkIsDmyParsable(input);
        return stringToLong(input);
    }

    /**
     * @param input     the string representing a date modification in days J-5 /
     *                  J+99
     * @param reference Long typed timestamp to wich the input change is to be
     *                  applied
     * @return Long typed timestamp in UTC+00:00 of updated reference date
     * @throws IllegalArgumentException if input doesn't respect expected format :
     *                                  "J([+-])([0-9]+)"
     */
    public static Long relativeDateParse(String input, Long reference) throws IllegalArgumentException {
        if (!isRelativeDateParsable(input))
            throw new IllegalArgumentException(input + " is not parsable, right pattern is : J([+-])(//d+)");
        return updateDays(input, longToLdt(reference));
    }

    public static void checkIsDmyParsable(String input) {

        if (!dmyPattern.matcher(input).matches()) {
            throw new IllegalArgumentException(input + PATTERN_ERROR_MESSAGE);
        }
    }

    public static boolean isRelativeDateParsable(String input) {
        return relativeDatePattern.matcher(input).find();
    }

    /**
     * Apply a date modificator (i.e.`J+2`) to a date and return the updated date
     *
     * @param input day modificator
     * @param ldt   date to update
     * @return updated date as a Long
     */
    public static Long updateDays(String input, LocalDateTime ldt) {
        Matcher matcher = relativeDatePattern.matcher(input);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(input + PATTERN_ERROR_MESSAGE);
        }
        String operator = matcher.group("operator");
        long value = Long.parseLong(matcher.group("value"));
        return ldtToLong(operator.equals("+") ? ldt.plusDays(value) : ldt.minusDays(value));
    }

    ///////////////////////////////////////////////////////
    // converting methods between String | Long | LocalDateTime

    public static Long stringToLong(String input) throws IllegalArgumentException {
        checkIsDmyParsable(input);
        return ldtToLong(stringToLdt(input, DMY_DATE_FORMAT));

    }

    public static LocalDateTime stringToLdt(String input, String dateFormat) throws IllegalArgumentException {
        checkIsDmyParsable(input);
        return LocalDateTime.from(formatterFromPattern(dateFormat).parse(input));
    }

    public static LocalDateTime longToLdt(Long date) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(date), ZoneOffset.UTC);
    }

    public static Long ldtToLong(LocalDateTime ldt) {
        return ldt.atZone(ZoneOffset.UTC).toInstant().toEpochMilli();
    }

    private static DateTimeFormatter formatterFromPattern(String regexp) {
        return DateTimeFormatter.ofPattern(regexp);
    }

}
