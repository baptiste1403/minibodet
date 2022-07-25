package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.*;
import fr.lesaffrefreres.rh.minibodet.view.EmployeeArchiveView;
import fr.lesaffrefreres.rh.minibodet.view.EmployeeView;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import net.synedra.validatorfx.Check;
import net.synedra.validatorfx.TooltipWrapper;
import net.synedra.validatorfx.Validator;

import java.time.DayOfWeek;
import java.util.ArrayList;

/**
 * This class is used as the controller for the {@link EmployeeView} class.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see EmployeeView
 */
public class EmployeeViewController {

    @FXML
    private Button submitDeleteButton;
    @FXML
    private ComboBox<Employee> deleteEmployeeListPicker;
    @FXML
    private HBox submitNewEmployeeButtonParent;
    @FXML
    private Button submitNewEmployeeButton;
    @FXML
    private HBox submitChangeNameButtonParent;
    @FXML
    private Button submitChangeNameButton;
    @FXML
    private HBox submitPlanningButtonParent;
    @FXML
    private Button submitPlanningButton;
    @FXML
    private ComboBox<Employee> paramEmployeeListPicker;
    @FXML
    private TextField paramEmployeeFirstnameInput;
    @FXML
    private TextField paramEmployeeLastnameInput;
    @FXML
    private TextField paramNewEmployeeFirstnameInput;
    @FXML
    private TextField paramNewEmployeeLastnameInput;
    @FXML
    private ArrayList<Spinner<Double>> totalHoursSpinnerList;
    @FXML
    private ArrayList<Spinner<Double>> nightHoursSpinnerList;
    @FXML
    private ArrayList<ComboBox<DayLabel>> labelsComboBoxs;

