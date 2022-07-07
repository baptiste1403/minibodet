package fr.lesaffrefreres.rh.minibodet.view;

import fr.lesaffrefreres.rh.minibodet.controller.EmployeeViewController;
import fr.lesaffrefreres.rh.minibodet.model.DayLabel;
import fr.lesaffrefreres.rh.minibodet.model.Employee;
import fr.lesaffrefreres.rh.minibodet.model.SQLDayLabelManager;
import fr.lesaffrefreres.rh.minibodet.model.SQLEmployeeManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.DayOfWeek;
import java.util.Locale;
import java.util.ResourceBundle;

public class EmployeeView extends VBox implements ChangeListener<Scene> {

    private EmployeeViewController controller;

    public EmployeeView() {
        ResourceBundle bundle = ResourceBundle.getBundle("/strings", new Locale("fr", "FR"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/employee-view.fxml"), bundle);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        controller = fxmlLoader.getController();

        sceneProperty().addListener(this);
    }

    @Override
    public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {

        SQLEmployeeManager em = SQLEmployeeManager.getInstance();

        ComboBox<Employee> cb = (ComboBox<Employee>) this.lookup("#paramEmployeeListPicker");

        cb.setItems(em.getEmployeesList());

        if(!cb.getItems().isEmpty()) {
            cb.getSelectionModel().selectFirst();

            TextField tffn = (TextField) this.lookup("#paramEmployeeFirstnameInput");
            tffn.setText(cb.getValue().getFirstName());

            TextField tfln = (TextField) this.lookup("#paramEmployeeLastnameInput");
            tfln.setText(cb.getValue().getLastName());

            GridPane gp = (GridPane) this.lookup("#paramEmployeePlanningGrid");

            SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();

            for(int i = 1; i <= 7; i++) {
                Spinner<Integer> curTotal = (Spinner<Integer>) gp.lookup("#paramEmployeeSpinnerT" + i);
                int test = cb.getValue().getPlanningNightHours(DayOfWeek.of(i));
                curTotal.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        cb.getValue().getPlanningNightHours(DayOfWeek.of(i)),
                        24,
                        cb.getValue().getPlanningTotalHours(DayOfWeek.of(i))));
                curTotal.valueProperty().addListener(controller::onHoursPlanningChange);

                Spinner<Integer> curNight = (Spinner<Integer>) gp.lookup("#paramEmployeeSpinnerN" + i);
                curNight.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        0,
                        cb.getValue().getPlanningTotalHours(DayOfWeek.of(i)),
                        cb.getValue().getPlanningNightHours(DayOfWeek.of(i))));

                curNight.valueProperty().addListener(controller::onHoursPlanningChange);

                ComboBox<DayLabel> cbLabel = (ComboBox<DayLabel>) gp.lookup("#paramEmployeeLabelList" + i);

                cbLabel.setItems(dlm.getLabelsObservableList());
                cbLabel.getSelectionModel().select(dlm.getDayLabelById(cb.getValue().getPlanningLabelId(DayOfWeek.of(i))));
            }
        } else {

            GridPane gp = (GridPane) this.lookup("#paramEmployeePlanningGrid");

            for(int i = 1; i <= 7; i++) {
                Spinner<Integer> curTotal = (Spinner<Integer>) gp.lookup("#paramEmployeeSpinnerT" + i);
                curTotal.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,0,0));
                curTotal.valueProperty().addListener(controller::onHoursPlanningChange);

                Spinner<Integer> curNight = (Spinner<Integer>) gp.lookup("#paramEmployeeSpinnerN" + i);
                curNight.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0,0,0));
                curNight.valueProperty().addListener(controller::onHoursPlanningChange);
            }
        }
    }
}
