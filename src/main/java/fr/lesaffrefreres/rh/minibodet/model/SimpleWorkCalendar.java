package fr.lesaffrefreres.rh.minibodet.model;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.*;

public class SimpleWorkCalendar implements Calendar<SimpleWorkDay>{

    private Map<LocalDate, SimpleWorkDay> daysMap;
    private int year;

    @Override
    public SimpleWorkDay getDay(LocalDate date) {
        Objects.requireNonNull(date);
        WorkDay res = daysMap.get(date);
        return daysMap.get(date);
    }

    @Override
    public int getYear() {
        return year;
    }

    public SimpleWorkCalendar(int y) {
        SimpleDayLabelManager dlm = SimpleDayLabelManager.getInstance();
        daysMap = new TreeMap<>();
        year = y;
        for(LocalDate cur = LocalDate.of(y, 1, 1); cur.isBefore(LocalDate.of(y+1, 1, 1)); cur = cur.plusWeeks(1)) {
            SimpleWorkWeek curWeek = new SimpleWorkWeek(cur);
            for(DayOfWeek dow : DayOfWeek.values()) {
                daysMap.put(curWeek.getDay(dow).getDate(), curWeek.getDay(dow));
            }
        }
    }
}
