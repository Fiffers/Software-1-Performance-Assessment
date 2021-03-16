package Controllers;

/**
 * This class handles modification of products that already exist. This one was much easier for me (vs. the addProduct)
 * because it allowed me to add or remove parts from the get-go, since the product already existed. However, I kept
 * having problems getting parts to delete. They'd disappear from the associated parts table, but would reappear if
 * I entered this scene again. It turns out I was using the .add and .remove methods for the tableviews the entire
 * time and was completely ignoring the methods in the products model I made a few days before. Replace the .add
 * and the .remove with .addAssociatedPart and .removeAssociatedPart respectively, and it was good to go.
 * @author Steven Pfeiffer
 */

import Model.*;

import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TableColumn;

import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;

public class ModifyProductController implements Initializable {

    // Make some button ids.
    @FXML private Button searchUnassociatedPartsButton;
    @FXML private Button clearSearchButton;
    @FXML private Button addAssociatedPartButton;
    @FXML private Button removeAssociatedPartButton;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    // Make some textfield ids.
    @FXML private TextField idTextField;
    @FXML private TextField nameTextField;
    @FXML private TextField priceTextField;
    @FXML private TextField inventoryTextField;
    @FXML private TextField minTextField;
    @FXML private TextField maxTextField;
    @FXML private TextField searchUnassociatedPartsTextField;

    // Make some tableview ids.
    @FXML private TableView<Part> unassociatedPartsTableView;
    @FXML private TableView<Part> associatedPartsTableView;
    // Make some tablecolumn ids.
    @FXML private TableColumn<Part, Integer> unassociatedPartIDTableColumn;
    @FXML private TableColumn<Part, String> unassociatedPartNameTableColumn;
    @FXML private TableColumn<Part, Integer> unassociatedInventoryLevelTableColumn;
    @FXML private TableColumn<Part, Double> unassociatedPricePerUnitTableColumn;
    @FXML private TableColumn<Part, Integer> associatedPartIDTableColumn;
    @FXML private TableColumn<Part, String> associatedPartNameTableColumn;
    @FXML private TableColumn<Part, Integer> associatedInventoryLevelTableColumn;
    @FXML private TableColumn<Part, Double> associatedPricePerUnitTableColumn;

    Inventory inventory;
    Product selectedProduct;

    MainFormController m = new MainFormController(inventory);

    private ObservableList<Part> partsInventory = FXCollections.observableArrayList();
    private ObservableList<Part> associatedParts = FXCollections.observableArrayList();

    /**
     * The constructor. Constructs the inventory and the selected product from the main form.
     * @param inventory the inventory.
     * @param selectedProduct the selected product.
     */
    public ModifyProductController(Inventory inventory, Product selectedProduct) {
        this.inventory = inventory;
        this.selectedProduct = selectedProduct;
    }

