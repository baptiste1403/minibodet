package fr.lesaffrefreres.rh.minibodet.model;

import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class WorkDayTest {

    @Test
    void setComment() {
        SimpleWorkWeek week = new SimpleWorkWeek(LocalDate.now());

        WorkDay wd = week.getDay(DayOfWeek.MONDAY);

        wd.setComment("this is a test comment !!");


        assertEquals("this is a test comment !!", wd.getComment());
    }
}