package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.*;
import fr.lesaffrefreres.rh.minibodet.model.event.CalendarEvent;
import fr.lesaffrefreres.rh.minibodet.view.CalendarView;
import fr.lesaffrefreres.rh.minibodet.view.EmployeeCalendarView;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.time.format.TextStyle;
import java.util.Locale;

/**
 * This class is used as the controller for the EmployeeCalendarView @see {@link EmployeeCalendarView}
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see EmployeeCalendarView
 */
public class EmployeeCalendarViewController implements EventHandler<CalendarEvent> {
    @FXML
    private Label bankHolidayHoursLabel;
    @FXML
    private Button buttonInputComment;
    @FXML
    private TextField inputComment;
    @FXML
    private Label statusLabel;
    @FXML
    private Button saveButton;
    @FXML
    private Label holidayStockLabel;
    @FXML
    private Label monthBankHolidayHoursLabel;
    @FXML
    private Label holidayLabel;
    @FXML
    private Label sickLeaveLabel;
    @FXML
    private Label sundayLabelLevel1;
    @FXML
    private Label sundayLabelLevel2;
    @FXML
    private Label monthOvertimeLabelLevel1;
    @FXML
    private Label monthOvertimeLabelLevel2;
    @FXML
    private Label monthSundayLabelLevel1;
    @FXML
    private Label monthSundayLabelLevel2;
    @FXML
    private Label monthCompensatoryHoursLabel;
    @FXML
    private Label compensatoryHoursLabel;
    @FXML
    private Label overtimeLabelLevel1;

    @FXML
    private Label overtimeLabelLevel2;
    @FXML
    private Label monthHoursLabel;
    @FXML
    private Label selectedDate;
    @FXML
    private Spinner<Double> choiceTotalHours;
    @FXML
    private Spinner<Double> choiceNightHours;
    @FXML
    private Label weekHoursLabel;
    @FXML
    private ComboBox<Employee> employeeChoicebox;

    @FXML
    private CalendarView<SQLWorkCalendar> employeeCalendar;

    private final Tooltip tooltip = new Tooltip("Veuillez entrer un nombre avec un pas de 0.25");

