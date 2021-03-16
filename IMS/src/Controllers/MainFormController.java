package Controllers;

/**
 * This class is the meat and potatoes of the application. It handles control of the main view, error messages, confirmation
 * dialogs, several change-scene methods, as well as control over the data in the application.
 *
 * @author Steven Pfeiffer
 */

import Model.Part;
import Model.Product;
import Model.Inventory;

import javafx.beans.property.DoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.*;

public class MainFormController implements Initializable {

    Inventory inventory;

    private ObservableList<Part> partsInventory = FXCollections.observableArrayList();
    private ObservableList<Product> productsInventory = FXCollections.observableArrayList();


    // Make some button ids, might use em later.
    @FXML private Button exitButton;
    @FXML private Button partsSearchButton;
    @FXML private Button partsClearButton;

    // Configure search TextField.
    @FXML private TextField partsSearchTextField;
    @FXML private TextField productsSearchTextField;

    // Configure parts tableview.
    @FXML public TableView<Part> partsTableView;
    @FXML public TableColumn<Part, Integer> partIDColumn;
    @FXML public TableColumn<Part, String> partNameColumn;
    @FXML public TableColumn<Part, Integer> partInventoryLevelColumn;
    @FXML public TableColumn<Part, Double> partPricePerUnitColumn;

    // Configure products tableview.
    @FXML public TableView<Product> productsTableView;
    @FXML public TableColumn<Product, Integer> productIDColumn;
    @FXML public TableColumn<Product, String> productNameColumn;
    @FXML public TableColumn<Product, Integer> productInventoryLevelColumn;
    @FXML public TableColumn<Product, Double> productPricePerUnitColumn;

    // MainFormController constructor.
    public MainFormController(Inventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Sets up columns to recieve data from the different models and formats the data appropriately.
     * Then, places the data in each tableview.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        DoubleProperty myDouble;

        // Setup columns to display data from Parts model.
        partIDColumn.setCellValueFactory(new PropertyValueFactory<Part, Integer>("id"));
        partNameColumn.setCellValueFactory(new PropertyValueFactory<Part, String>("name"));
        partInventoryLevelColumn.setCellValueFactory(new PropertyValueFactory<Part, Integer>("stock"));
        partPricePerUnitColumn.setCellValueFactory(new PropertyValueFactory<Part, Double>("price"));

        // Setup columns to display data from Products model.
        productIDColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("id"));
        productNameColumn.setCellValueFactory(new PropertyValueFactory<Product, String>("name"));
        productInventoryLevelColumn.setCellValueFactory(new PropertyValueFactory<Product, Integer>("stock"));
        productPricePerUnitColumn.setCellValueFactory(new PropertyValueFactory<Product, Double>("price"));

