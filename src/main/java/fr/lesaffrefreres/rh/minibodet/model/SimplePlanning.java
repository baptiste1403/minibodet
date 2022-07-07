package fr.lesaffrefreres.rh.minibodet.model;

import java.time.DayOfWeek;
import java.util.EnumMap;

public class SimplePlanning implements Planning {
    private EnumMap<DayOfWeek, SimpleWorkSchedule> scheduleWeek;
    private EnumMap<DayOfWeek, Long> labelIdWeek;

    public SimplePlanning() {
        scheduleWeek = new EnumMap<DayOfWeek, SimpleWorkSchedule>(DayOfWeek.class);
        labelIdWeek = new EnumMap<DayOfWeek, Long>(DayOfWeek.class);
        for(DayOfWeek cur : DayOfWeek.values()) {
            scheduleWeek.put(cur, new SimpleWorkSchedule());
            labelIdWeek.put(cur, SimpleDayLabelManager.getInstance().getUndefinedDayLabelId());
        }
    }

    public SimpleWorkSchedule getSchedule(DayOfWeek dow) {
        return scheduleWeek.get(dow);
    }

    public long getDayLabelId(DayOfWeek dow) {
        return labelIdWeek.get(dow);
    }

    public void setDayLabel(DayOfWeek dow, long id) {
        if(!SimpleDayLabelManager.getInstance().labelExist(id)) {
            throw new IllegalArgumentException("label with id " + id + " do not exist");
        }

        labelIdWeek.put(dow, id);
    }
}
