/**
 * Hexagon is a class that is a part of Map package
 * which is a part of London Property Marketplace program.
 * It is responsible for mathematical implementation of the
 * equilateral hexagon to be drawn on the map.
 *
 * @author Mateusz Adamski K20063061
 * @version 1.0
 */
public class Hexagon {


    Double[] verticesXCoordinates = new Double[6];
    Double[] verticesYCoordinates = new Double[6];
    private int row;//row in the web where this hexagon is placed
    private int col;//col in the web where this hexagon is placed
    private double sideLength;//the length of the side
    private double height;//the height of the equilateral triangle i.e. half of the hexagon's height
    private double centerXCoordinate;
    private double centerYCoordinate;

    /**
     * Initialises the Hexagon object.
     *
     * @param row  The hexagon's row.
     * @param col  The hexagon's col.
     * @param sideLength  The hexagon's side's length.
     */
    public Hexagon(int row, int col, double sideLength) {
        this.row = row;
        this.col = col;
        this.sideLength = sideLength;
        height = Math.sqrt(3) / 2 * sideLength;//the equilateral triangles length
        calcculateCenter();
        calculateVertices();
    }

    /**
     * @return  Array with x coordinates of hexagon's vertices.
     */
    public Double[] getVerticesX() {return verticesXCoordinates;}

    /**
     * @return  Array with y coordinates of hexagon's vertices.
     */
    public Double[] getVerticesY() {return verticesYCoordinates;}

    /**
     * @return  The length of the hexagon's side.
     */
    public double getSideLength() {
        return sideLength;
    }

    /**
     * @return  The hexagon's row.
     */
    public int getRow() {
        return row;
    }

    /**
     * @return  The hexagon's col.
     */
    public int getCol() {
        return col;
    }

    /**
     * @return  The x coordinate of hexagon's center.
     */
    public double getCenterX() {return centerXCoordinate;}

    /**
     * @return  The y coordinate of hexagon's center.
     */
    public double getCenterY() {return centerYCoordinate;}

    /**
     *Calculates the coordinates of the hexagon's center.
     */
    private void calcculateCenter() {
        centerXCoordinate =  2 * height * (col + (row % 2) * 0.5);
        centerYCoordinate =  (row * 1.5 + 1) * sideLength;
    }

    /**
     *Calculates the coordinates of the hexagon's vertices
     * and stores it in arrays.
     */
    private void calculateVertices() {
        verticesXCoordinates = new Double[6];
        verticesYCoordinates = new Double[6];

        verticesXCoordinates[0] = centerXCoordinate + 0;
        verticesXCoordinates[1] = centerXCoordinate - height;
        verticesXCoordinates[2] = centerXCoordinate - height;
        verticesXCoordinates[3] = centerXCoordinate + 0;
        verticesXCoordinates[4] =centerXCoordinate + height;
        verticesXCoordinates[5] = centerXCoordinate + height;

        verticesYCoordinates[0] = centerYCoordinate + sideLength;
        verticesYCoordinates[1] = centerYCoordinate + 0.5 * sideLength;
        verticesYCoordinates[2] = centerYCoordinate - 0.5 * sideLength;
        verticesYCoordinates[3] = centerYCoordinate - sideLength;
        verticesYCoordinates[4] = centerYCoordinate - 0.5 * sideLength;
        verticesYCoordinates[5] = centerYCoordinate + 0.5 * sideLength;
    }

    /**
     * Equals method based on lectures material.
     */
    @Override
    public boolean equals(Object obj)
    {
        if(this == obj) {
            return true;
        }
        if(!(obj instanceof Hexagon)) {
            return false;
        }
        Hexagon other = (Hexagon) obj;
        return row == other.getRow() &&
                col == other.getCol() &&
                sideLength == other.getSideLength() &&
                centerXCoordinate == other.getCenterX() &&
                centerYCoordinate == other.getCenterY();
    }

    /**
     * Hashcode technique taken from
     * Effective Java by Joshua Bloch.
     */
    @Override
    public int hashCode()
    {
        int result = 17;
        result = 37 * result + row;
        result = 37 * result + col;
        result = 37 * result + (int) sideLength;
        result = 37 * result + (int) centerXCoordinate;
        result = 37 * result + (int) centerYCoordinate;
        double vertices = 0;
        for(int i = 0; i < 6; i++) {
            result = 37 * result + verticesXCoordinates[i].intValue();
            result = 37 * result + verticesYCoordinates[i].intValue();
        }
        return result;
    }

}