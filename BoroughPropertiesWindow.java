//import AirbnbListing;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * BoroughPropertiesWindow is a class that is a part of Map package
 * which is a part of London Property Marketplace program. It is responsible
 * for creating a list of properties from a particular borough entered by a user
 * and displaying it in a new window.
 * The list can be ordered by price, number of reviews or host name.
 * A new window with detailed information about a property will be displayed
 * after the user will click any of the property on the list.
 *
 * @author Mateusz Adamski K20063061, Alexandru Matei k20054925
 * @version 1.1
 */
public class BoroughPropertiesWindow {
    //the main panel of the list
    private BorderPane root;
    // the list of properties to be displayed
    private ArrayList<AirbnbListing> listings;

    private StatsContainerHelper statsContainerHelper;
    /**
     * Initialises the BoroughPropertiesWindow. By default the items in the list
     * are displayed in the order they were passed to this constructor.
     *
     * @param borough  The BoroughInformation instant which information is to be displayed.
     */
    public BoroughPropertiesWindow(BoroughInformation borough, StatsContainerHelper statsContainerHelper) {
        this.statsContainerHelper = statsContainerHelper;

        Stage stage = new Stage();
        //point list to the right list stored in a BoroughInformation object passed as a parameter
        listings = borough.getProperties();

        ComboBox<String> cmb = initialiseComboBox();

        root = new BorderPane();
        ListView listView = initialiseList();
        root.setCenter(listView);
        Text text = new Text( "Order by: ");
        text.setFont(new Font(20));
        root.setTop(new HBox(text, cmb));

        stage.setTitle(borough.getName() + " properties:");
        stage.setScene(new Scene(root));
        stage.sizeToScene();
        stage.setMinWidth(listView.getPrefWidth());
        stage.setMinHeight(listView.getPrefHeight());
        stage.show();

        // Hide the former current window
        //((Node)(event.getSource())).getScene().getWindow().hide();
    }

    /**
     * Initialise ComboBox of strings that will control the order in the list.
     *
     * @return  The ComboBox of strings object to be used to sort a list.
     */
    private ComboBox<String> initialiseComboBox() {
        ComboBox<String> cmb = new ComboBox<>();

        String priceLowToHigh = "Price: low to high";
        String priceHighToLow = "Price: high to low";

        String numberOfReviewsLowToHigh = "Number of reviews: low to high";
        String numberOfReviewsHighToLow = "Number of reviews: high to low";

        String alphabeticallyLowToHigh = "Alphabetically: low to high";
        String alphabeticallyHighToLow = "Alphabetically: high to low";

        cmb.getItems().addAll(priceLowToHigh, priceHighToLow,
                numberOfReviewsLowToHigh, numberOfReviewsHighToLow,
                alphabeticallyLowToHigh, alphabeticallyHighToLow);
        cmb.setOnAction(this::comboBoxChoice);
        return cmb;
    }

    /**
     * Recognises the ComboBox current state and orders the list appropriately.
     *
     * @param actionEvent  The actionEvent that triggered evocation of this method.
     * @return           The image object or null if it was not a valid image file.
     */
    private void comboBoxChoice(ActionEvent actionEvent) {
        ComboBox cmb = (ComboBox) actionEvent.getSource();
        String  event = (String) cmb.getValue();
        switch (event) {
            case "Price: low to high":
                priceLowToHigh();
                break;

            case "Price: high to low":
                priceHighToLow();
                break;

            case "Number of reviews: low to high":
                numberOfReviewsLowToHigh();
                break;

            case "Number of reviews: high to low":
                numberOfReviewsHighToLow();
                break;

            case "Alphabetically: low to high":
                alphabeticallyLowToHigh();
                break;

            case "Alphabetically: high to low":
                alphabeticallyHighToLow();
                break;
        }
    }

    /**
     * Orders list by price from the cheapest to most expensive.
     */
    private void priceLowToHigh() {
        listings.sort(Comparator.comparingInt(AirbnbListing::getPrice));
        root.setCenter(initialiseList());
    }

    /**
     * Orders list by price from the most expensive to the cheapest.
     */
    private void priceHighToLow() {
        Comparator<AirbnbListing> comparator = Comparator.comparingInt(AirbnbListing::getPrice);
        listings.sort(comparator.reversed());
        root.setCenter(initialiseList());
    }

    /**
     * Orders list by he number of reviews from the smallest to the biggest.
     */
    private void numberOfReviewsLowToHigh() {
        listings.sort(Comparator.comparingInt(AirbnbListing::getNumberOfReviews));
        root.setCenter(initialiseList());
    }