        // Configure currency display styles.
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        partPricePerUnitColumn.setCellFactory(tc -> new TableCell<Part, Double>() {
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(price));
                }
            }
        });
        productPricePerUnitColumn.setCellFactory(tc -> new TableCell<Product, Double>() {
            protected void updateItem(Double price, boolean empty) {
                super.updateItem(price, empty);
                if (empty) {
                    setText(null);
                } else {
                    setText(currencyFormat.format(price));
                }
            }
        });

        // Call the method to insert data into the tableView.
        createTableView(true, partsInventory, null, partsTableView);
        createTableView(false, null, productsInventory, productsTableView);
    }

    /**
     * Handles error messages throughout the application.
     * @param title the error title.
     * @param header the error header.
     * @param content the error content.
     */
    public void errorMessage (String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    /**
     * Handles confirmation dialog when user has a yes/no option.
     * @param title the confirmation dialog title.
     * @param header the confirmation dialog header.
     * @param content the confirmation dialog content.
     * @return boolean based on user input.
     */
    public boolean confirmationDialog (String title, String header, String content) {
        ButtonType yes = new ButtonType("Yes", ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION,"",yes,no);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.orElse(no) == yes) {
            return true;
        }
        else {
            return false;
        }
    }

    /**
     * Method used to easily change scenes.
     * @param event the button press.
     * @param url the location of the fxml file for the controller.
     * @param controller the name of the controller.
     * @param selectedPart passes data to the modify part controller.
     * @param selectedProduct passes data to the modify product controller.
     * @throws IOException handles exceptions.
     */
    public void changeSceneMethod(ActionEvent event, String url, String controller, Part selectedPart, Product selectedProduct) throws IOException{
        FXMLLoader loader = new FXMLLoader(getClass().getResource(url));
        if (controller == "MainFormController") {
            loader = new FXMLLoader(getClass().getResource(url));
            Callback<Class<?>, Object> controllerFactory = controllerType -> {
                if (controllerType == MainFormController.class) {
                    return new MainFormController(inventory);
                }
                else {
                    throw new IllegalStateException("Unexpected controller class: " + controllerType.getName());
                }
            };
            loader.setControllerFactory(controllerFactory);
        }
        else if (controller == "AddPartController") {
            loader.setControllerFactory(type ->new AddPartController(this.inventory));
        }
        else if (controller == "ModifyPartController") {
            loader.setControllerFactory(type ->new ModifyPartController(this.inventory, selectedPart));
        }
        else if (controller == "AddProductController") {
            loader.setControllerFactory(type ->new AddProductController(this.inventory));
        }
        else if (controller == "ModifyProductController") {
            loader.setControllerFactory(type ->new ModifyProductController(this.inventory, selectedProduct));
        }
        else if (controller == null) {
            System.out.println("You made a whoopsie!");
        }
        Parent parent = loader.load();
        Scene scene = new Scene(parent);
        Stage window = (Stage) ((Node)event.getSource()).getScene().getWindow();
        window.setScene(scene);
        window.show();
    }

    /**
     * Handles the search function for the Parts tableview.
     */
    public void searchPartsButtonPushed (ActionEvent event) throws IOException {
        String searchQuery = partsSearchTextField.getText();
        if (searchQuery.length() > 0 && searchQuery.chars().allMatch(Character::isDigit)) {
            partsInventory.setAll(inventory.lookupPart(Integer.parseInt(searchQuery)));
            partsTableView.setItems(partsInventory);
            partsTableView.refresh();
        }
        else if (searchQuery.length() > 0 && searchQuery.chars().noneMatch(Character::isDigit)) {
            partsInventory.setAll(inventory.lookupPart(searchQuery));
            partsTableView.setItems(partsInventory);
            partsTableView.refresh();
        }
        else {
            errorMessage("Error", "Something went wrong! Try searching again.", "Be sure to enter in a part ID or name in the search field");
        }
    }

    /**
     * Handles the search function for the Products tableview.
     */
    public void searchProductsButtonPushed (ActionEvent event) throws IOException {
        String searchQuery = productsSearchTextField.getText();
        if (searchQuery.length() > 0 && searchQuery.chars().allMatch(Character::isDigit)) {
            productsInventory.setAll(inventory.lookupProduct(Integer.parseInt(searchQuery)));
            productsTableView.setItems(productsInventory);
            productsTableView.refresh();
        }
//        else (searchQuery.matches("^[a-zA-Z\\s]*$")) {
        else if (searchQuery.length() > 0 && searchQuery.chars().noneMatch(Character::isDigit)) {
            productsInventory.setAll(inventory.lookupProduct(searchQuery));
            productsTableView.setItems(productsInventory);
            productsTableView.refresh();
        }
        else {
            errorMessage("Error", "Something went wrong! Try searching again.", "Be sure to enter in a product ID or name in the search field");
        }
    }

    /**
     * Clears the search textfield for parts and resets the tableview.
     */
    public void clearPartsButtonPushed (ActionEvent event) throws IOException {
        partsSearchTextField.setText("");
        createTableView(true, partsInventory, null, partsTableView);
    }

    /**
     * Clears the search textfield for products and resets the tableview.
     */
    public void clearProductsButtonPushed (ActionEvent event) throws IOException {
        productsSearchTextField.setText("");
        createTableView(false, null, productsInventory, productsTableView);
    }

    /** When this method is called, it will change the scene to the Add Parts scene. */
    public void addPartButtonPushed (ActionEvent event) throws IOException {
        changeSceneMethod(event, "/Controllers/addPart.fxml", "AddPartController", null, null);
    }

    /** When this method is called, it will change the scene to the Add Products scene. */
    public void addProductButtonPushed (ActionEvent event) throws IOException {
        changeSceneMethod(event, "/Controllers/addProduct.fxml", "AddProductController", null, null);
    }

    /** When this method is called, the application will close after a confirmation dialog prompt.*/
    @FXML void exitButtonPressed (ActionEvent event) {
        boolean result = confirmationDialog("Confirmation Dialog", "You clicked the exit button.", "Are you sure you want to exit?");
        if (result) {
            Stage stage = (Stage) exitButton.getScene().getWindow();
            stage.close();
        }
    }

    /**
     * This method adds the data from storage to the tableviews.
     * @param parts if true, the data is for parts. False is for products.
     * @param partsInv the list of parts.
     * @param productsInv the list of products.
     * @param tableView the tableview those lists are getting added to.
     */
    private void createTableView (boolean parts, ObservableList<Part> partsInv, ObservableList<Product> productsInv, TableView tableView) {
        if (parts) {
            partsInv.setAll(inventory.getAllParts());
            tableView.setItems(partsInv);
            tableView.refresh();
        }
        else {
            productsInv.setAll(inventory.getAllProducts());
            tableView.setItems(productsInv);
            tableView.refresh();
        }
    }

    /** This method will remove the selected rows from the parts table as well as the Parts object.*/
    public void partsDeleteButtonPushed (ActionEvent event) {

        // Handle exception for no part selected when delete is clicked.
        if (partsTableView.getSelectionModel().getSelectedItems().isEmpty()) {
            errorMessage("Error", "No parts were selected.", "Remember to select a part to delete.");
        }

        // Delete the part from the tableview, as well as the inventory object.
        if (!partsTableView.getSelectionModel().getSelectedItems().isEmpty()){

            // Figure out which product is selected.
            Part partToDelete = partsTableView.getSelectionModel().getSelectedItem();

            // Get the name of the part and pass it to the Alert Message.
            Part partName = (Part)partsTableView.getSelectionModel().getSelectedItem();
            String productToDeleteMessage = "Are you sure you want to delete " + partName.getName() + "?";
            boolean result = confirmationDialog("Confirmation Dialog", "You clicked the delete button.", productToDeleteMessage);
            if (result) {
                // Delete the part.
                inventory.deletePart(partToDelete);

                // Update the tableview container.
                createTableView(true, partsInventory, null, partsTableView);
            }
        }
    }
    /** This method will remove the selected rows from the products table as well as the Products object.*/
    public void productsDeleteButtonPushed (ActionEvent event) {

        // Handle exception for no part selected when delete is clicked.
        if (productsTableView.getSelectionModel().getSelectedItems().isEmpty()) {
            errorMessage("Error", "No products were selected.", "Remember to select a product to delete.");
        }
        else if (!productsTableView.getSelectionModel().getSelectedItem().getAllAssociatedParts().isEmpty()) {
            errorMessage("Error", "You cannot delete this product.", "This product has a part or parts associated with it and cannot be deleted.");
        }

        // Delete the product from the tableview, as well as the inventory object.
        if (!productsTableView.getSelectionModel().getSelectedItems().isEmpty() && productsTableView.getSelectionModel().getSelectedItem().getAllAssociatedParts().isEmpty()){

            // Figure out which product is selected.
            Product productToDelete = productsTableView.getSelectionModel().getSelectedItem();

            // Get the name of the part and pass it to the Alert Message.
            Product productName = (Product)productsTableView.getSelectionModel().getSelectedItem();
            String productToDeleteMessage = "Are you sure you want to delete " + productName.getName() + "?";
            boolean result = confirmationDialog("Confirmation Dialog", "You clicked the delete button.", productToDeleteMessage);
            if (result) {
                // Delete the product.
                inventory.deleteProduct(productToDelete);

                // Update the tableview container.
                createTableView(false, null, productsInventory, productsTableView);
            }
        }
    }

    /**
     * Handles buttonpress to modify a part.
     * @param event the button press.
     * @throws IOException handles exceptions.
     */
    public void partsModifyButtonPushed (ActionEvent event) throws IOException {
        if (partsTableView.getSelectionModel().getSelectedItems().isEmpty()) {
            errorMessage("Error", "No parts were selected.", "Remember to select a part to modify.");
        }

        else {
            Part selectedPart = partsTableView.getSelectionModel().getSelectedItem();
            changeSceneMethod(event, "/Controllers/modifyPart.fxml", "ModifyPartController", selectedPart, null);
        }
    }

    /**
     * Handles buttonpress to modify a product.
     * @param event the button press.
     * @throws IOException handles exceptions.
     */
    public void productsModifyButtonPushed (ActionEvent event) throws IOException {
        if (productsTableView.getSelectionModel().getSelectedItems().isEmpty()) {
            errorMessage("Error", "No parts were selected.", "Remember to select a product to modify.");
        }

        else {
            Product selectedProduct = productsTableView.getSelectionModel().getSelectedItem();
            changeSceneMethod(event, "/Controllers/modifyProduct.fxml", "ModifyProductController", null, selectedProduct);
        }
    }

    /**
     * Validates part and product data entry to make sure it's all logical.
     * @param nameTextField the name of the item.
     * @param priceTextField the price of the item.
     * @param inventoryTextField the stock of the item.
     * @param minTextField the minimum amount in stock.
     * @param maxTextField the maximum amount in stock.
     * @return true if the data checks out.
     */
    public boolean partEntryValidator (TextField nameTextField, TextField priceTextField, TextField inventoryTextField, TextField minTextField, TextField maxTextField) {
        if (nameTextField.getText().isEmpty() || priceTextField.getText().isEmpty()) {
            return false;
        }
        if (!inventoryTextField.getText().chars().allMatch(Character::isDigit) || inventoryTextField.getText().isEmpty()) {
            return false;
        }
        if (!minTextField.getText().chars().allMatch(Character::isDigit) || minTextField.getText().isEmpty()) {
            return false;
        }
        if (!maxTextField.getText().chars().allMatch(Character::isDigit) || maxTextField.getText().isEmpty()) {
            return false;
        }
        if (Integer.parseInt(inventoryTextField.getText()) < Integer.parseInt(minTextField.getText())) {
            return false;
        }
        if (Integer.parseInt(inventoryTextField.getText()) > Integer.parseInt(maxTextField.getText())) {
            return false;
        }
        if (Integer.parseInt(minTextField.getText()) > Integer.parseInt(maxTextField.getText())) {
            return false;
        }
        return true;
    }

    /**
     * Uses all known IDs as well as the current amount of parts or products to calculate a unique ID number for a new item.
     * @param id an array of all the existing part or product id's
     * @param size the total number of all parts or products.
     * @return unique, generated Model.Part or Product ID for creation.
     */
    public static int generateNewID (int id[], int size) {
        int generatedProductID = (size + 1 ) * ( size + 2 ) / 2;
        for (int i = 0; i < size; i++ )
            generatedProductID -= id[i];
        return generatedProductID;
    }
}
