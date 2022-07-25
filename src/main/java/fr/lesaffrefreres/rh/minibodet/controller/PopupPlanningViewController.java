package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.DayLabel;
import fr.lesaffrefreres.rh.minibodet.model.DayLabelManager;
import fr.lesaffrefreres.rh.minibodet.model.Employee;
import fr.lesaffrefreres.rh.minibodet.model.SQLDayLabelManager;
import fr.lesaffrefreres.rh.minibodet.view.PopupPlanningView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.VBox;
import javafx.stage.Window;
import net.synedra.validatorfx.Check;
import net.synedra.validatorfx.TooltipWrapper;
import net.synedra.validatorfx.Validator;

import java.time.DayOfWeek;
import java.util.ArrayList;

/**
 * This class is used as a controller for the {@link PopupPlanningView} class.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see PopupPlanningView
 */
public class PopupPlanningViewController {

    @FXML
    private Button validationButton;
    @FXML
    private VBox root;
    private Employee employee;
    @FXML
    private ArrayList<Spinner<Double>> totalHoursSpinnerList;

    @FXML
    private ArrayList<Spinner<Double>> nightHoursSpinnerList;

    @FXML
    private ArrayList<ComboBox<DayLabel>> labelComboBoxList;

    /**
     * This method is used to set employee who's planning will be edited to the controller.
     * it must be called just after the view is created.
     * @param emp employee who's planning will be edited
     */
    public void setEmployee(Employee emp) {
        employee = emp;
        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();

        Validator validator = new Validator();
        Check check = validator.createCheck();
        check.withMethod(this::checkSpinner);

        for(int i = 0; i < 6; i++) {
            Spinner<Double> curTotalHoursSpinnerList = totalHoursSpinnerList.get(i);
            curTotalHoursSpinnerList.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(
                    0,
                    24,
                    employee.getPlanningTotalHours(DayOfWeek.of(i+1)),
                    0.25));
            curTotalHoursSpinnerList.focusedProperty().addListener((observable, oldValue, newValue) ->
                    Platform.runLater(() -> curTotalHoursSpinnerList.getEditor().selectAll()));

            Spinner<Double> curNightHoursSpinnerList = nightHoursSpinnerList.get(i);
            curNightHoursSpinnerList.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(
                    0,
                    24,
                    employee.getPlanningNightHours(DayOfWeek.of(i+1)),
                    0.25));
            curNightHoursSpinnerList.focusedProperty().addListener((observable, oldValue, newValue) ->
                    Platform.runLater(() -> curNightHoursSpinnerList.getEditor().selectAll()));

            check.dependsOn(curTotalHoursSpinnerList.getId(), curTotalHoursSpinnerList.valueProperty());
            check.dependsOn(curNightHoursSpinnerList.getId(), curNightHoursSpinnerList.valueProperty());

            labelComboBoxList.get(i).setItems(dlm.getLabelsObservableList());
            labelComboBoxList.get(i).getSelectionModel().select(dlm.getDayLabelById(employee.getPlanningLabelId(DayOfWeek.of(i+1))));
        }
        check.immediate();

        root.getChildren().add(root.getChildren().indexOf(validationButton), new TooltipWrapper<>(
                validationButton,
                validator.containsErrorsProperty(),
                Bindings.concat(validator.createStringBinding())
        ));
    }

    private void checkSpinner(Check.Context context) {
        for(int i = 0; i < 6; i++) {
            double totalHours = context.get(totalHoursSpinnerList.get(i).getId());
            double nightHours = context.get(nightHoursSpinnerList.get(i).getId());
            if(nightHours > totalHours) {
                context.error("Le nombre d'heures de nuit ne doit pas dépasser le nombre d'heures total");
            }

            if(!checkIncrement(totalHours) || !checkIncrement(nightHours)) {
                context.error("Le nombre d'heures doit avoir un pas de 0.25");
            }
        }
    }

    private boolean checkIncrement(double value) {
        return value % 0.25 == 0;
    }

    /**
     * This mathod is called when the user click the save button
     * it save the new planning of the employee.
     * @param event event of the click
     */
    @FXML
    private void onButtonValidatePlanningClicked(ActionEvent event) {
        if(labelComboBoxList.get(0).getValue() == null) {
            return;
        }

        DayLabelManager dlm = SQLDayLabelManager.getInstance();
        for(int i = 0; i < 6; i++) {
            if(totalHoursSpinnerList.get(i).getValue() < nightHoursSpinnerList.get(i).getValue()) {
                nightHoursSpinnerList.get(i).getValueFactory().setValue(totalHoursSpinnerList.get(i).getValue());
            }
            employee.setPlanningTotalHours(DayOfWeek.of(i+1), totalHoursSpinnerList.get(i).getValue());
            employee.setPlanningNightHours(DayOfWeek.of(i+1), nightHoursSpinnerList.get(i).getValue());
            employee.setPlanningLabelId(DayOfWeek.of(i+1), dlm.getDayLabelIdByName(labelComboBoxList.get(i).getValue().getText()));
        }

        Window window = ((Button)event.getSource()).getScene().getWindow();
        window.hide();
    }
}
