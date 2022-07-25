package fr.lesaffrefreres.rh.minibodet.controller.extractor;

import fr.lesaffrefreres.rh.minibodet.model.DayLabel;
import fr.lesaffrefreres.rh.minibodet.model.SQLDayLabel;
import fr.lesaffrefreres.rh.minibodet.model.SQLDayLabelManager;
import javafx.beans.Observable;
import javafx.util.Callback;

/**
 * This class is used as an extractor for the {@link DayLabel} class.
 * it si used to notify change to combo boxs containing day labels.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 */
public class DayLabelExtractor implements Callback<DayLabel, Observable[]> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable[] call(DayLabel param) {
        return new Observable[] {param.textProperty()};
    }
}