    /**
     * Orders list by he number of reviews from the biggest to the smallest.
     */
    private void numberOfReviewsHighToLow() {
        Comparator<AirbnbListing> comparator = Comparator.comparingInt(AirbnbListing::getNumberOfReviews);
        listings.sort(comparator.reversed());
        root.setCenter(initialiseList());
    }

    /**
     * Orders list alphabetically from special characters and 'a' to 'z'.
     */
    private void alphabeticallyLowToHigh() {
        listings.sort(Comparator.comparing(AirbnbListing::getHost_name));
        root.setCenter(initialiseList());
    }

    /**
     * Orders list alphabetically from 'z' to special characters and 'a'.
     */
    private void alphabeticallyHighToLow() {
        Comparator<AirbnbListing> comparator = Comparator.comparing(AirbnbListing::getHost_name);
        listings.sort(comparator.reversed());
        root.setCenter(initialiseList());
    }

    /**
     * Initialises the list and creates it's visual representation.
     *
     * @return  The ArrayList of Text entries.
     */
    private ListView<Text> initialiseList() {
        //The JavaFX version of an ArrayList to be used in an instant of the ListView class.
        ObservableList entry = FXCollections.observableArrayList();

        //creates entries (Text objects) from the list from the borough
        for(int i = 0; i < listings.size(); i++ ){
            AirbnbListing property = listings.get(i);
            Text text = new Text("Host name: " + property.getHost_name() + "\n" +
                    "Price: " + property.getPrice() + "\n" +
                    "Number of reviews: " + property.getNumberOfReviews() + "\n" +
                    "Minimal number of nights: " + property.getMinimumNights());
            text.setWrappingWidth(400); // if a text is very long it will be wrapped
            entry.add(text);
            text.setOnMouseClicked(e -> showProperty(e, property));//add the action if clicked

        }
        
        ListView listView = new ListView(entry);
        listView.setPrefSize(200, 350);
        listView.setEditable(false);//we don't want to allow users to edit the list

        listView.setItems(entry);

        return listView;
    }

    /**
     * Creates the detailed information about a property clicked by the user
     * and shows it in a new window.
     *
     * @param borough  The property which information is to be displayed..
     */
    private void showProperty(MouseEvent mouseEvent, AirbnbListing borough) {
        Stage stage = new Stage();
        VBox pane = new VBox();
        Text text = new Text(
                "ID:  " + borough.getId() + '\n' +
                        "Name:  " + borough.getName() + '\n' +
                        "The ID of the host:  " + borough.getHost_id() + '\n' +
                        "The name of the host:  " + borough.getHost_name() + '\n' +
                        "Neighbourhood:  " + borough.getNeighbourhood() + '\n' +
                        "Latitude:  " + borough.getLatitude() + '\n' +
                        "Longitude:  " + borough.getLatitude() + '\n' +
                        "The type of the property:  " + borough.getRoom_type() + '\n' +
                        "Price:  " + borough.getPrice() + '\n' +
                        "The minimum number of nights the listed property must be booked for:  "
                                + borough.getMinimumNights() + '\n' +
                        "Number of Reviews:  " + borough.getNumberOfReviews() + '\n' +
                        "The date of the last review:  " + borough.getLastReview() + '\n' +
                        "Number of Reviews Per Month:  " + borough.getReviewsPerMonth() + '\n' +
                        "The total number of listings the host holds across AirBnB:  "
                                + borough.getCalculatedHostListingsCount() + '\n' +
                        "The total number of days in the year that the property is available for:  "
                                + borough.getAvailability365()
        );
        text.setWrappingWidth(450);

        Text favText = new Text();
        if (StatLogicHelper.checkIfFavorite(borough))favText.setText("Favorited");
        else favText.setText("Not favorited");

        Button toggleFavButton = new Button("Toggle favorite");
        toggleFavButton.setOnAction(e -> toggleFavorite(e, borough, favText));



        pane.getChildren().addAll(text,toggleFavButton,favText);


        stage.setTitle("Property detailed description:");
        stage.setScene(new Scene(pane, 450, 450));
        stage.show();
    }

    private void toggleFavorite(ActionEvent actionEvent, AirbnbListing property, Text text){
        if (StatLogicHelper.checkIfFavorite(property)){
            StatLogicHelper.removeFromFavorites(property);
            text.setText("Not favorited");
            statsContainerHelper.updateOnlyFavStats();
        }
        else {
            StatLogicHelper.addToFavorites(property);
            text.setText("Favorited");
            statsContainerHelper.updateOnlyFavStats();
        }
    }
}