package fr.lesaffrefreres.rh.minibodet.model;

import javafx.beans.property.*;
import javafx.scene.paint.Color;

import java.util.Objects;

public interface DayLabel {

    public String getText();

    public Color getColor();

    @Override
    public String toString();

    public ReadOnlyProperty<String> textProperty();
}
