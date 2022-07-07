package fr.lesaffrefreres.rh.minibodet.model;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SimpleCalendarTest {

    @Test
    void getDay() {
        SimpleCalendar sc = new SimpleCalendar(2022);
        SimpleDayLabelManager dlm = SimpleDayLabelManager.getInstance();
        List<LocalDate> dateList = LocalDate.of(2022, 1, 1).datesUntil(LocalDate.of(2023, 1, 1)).toList();

        for(LocalDate cur : dateList) {
            assertEquals(cur, sc.getDay(cur).getDate());
            assertEquals(dlm.getDayLabelById(dlm.getUndefinedDayLabelId()), sc.getDay(cur).getLabelId());
        }

        long id = dlm.createDayLabel("test label", Color.ALICEBLUE);

        LocalDate testDate = LocalDate.of(2022, 5, 26);

        sc.getDay(testDate).setLabelId(id);

        assertEquals(dlm.getDayLabelById(id).getText(), dlm.getDayLabelById(sc.getDay(testDate).getLabelId()).getText());
        assertEquals(dlm.getDayLabelById(id).getColor(), dlm.getDayLabelById(sc.getDay(testDate).getLabelId()).getColor());

        dlm.removeDayLabelById(id);
    }
}