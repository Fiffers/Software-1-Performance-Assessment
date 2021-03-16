package Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Product {
    private ObservableList<Part> associatedParts = FXCollections.observableArrayList();
    private int id;
    private String name;
    private double price;
    private int stock;
    private int min;
    private int max;

    public Product(int id, String name, double price, int stock, int min, int max) {
        setId(id);
        setName(name);
        setPrice(price);
        setStock(stock);
        setMin(min);
        setMax(max);
    }

    /**
     * Sets the product ID.
     * @param id the id
     */
    public void setId (int id) {
        this.id = id;
    }

    /**
     * Sets the product name.
     * @param name the product name.
     */
    public void setName (String name) {
        this.name = name;
    }

    /**
     * Sets the product price.
     * @param price the product price.
     */
    public void setPrice (double price) {
        this.price = price;
    }

    /**
     * Sets the product stock.
     * @param stock the product stock.
     */
    public void setStock (int stock) {
        this.stock = stock;
    }

    /**
     * Sets the minimum stock.
     * @param min the minimum stock.
     */
    public void setMin (int min) {
        this.min = min;
    }

    /**
     * Sets the maximum stock.
     * @param max the maximum stock.
     */
    public void setMax (int max) {
        this.max = max;
    }

    /**
     * Gets the product ID.
     * @return the product ID.
     */
    public int getId () {
        return this.id;
    }

    /**
     * Gets the product name.
     * @return the product name.
     */
    public String getName () {
        return this.name;
    }

    /**
     * Gets the product price.
     * @return the product price.
     */
    public double getPrice () {
        return this.price;
    }

    /**
     * Gets the current stock.
     * @return the current stock.
     */
    public int getStock () {
        return this.stock;
    }

    /**
     * Gets the minimum stock.
     * @return the minimum stock.
     */
    public int getMin () {
        return this.min;
    }

    /**
     * Gets the maximum stock.
     * @return the maximum stock.
     */
    public int getMax () {
        return this.max;
    }

    /**
     * Adds an associated part to a product.
     * @param part the part to add.
     */
    public void addAssociatedPart(Part part) {
        associatedParts.add(part);
    }

    /**
     * Deletes an associated part from a product.
     * @param selectedAssociatedPart the part to delete.
     * @return true if deleted.
     */
    public boolean deleteAssociatedPart(Part selectedAssociatedPart) {
        return getAllAssociatedParts().remove(selectedAssociatedPart);
    }

    /**
     * Gets a list of all associated parts.
     * @return the list of all associated parts.
     */
    public ObservableList<Part> getAllAssociatedParts () {
        return associatedParts;
    }

}
