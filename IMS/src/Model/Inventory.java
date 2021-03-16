package Model;
/**
 * @author Steven Pfeiffer
 */

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Inventory {

    private static ObservableList<Part> allParts = FXCollections.observableArrayList();
    private static ObservableList<Product> allProducts = FXCollections.observableArrayList();

    /**
     * Adds a new part.
     * @param newPart the part to add.
     */
    public static void addPart (Part newPart) {
        if (newPart != null) {
            allParts.add(newPart);
        }
    }

    /**
     * Adds a new product.
     * @param newProduct the product to add.
     */
    public static void addProduct (Product newProduct) {
        if (newProduct != null) {
            allProducts.add(newProduct);
        }
    }

    /**
     * Gets results for search queries on the ids.
     * @param partID the number to look for.
     * @return the matching part.
     */
    public static Part lookupPart (int partID) {
        // Create a array to be used to slap some matching objects into. It is later passed back to the controller through a return.
        Part matchingParts = null;

        for (int i = 0; i < allParts.size(); i++){
            // Use the matching part ids to figure out which parts match the search, add them to the array, then return em.
            if (allParts.get(i).getId() == partID) {
                matchingParts = allParts.get(i);
            }
        }

        return matchingParts;
    }

    /**
     * Gets results for search queries on the ids.
     * @param productID the number to look for.
     * @return the matching product.
     */
    public static Product lookupProduct (int productID) {
        // Create a array to be used to slap some matching objects into. It is later passed back to the controller through a return.
        Product matchingProducts = null;

        for (int i = 0; i < allProducts.size(); i++){
            // Use the matching part ids to figure out which parts match the search, add them to the array, then return em.
            if (allProducts.get(i).getId() == productID) {
                matchingProducts = allProducts.get(i);
            }
        }

        return matchingProducts;
    }

    /**
     * Gets results for search queries on the names.
     * @param partName the string to look for.
     * @return the matching parts.
     */
    public static ObservableList<Part> lookupPart (String partName) {
        // Create a array to be used to slap some matching objects into. It is later passed back to the controller through a return.
        ObservableList<Part> matchingParts = FXCollections.observableArrayList();
        for (int i = 0; i < allParts.size(); i++) {
            // Use the matching part ids to figure out which parts match the search, add them to the array, then return em.
            if (allParts.get(i).getName().toLowerCase().contains(partName.toLowerCase())) {
                matchingParts.add(allParts.get(i));
            }
        }
        return matchingParts;
    }

    /**
     * Gets results for search queries on the names.
     * @param productName the string to look for.
     * @return the matching products.
     */
    public static ObservableList<Product> lookupProduct (String productName) {
        // Create a array to be used to slap some matching objects into. It is later passed back to the controller through a return.
        ObservableList<Product> matchingProducts = FXCollections.observableArrayList();

        for (int i = 0; i < allProducts.size(); i++) {
            // Use the matching part ids to figure out which parts match the search, add them to the array, then return em.
            if (allProducts.get(i).getName().toLowerCase().contains(productName.toLowerCase())) {
                matchingProducts.add(allProducts.get(i));
            }
        }
        return matchingProducts;
    }

    /**
     * Updates a preexisting part with new information
     * @param index of the part.
     * @param selectedPart the new information.
     */
    public static void updatePart (int index, Part selectedPart) {
        allParts.set(index, selectedPart);
    }

    /**
     * Updates a preexisting product with new information
     * @param index of the product.
     * @param selectedProduct the new information.
     */
    public static void updateProduct(int index, Product newProduct) {
        allProducts.set(index, newProduct);
    }

    /**
     * Deletes a part.
     * @param partToDelete the part to delete.
     * @return true if the part was found and deleted.
     */
    public static boolean deletePart(Part partToDelete) {
        for (int i = 0; i < allParts.size(); i++) {
            if (allParts.get(i).getId() == partToDelete.getId()) {
                allParts.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Deletes a product.
     * @param productToDelete the product to delete.
     * @return true if the product was found and deleted.
     */
    public static boolean deleteProduct(Product productToDelete) {
        for (int i = 0; i < allProducts.size(); i++) {
            if (allProducts.get(i).getId() == productToDelete.getId()) {
                allProducts.remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Gets a list of all parts.
     * @return all of the parts.
     */
    public static ObservableList<Part> getAllParts() {
        return allParts;
    }

    /**
     * Gets a list of all the products.
     * @return all of the products.
     */
    public static ObservableList<Product> getAllProducts() {
        return allProducts;
    }
}
