package Controllers;

/**
 * This class is used to display the add part scene as well as handle data entry and storage.
 * @author Steven Pfeiffer
 */

import Model.InhousePart;
import Model.Inventory;
import Model.OutsourcedPart;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class AddPartController {

    @FXML private RadioButton inHouseButton;
    @FXML private RadioButton outsourcedButton;
    @FXML private ToggleGroup partsToggleGroup;

    @FXML private TextField nameTextField;
    @FXML private TextField priceTextField;
    @FXML private TextField inventoryTextField;
    @FXML private TextField minTextField;
    @FXML private TextField maxTextField;
    @FXML private TextField machineIDField;
    @FXML private Label machineIDLabel;

    Inventory inventory;

    MainFormController m = new MainFormController(inventory);

    /**
     * Inventory constructor.
     * @param inventory constructs the inventory.
     */
    public AddPartController(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Returns user to the main form.
     * @param event an action occurred.
     * @throws IOException an exception.
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
     * Changes display of view based on which radio button is selected.
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

    /**
     * Creates new Model.Part object using the textfields in the scene and adds it to the tableview.
     * @param event an event occurred.
     * @throws IOException an exception.
     */
    public void saveButtonPushed (ActionEvent event) throws IOException {

        // Create the data we need to pass to the Model.Part ID Generator.
        int[] partIDs = new int[inventory.getAllParts().size()];
        for (int i = 0; i < inventory.getAllParts().size(); i++) {
            partIDs[i] = inventory.getAllParts().get(i).getId();
        }

        // Make sure the machine ID field is good to go.
        boolean machineIDFieldChecker = !machineIDField.getText().chars().allMatch(Character::isDigit) || machineIDField.getText().isEmpty();

        if (m.partEntryValidator(nameTextField, priceTextField, inventoryTextField, minTextField, maxTextField) && !machineIDFieldChecker){
            int partID = m.generateNewID(partIDs, inventory.getAllParts().size());
            String partName = nameTextField.getText();
            int partInventory = Integer.parseInt(inventoryTextField.getText());
            double partPrice = Double.parseDouble(priceTextField.getText());
            int partMin = Integer.parseInt(minTextField.getText());
            int partMax = Integer.parseInt(maxTextField.getText());

            // Add the part to the Inventory Model.
            if (inHouseButton.isSelected()){
                int machID = Integer.parseInt(machineIDField.getText());
                inventory.addPart(new InhousePart(partID, partName, partPrice, partInventory, partMin, partMax, machID));
            }

            else {
                String companyName = machineIDField.getText();
                inventory.addPart(new OutsourcedPart(partID, partName, partPrice, partInventory, partMin, partMax, companyName));
            }

            backToMainView(event);
        }
        else{
            m.errorMessage("Error", "There was an error with your entries.", "Make sure you typed everything in correctly.");
        }



    }
}
