package fr.lesaffrefreres.rh.minibodet.model;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WorkWeekTest {

    @Test
    void getDay() {
        SimpleWorkWeek week = new SimpleWorkWeek(LocalDate.of(2022, 1, 1));

        LocalDate testDate = LocalDate.of(2022, 1, 1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));



        List<LocalDate> testList = testDate.datesUntil(testDate.plusWeeks(1)).toList();

        int i = 1;
        for(LocalDate cur : testList) {
            assertEquals(cur, week.getDay(DayOfWeek.of(i)).getDate());
            i++;
        }
    }
}