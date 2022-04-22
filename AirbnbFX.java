import javafx.application.Application;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

/**
 * Main GUI class
 * 
 * @author Ejaz Karim, K20059213, Mateusz Adamski K20063061
 */

public class AirbnbFX extends Application implements Initializable {

    @FXML
    public Spinner<Integer> toSpinner;

    @FXML
    public Spinner<Integer> fromSpinner;

    @FXML
    public Button previousButton;

    @FXML
    public Button nextButton;

    private Boolean incorrectPriceRange = true;

    @FXML
    public BorderPane borderPane;

    private BorderPane newPaneltest;

    private ArrayList<AirbnbListing> listing;

    private ArrayList<AirbnbListing> temporaryListing;

    @FXML
    private TextField incorrectPriceRangeText;

    private int indexButton = 0;

    private ArrayList<Node> panels = new ArrayList<>();

    private boolean pastFirstValidRange = false;

    private StatsContainerHelper statsContainerHelper;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setResizable(false);

        Parent airbnbFX = FXMLLoader.load(getClass().getResource("AirbnbFX.fxml"));
        primaryStage.setTitle("Airbnb London");
        Scene scene1 = new Scene(airbnbFX);
        primaryStage.setScene(scene1);
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {

    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        listing = new AirbnbDataLoader().load();
        temporaryListing = listing;

        SpinnerValueFactory<Integer> toValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000);
        SpinnerValueFactory<Integer> fromValue = new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000);
        toSpinner.setValueFactory(toValue);
        fromSpinner.setValueFactory(fromValue);

        toSpinner.setOnMouseClicked(this::toSpinnerClicked);
        fromSpinner.setOnMouseClicked(this::fromSpinnerClicked);

        toSpinner.setOnKeyPressed(this::toSpinnerClicked);
        fromSpinner.setOnKeyPressed(this::toSpinnerClicked);

        disableButtons(incorrectPriceRange);

        nextButton.setOnMouseClicked(this::nextButtonClicked);
        previousButton.setOnMouseClicked(this::previousButtonClicked);


        borderPane.setCenter(initializeWelcomePanel());

    }


    public void nextButtonClicked(MouseEvent mouseEvent){
        indexButton = (indexButton + 1) % panels.size();
        borderPane.setCenter(panels.get(indexButton));

    }

    public void previousButtonClicked(MouseEvent mouseEvent){
        indexButton = (indexButton + panels.size() - 1) % panels.size();
        borderPane.setCenter(panels.get(indexButton));

    }

    public void disableButtons(Boolean disable){
        nextButton.setDisable(disable);
        previousButton.setDisable(disable);

    }


    public void fromSpinnerClicked(Event event){
        checkPriceRange(fromSpinner.getValue(), toSpinner.getValue());
    }

    public void toSpinnerClicked(Event event){
        checkPriceRange(fromSpinner.getValue(), toSpinner.getValue());
    }

    public void checkPriceRange(int toValue, int fromValue){

        if(toValue < fromValue) {
            incorrectPriceRangeAlert();
            incorrectPriceRange = true;
            disableButtons(incorrectPriceRange);
            borderPane.setCenter(panels.get(0));
            return;
        }
        filterArray(fromValue, toValue);

        if (pastFirstValidRange == false)
        {
            statsContainerHelper = new StatsContainerHelper();

            Pane statisticsPanel = statsContainerHelper.getStatsGrid(temporaryListing);

            panels.add(new MapPanel(temporaryListing, statsContainerHelper));

            panels.add(statisticsPanel);

            pastFirstValidRange = true;
        }
        else {
            MapPanel mapPanel = (MapPanel) panels.get(1);
            mapPanel.update(temporaryListing);

            statsContainerHelper.updateAllStats(temporaryListing);
            //invoke update map/stats panel here
            //MapPanel mapPanel = (MapPanel) panels.get(1);
        }




    }


    /* public void initializeArrayList(){
        panels.addAll(initializeWelcomePanel(), new MapPanel(listing), new StatisticPanel(listing));
    }
    */


    public void incorrectPriceRangeAlert(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Invalid price range");
        alert.setHeaderText(null);
        alert.setContentText("Error: To value is less than From value");
        alert.showAndWait();
    }

    public void filterArray(int fromValue, int toValue){
        temporaryListing = listing;
        temporaryListing = (ArrayList) temporaryListing.stream()
                .filter(e -> e.getPrice() < toValue && e.getPrice() > fromValue)
        .collect(Collectors.toList());
        //temporaryListing.stream().forEach(e -> System.out.println(e.getPrice()));

        incorrectPriceRange = temporaryListing.isEmpty();
        disableButtons(incorrectPriceRange);
    }


    public BorderPane initializeWelcomePanel(){
        BorderPane root = new BorderPane();
        panels.add(root);

        File file = new File("images/airbnbresizedicon.png");

        Image airbnblogo = new Image(file.toURI().toString());

        if(airbnblogo == null || airbnblogo.isError()){
            System.out.println(airbnblogo.getException());
        }

        Label logoLabel = new Label();
        WritableImage writableImage = new WritableImage(airbnblogo.getPixelReader(), (int) airbnblogo.getWidth(), (int) airbnblogo.getHeight());
        logoLabel.setGraphic(new ImageView(writableImage));

        HBox hBox = new HBox();
        VBox vBox = new VBox();

        Text welcome1 = new Text(15, 0,"Welcome to Airbnb");
        Text welcome2 = new Text(15, 0,"To begin, enter a valid price range in the top right");
        Text welcome3 = new Text(15, 0,"Then, you may visit the borough map in the next panel.");
        Text welcome4 = new Text(15, 0, "Click on any borough to list its properties. Click on any property to see extra details.");
        Text welcome5 = new Text(15, 0,"When viewing a property, you may choose to make it one of your favorites.");
        Text welcome6 = new Text(15, 0,"If you need more help with your analysis of the properties, press next again and visit the next panel.");
        Text welcome7 = new Text(15, 0,"The statistics panel offers four basic statistics based on the price range.");
        Text welcome8 = new Text(15, 0,"There are four more statistics that refer to your favorited properties, to help you with your choice.");

        welcome1.setFont(new Font(40));
        welcome2.setFont(new Font(20));
        welcome3.setFont(new Font(20));
        welcome4.setFont(new Font(20));
        welcome5.setFont(new Font(20));
        welcome6.setFont(new Font(20));
        welcome7.setFont(new Font(20));
        welcome8.setFont(new Font(20));

        hBox.getChildren().addAll(logoLabel, welcome1);
        vBox.getChildren().addAll(hBox, welcome2, welcome3, welcome4, welcome5, welcome6, welcome7, welcome8);

        Insets insets = new Insets(20, 20, 20, 70);

        VBox.setMargin(hBox, new Insets(20, 20, 20, 50));
        VBox.setMargin(welcome2, insets);
        VBox.setMargin(welcome3, insets);
        VBox.setMargin(welcome4, insets);
        VBox.setMargin(welcome5, insets);
        VBox.setMargin(welcome6, insets);
        VBox.setMargin(welcome7, insets);
        VBox.setMargin(welcome8, insets);

        HBox.setMargin(welcome1, new Insets(20, 100, 20, 200));


        root.setCenter(vBox);
        return root;
    }





}