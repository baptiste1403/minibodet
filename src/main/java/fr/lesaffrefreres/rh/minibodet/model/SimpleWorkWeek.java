package fr.lesaffrefreres.rh.minibodet.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumMap;

public class SimpleWorkWeek implements WorkWeek{

    private EnumMap<DayOfWeek, SimpleWorkDay> days;

    public SimpleWorkWeek(LocalDate ld) {
        days = new EnumMap<>(DayOfWeek.class);
        LocalDate date = ld.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        for (DayOfWeek cur : DayOfWeek.values()) {
            days.put(cur, new SimpleWorkDay(date, SimpleDayLabelManager.getInstance().getUndefinedDayLabelId(), this));
            date = date.plusDays(1);
        }
    }

    public SimpleWorkDay getDay(DayOfWeek dow) {
        return days.get(dow);
    }

    public int getNbHours() {
        int res = 0;
        for(WorkDay cur : days.values()) {
            res += cur.getSchedule().getTotalHours();
        }
        return res;
    }
}
