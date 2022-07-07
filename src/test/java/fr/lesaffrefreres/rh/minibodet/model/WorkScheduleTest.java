package fr.lesaffrefreres.rh.minibodet.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WorkScheduleTest {

    @Test
    void getNightHours() {
        SimpleWorkSchedule ws = new SimpleWorkSchedule(10, 5);
        assertEquals(5, ws.getNightHours());
    }

    @Test
    void getTotalHours() {
        SimpleWorkSchedule ws = new SimpleWorkSchedule(10, 5);
        assertEquals(10, ws.getTotalHours());
    }

    @Test
    void setNightHours() {
        SimpleWorkSchedule ws = new SimpleWorkSchedule();

        assertThrows(IllegalStateException.class, () -> {ws.setNightHours(5);});

        SimpleWorkSchedule ws2 = new SimpleWorkSchedule(5, 0);

        ws2.setNightHours(2);

        assertEquals(2, ws2.getNightHours());
        assertThrows(IllegalStateException.class, () -> {ws.setNightHours(-10);});
    }

    @Test
    void setTotalHours() {
        SimpleWorkSchedule ws = new SimpleWorkSchedule();

        ws.setTotalHours(5);

        assertEquals(5, ws.getTotalHours());

        assertThrows(IllegalStateException.class, () -> {ws.setTotalHours(-10);});

        SimpleWorkSchedule ws2 = new SimpleWorkSchedule(10, 10);

        assertThrows(IllegalStateException.class, () -> {ws2.setTotalHours(8);});
    }

    @Test
    void WorkScheduleConstructor() {

        assertThrows(IllegalArgumentException.class, () -> {
            SimpleWorkSchedule ws = new SimpleWorkSchedule(-5, -3);});
        assertThrows(IllegalArgumentException.class, () -> {
            SimpleWorkSchedule ws = new SimpleWorkSchedule(5, 10);});
    }
}