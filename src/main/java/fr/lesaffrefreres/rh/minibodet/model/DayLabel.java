package fr.lesaffrefreres.rh.minibodet.model;

import javafx.scene.paint.Color;

import java.util.Objects;

public class DayLabel {
    protected String text;
    protected Color color;

    public DayLabel(String txt, Color c) {
        Objects.requireNonNull(txt);
        Objects.requireNonNull(c);
        text = txt;
        color = c;
    }

    public String getText() {
        return text;
    }

    public Color getColor() {
        return color;
    }
}
