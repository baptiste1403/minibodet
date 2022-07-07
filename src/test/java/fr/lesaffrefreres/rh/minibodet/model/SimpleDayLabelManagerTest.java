package fr.lesaffrefreres.rh.minibodet.model;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SimpleDayLabelManagerTest {

    @Test
    void getUndefinedDayLabelId() {
        SimpleDayLabelManager dlm = SimpleDayLabelManager.getInstance();
        long id = dlm.getUndefinedDayLabelId();
        assertThrows(IllegalArgumentException.class, () -> dlm.removeDayLabelById(id));
    }

    @Test
    void getInstance() {
        SimpleDayLabelManager dlm1 = SimpleDayLabelManager.getInstance();
        SimpleDayLabelManager dlm2 = SimpleDayLabelManager.getInstance();
        assertEquals(dlm1, dlm2);
    }

    @Test
    void createDayLabel() {
        SimpleDayLabelManager dlm = SimpleDayLabelManager.getInstance();
        long id = dlm.createDayLabel("test", Color.ALICEBLUE);

        DayLabel dl = dlm.getDayLabelById(id);

        assertEquals(dl.getColor(), dlm.getDayLabelById(id).getColor());
        assertEquals(dl.getText(), dlm.getDayLabelById(id).getText());

        dlm.removeDayLabelById(id);
    }

    @Test
    void getDayLabelIdByName() {
        SimpleDayLabelManager dlm = SimpleDayLabelManager.getInstance();
        long id = dlm.createDayLabel("label test", Color.ALICEBLUE);
        long id2 = dlm.getDayLabelIdByName("label test");

        assertEquals(id, id2);

        dlm.removeDayLabelById(id);
    }

    @Test
    void getDayLabelById() {
        SimpleDayLabelManager dlm = SimpleDayLabelManager.getInstance();
        long id = dlm.createDayLabel("label test", Color.ALICEBLUE);

        DayLabel dl = dlm.getDayLabelById(id);

        assertEquals("label test", dl.getText());
        assertEquals(Color.ALICEBLUE, dl.getColor());

        dlm.removeDayLabelById(id);
    }

    @Test
    void removeDayLabelById() {
        SimpleDayLabelManager dlm = SimpleDayLabelManager.getInstance();

        long id = dlm.createDayLabel("label test", Color.ALICEBLUE);

        dlm.removeDayLabelById(id);

        assertThrows(IllegalArgumentException.class, () -> {dlm.getDayLabelById(id);});
    }

    @Test
    void labelExist() {
        SimpleDayLabelManager dlm = SimpleDayLabelManager.getInstance();
        long id = dlm.createDayLabel("label test", Color.ALICEBLUE);

        assertTrue(dlm.labelExist(id));

        dlm.removeDayLabelById(id);

        assertFalse(dlm.labelExist(id));
    }
}