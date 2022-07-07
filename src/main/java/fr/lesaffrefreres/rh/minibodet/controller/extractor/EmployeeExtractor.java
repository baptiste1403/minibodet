package fr.lesaffrefreres.rh.minibodet.controller.extractor;

import fr.lesaffrefreres.rh.minibodet.model.Employee;
import javafx.beans.Observable;
import javafx.util.Callback;

public class EmployeeExtractor implements Callback<Employee, Observable[]> {

    @Override
    public Observable[] call(Employee param) {
        return new Observable[] {param.FirstNameProperty(), param.LastNameProperty()};
    }
}
