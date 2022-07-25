package fr.lesaffrefreres.rh.minibodet.view;

import fr.lesaffrefreres.rh.minibodet.model.StatusListPickerMenuItemProvider;
import fr.lesaffrefreres.rh.minibodet.model.StatusListPickerMenuItemProviderBase;
import javafx.scene.control.ContextMenu;

/**
 * This class represents the view of the status list picker view.
 *
 * @author lesaffrefreres
 * @version 1.0
 * @since 1.0
 */
public class StatusListPickerView extends ContextMenu {

    StatusListPickerMenuItemProvider provider;

    /**
     * Load the view from the FXML file.
     */
    public StatusListPickerView() {
        provider = new StatusListPickerMenuItemProviderBase();
        getItems().setAll(provider.getMenuItemList());
    }

    /**
     * set the provider for the menuitem list
     * @param prov the provider
     */
    public void setProvider(StatusListPickerMenuItemProvider prov) {
        provider = prov;
    }

    @Override
    protected void show() {
        getItems().setAll(provider.getMenuItemList());
        super.show();
    }
}