    /**
     * This method is called by the FXML loader when the view is loaded.
     * It initializes the view.
     */
    @FXML
    private void initialize() {

        EmployeeManager<Employee> em = SQLEmployeeManager.getInstance();

        employeeChoicebox.setItems(em.getEmployeesList());

        choiceTotalHours.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 24.0, 0.0, 0.25));
        choiceTotalHours.valueProperty().addListener(this::onTotalHoursChange);
        choiceTotalHours.setDisable(true);
        // on ne set pas le focus listener car il est mis focus automatiquement lors de l'update
        choiceTotalHours.getEditor().setOnKeyPressed(event -> {
            if(event.getCode().equals(KeyCode.ENTER)  && event.isControlDown()) {
                employeeCalendar.selectPrevious();
            } else if (event.getCode().equals(KeyCode.ENTER)) {
                employeeCalendar.selectNext();
            }
            event.consume();
        });

        choiceNightHours.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, 24.0, 0.0, 0.25));
        choiceNightHours.valueProperty().addListener(this::onNightHoursChange);
        choiceNightHours.setDisable(true);
        choiceNightHours.focusedProperty().addListener((obs, wasFocused, isFocused) -> {
            if (isFocused) {
                Platform.runLater(choiceNightHours.getEditor()::selectAll);
            }
        });

        inputComment.setDisable(true);
        buttonInputComment.setDisable(true);

        saveButton.setDisable(true);

        employeeCalendar.addEventHandlers(this);

        if(!em.getEmployeesList().isEmpty()) {
            employeeChoicebox.getSelectionModel().selectFirst();
            employeeCalendar.setCalendar((SQLWorkCalendar) employeeChoicebox.getValue().getCalendar(employeeCalendar.getSelectedYear()));
        }
    }


    /**
     * This method is called when the user changes the selected employee on choice box.
     * It updates the calendar view with the new employees calendar.
     * and the side panel with the employee informations.
     * @param event The event that triggered the method.
     */
    @FXML
    public void onEmployeeListChange(ActionEvent event) {
        if(employeeCalendar == null) {
            return;
        }
        if(employeeChoicebox.getItems().isEmpty()) {
            return;
        }
        if(employeeChoicebox.getValue() == null) {
            employeeChoicebox.getSelectionModel().selectFirst();
        }
        employeeCalendar.setCalendar((SQLWorkCalendar) employeeChoicebox.getValue().getCalendar(employeeCalendar.getSelectedYear()));
        update();
    }

    /**
     * This method is called when the user changes the total hours' field in the side panel
     * if the value is valid it updates the employee calendar with the new value.
     * @param observable The observable value that triggered the method.
     * @param oldValue The old value of the observable.
     * @param newValue The new value of the observable.
     */
    public void onTotalHoursChange(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
        if(oldValue == null || newValue == null || oldValue.equals(newValue)) {
            return;
        }
        if(employeeCalendar == null || employeeChoicebox.getItems().isEmpty()) {
            return;
        }

        if(!checkIncrement(newValue) || newValue > 24.0 || newValue < 0.0) {
            choiceTotalHours.setStyle("-fx-text-inner-color: red;");
            Tooltip.install(choiceTotalHours, tooltip);
            return;
        } else {
            choiceTotalHours.setStyle("-fx-text-inner-color: black;");
            Tooltip.uninstall(choiceTotalHours, tooltip);
        }

        ((WorkDay) employeeCalendar.getCalendar().getDay(employeeCalendar.getSelectedDate())).getSchedule().setTotalHours(newValue);

        double curNightHours = choiceNightHours.getValueFactory().getValue();
        choiceNightHours.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, newValue, curNightHours, 0.25));

        employeeCalendar.updateAtDate(employeeCalendar.getSelectedDate());
        updateOperation();
    }

    private boolean checkIncrement(double value) {
        return value % 0.25 == 0;
    }

    /**
     * This method is called when the user changes the night hours' field in the side panel
     * if the value is valid it updates the employee calendar with the new value.
     * @param observable The observable value that triggered the method.
     * @param oldValue The old value of the observable.
     * @param newValue The new value of the observable.
     */
    public void onNightHoursChange(ObservableValue<? extends Double> observable, Double oldValue, Double newValue) {
        if(oldValue == null || newValue == null || oldValue.equals(newValue)) {
            return;
        }
        if(employeeCalendar == null || employeeChoicebox.getItems().isEmpty()) {
            return;
        }

        if(!checkIncrement(newValue) || newValue > 24 || newValue < 0) {
            choiceNightHours.setStyle("-fx-text-inner-color: red;");
            Tooltip.install(choiceNightHours, tooltip);
            return;
        } else {
            choiceNightHours.setStyle("-fx-text-inner-color: black;");
            Tooltip.uninstall(choiceNightHours, tooltip);
        }

        ((WorkDay) employeeCalendar.getCalendar().getDay(employeeCalendar.getSelectedDate())).getSchedule().setNightHours(newValue);

        double curTotalHours = choiceTotalHours.getValueFactory().getValue();
        choiceTotalHours.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(newValue, 24.0, curTotalHours, 0.25));

        employeeCalendar.updateAtDate(employeeCalendar.getSelectedDate());
        updateOperation();
    }

    /**
     * This method is called when the user change the commentary for the selected day.
     * It updates the employee calendar with the new value if the value is not blank.
     * @param event The event that triggered the method.
     */
    @FXML
    private void onInputCommentValidate(ActionEvent event) {
        if(employeeCalendar == null) {
            return;
        }

        if(!inputComment.getText().isBlank()) {
            employeeCalendar.getCalendar().getDay(employeeCalendar.getSelectedDate()).setComment(inputComment.getText());
        }
    }

    /**
     * This method is called by listeners to update the view on user interaction.
     * It updates the employee calendar and the side panel with the employee information.
     */
    public void update() {

        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();

        if (employeeCalendar == null || employeeChoicebox.getItems().isEmpty()) {
            initialize(); // reset
            return;
        }
        selectedDate.setText(employeeCalendar.getSelectedDate().format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).localizedBy(Locale.getDefault())));

        double curNightHours = ((WorkDay) employeeCalendar.getCalendar().getDay(employeeCalendar.getSelectedDate())).getSchedule().getNightHours();
        double curTotalHours = ((WorkDay) employeeCalendar.getCalendar().getDay(employeeCalendar.getSelectedDate())).getSchedule().getTotalHours();

        statusLabel.setText("Label : " + dlm.getDayLabelById(employeeCalendar.getCalendar().getDay(employeeCalendar.getSelectedDate()).getLabelId()).getText());

        choiceTotalHours.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(curNightHours, 24.0, curTotalHours, 0.25));
        choiceTotalHours.setDisable(false);
        choiceTotalHours.requestFocus();
        choiceTotalHours.getEditor().selectAll();
        choiceNightHours.setValueFactory(new SpinnerValueFactory.DoubleSpinnerValueFactory(0.0, curTotalHours, curNightHours, 0.25));
        choiceNightHours.setDisable(false);

        inputComment.setDisable(false);
        buttonInputComment.setDisable(false);

        saveButton.setDisable(false);

        if(employeeCalendar.getCalendar().isSavedMonth(employeeCalendar.getSelectedYear(), employeeCalendar.getSelectedDate().getMonth())) {
            saveButton.setDisable(true);
        } else {
            saveButton.setDisable(false);
        }

        inputComment.setText(employeeCalendar.getCalendar().getDay(employeeCalendar.getSelectedDate()).getComment());

        updateOperation();
    }

    /**
     * This method is called by {@link #update()} to update the side panel with the employee information of the selected day.
     * It used a {@link WorkCalendarOperation} to compute the work information for the employee at a given date.
     */
    public void updateOperation() {
        WorkCalendarOperation<WorkDay> operation = new WorkCalendarOperation<>(employeeCalendar.getCalendar());

        LocalDate selectedDate = employeeCalendar.getSelectedDate();
        Employee emp = employeeChoicebox.getValue();
        weekHoursLabel.setText("total : " + operation.getWeekNbHours(selectedDate) + " h, dont nuit : " + operation.getWeekNbHoursNight(selectedDate) + " h");
        overtimeLabelLevel1.setText("heures sup (25%) : " + operation.getOvertimeWeekLevel1(selectedDate, emp) + " h");
        overtimeLabelLevel2.setText("heures sup (50%) : " + operation.getOvertimeWeekLevel2(selectedDate, emp) + " h");
        sundayLabelLevel1.setText("dimanche (25%) : " + operation.getOvertimeSundayLevel1(selectedDate, emp) + " h");
        sundayLabelLevel2.setText("dimanche (50%) : " + operation.getOvertimeSundayLevel2(selectedDate, emp) + " h");
        compensatoryHoursLabel.setText("heures complémentaires : " + operation.getCompensatoryHoursWeek(selectedDate, emp) + " h");
        bankHolidayHoursLabel.setText("heures jours fériés : " + operation.getNbWorkedBankHolidayHours(selectedDate) + " h");

        monthHoursLabel.setText("total : " + operation.getMonthNbHours(selectedDate) + " h, dont nuit : " + operation.getMonthNbHoursNight(selectedDate) + " h");
        monthOvertimeLabelLevel1.setText("heures sup (25%) : " + operation.getMonthOvertimeLevel1(selectedDate, emp) + " h");
        monthOvertimeLabelLevel2.setText("heures sup (50%) : " + operation.getMonthOvertimeLevel2(selectedDate, emp) + " h");
        monthSundayLabelLevel1.setText("dimanche (25%) : " + operation.getMonthSundayOvertimeLevel1(selectedDate, emp) + " h");
        monthSundayLabelLevel2.setText("dimanche (50%) : " + operation.getMonthSundayOvertimeLevel2(selectedDate, emp) + " h");
        monthCompensatoryHoursLabel.setText("heures complémentaires : " + operation.getMonthCompensatoryHours(selectedDate, emp) + " h");
        monthBankHolidayHoursLabel.setText("heures jours fériés : " + operation.getMonthNbWorkedBankHolidayHours(selectedDate) + " h");
        holidayLabel.setText("CP : " + operation.getMonthNbHoliday(selectedDate));
        sickLeaveLabel.setText("AM : " + operation.getMonthSickLeave(selectedDate));
        int stockHoliday = (30 - operation.getYearNbDayHoliday(selectedDate));
        int previousStockHoliday = operation.getYearNbDayHoliday(selectedDate.minusYears(1));
        if(previousStockHoliday < 0) {
            stockHoliday += previousStockHoliday;
        }
        holidayStockLabel.setText("CP restant : " + stockHoliday);
        if(stockHoliday < 0) {
            holidayStockLabel.setTextFill(Color.RED);
        } else {
            holidayStockLabel.setTextFill(Color.BLACK);
        }
    }

    /**
     * This method is called when the user click on the save button.
     * It saves the whole month of the employee calendar corresponding to the selected date.
     * @see @{@link SQLWorkCalendar#saveMonth(int, Month)} for further information.
     * @param event The event that triggered the method.
     */
    @FXML
    public void onSaveButtonClick(ActionEvent event) {
        ButtonType btnYes = new ButtonType("Oui");
        ButtonType btnNo = new ButtonType("Non");
        Alert alert = new Alert(Alert.AlertType.WARNING, "Voulez-vous vraiment archiver les informations pour le mois de " +
                employeeCalendar.getSelectedDate().getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH) + " " + employeeCalendar.getSelectedDate().getYear() +
                ", cette action est irréversible et vous ne pourrez modifer les données enregistrées que manuellement.", btnYes, btnNo);
        alert.setTitle("Archivage du mois de " + employeeCalendar.getSelectedDate().getMonth().getDisplayName(TextStyle.FULL, Locale.FRENCH) + " " + employeeCalendar.getSelectedDate().getYear());
        alert.showAndWait();
        if(alert.getResult() == btnYes) {
            employeeCalendar.getCalendar().saveMonth(employeeCalendar.getSelectedYear(), employeeCalendar.getSelectedDate().getMonth());
            ((Button)event.getSource()).setDisable(true);
        }
    }

    /**
     * This method is called by the calendar view when the user click on a day.
     * it call the update of the side panel with the employee information of the selected day.
     * @param event the event which occurred
     */
    @Override
    public void handle(CalendarEvent event) {
        update();
    }
}
