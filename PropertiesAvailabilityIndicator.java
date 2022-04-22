//import AirbnbListing;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * PropertiesAvailabilityIndicator is a class that is a part of Map package
 * which is a part of London Property Marketplace program.
 * It is responsible for setting hexagon representing boroughs.
 * The hexagon's colour depends on amount of properties available
 * in that borough in comparison to other Boroughs.
 *
 * @author Mateusz Adamski K20063061
 * @version 1.0
 */
public class PropertiesAvailabilityIndicator {

    //transparency of the mid range
    private static final float TRANSPARENCY = 0.4f;
    //list of all properties
    private ArrayList<AirbnbListing> list;
    //map storing BoroughsInfoMap
    private ArrayList<BoroughInformation> boroughInformationList;
    //private int numOfProp;
    //private int numOfBoroughs;
    private static final int NUM_OF_COLS = 7;
    private static final int NUM_OF_ROWS = 7;

    private int sideLength;

    /**
     * Initialises the PropertiesAvailabilityIndicator object.
     *
     * @param width  The width that the drawing should have.
     * @param height  The height that the drawing should have.
     * @param airbnbListings  The list of available properties to be shown.
     */
    public PropertiesAvailabilityIndicator(double width, double height, ArrayList<AirbnbListing> airbnbListings) {
        //a sideLength to be passed as a parameter
        sideLength = (int) Math.round( ( height / (1.0 + (NUM_OF_ROWS - 1) * 1.5 + 1) +
                ( ( width / (NUM_OF_COLS * 2 + 1 ) * 2 / Math.sqrt(3) ) ) ) / 2 );
        this.list = airbnbListings;
        putBoroughsIntoMap();
        putProperties();
        putColor();
        putPolygons();
    }

    /**
     * Updates appropriate date so that the application shows appropriate
     * data on an event that list of properties has changed.
     *
     * @param list  The updated list of properties.
     */
    public void update(ArrayList<AirbnbListing> list) {
        try {
            this.list = list;
            boroughInformationList.stream()
                    .forEach(v -> v.getProperties().clear());
            putProperties();
            putColor();
        }
        catch (NullPointerException e) {
        System.out.println("Provided list is null. \n" +
                "Check if the list was properly initialised");
    }
    }

    /**
     * Adds appropriate polygon to appropriate BoroughInformation object.
     */
    private void putPolygons() {
        for(BoroughInformation borough : boroughInformationList) {
            int row = borough.getRow();
            int col = borough.getCol();
            Hexagon hexagon = new Hexagon(row, col, sideLength);
            Polygon polygon = new Polygon();
            Double[] vertices = new Double[12];
            Double[] tempX = hexagon.getVerticesX();
            Double[] tempY = hexagon.getVerticesY();
            for(int i = 0; i < tempX.length; i++)  {
                vertices[2 * i] = tempX[i];
                vertices[2 * i + 1] = tempY[i];
            }
            polygon.getPoints().addAll(vertices);
            //polygon.setFill(boroughsInfoMap.get(v).getColor());
            borough.setPolygon(polygon);
        }
    }

    /**
     * Adds properties to appropriate boroughs.
     */
    private void putProperties() {
        //numOfBoroughs = (int) boroughInformationList.stream().count();

        list.stream()
                .forEach(s -> {
                    for (BoroughInformation v : boroughInformationList) {
                        if (s.getNeighbourhood().equals(v.getName())) {
                            v.getProperties().add(s);
                        }
                    }
                });
    }

    /**
     * Initialises every borough with appropriate color.
     */
    private void putColor() {
        for(BoroughInformation borough : boroughInformationList) borough.setColor(setColor(borough.getName()));
    }

