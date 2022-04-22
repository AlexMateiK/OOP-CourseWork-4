import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

import java.util.ArrayList;

/**
 * BoroughInformation is a class that is a part of Map package
 * which is a part of London Property Marketplace program.
 * It is responsible for storing borough's information.
 *
 * @author Mateusz Adamski K20063061
 * @version 1.0
 */
public class BoroughInformation {
    private String name;
    private int row;
    private int col;
    private Color color;
    private Polygon polygon;
    private ArrayList<AirbnbListing> properties;

    /**
     * Initialises the BoroughInformation. The stored information are the boroughs name,
     * row, col (location on map), color, polygon (mathematical information),
     * number of properties and list of properties in this borrow.
     *
     * @param name  The BoroughInformation instant which information is to be displayed.
     * @param row  The BoroughInformation instant which information is to be displayed.
     * @param col  The BoroughInformation instant which information is to be displayed.
     */
    public BoroughInformation(String name, int row, int col) {
        this.name = name;
        this.row = row;
        this.col = col;
        color = null;
        polygon = null;
        properties = new ArrayList<>();
    }

    public String getName() {return name;}

    public int getRow() {return row;}

    public int getCol() {return col;}

    public Color getColor() {return color;}

    public Polygon getPolygon() {return polygon;}

    public void setName(String name) {this.name = name;}

    public void setRow(int row) {this.row = row;}

    public void setCol(int row) {this.col = col;}

    public void setColor(Color color) {this.color = color;}

    public void setPolygon(Polygon polygon) {this.polygon = polygon;}

    public ArrayList<AirbnbListing> getProperties() { return properties; }
}
