package fr.lesaffrefreres.rh.minibodet.model;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

public class SimpleCalendar implements Calendar<SimpleDay>{

    private Map<LocalDate, SimpleDay> daysMap;

    private int year;

    @Override
    public SimpleDay getDay(LocalDate date) {
        Objects.requireNonNull(date);
        return daysMap.get(date);
    }

    @Override
    public int getYear() {
        return year;
    }

    public SimpleCalendar(int y) {
        DayLabelManager dlm = SQLDayLabelManager.getInstance();
        daysMap = new TreeMap<>();
        year = y;
        List<LocalDate> dayList = LocalDate.of(y, 1, 1).datesUntil(LocalDate.of(y+1, 1, 1)).toList();
        for(LocalDate cur : dayList) {
            daysMap.put(cur, new SimpleDay(cur, dlm.getUndefinedDayLabelId()));
        }
    }
}
