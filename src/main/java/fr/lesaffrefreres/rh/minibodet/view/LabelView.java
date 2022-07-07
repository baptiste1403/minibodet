package fr.lesaffrefreres.rh.minibodet.view;

import fr.lesaffrefreres.rh.minibodet.controller.LabelViewController;
import fr.lesaffrefreres.rh.minibodet.model.DayLabel;
import fr.lesaffrefreres.rh.minibodet.model.DayLabelManager;
import fr.lesaffrefreres.rh.minibodet.model.SQLDayLabelManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class LabelView extends VBox implements ChangeListener<Scene> {

    public LabelView() {
        ResourceBundle bundle = ResourceBundle.getBundle("/strings", new Locale("fr", "FR"));
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/label-view.fxml"), bundle);
        fxmlLoader.setRoot(this);

        try {
            fxmlLoader.load();
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        sceneProperty().addListener(this);
    }

    public void setup() {
        SQLDayLabelManager dlm = SQLDayLabelManager.getInstance();

        ComboBox<DayLabel> cb = (ComboBox<DayLabel>) this.lookup("#paramLabelPicker");
        cb.setItems(dlm.getLabelsObservableList());
        cb.getSelectionModel().select(dlm.getDayLabelById(dlm.getUndefinedDayLabelId()));

        TextField tf = (TextField) this.lookup("#paramLabelTextInput");
        tf.setText(cb.getValue().getText());

        ColorPicker cp = (ColorPicker) this.lookup("#paramLabelColorPicker");
        cp.setValue(cb.getValue().getColor());
    }

    @Override
    public void changed(ObservableValue<? extends Scene> observable, Scene oldValue, Scene newValue) {
        setup();
    }
}