    /**
     * This method is called by the FXMl loader when the view is loaded.
     * It initializes the view and setup the validators. (used to validate formulary inputs)
     */
    @FXML
    private void initialize() {
        SQLEmployeeManager em = SQLEmployeeManager.getInstance();
        paramEmployeeListPicker.setItems(em.getEmployeesList());
        deleteEmployeeListPicker.setItems(em.getEmployeesList());
        submitDeleteButton.setDisable(true);
        if(!paramEmployeeListPicker.getItems().isEmpty()) {
            paramEmployeeListPicker.getSelectionModel().selectFirst();
            paramEmployeeFirstnameInput.setText(paramEmployeeListPicker.getValue().getFirstName());
            paramEmployeeLastnameInput.setText(paramEmployeeListPicker.getValue().getLastName());
            initPlanningUI();
        } else {
            for(int i = 0; i < 6; i++) {
                Spinner<Double> curTotalHoursSpinner = totalHoursSpinnerList.get(i);
                curTotalHoursSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 24.0, 0.0, 0.25));
                curTotalHoursSpinner.focusedProperty().addListener((observable, oldValue, newValue) ->
                        Platform.runLater(() -> curTotalHoursSpinner.getEditor().selectAll()));

                Spinner<Double> curNightHoursSpinner = nightHoursSpinnerList.get(i);
                curNightHoursSpinner.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 24.0, 0.0, 0.25));
                curNightHoursSpinner.focusedProperty().addListener((observable, oldValue, newValue) ->
                        Platform.runLater(() -> curNightHoursSpinner.getEditor().selectAll()));

                labelsComboBoxs.get(i).setDisable(true);
                totalHoursSpinnerList.get(i).setDisable(true);
                nightHoursSpinnerList.get(i).setDisable(true);
            }
            labelsComboBoxs.get(6).setDisable(true);
        }

        Validator validatorChangeName = new Validator();
        Check checkChangeName = validatorChangeName.createCheck();
        checkChangeName.withMethod(this::checkChangeName)
                .dependsOn(paramEmployeeFirstnameInput.getId(), paramEmployeeFirstnameInput.textProperty())
                .dependsOn(paramEmployeeLastnameInput.getId(), paramEmployeeLastnameInput.textProperty())
                .immediate();

        submitChangeNameButtonParent.getChildren().add(submitChangeNameButtonParent.getChildren().indexOf(submitChangeNameButton), new TooltipWrapper<Button>(
                submitChangeNameButton,
                validatorChangeName.containsErrorsProperty(),
                Bindings.concat(validatorChangeName.createStringBinding())
        ));

        Validator validatorNewEmployee = new Validator();
        Check checkNewEmployee = validatorNewEmployee.createCheck();
        checkNewEmployee.withMethod(this::checkNewEmployee)
                .dependsOn(paramNewEmployeeFirstnameInput.getId(), paramNewEmployeeFirstnameInput.textProperty())
                .dependsOn(paramNewEmployeeLastnameInput.getId(), paramNewEmployeeLastnameInput.textProperty())
                .immediate();

        submitNewEmployeeButtonParent.getChildren().add(submitNewEmployeeButtonParent.getChildren().indexOf(submitNewEmployeeButton), new TooltipWrapper<Button>(
                submitNewEmployeeButton,
                validatorNewEmployee.containsErrorsProperty(),
                Bindings.concat(validatorNewEmployee.createStringBinding())
        ));

        Validator validatorSpinner = new Validator();
        Check checkSpinner = validatorSpinner.createCheck();
        checkSpinner.withMethod(this::checkSpinner);

        for(int i = 0; i < 6; i++) {
            Spinner<Double> curTotal = totalHoursSpinnerList.get(i);
                    checkSpinner.dependsOn("T" + i, curTotal.valueProperty());
            Spinner<Double> curNight = nightHoursSpinnerList.get(i);
                    checkSpinner.dependsOn("N" + i, curNight.valueProperty());
        }
        checkSpinner.immediate();

        submitPlanningButtonParent.getChildren().add(submitPlanningButtonParent.getChildren().indexOf(submitPlanningButton), new TooltipWrapper<Button>(
                submitPlanningButton,
                validatorSpinner.containsErrorsProperty(),
                Bindings.concat(validatorSpinner.createStringBinding())
        ));
    }

    private void checkSpinner(Check.Context context) {
        for(int i = 0; i < 6; i++) {
            double totalHours = context.get("T" + i);
            double nightHours = context.get("N" + i);
            if(nightHours > totalHours) {
                context.error("Le nombre d'heures de nuit ne doit pas dépasser le nombre d'heures total");
            }

            if(!checkIncrement(totalHours) || !checkIncrement(nightHours)) {
                context.error("Le nombre d'heures doit avoir un pas de 0.25");
            }
        }
    }

    private void checkChangeName(Check.Context context) {
        SQLEmployeeManager emp = SQLEmployeeManager.getInstance();

        String firstname = context.get(paramEmployeeFirstnameInput.getId());
        String lastname = context.get(paramEmployeeLastnameInput.getId());

        if(firstname.isBlank() || lastname.isBlank()) {
            context.error("le nom ou le prénom d'un employé ne doit pas être vide");
        }

        if(emp.employeeExist(firstname, lastname)) {
            context.error("Un employé avec ce nom existe déjà");
        }

        if(firstname.length() > 255 || lastname.length() > 255) {
            context.error("le taille maximum pour un nom ou prénom est de 255 caractères");
        }
    }

    private void checkNewEmployee(Check.Context context) {
        SQLEmployeeManager emp = SQLEmployeeManager.getInstance();

        String firstname = context.get(paramNewEmployeeFirstnameInput.getId());
        String lastname = context.get(paramNewEmployeeLastnameInput.getId());

        if(firstname.isBlank() || lastname.isBlank()) {
            context.error("le nom ou le prénom d'un employé ne doit pas être vide");
        }

        if(emp.employeeExist(firstname, lastname)) {
            context.error("Un employé avec ce nom existe déjà");
        }

        if(firstname.length() > 255 || lastname.length() > 255) {
            context.error("le taille maximum pour un nom ou prénom est de 255 caractères");
        }
    }

    private boolean checkIncrement(double value) {
        return value % 0.25 == 0;
    }

    /**
     * This method is called only once on the initialization of the view.
     * it set up the UI of the view and is focus on the Spinner initialization.
     */
    private void initPlanningUI() {
        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();
        for(int i = 0; i < totalHoursSpinnerList.size(); i++) {
            Spinner<Double> currentTotal = totalHoursSpinnerList.get(i);
            Spinner<Double> currentNight = nightHoursSpinnerList.get(i);
            ComboBox<DayLabel> currentComboBox = labelsComboBoxs.get(i);

            currentTotal.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(
                    0,
                    24,
                    paramEmployeeListPicker.getValue().getPlanningTotalHours(DayOfWeek.of(i+1)),
                    0.25));
            currentTotal.setEditable(true);
            currentTotal.focusedProperty().addListener((observable, oldValue, newValue) ->
                    Platform.runLater(() -> currentTotal.getEditor().selectAll()));


            currentNight.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(
                    0,
                    24,
                    paramEmployeeListPicker.getValue().getPlanningNightHours(DayOfWeek.of(i+1)),
                    0.25));
            currentNight.setEditable(true);
            currentNight.focusedProperty().addListener((observable, oldValue, newValue) ->
                    Platform.runLater(() -> currentNight.getEditor().selectAll()));


            currentComboBox.setItems(dlm.getLabelsObservableList());
            currentComboBox.getSelectionModel().select(dlm.getDayLabelById(paramEmployeeListPicker.getValue().getPlanningLabelId(DayOfWeek.of(i+1))));
        }
        labelsComboBoxs.get(6).setItems(dlm.getLabelsObservableList());
        labelsComboBoxs.get(6).getSelectionModel().select(dlm.getDayLabelById(paramEmployeeListPicker.getValue().getPlanningLabelId(DayOfWeek.of(7))));
    }

    /**
     * This methods is called by listeners to update the view on user interaction.
     * it update the UI according to the new value of the selected employee.
     */
    private void updatePlanning() {
        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();

        if(paramEmployeeListPicker.getValue() == null) {
            for(int i = 0; i < totalHoursSpinnerList.size(); i++) {
                Spinner<Double> currentTotal = totalHoursSpinnerList.get(i);
                Spinner<Double> currentNight = nightHoursSpinnerList.get(i);
                ComboBox<DayLabel> currentComboBox = labelsComboBoxs.get(i);

                currentTotal.setDisable(true);
                currentNight.setDisable(true);
                currentComboBox.setDisable(true);

                currentTotal.getValueFactory().setValue(0.0);
                currentNight.getValueFactory().setValue(0.0);
            }
            ComboBox<DayLabel> currentComboBox = labelsComboBoxs.get(6);
            currentComboBox.setDisable(true);
        } else {
            for(int i = 0; i < totalHoursSpinnerList.size(); i++) {
                Spinner<Double> currentTotal = totalHoursSpinnerList.get(i);
                Spinner<Double> currentNight = nightHoursSpinnerList.get(i);
                ComboBox<DayLabel> currentComboBox = labelsComboBoxs.get(i);

                currentTotal.setDisable(false);
                currentNight.setDisable(false);
                currentComboBox.setDisable(false);

                currentTotal.getValueFactory().setValue(paramEmployeeListPicker.getValue().getPlanningTotalHours(DayOfWeek.of(i+1)));
                currentNight.getValueFactory().setValue(paramEmployeeListPicker.getValue().getPlanningNightHours(DayOfWeek.of(i+1)));

                currentComboBox.setItems(dlm.getLabelsObservableList());
                ((SQLEmployee)paramEmployeeListPicker.getValue()).updateBuffer();
                currentComboBox.getSelectionModel().select(dlm.getDayLabelById(paramEmployeeListPicker.getValue().getPlanningLabelId(DayOfWeek.of(i+1))));
            }
            ComboBox<DayLabel> currentComboBox = labelsComboBoxs.get(6);
            currentComboBox.setItems(dlm.getLabelsObservableList());
            currentComboBox.getSelectionModel().select(dlm.getDayLabelById(paramEmployeeListPicker.getValue().getPlanningLabelId(DayOfWeek.of(7))));
            currentComboBox.setDisable(false);
        }
    }

    /**
     * This method is called when the user change the selected employee.
     * it call the update of the view.
     * @param event the event that triggered the method.
     */
    @FXML
    public void onEmployeeListChange(ActionEvent event) {
        if(paramEmployeeListPicker.getSelectionModel().isEmpty()) {
            paramEmployeeListPicker.getSelectionModel().selectFirst();
        }
        update();
    }

    /**
     * This method is called when the user click the button for the modification of the name of an employee
     * it check the validity of the new name and update the employee's data if it's valid.
     * @param event
     */
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

    /**
     * This method is called when the user click the button for the creation of a new employee.
     * it check the validity of the new name and create the employee if it's valid.
     * @param event the event that triggered the method.
     */
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

    /**
     * This method is called when the user click the button for the deletion of an employee.
     * it put the employee selected for deletion in the list of archived employees and remove it from the list of employees.
     * @param event the event that triggered the method.
     */
    @FXML
    public void onButtonArchiveEmployeeClick(ActionEvent event) {
        if(deleteEmployeeListPicker.getSelectionModel().isEmpty()) {
            return;
        }
        SQLEmployeeManager sem = SQLEmployeeManager.getInstance();
        Employee emp = deleteEmployeeListPicker.getValue();
        if(paramEmployeeListPicker.getValue() != null && paramEmployeeListPicker.getValue().equals(emp)) {
            paramEmployeeListPicker.getSelectionModel().selectNext();
        }
        sem.archiveEmployee(emp);
        deleteEmployeeListPicker.getSelectionModel().selectFirst();
    }

    /**
     * This method is called when the user click the button to validate the change on the planning
     * It call {@link #savePlanning()} to save the new planning.
     * @param event the event that triggered the method.
     */
    public void onButtonValidatePlanningCLicked(ActionEvent event) {
        savePlanning();
    }

    /**
     * Save the planning of the selected employee.
     */
    public void savePlanning() {
        if(labelsComboBoxs.get(0).getValue() == null) {
            return;
        }
        Employee emp = paramEmployeeListPicker.getValue();
        DayLabelManager dlm = SQLDayLabelManager.getInstance();
        for(int i = 1; i <= 6; i++) {
            if(totalHoursSpinnerList.get(i-1).getValue() < nightHoursSpinnerList.get(i-1).getValue()) {
                nightHoursSpinnerList.get(i-1).getValueFactory().setValue(totalHoursSpinnerList.get(i-1).getValue());
            }
            emp.setPlanningTotalHours(DayOfWeek.of(i), totalHoursSpinnerList.get(i-1).getValue());
            emp.setPlanningNightHours(DayOfWeek.of(i), nightHoursSpinnerList.get(i-1).getValue());
            emp.setPlanningLabelId(DayOfWeek.of(i), dlm.getDayLabelIdByName(labelsComboBoxs.get(i-1).getValue().getText()));
        }
        emp.setPlanningLabelId(DayOfWeek.SUNDAY, dlm.getDayLabelIdByName(labelsComboBoxs.get(6).getValue().getText()));
    }

    /**
     * This methods is called to update the view on user interaction.
     */
    public void update() {
        if(paramEmployeeListPicker.getValue() == null) {
            paramEmployeeFirstnameInput.setText("");
            paramEmployeeLastnameInput.setText("");
            updatePlanning();
            return;
        }
        paramEmployeeFirstnameInput.setText(paramEmployeeListPicker.getValue().getFirstName());
        paramEmployeeLastnameInput.setText(paramEmployeeListPicker.getValue().getLastName());
        updatePlanning();
    }

    /**
     * This method is called when the user change the selected employee for deletion.
     * it disabled the button for the deletion of the employee if there is no employee selected in the list.
     * @param event the event that triggered the method.
     */
    @FXML
    public void onDeleteEmployeeListChange(ActionEvent event) {
        if(deleteEmployeeListPicker.getSelectionModel().isEmpty()) {
            submitDeleteButton.setDisable(true);
        } else {
            submitDeleteButton.setDisable(false);
        }
    }
}
