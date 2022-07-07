package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.model.DayLabel;
import fr.lesaffrefreres.rh.minibodet.model.SQLDayLabelManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class LabelViewController {
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

    @FXML
    public void onLabelListChange(ActionEvent event) {
        if(paramLabelPicker.getValue() == null) {
            return;
        }
        DayLabel selected = paramLabelPicker.getValue();
        paramLabelTextInput.setText(selected.getText());
        paramLabelColorPicker.setValue(selected.getColor());
    }

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

    @FXML
    public void onColorPickerChange(ActionEvent event) {
        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();
        DayLabel selected = paramLabelPicker.getValue();
        long labelId = dlm.getDayLabelIdByName(selected.getText());
        dlm.setDayLabelColor(labelId, paramLabelColorPicker.getValue());
    }

    @FXML
    public void onCreateButtonClick(ActionEvent event) {
        if(paramNewLabelTextInput.getText().isBlank()) {
            return;
        }
        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();
        long labelId = dlm.createDayLabel(paramNewLabelTextInput.getText(), paramNewLabelColorPicker.getValue());
        paramLabelPicker.setValue(dlm.getDayLabelById(labelId));
    }
}
