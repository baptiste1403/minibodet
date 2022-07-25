package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.DayLabel;
import fr.lesaffrefreres.rh.minibodet.model.SQLDayLabel;
import fr.lesaffrefreres.rh.minibodet.model.SQLDayLabelManager;
import fr.lesaffrefreres.rh.minibodet.view.LabelView;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.paint.Color;

/**
 * This class is used as the controller for the {@link LabelView} class.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see LabelView
 */
public class LabelViewController {

    @FXML
    private Button buttonTextInputValidate;
    @FXML
    private Button deleteButton;
    @FXML
    private ComboBox<DayLabel> deleteLabelPicker;
    @FXML
    private ColorPicker paramNewLabelColorPicker;
    @FXML
    private TextField paramNewLabelTextInput;
    @FXML
    private ColorPicker paramLabelColorPicker;
    @FXML
    private TextField paramLabelTextInput;
    @FXML
    private ComboBox<DayLabel> paramLabelPicker;

    private EmployeeViewController employeeController;

    public void setEmployeeController(EmployeeViewController ec) {
        employeeController = ec;
    }

    /**
     * This method is called by the FXML loader when the view is loaded.
     */
    @FXML
    private void initialize() {
        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();

        paramLabelPicker.setItems(dlm.getLabelsObservableList());
        paramLabelPicker.getSelectionModel().select(dlm.getDayLabelById(dlm.getUndefinedDayLabelId()));
        deleteLabelPicker.setItems(dlm.getLabelsObservableList());
        deleteLabelPicker.getSelectionModel().select(dlm.getDayLabelById(dlm.getUndefinedDayLabelId()));

        paramLabelTextInput.setText(paramLabelPicker.getValue().getText());
        paramLabelColorPicker.setValue(paramLabelPicker.getValue().getColor());
        deleteButton.setDisable(true);
        paramLabelTextInput.setDisable(true);
        buttonTextInputValidate.setDisable(true);
    }

    /**
     * This method is called when the user change the selected label for deletion.
     * it disabled the delete button if the label is reserved. @see {@link SQLDayLabelManager#isReserved(long)}
     * @param event The event that triggered the method.
     */
    @FXML
    public void onDeleteLabelListChange(ActionEvent event) {
        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();
        deleteButton.setDisable(dlm.isReserved(((SQLDayLabel)deleteLabelPicker.getValue()).getId()));
    }

    /**
     * This method is called when the user change the selected label for modification.
     * it disabled the text fields if the label is reserved. @see {@link SQLDayLabelManager#isReserved(long)}
     * @param event The event that triggered the method.
     */
    @FXML
    public void onLabelListChange(ActionEvent event) {
        if(paramLabelPicker.getValue() == null) {
            return;
        }
        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();
        DayLabel selected = paramLabelPicker.getValue();
        long idSelected = ((SQLDayLabel)selected).getId();
        paramLabelTextInput.setDisable(dlm.isReserved(idSelected));
        buttonTextInputValidate.setDisable(dlm.isReserved(idSelected));
        paramLabelTextInput.setText(selected.getText());
        paramLabelColorPicker.setValue(selected.getColor());
    }

    /**
     * This method is called when the user click on the button to validate the modification of text of the label.
     * if the text field is not empty, the label is updated. @see {@link SQLDayLabelManager#setDayLabelText(long, String)}
     * @param event The event that triggered the method.
     */
    @FXML
    public void onTextInputValidate(ActionEvent event) {
        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();
        if(paramLabelTextInput.getText().isBlank() || dlm.labelExist(paramLabelTextInput.getText())) {
            return;
        }

        DayLabel selected = paramLabelPicker.getValue();
        long labelId = dlm.getDayLabelIdByName(selected.getText());
        dlm.setDayLabelText(labelId, paramLabelTextInput.getText());
        paramLabelPicker.setItems(dlm.getLabelsObservableList());
        paramLabelPicker.setValue(selected);
    }

    /**
     * This method is called when the user change the color of the label in the color picker.
     * it saves the new color in the selected label. @see {@link SQLDayLabelManager#setDayLabelColor(long, Color)}
     * @param event The event that triggered the method.
     */
    @FXML
    public void onColorPickerChange(ActionEvent event) {
        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();
        DayLabel selected = paramLabelPicker.getValue();
        long labelId = dlm.getDayLabelIdByName(selected.getText());
        dlm.setDayLabelColor(labelId, paramLabelColorPicker.getValue());
    }

    /**
     * This method is called when the user click on the button to validate the creation of a new label.
     * if the label doesn't exist, it creates a new label with the text and color of the color picker. @see {@link SQLDayLabelManager#createDayLabel(String, Color)}
     * otherwise, it does nothing.
     * @param event The event that triggered the method.
     */
    @FXML
    public void onCreateButtonClick(ActionEvent event) {
        if(paramNewLabelTextInput.getText().isBlank()) {
            return;
        }
        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();
        long labelId = dlm.createDayLabel(paramNewLabelTextInput.getText(), paramNewLabelColorPicker.getValue());
        paramLabelPicker.setValue(dlm.getDayLabelById(labelId));
    }

    /**
     * This method is called when the user click on the button to delete the selected label for deletion.
     * it delete the label @see {@link SQLDayLabelManager#removeDayLabelById(long)}
     * @param event The event that triggered the method.
     */
    @FXML
    public void onDeleteButtonClick(ActionEvent event) {
        if(deleteLabelPicker.getItems().isEmpty()) {
            return;
        }
        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();
        dlm.removeDayLabelById(dlm.getDayLabelIdByName(deleteLabelPicker.getValue().getText()));

    }
}
