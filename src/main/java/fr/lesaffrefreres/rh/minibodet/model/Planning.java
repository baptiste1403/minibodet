package fr.lesaffrefreres.rh.minibodet.model;

import java.time.DayOfWeek;

public interface Planning {
    public WorkSchedule getSchedule(DayOfWeek dow);

    public long getDayLabelId(DayOfWeek dow);

    public void setDayLabel(DayOfWeek dow, long id);
}
