package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.*;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.time.DayOfWeek;

public class EmployeeViewController {

    @FXML
    private ComboBox<Employee> paramEmployeeListPicker;
    @FXML
    private TextField paramEmployeeFirstnameInput;
    @FXML
    private TextField paramEmployeeLastnameInput;
    @FXML
    private GridPane paramEmployeePlanningGrid;
    @FXML
    private TextField paramNewEmployeeFirstnameInput;
    @FXML
    private TextField paramNewEmployeeLastnameInput;

    @FXML
    public void onEmployeeListChange(ActionEvent event) {
        update();
    }

    @FXML
    public void onButtonEditEmployeeClick(ActionEvent event) {

        SQLEmployeeManager em = SQLEmployeeManager.getInstance();

        if(paramEmployeeListPicker.getValue() == null ||
            paramEmployeeFirstnameInput.getText().isBlank() ||
            paramEmployeeLastnameInput.getText().isBlank() ||
            em.employeeExist(paramEmployeeFirstnameInput.getText(), paramEmployeeLastnameInput.getText())
        ) {
            return;
        }

        paramEmployeeListPicker.getValue().setFirstName(paramEmployeeFirstnameInput.getText());
        paramEmployeeListPicker.getValue().setLastName(paramEmployeeLastnameInput.getText());
    }

    @FXML
    public void onCreateEmployeeButtonClick(ActionEvent event) {
        SQLEmployeeManager em = SQLEmployeeManager.getInstance();

        if(paramNewEmployeeFirstnameInput.getText().isBlank() ||
                paramNewEmployeeLastnameInput.getText().isBlank() ||
                em.employeeExist(paramNewEmployeeFirstnameInput.getText(), paramNewEmployeeLastnameInput.getText())
        ) {
            return;
        }

        Employee emp = em.createEmployee(paramNewEmployeeFirstnameInput.getText(), paramNewEmployeeLastnameInput.getText());
        paramEmployeeListPicker.getSelectionModel().select(emp);
    }

    public void onEmployeePlaningLabelChange(ActionEvent event) {
        int row = GridPane.getRowIndex((Node)event.getSource());
        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();

        DayLabel dl = ((ComboBox<DayLabel>)event.getSource()).getValue();

        paramEmployeeListPicker.getValue().setPlanningLabelId(DayOfWeek.of(row+1), dlm.getDayLabelIdByName(dl.getText()));
    }

    public void onHoursPlanningChange(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
        if(paramEmployeeListPicker.getValue() == null) {
            return;
        }

        for(int i = 1; i <= 7; i++) {
            Spinner<Integer> curTotal = (Spinner<Integer>) paramEmployeePlanningGrid.lookup("#paramEmployeeSpinnerT" + i);
            paramEmployeeListPicker.getValue().setPlanningTotalHours(DayOfWeek.of(i), curTotal.getValue());
            curTotal.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                    paramEmployeeListPicker.getValue().getPlanningNightHours(DayOfWeek.of(i)),
                    24,
                    paramEmployeeListPicker.getValue().getPlanningTotalHours(DayOfWeek.of(i))));

            Spinner<Integer> curNight = (Spinner<Integer>) paramEmployeePlanningGrid.lookup("#paramEmployeeSpinnerN" + i);
            paramEmployeeListPicker.getValue().setPlanningNightHours(DayOfWeek.of(i), curNight.getValue());
            curNight.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                    0,
                    paramEmployeeListPicker.getValue().getPlanningTotalHours(DayOfWeek.of(i)),
                    paramEmployeeListPicker.getValue().getPlanningNightHours(DayOfWeek.of(i))));
        }
    }

    public void update() {
        paramEmployeeFirstnameInput.setText(paramEmployeeListPicker.getValue().getFirstName());
        paramEmployeeLastnameInput.setText(paramEmployeeListPicker.getValue().getLastName());

        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();

        for(int i = 1; i <= 7; i++) {
            Spinner<Integer> curTotal = (Spinner<Integer>) paramEmployeePlanningGrid.lookup("#paramEmployeeSpinnerT" + i);
            curTotal.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                    paramEmployeeListPicker.getValue().getPlanningNightHours(DayOfWeek.of(i)),
                    24,
                    paramEmployeeListPicker.getValue().getPlanningTotalHours(DayOfWeek.of(i))));

            Spinner<Integer> curNight = (Spinner<Integer>) paramEmployeePlanningGrid.lookup("#paramEmployeeSpinnerN" + i);
            curNight.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                    0,
                    paramEmployeeListPicker.getValue().getPlanningTotalHours(DayOfWeek.of(i)),
                    paramEmployeeListPicker.getValue().getPlanningNightHours(DayOfWeek.of(i))));

            ComboBox<DayLabel> cbLabel = (ComboBox<DayLabel>) paramEmployeePlanningGrid.lookup("#paramEmployeeLabelList" + i);
            cbLabel.setItems(dlm.getLabelsObservableList());
            cbLabel.getSelectionModel().select(dlm.getDayLabelById(paramEmployeeListPicker.getValue().getPlanningLabelId(DayOfWeek.of(i))));
        }
    }
}
