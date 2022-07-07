package fr.lesaffrefreres.rh.minibodet.model;

import javafx.scene.paint.Color;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DayLabelTest {

    @Test
    void getText() {
        DayLabel label = new DayLabel("text test", Color.ALICEBLUE);
        assertEquals("text test", label.getText());
    }

    @Test
    void getColor() {
        DayLabel label = new DayLabel("text", Color.ALICEBLUE);
        assertEquals(Color.ALICEBLUE, label.getColor());
    }
}