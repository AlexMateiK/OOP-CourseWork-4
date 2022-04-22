/**
 * Container class for holding a datapoint on connectivity.
 * It associates a pair of BNG (British National Grid) coordinates with a connectivity rating ( PTAL = Public Transport Accesibility Level ).
 */

public class PTALValueContainer {

    private String northing,easting,PTAL;

    /**
     * Creates a container for a datapoint on connectivity
     * @param easting Easting BNG Coordinate
     * @param northing Northing BNG Coordinate
     * @param PTAL PTAL Value for the given coordinates (PTAL = connectivity rating)
     */
    public PTALValueContainer(String easting, String northing, String PTAL){
        this.easting = easting;
        this.northing = northing;
        this.PTAL = PTAL;
    }

    /**
     * Northing BNG coordinate for this datapoint.
     * @return Northing BNG coordinate
     */
    public String getNorthing(){
        return northing;
    }

    /**
     * Easting BNG coordinate for this datapoint
     * @return Easting BNG Coordinate
     */
    public String getEasting(){
        return easting;
    }

    /**
     * PTAL Value for this datapoint
     * @return Connectivity rating
     */
    public String getPTAL(){
        return PTAL;
    }
}