    /**
     * Sets up the textfields with the data from the selected product and formats them appropriately.
     * It also calls for the tableview data input.
     */
    public void initialize(URL url, ResourceBundle resourceBundle) {

        DoubleProperty myDouble;

        unassociatedPartIDTableColumn.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
        unassociatedPartNameTableColumn.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
        unassociatedInventoryLevelTableColumn.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
        unassociatedPricePerUnitTableColumn.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));

        associatedPartIDTableColumn.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
        associatedPartNameTableColumn.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
        associatedInventoryLevelTableColumn.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
        associatedPricePerUnitTableColumn.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));

        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        unassociatedPricePerUnitTableColumn.setCellFactory(tc -> new TableCell<Part, Double>() {
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(price));
                }
            }
        });

        generateUnassociatedPartsTableView();
        generateAssociatedPartsTableView();

        populateTextFields();
    }

    /**
     * Populates the text fields.
     */
    private void populateTextFields() {
        //Populate the text fields.
        idTextField.setText(String.valueOf(selectedProduct.getId()));
        nameTextField.setText(selectedProduct.getName());
        priceTextField.setText(String.valueOf(selectedProduct.getPrice()));
        inventoryTextField.setText(String.valueOf(selectedProduct.getStock()));
        minTextField.setText(String.valueOf(selectedProduct.getMin()));
        maxTextField.setText(String.valueOf(selectedProduct.getMax()));
        associatedPartsTableView.setItems(selectedProduct.getAllAssociatedParts());
    }

    /**
     * Sends the user back to the main form.
     */
    public void backToMainForm (ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controllers/mainForm.fxml"));
        loader.setControllerFactory(type -> new MainFormController(inventory));
        Parent mainFormParent = loader.load();
        Scene mainFormScene = new Scene(mainFormParent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(mainFormScene);
        window.show();
    }

    /**
     * Validates the data and overwrites old with new.
     */
    public void saveButtonPushed (ActionEvent event) throws IOException {
        if (m.partEntryValidator(nameTextField, priceTextField, inventoryTextField, minTextField, maxTextField)) {

            int toArrayIndex = -1;

            String productName = nameTextField.getText();
            int productInventory = Integer.parseInt(inventoryTextField.getText());
            double productPrice = Double.parseDouble(priceTextField.getText());
            int productMin = Integer.parseInt(minTextField.getText());
            int productMax = Integer.parseInt(maxTextField.getText());

            selectedProduct.setId(selectedProduct.getId());
            selectedProduct.setName(productName);
            selectedProduct.setStock(productInventory);
            selectedProduct.setPrice(productPrice);
            selectedProduct.setMin(productMin);
            selectedProduct.setMax(productMax);

            for (Part part : associatedParts) {
                selectedProduct.addAssociatedPart(part);
            }

            inventory.updateProduct(selectedProduct.getId(), selectedProduct);

            backToMainForm(event);

        }
        else{
            m.errorMessage("Error", "There was an error with your entries.", "Make sure you typed everything in correctly.");
        }
    }

    /**
     * Generates the tableview for unassociated parts.
     */
    private void generateUnassociatedPartsTableView () {
        // Set partsInventory to have all the parts in the inventory object.
        partsInventory.setAll(inventory.getAllParts());
        unassociatedPartsTableView.setItems(partsInventory);
    }

    /**
     * Generates the tableview for associated parts.
     */
    private void generateAssociatedPartsTableView () {
        associatedPartsTableView.setItems(selectedProduct.getAllAssociatedParts());
    }

    /**
     * Adds parts to the associated tableview from the unassociated one.
     * @param event the button press.
     */
    public void addAssociatedPartButtonPushed (ActionEvent event) {
        if (unassociatedPartsTableView.getSelectionModel().getSelectedItems().isEmpty()) {
            m.errorMessage("Error", "No parts were selected", "Remember to select a part to associate with the product");
        }
        else {
            Part selectedPart = unassociatedPartsTableView.getSelectionModel().getSelectedItem();
            selectedProduct.addAssociatedPart(selectedPart);
            unassociatedPartsTableView.getSelectionModel().clearSelection();
            generateAssociatedPartsTableView();
        }
    }

    /**
     * Removes parts from the associated tableview.
     * @param event the button press.
     */
    public void removeAssociatedPartButtonPushed (ActionEvent event) {
        if (associatedPartsTableView.getSelectionModel().getSelectedItems().isEmpty()) {
            m.errorMessage("Error", "No parts were selected", "Remember to select a part to disassociate with the product");
        }
        else {
            boolean result = m.confirmationDialog("Confirmation Dialog", "You clicked the remove button", "Are you sure you want to disassociate this part?");
            if (result) {
                Part selectedPart = associatedPartsTableView.getSelectionModel().getSelectedItem();
                selectedProduct.deleteAssociatedPart(selectedPart);
                generateAssociatedPartsTableView();
            }
        }
    }

    /**
     * Handles the search function for the unassociated parts tableview.
     * @param event the button press.
     */
    public void searchButtonPushed (ActionEvent event) throws IOException {
        String searchQuery = searchUnassociatedPartsTextField.getText();
        if (searchQuery.length() > 0 && searchQuery.chars().allMatch(Character::isDigit)) {
            partsInventory.setAll(inventory.lookupPart(Integer.parseInt(searchQuery)));
            unassociatedPartsTableView.setItems(partsInventory);
            unassociatedPartsTableView.refresh();
        }
        else if (searchQuery.length() > 0 && searchQuery.chars().noneMatch(Character::isDigit)) {
            partsInventory.setAll(inventory.lookupPart(searchQuery));
            unassociatedPartsTableView.setItems(partsInventory);
            unassociatedPartsTableView.refresh();
        }
        else {
            m.errorMessage("Error", "Something went wrong! Try searching again.", "Be sure to enter in a part ID or name in the search field");
        }

    }

    /**
     * Clears the search field and resets the tableview to show all parts.
     * @param event the button press.
     * @throws IOException
     */
    public void clearSearchButtonPushed (ActionEvent event) throws IOException {
        searchUnassociatedPartsTextField.setText("");
        generateUnassociatedPartsTableView();
    }
}