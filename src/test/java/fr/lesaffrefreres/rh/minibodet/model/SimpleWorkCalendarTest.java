package fr.lesaffrefreres.rh.minibodet.model;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimpleWorkCalendarTest {

    @Test
    void getDay() {
        SimpleWorkCalendar wc = new SimpleWorkCalendar(2022);

        List<LocalDate> dateList = LocalDate.of(2022, 1, 1).datesUntil(LocalDate.of(2023, 1, 1)).toList();

        for(LocalDate ld : dateList) {
            assertNotNull(wc.getDay(ld));
            assertNotNull(wc.getDay(ld).getWeek().getDay(DayOfWeek.MONDAY));
            assertNotNull(wc.getDay(ld).getWeek().getDay(DayOfWeek.SUNDAY));
        }
    }
}