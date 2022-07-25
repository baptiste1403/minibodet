package fr.lesaffrefreres.rh.minibodet.controller.extractor;

import fr.lesaffrefreres.rh.minibodet.model.DayLabel;
import fr.lesaffrefreres.rh.minibodet.model.Employee;
import javafx.beans.Observable;
import javafx.util.Callback;

/**
 * This class is used as an extractor for the {@link Employee} class.
 * it si used to notify change to combo boxs containing employees.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 */
public class EmployeeExtractor implements Callback<Employee, Observable[]> {

    /**
     * {@inheritDoc}
     */
    @Override
    public Observable[] call(Employee param) {
        return new Observable[] {param.FirstNameProperty(), param.LastNameProperty()};
    }
}
