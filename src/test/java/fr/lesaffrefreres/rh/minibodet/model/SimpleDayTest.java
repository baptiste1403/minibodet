package fr.lesaffrefreres.rh.minibodet.model;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class SimpleDayTest {

    @Test
    void getLabel() {
        SimpleDayLabelManager dlm = SimpleDayLabelManager.getInstance();
        long id = dlm.createDayLabel("label test", Color.ALICEBLUE);

        SimpleDay d = new SimpleDay(LocalDate.now(), id);

        DayLabel dl = dlm.getDayLabelById(d.getLabelId());

        assertEquals(dlm.getDayLabelById(id), dl);

        dlm.removeDayLabelById(id);
    }

    @Test
    void getDate() {
        SimpleDayLabelManager dlm = SimpleDayLabelManager.getInstance();

        SimpleDay d = new SimpleDay(LocalDate.of(2022, 1, 1), dlm.getUndefinedDayLabelId());

        assertEquals(LocalDate.of(2022, 1, 1), d.getDate());
    }

    @Test
    void setLabelId() {
        SimpleDayLabelManager dlm = SimpleDayLabelManager.getInstance();
        long id = dlm.createDayLabel("test label", Color.ALICEBLUE);

        SimpleDay d = new SimpleDay(LocalDate.now(), dlm.getUndefinedDayLabelId());

        d.setLabelId(id);

        DayLabel dl = dlm.getDayLabelById(id);

        assertEquals(dl, d.getLabelId());

        dlm.removeDayLabelById(id);
    }
}