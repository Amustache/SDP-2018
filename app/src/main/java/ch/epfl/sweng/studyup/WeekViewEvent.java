package ch.epfl.sweng.studyup;

import java.util.Calendar;

/**
 * Created by Raquib-ul-Alam Kanak on 7/21/2014.
 * Website: http://april-shower.com
 */
public class WeekViewEvent {
    private long id;
    private Calendar startTime;
    private Calendar endTime;
    private String name;
    private String location;
    private int color;
    private boolean allDay;

    public WeekViewEvent(){

    }

    /**
     * Initializes the event for week view.
     * @param id The id of the event.
     * @param name Name of the event.
     * @param startYear Year when the event starts.
     * @param startMonth Month when the event starts.
     * @param startDay Day when the event starts.
     * @param startHour Hour (in 24-hour format) when the event starts.
     * @param startMinute Minute when the event starts.
     * @param endYear Year when the event ends.
     * @param endMonth Month when the event ends.
     * @param endDay Day when the event ends.
     * @param endHour Hour (in 24-hour format) when the event ends.
     * @param endMinute Minute when the event ends.
     */
    public WeekViewEvent(long id, String name, int startYear, int startMonth, int startDay, int startHour, int startMinute, int endYear, int endMonth, int endDay, int endHour, int endMinute) {
        this.id = id;

        this.startTime = Calendar.getInstance();
        this.startTime.set(Calendar.YEAR, startYear);
        this.startTime.set(Calendar.MONTH, startMonth-1);
        this.startTime.set(Calendar.DAY_OF_MONTH, startDay);
        this.startTime.set(Calendar.HOUR_OF_DAY, startHour);
        this.startTime.set(Calendar.MINUTE, startMinute);

        this.endTime = Calendar.getInstance();
        this.endTime.set(Calendar.YEAR, endYear);
        this.endTime.set(Calendar.MONTH, endMonth-1);
        this.endTime.set(Calendar.DAY_OF_MONTH, endDay);
        this.endTime.set(Calendar.HOUR_OF_DAY, endHour);
        this.endTime.set(Calendar.MINUTE, endMinute);

        this.name = name;
    }

    /**
     * Initializes the event for week view.
     * @param id The id of the event.
     * @param name Name of the event.
     * @param location The location of the event.
     * @param startTime The time when the event starts.
     * @param endTime The time when the event ends.
     * @param allDay Is the event an all day event.
     */
    public WeekViewEvent(long id, String name, String location, Calendar startTime, Calendar endTime, boolean allDay) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.allDay = allDay;
    }

    /**
     * Initializes the event for week view.
     * @param id The id of the event.
     * @param name Name of the event.
     * @param location The location of the event.
     * @param startTime The time when the event starts.
     * @param endTime The time when the event ends.
     */
    public WeekViewEvent(long id, String name, String location, Calendar startTime, Calendar endTime) {
        this(id, name, location, startTime, endTime, false);
    }

    /**
     * Initializes the event for week view.
     * @param id The id of the event.
     * @param name Name of the event.
     * @param startTime The time when the event starts.
     * @param endTime The time when the event ends.
     */
    public WeekViewEvent(long id, String name, Calendar startTime, Calendar endTime) {
        this(id, name, null, startTime, endTime);
    }


    public Calendar getStartTime() {
        return startTime;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public boolean isAllDay() {
        return allDay;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        WeekViewEvent that = (WeekViewEvent) o;

        return id == that.id;

    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}