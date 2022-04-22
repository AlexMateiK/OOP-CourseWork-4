import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Polygon;

import java.io.File;
import java.util.ArrayList;

/**
 * MapPanel is a class that is a part of Map package
 * which is a part of London Property Marketplace program.
 * It is responsible for drawing hexagons on boroughs map.
 * It sets whole GUI of this part of the program i.e. this panel
 * and one that follows after actions invoked on it.
 *
 * @author Mateusz Adamski K20063061
 * @version 1.0
 */
public class MapPanel extends Group {

    private WritableImage boroughsImage;
    private PropertiesAvailabilityIndicator boroughsMap;
    private StatsContainerHelper statsContainerHelper;

    /**
     * Initialises  object of class MapPanel.
     *
     * @param airbnbListings  The array list of properties to be shown.
     */
    public MapPanel(ArrayList<AirbnbListing> airbnbListings, StatsContainerHelper statsContainerHelper) {
        this.statsContainerHelper = statsContainerHelper;

        try {
            setBackground();
            boroughsMap = new PropertiesAvailabilityIndicator(boroughsImage.getWidth(), boroughsImage.getHeight(), airbnbListings);
            drawPolygons();
        }
        catch (NullPointerException e) {
            showImageNotFoundAlert();
            System.out.println("Image not loaded. \n" +
                    "Check if the image is in out folder of the application");
        }
    }

    /**
     * Updates appropriate date so that the application shows appropriate
     * data on an event that list of properties has changed.
     *
     * @param list  The updated list of properties.
     */
    public void update(ArrayList<AirbnbListing> list) {
        try {
            getChildren().clear();
            boroughsMap.update(list);
        }
        catch (NullPointerException e) {
            System.out.println("Provided list is null. \n" +
                    "Check if the list was properly initialised");
        }
        try {
            setBackground();
            drawPolygons();
        }
        catch (NullPointerException e) {
            System.out.println("Image not loaded. \n" +
                    "Check if the image is in out folder of the application");
        }
    }

    /**
     * Is responsible for drawing polygons on the map.
     */
    private void drawPolygons() {
        for(BoroughInformation borough : boroughsMap.getBoroughsInfoList()) {
            Polygon polygon = borough.getPolygon();
            polygon.setFill(borough.getColor());
            polygon.setOnMouseClicked(this::boroughClicked);
            getChildren().add(polygon);
        }
    }

    /**
     * Defines action to be taken on an event that the borough was clicked.
     * @param mouseEvent The mouseEvent that triggered this method invocation.
     */
    private void boroughClicked(MouseEvent mouseEvent) {
        Object polygon = mouseEvent.getSource();
        BoroughInformation borough = null;
        for(BoroughInformation boroughInformation : boroughsMap.getBoroughsInfoList())
            if(boroughInformation.getPolygon() == polygon) borough = boroughInformation;

        new BoroughPropertiesWindow(borough, statsContainerHelper);
    }

    /**
     * Loads image with the map that is to be set as
     * a background of the panel.
     */
    private void setBackground() {

        File mapFile = new File("boroughs4.png");
        //File mapFile = new File("boroughs4.png");
        boroughsImage = loadImage(mapFile);

        Label imageLabel = new Label();
        imageLabel.setGraphic(new ImageView(boroughsImage));
        getChildren().add(imageLabel);
    }

    /**
     * Loads the image file. Returns null if the image was broken
     * or if any other problems occurred during it's loading
     *
     * @param file  The file containing image to be loaded.
     * @return  The loaded image.
     */
    private WritableImage loadImage(File file) {
        Image image = new Image(file.toURI().toString());      // Image is a read-only image

        if (image == null || image.isError()) {
            return null;
        }
        else {
            return new WritableImage(image.getPixelReader(), (int)image.getWidth(), (int)image.getHeight());
        }
    }

    /**
     * Show an error message to the user informing them that a file could not be
     * loaded because it was not found.
     */
    private void showImageNotFoundAlert() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Map Image not Loaded");
        alert.setHeaderText(null);  // Alerts have an optionl header. We don't want one.
        alert.setContentText("The file containing map image was not found. \n"
            + "Check if the image file is in out package of this application.");

        alert.showAndWait();
    }
}
