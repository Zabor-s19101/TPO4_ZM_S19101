/**
 * @author Zaborowski Mateusz S19101
 */

package zad1;


import java.text.DecimalFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;

public class Time {
    public static String passed(String from, String to) {
        StringBuilder result = new StringBuilder();
        try {
            String tPat = "d MMMM yyyy (EEEE) 'godz.' HH:mm";
            String dPat = "d MMMM yyyy (EEEE)";
            if (from.contains("T") && to.contains("T")) {
                LocalDateTime dateTimeFrom = LocalDateTime.parse(from);
                LocalDateTime dateTimeTo = LocalDateTime.parse(to);
                long daysBetween = ChronoUnit.DAYS.between(dateTimeFrom.toLocalDate(), dateTimeTo.toLocalDate());
                long hoursBetween = ChronoUnit.HOURS.between(ZonedDateTime.of(dateTimeFrom, ZoneId.of("Europe/Warsaw")), ZonedDateTime.of(dateTimeTo, ZoneId.of("Europe/Warsaw")));
                long minutesBetween = ChronoUnit.MINUTES.between(ZonedDateTime.of(dateTimeFrom, ZoneId.of("Europe/Warsaw")), ZonedDateTime.of(dateTimeTo, ZoneId.of("Europe/Warsaw")));
                Period p = Period.between(dateTimeFrom.toLocalDate(), dateTimeTo.toLocalDate());
                long yBetween = p.getYears();
                long mBetween = p.getMonths();
                long dBetween = p.getDays();
                result.append("Od ")
                        .append(dateTimeFrom.format(DateTimeFormatter.ofPattern(tPat)))
                        .append(" do ")
                        .append(dateTimeTo.format(DateTimeFormatter.ofPattern(tPat))).append("\n - mija: ")
                        .append(daysBetween)
                        .append(daysBetween == 1 ? " dzień, tygodni " : " dni, tygodni ")
                        .append(new DecimalFormat("#.##").format(daysBetween / 7.0).replace(",", ".")).append("\n - godzin: ")
                        .append(hoursBetween)
                        .append(", minut: ")
                        .append(minutesBetween).append("\n");
                kaledarzowo(result, yBetween, mBetween, dBetween);
            } else {
                LocalDate dateFrom = LocalDate.parse(from);
                LocalDate dateTo = LocalDate.parse(to);
                long daysBetween = ChronoUnit.DAYS.between(dateFrom, dateTo);
                Period p = Period.between(dateFrom, dateTo);
                long yBetween = p.getYears();
                long mBetween = p.getMonths();
                long dBetween = p.getDays();
                result.append("Od ")
                        .append(dateFrom.format(DateTimeFormatter.ofPattern(dPat)))
                        .append(" do ")
                        .append(dateTo.format(DateTimeFormatter.ofPattern(dPat))).append("\n - mija: ")
                        .append(daysBetween)
                        .append(daysBetween == 1 ? " dzień, tygodni " : " dni, tygodni ")
                        .append(new DecimalFormat("#.##").format(daysBetween / 7.0).replace(",", ".")).append("\n");
                kaledarzowo(result, yBetween, mBetween, dBetween);
            }
        } catch (DateTimeParseException e) {
            return "*** " + e.toString();
        }
        return result.toString();
    }

    private static void kaledarzowo(StringBuilder result, long yBetween, long mBetween, long dBetween) {
        if (yBetween > 0 || mBetween > 0 || dBetween > 0) {
            result.append(" - kalendarzowo: ");
            String y = yBetween == 1 ? "rok" : yBetween > 1 && yBetween < 5 ? "lata" : "lat";
            String m = mBetween == 1 ? "miesiąc" : mBetween > 1 && mBetween < 5 ? "miesiące" : "miesięcy";
            String d = dBetween == 1 ? "dzień" : "dni";
            if (yBetween > 0 && mBetween > 0 && dBetween > 0) {
                result.append(yBetween).append(" ").append(y).append(", ")
                        .append(mBetween).append(" ").append(m).append(", ")
                        .append(dBetween).append(" ").append(d);
            } else if (yBetween > 0 && mBetween > 0) {
                result.append(yBetween).append(" ").append(y).append(", ")
                        .append(mBetween).append(" ").append(m);
            } else if (mBetween > 0 && dBetween > 0) {
                result.append(mBetween).append(" ").append(m).append(", ")
                        .append(dBetween).append(" ").append(d);
            } else if (yBetween > 0 && dBetween > 0) {
                result.append(yBetween).append(" ").append(y).append(", ")
                        .append(dBetween).append(" ").append(d);
            } else if (yBetween > 0) {
                result.append(yBetween).append(" ").append(y);
            } else if (mBetween > 0) {
                result.append(mBetween).append(" ").append(m);
            } else {
                result.append(dBetween).append(" ").append(d);
            }
        }
    }
}