    /**
     * Read an image file from disk and return it as a OFImage. This method
     * can read JPG and PNG file formats. In case of any problem (e.g the file does
     * not exist, is in an undecodable format, or any other read error) this method
     * returns null.
     *
     * @param boroughName The name of borough which color should be returned.
     * @return  The color that should be assigned with the borough.
     */
    private Color setColor(String boroughName) {
        int maxQuantityOfProp = boroughInformationList.stream()
                .mapToInt(v -> v.getProperties().size())
                .max().orElseThrow(NoSuchElementException::new);

        int minQuantityOfProp = boroughInformationList.stream()
                .mapToInt(v -> v.getProperties().size())
                .min().orElseThrow(NoSuchElementException::new);

        int factor = (int) ((maxQuantityOfProp - minQuantityOfProp) / 7.0);

        //we are searching for the appropriate borough in our list and fetch its number of properties
        int numOfProp = 0;
        for(BoroughInformation borough : boroughInformationList)
            if(borough.getName().equals(boroughName)) { numOfProp = borough.getProperties().size(); }

        if(numOfProp < 1)
            return Color.rgb(0, 0, 0);
        if(numOfProp > 0 &&
                numOfProp < maxQuantityOfProp - 6 * factor)
            return Color.rgb(245, 24, 24, 1.2 * TRANSPARENCY);
        if(numOfProp < maxQuantityOfProp - 5 * factor)
            return Color.rgb(240, 112, 20, TRANSPARENCY);
        if(numOfProp < maxQuantityOfProp - 4 * factor)
            return Color.rgb(255, 183, 20, 0.8 * TRANSPARENCY);
        if(numOfProp < maxQuantityOfProp - 3 * factor)
            return Color.rgb(255, 244, 0, 0.8 * TRANSPARENCY);
        if(numOfProp < maxQuantityOfProp - 2 * factor)
            return Color.rgb(150, 255, 150, 0.8 * TRANSPARENCY);
        if(numOfProp < maxQuantityOfProp - factor)
            return Color.rgb(75, 255, 75, TRANSPARENCY);
        else return Color.rgb(12, 190, 21, 1.2 *  TRANSPARENCY);
    }

    /**
     * Creates a List of BoroughInformation objects to be displayed on the screen.
     */
    private void putBoroughsIntoMap() {
        boroughInformationList = new ArrayList<>();
        boroughInformationList.add(new BoroughInformation("Enfield", 0, 4));
        boroughInformationList.add(new BoroughInformation("Westminster", 3, 3));
        boroughInformationList.add(new BoroughInformation("Hillingdon", 3, 0));
        boroughInformationList.add(new BoroughInformation("Havering", 2, 7));
        boroughInformationList.add(new BoroughInformation("Wandsworth", 4, 3));
        boroughInformationList.add(new BoroughInformation("Lewisham", 5, 5));
        boroughInformationList.add(new BoroughInformation("Tower Hamlets", 3, 4));
        boroughInformationList.add(new BoroughInformation("Hounslow", 4, 1));
        boroughInformationList.add(new BoroughInformation("Redbridge", 2, 6));
        boroughInformationList.add(new BoroughInformation("Southwark", 5, 4));
        boroughInformationList.add(new BoroughInformation("Camden", 2, 3));
        boroughInformationList.add(new BoroughInformation("Bromley", 6, 5));
        boroughInformationList.add(new BoroughInformation("Lambeth", 5, 3));
        boroughInformationList.add(new BoroughInformation("Kensington and Chelsea", 3, 2));
        boroughInformationList.add(new BoroughInformation("Islington", 2, 4));
        boroughInformationList.add(new BoroughInformation("Barnet", 1, 2));
        boroughInformationList.add(new BoroughInformation("Richmond upon Thames", 5, 1));
        boroughInformationList.add(new BoroughInformation("Kingston upon Thames", 6, 2));
        boroughInformationList.add(new BoroughInformation("Harrow", 2, 1));
        boroughInformationList.add(new BoroughInformation("Sutton", 6, 3));
        boroughInformationList.add(new BoroughInformation("Haringey", 1, 3));
        boroughInformationList.add(new BoroughInformation("Brent", 2, 2));
        boroughInformationList.add(new BoroughInformation("Bexley", 4, 6));
        boroughInformationList.add(new BoroughInformation("Hackney", 2, 5));
        boroughInformationList.add(new BoroughInformation("Greenwich", 4, 5));
        boroughInformationList.add( new BoroughInformation("Hammersmith and Fulham", 4, 2));
        boroughInformationList.add( new BoroughInformation("Waltham Forest", 1, 4));
        boroughInformationList.add(new BoroughInformation("Merton", 5, 2));
        boroughInformationList.add( new BoroughInformation("Croydon", 6, 4));
        boroughInformationList.add(new BoroughInformation("Newham", 3, 5));
        boroughInformationList.add(new BoroughInformation("Ealing", 3, 1));
        boroughInformationList.add(new BoroughInformation("City of London", 4, 4));
        boroughInformationList.add(new BoroughInformation("Barking and Dagenham", 3, 6));
    }

    /**
     * @return  Returns the number of rows in the map drawing.
     */
    public int getNumOfRows() {return NUM_OF_ROWS;}

    /**
     * @return  Returns the number of columns in the map drawing.
     */
    public int getNumOfCols() {return NUM_OF_COLS;}

    /**
     * @return  Returns the list of BoroughInformation objects.
     */
    public ArrayList<BoroughInformation> getBoroughsInfoList() {return boroughInformationList;}
}
