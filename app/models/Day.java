package models;

public enum Day {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    public static Day parseDay(String day) {

        switch (day.toLowerCase()) {
            case "monday":
                return Day.MONDAY;
            case "tuesday":
                return Day.TUESDAY;
            case "wednesday":
                return Day.WEDNESDAY;
            case "thursday":
                return Day.THURSDAY;
            case "friday":
                return Day.FRIDAY;
            case "saturday":
                return Day.SATURDAY;
            case "sunday":
                return Day.SUNDAY;
            default:
                return null;
        }
    }
}
