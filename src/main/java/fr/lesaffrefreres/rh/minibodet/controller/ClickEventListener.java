package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.view.StatusListPickerView;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class ClickEventListener implements EventHandler<MouseEvent> {

    StatusListPickerView picker;
    Parent container;

    public ClickEventListener(StatusListPickerView slpv, Parent prt) {
        picker =  slpv;
        container = prt;
    }

    @Override
    public void handle(MouseEvent event) {
        if(event.getButton() == MouseButton.SECONDARY) {
            picker.show(container, event.getScreenX(), event.getScreenY());
        } else if (event.getButton() == MouseButton.PRIMARY) {
            picker.hide();
        }
    }
}
