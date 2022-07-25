package fr.lesaffrefreres.rh.minibodet.controller;

import fr.lesaffrefreres.rh.minibodet.view.StatusListPickerView;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

/**
 * This class is used as the event handler for the StatusListPickerView. @see {@link StatusListPickerView}
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 *
 * @see StatusListPickerView
 */
public class ClickEventListener implements EventHandler<MouseEvent> {

    StatusListPickerView picker;
    Parent container;

    /**
     * create the event handler and set the picker and the container of the picker references
     * @param slpv the picker to handle
     * @param prt the container of the picker
     */
    public ClickEventListener(StatusListPickerView slpv, Parent prt) {
        picker =  slpv;
        container = prt;
    }

    /**
     * handle the click event and show the picker view if the click is a right click,
     * if the click is a left click, the picker view is hidden
     * @param event the click event
     */
    @Override
    public void handle(MouseEvent event) {
        if(event.getButton() == MouseButton.SECONDARY) {
            picker.show(container, event.getScreenX(), event.getScreenY());
        } else if (event.getButton() == MouseButton.PRIMARY) {
            picker.hide();
        }
    }
}
