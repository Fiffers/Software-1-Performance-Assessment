package Controllers;

/**
 * This class handles the modification of Parts that already exist.
 * @auther Steven Pfeiffer
 */

import Model.Inventory;
import Model.OutsourcedPart;
import Model.Part;
import Model.InhousePart;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import jdk.jfr.Enabled;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class ModifyPartController implements Initializable {

    @FXML private Button cancelButton;
    @FXML private RadioButton inHouseButton;
    @FXML private RadioButton outsourcedButton;
    @FXML private ToggleGroup partsToggleGroup;

    @FXML private TextField idTextField;
    @FXML private TextField nameTextField;
    @FXML private TextField priceTextField;
    @FXML private TextField inventoryTextField;
    @FXML private TextField minTextField;
    @FXML private TextField maxTextField;
    @FXML private TextField machineIDField;
    @FXML private Label machineIDLabel;

    Inventory inventory;
    Part selectedPart;

    MainFormController m = new MainFormController(inventory);
    AddPartController a = new AddPartController(inventory);

    /**
     * The constructor. Constructs the inventory and the selected part from the main form.
     * @param inventory the inventory.
     * @param selectedPart the selected part.
     */
    public ModifyPartController(Inventory inventory, Part selectedPart) {
        this.inventory = inventory;
        this.selectedPart = selectedPart;
    }

    /**
     * Populate the text fields with the selected part after loading is complete.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        populateTextFields();
    }

    /**
     * Sends user back to the main view.
     * @param event the buttonpress.
     */
    public void backToMainView (ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controllers/mainForm.fxml"));
        loader.setControllerFactory(type -> new MainFormController(inventory));
        Parent mainFormParent = loader.load();
        Scene mainFormScene = new Scene(mainFormParent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainFormScene);
        window.show();
    }

    /**
     * Gets data from the selected part and puts it in the textfields of the modify part view.
     */
    public void populateTextFields() {
        //Populate the text fields.
        idTextField.setText(String.valueOf(selectedPart.getId()));
        nameTextField.setText(selectedPart.getName());
        priceTextField.setText(String.valueOf(selectedPart.getPrice()));
        inventoryTextField.setText(String.valueOf(selectedPart.getStock()));
        minTextField.setText(String.valueOf(selectedPart.getMin()));
        maxTextField.setText(String.valueOf(selectedPart.getMax()));

        // Handle different part types.
        if (selectedPart instanceof InhousePart) {
            // Make sure the correct radio button is selected.
            inHouseButton.setSelected(true);
            // Make the selected part act like an inHousePart. We do this so we can get the machine ID.
            InhousePart part = (InhousePart) selectedPart;
            // Supply data to unique textfield.
            machineIDField.setText(String.valueOf(part.getMachineID()));
        }
        if (selectedPart instanceof OutsourcedPart) {
            // Make sure the correct radio button is selected.
            outsourcedButton.setSelected(true);
            // Make the machine ID label change to be appropriate for an outsourced part.
            machineIDLabel.setText("Company Name");
            // Make the selected part act like an  outsourcedPart. We do this so we can get the Company Name.
            OutsourcedPart part = (OutsourcedPart) selectedPart;
            // Supply data to unique textfield.
            machineIDField.setText(String.valueOf(part.getCompanyName()));
        }
    }

    /**
     * Handles the overwrite of the parts.
     * @param event the buttonpress.
     */
    public void saveButtonPushed (ActionEvent event) throws IOException {
        if (m.partEntryValidator(nameTextField, priceTextField,inventoryTextField, minTextField, maxTextField)) {
            //Initialize some variables.
            boolean result = false;
            int machID = 0;
            String companyName = null;

            // Text getters.
            int partID = Integer.parseInt(idTextField.getText());
            String partName = nameTextField.getText();
            int partInventory = Integer.parseInt(inventoryTextField.getText());
            double partPrice = Double.parseDouble(priceTextField.getText());
            int partMin = Integer.parseInt(minTextField.getText());
            int partMax = Integer.parseInt(maxTextField.getText());

            if (machineIDField.getText().chars().allMatch(Character::isDigit) && inHouseButton.isSelected()) {
                machID = Integer.parseInt(machineIDField.getText());
                inventory.updatePart(partID, new InhousePart(partID, partName, partPrice, partInventory, partMin, partMax, machID));
            }
            else if (machineIDField.getText().chars().anyMatch(Character::isAlphabetic) && outsourcedButton.isSelected()) {
                companyName = machineIDField.getText();
                inventory.updatePart(partID, new OutsourcedPart(partID, partName, partPrice, partInventory, partMin, partMax, companyName));
            }
            else {
                m.errorMessage("Error", "There was an error when you tried to save this part", "Verify that your Machine ID or Company are correct and you have the correct radio button selected.");
            }
        }

        backToMainView(event);

    }

    /**
     * Handles the cancel button.
     */
    public void cancelButtonPushed (ActionEvent event) throws IOException {
        backToMainView(event);
    }

    /**
     * Handles the radio buttons in the scene. When toggled, they will automatically change labels and text appropriately.
     */
    public void radioButtonManager () {
        if (this.partsToggleGroup.getSelectedToggle().equals(this.outsourcedButton)) {
            machineIDLabel.setText("Company Name");
            machineIDField.setPromptText("Name");
        }
        if (this.partsToggleGroup.getSelectedToggle().equals(this.inHouseButton)) {
            machineIDLabel.setText("Machine ID");
            machineIDField.setPromptText("#");
        }
    }

}
