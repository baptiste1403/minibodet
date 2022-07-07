package fr.lesaffrefreres.rh.minibodet.model;

import java.time.DayOfWeek;

public interface Employee {

    public void setLastName(String ln);

    public void setFirstName(String fn);

    public String getLastName();

    public String getFirstName();

    public void setCalendar(int year);

    public <T extends WorkDay> Calendar<T> getCalendar();

    public void setPlanningNightHours(DayOfWeek dow, int nh);

    public void setPlanningTotalHours(DayOfWeek dow, int th);

    public void setPlanningLabelId(DayOfWeek dow, long id);

    public int getPlanningNightHours(DayOfWeek dow);

    public int getPlanningTotalHours(DayOfWeek dow);

    public long getPlanningLabelId(DayOfWeek dow);
}
