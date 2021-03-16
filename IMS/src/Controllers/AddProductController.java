package Controllers;

/**
 * This class is used to display the add product scene as well as handle data entry, associated parts, and storage.
 * This one was a very tough cookie to crack because it took me quite some time to figure out how to add an associated
 * part to a product that doesn't already exist. I kept trying to add it to a blank product object but it never worked,
 * and only gave a nullPointerException, cause of course it would. One I realized I could store the associated parts
 * temporarily inside an already initialized arraylist, this part became easy.
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

public class AddProductController implements Initializable {

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
    Product product;
    MainFormController m = new MainFormController(inventory);

    private ObservableList<Part> partsInventory = FXCollections.observableArrayList();
    private ObservableList<Part> associatedParts = FXCollections.observableArrayList();
    private ObservableList<Part> partsList = FXCollections.observableArrayList();

    /**
     * Inventory constructor.
     * @param inventory constructs the inventory.
     */
    public AddProductController(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * One scene is loaded, assigns values to tableview columns, and formats the data.
     * @param url location for relative paths to root.
     * @param resourceBundle used to localize if needed.
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
    }

    /**
     * Sends user back to main form.
     * @param event the buttonpress.
     * @throws IOException an exception.
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

    /** This method will take the data from the observableList, then match the data to the columns.*/
    public void generateUnassociatedPartsTableView () {
        // Set partsInventory to have all the parts in the inventory object.
        partsInventory.setAll(inventory.getAllParts());
        unassociatedPartsTableView.setItems(partsInventory);
    }

    /**
     * Generates the tableview for associated parts.
     */
    public void generateAssociatedPartsTableView () {
        associatedPartsTableView.setItems(associatedParts);

    }

    /**
     * Handles part transfer to another table when a button is pressed.
     * @param event the buttonpress.
     */
    public void addAssociatedPartButtonPushed (ActionEvent event) {
        if (unassociatedPartsTableView.getSelectionModel().getSelectedItems().isEmpty()) {
            m.errorMessage("Error", "No parts were selected", "Remember to select a part to associate with the product");
        }
        else {
            Part selectedPart = unassociatedPartsTableView.getSelectionModel().getSelectedItem();
            partsList.add(selectedPart);
            associatedPartsTableView.setItems(partsList);
            unassociatedPartsTableView.getSelectionModel().clearSelection();
        }
    }

    /**
     * Handles part transfer to another table when a button is pressed.
     * @param event the buttonpress.
     */
    public void removeAssociatedPartButtonPushed (ActionEvent event) {
        if (associatedPartsTableView.getSelectionModel().getSelectedItems().isEmpty()) {
            m.errorMessage("Error", "No parts were selected", "Remember to select a part to disassociate with the product");
        }
        else {
            boolean result = m.confirmationDialog("Confirmation Dialog", "You clicked the remove button", "Are you sure you want to disassociate this part?");
            if (result) {
                Part selectedPart = associatedPartsTableView.getSelectionModel().getSelectedItem();
                partsList.remove(selectedPart);
                associatedPartsTableView.setItems(partsList);
                associatedPartsTableView.getSelectionModel().clearSelection();
            }
        }
    }


    /**
     * Creates new Model.Part object using the textfields in the scene and adds it to the tableview.
     * @param event the button press.
     */
    public void saveButtonPushed (ActionEvent event) throws IOException {
        // TODO: Make an exception notice for saving with no data entered.
        if (m.partEntryValidator(nameTextField, priceTextField, inventoryTextField, minTextField, maxTextField)) {
            // Create the data we need to pass to the Model.Part ID Generator.
            int[] productIDs = new int[inventory.getAllProducts().size()];
            for (int i = 0; i < inventory.getAllProducts().size(); i++) {
                productIDs[i] = inventory.getAllProducts().get(i).getId();
            }

            // Attach text fields to variables.
            int productID = m.generateNewID(productIDs, inventory.getAllProducts().size());
            String productName = nameTextField.getText();
            int productInventory = Integer.parseInt(inventoryTextField.getText());
            double productPrice = Double.parseDouble(priceTextField.getText());
            int productMin = Integer.parseInt(minTextField.getText());
            int productMax = Integer.parseInt(maxTextField.getText());

            Product newProduct = (new Product(productID, productName, productPrice, productInventory, productMin, productMax));

            for (Part part : partsList) {
                newProduct.addAssociatedPart(part);
            }

            inventory.addProduct(newProduct);

            backToMainForm(event);
        }
        else{
            m.errorMessage("Error", "There was an error with your entries.", "Make sure you typed everything in correctly.");
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
