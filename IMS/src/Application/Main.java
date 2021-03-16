package Application;

/**
 *  This class is used to start the application and display the main form of the application.
 *  In the next version of this application, I'd add a database so that the data would persist through an exit.
 *  @author Steven Pfeiffer
 */

import Controllers.MainFormController;
import Model.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.Random;

public class Main extends Application {
    /**
     * Creates the stage and adds a scene to the stage.
     */
    @Override
    public void start(Stage stage) throws Exception {
        int dummyPartsNumber = 0;

        Inventory inventory = new Inventory();

        dummyData(inventory, dummyPartsNumber);

        inventory.addPart(new OutsourcedPart(0, "Muffler Bearings",10.99, 20, 1, 30, "Aperture Laboratories"));
        inventory.addProduct(new Product(0, "Jalopy", 199.99, 2, 1, 10));

        Callback<Class<?>, Object> controllerFactory = controllerType -> {
            if (controllerType == MainFormController.class) {
                return new MainFormController(inventory);
            }
            else {
                throw new IllegalStateException("Unexpected controller class: " + controllerType.getName());
            }
        };
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/Controllers/mainForm.fxml"));
        loader.setControllerFactory(controllerFactory);
        Scene scene = new Scene(loader.load());
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Generates a random alphanumeric string.
     * @param n the number of letters.
     * @return the string.
     */
    static String getAlphaNumericString(int n) {
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {
            int index = (int)(AlphaNumericString.length() * Math.random());
            sb.append(AlphaNumericString.charAt(index));
        }
        return sb.toString();
    }

    /**
     * Generates a random double between rangeMin and rangeMax.
     * @param rangeMin lowest possible value.
     * @param rangeMax highest possible value.
     * @return the double.
     */
    static double getDouble(double rangeMin, double rangeMax) {
        Random r = new Random();
        double randomValue = rangeMin + (rangeMax - rangeMin) * r.nextDouble();
        return randomValue;
    }

    /**
     * Generates a random int between rangeMin and rangeMax.
     * @param rangeMin lowest possible value.
     * @param rangeMax highest possible value.
     * @return the int.
     */
    static int getInt(int rangeMin, int rangeMax) {
        Random r = new Random();
        int randomValue = r.nextInt((rangeMax - rangeMin) + 1) + rangeMin;
        return randomValue;
    }

    /**
     * Generates data for input into our tableviews.
     * @param inventory creates parts and products.
     * @param numberOfItems to make.
     */
    void dummyData(Inventory inventory, int numberOfItems) {
        int n = numberOfItems; // Because we don't want duplicate part id's.

        for (int i = 1; i < numberOfItems; i ++, n++) {
            inventory.addPart(new InhousePart(i, getAlphaNumericString(10),getDouble(1,20000), getInt(1, 20), 1, getInt(500, 1000), getInt(1,1000)));
            inventory.addPart(new OutsourcedPart(n, getAlphaNumericString(10),getDouble(1,20000), getInt(1, 20), 1, getInt(500, 1000), getAlphaNumericString(10)));
            inventory.addProduct(new Product(i, getAlphaNumericString(10), getDouble(1,20000), getInt(1,500),1,getInt(500,1000)));
        }
    }

    /**
     * Launch the scene.
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        launch(args);
    }
}