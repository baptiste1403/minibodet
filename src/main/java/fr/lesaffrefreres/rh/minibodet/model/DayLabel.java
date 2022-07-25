package fr.lesaffrefreres.rh.minibodet.model;

import javafx.beans.property.*;
import javafx.scene.paint.Color;

import java.util.Objects;

/**
 * This Interface represents a day's label with a text and a color. For example "work" or "holiday" in blue or red.
 *
 * DayLabel are usually produced by a DayLabelManager implementation.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 * @see DayLabelManager
 */
public interface DayLabel {

    /**
     * Return the label text.
     * @return the label text.
     */
    public String getText();

    /**
     * Return the label color.
     * @return the label color.
     */
    public Color getColor();


    @Override
    public String toString();

    /**
     * Return a readOnly property containing the label text.
     * It's used in DayLabel implementation to track the label text changes.
     * @return
     */
    public ReadOnlyProperty<String> textProperty();
}
