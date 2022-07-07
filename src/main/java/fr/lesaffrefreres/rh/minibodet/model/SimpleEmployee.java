package fr.lesaffrefreres.rh.minibodet.model;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class SimpleEmployee implements Employee {

    private String lastName;
    private String firstName;
    private final SimplePlanning planning;
    private SimpleWorkCalendar calendar;

    public SimpleEmployee(String ln, String fn) {
        lastName = ln;
        firstName = fn;
        planning = new SimplePlanning();
        calendar = null;
    }

    public void setLastName(String ln) {
        lastName = ln;
    }

    public void setFirstName(String fn) {
        firstName = fn;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setCalendar(int year) {
        calendar = new SimpleWorkCalendar(year);

        updateCalendar();
    }

    public Calendar<SimpleWorkDay> getCalendar() {
        return calendar;
    }

    private void updateCalendar() {
        for(LocalDate cur = LocalDate.of(calendar.getYear(), 1, 1); cur.isBefore(LocalDate.of(calendar.getYear()+1, 1, 1)); cur = cur.plusWeeks(1)) {
            WorkWeek curWeek = calendar.getDay(cur).getWeek();
            for(DayOfWeek dow : DayOfWeek.values()) {
                updateDay(dow, curWeek);
            }
        }
    }

    private void updateCalendarAtDay(DayOfWeek dow) {
        for(LocalDate cur = LocalDate.of(calendar.getYear(), 1, 1); cur.isBefore(LocalDate.of(calendar.getYear()+1, 1, 1)); cur = cur.plusWeeks(1)) {
            WorkWeek curWeek = calendar.getDay(cur).getWeek();
            updateDay(dow, curWeek);
        }
    }

    private void updateDay(DayOfWeek dow, WorkWeek curWeek) {
        calendar.getDay(curWeek.getDay(dow).getDate()).setLabelId(planning.getDayLabelId(dow));
        if(calendar.getDay(curWeek.getDay(dow).getDate()).getSchedule().isNormalSchedule()) {
            calendar.getDay(curWeek.getDay(dow).getDate()).getSchedule().setTotalHours(planning.getSchedule(dow).getTotalHours());
            calendar.getDay(curWeek.getDay(dow).getDate()).getSchedule().setNightHours(planning.getSchedule(dow).getNightHours());
        }
    }

    public void setPlanningNightHours(DayOfWeek dow, int nh) {
        planning.getSchedule(dow).setNightHours(nh);
        updateCalendarAtDay(dow);
    }

    public void setPlanningTotalHours(DayOfWeek dow, int th) {
        planning.getSchedule(dow).setTotalHours(th);
        updateCalendarAtDay(dow);
    }

    public void setPlanningLabelId(DayOfWeek dow, long id) {
        planning.setDayLabel(dow, id);
        updateCalendarAtDay(dow);
    }

    public int getPlanningNightHours(DayOfWeek dow) {
        return planning.getSchedule(dow).getNightHours();
    }

    public int getPlanningTotalHours(DayOfWeek dow) {
        return planning.getSchedule(dow).getTotalHours();
    }

    public long getPlanningLabelId(DayOfWeek dow) {
        return planning.getDayLabelId(dow);
    }

    @Override
    public String toString() {
        return firstName + " " + lastName;
    }
}
