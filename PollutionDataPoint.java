public class PollutionDataPoint {

    private String easting;
    private String northing;
    private String borough;
    private double  noxSum ;
    private double pm10Sum;
    private double pm25Sum;
    private double carbonDioxideSum;
    private double geometricMeanOfSums;

    /**
     * Class defining a container for a single data point on pollution.
     * Such a data point is comprised of four sums of quantities, each for a single species of pollutant,
     * a pair of BNG coordinates and a geometric mean of the quantities.
     * Each sum is computed by adding together the quantities of a pollutant generated by all its sources.
     *
     * @Author Alexandru Matei, k20054925
     * @Version 7/04/2021
     * /

    /**
     * Create an object that stores data on pollutant quantities for a location.
     * @param easting Easting BNG (British National Grid) value
     * @param northing Northing BNG (British National Grid) value
     * @param borough Name of the borough
     */
    public PollutionDataPoint(String easting, String northing, String borough){
        this.easting = easting;
        this.northing = northing;
        this.borough = borough;
        noxSum = 0;
        pm10Sum = 0;
        pm25Sum = 0;
        carbonDioxideSum = 0;
        geometricMeanOfSums = 0;
    }

    /**
     * Add to the overall quantity of NOx recorded at this location.
     * @param valueToAdd Quantity of NOx to add (in tons)
     */
    public void addToNox(double valueToAdd) {
        noxSum += valueToAdd;
    }

    /**
     * Add to the overall quantity of PM10 recorded at this location.
     * @param valueToAdd Quantity of PM10 particles to add (in tons).
     */
    public void addToPm10Sum(double valueToAdd) {
        pm10Sum+=valueToAdd;
    }

    /**
     * Add to the overall quantity of PM2.5 recorded at this location.
     * @param valueToAdd Quantity of PM2.5 to add (in tons).
     */
    public void addToPm25Sum(double valueToAdd) {
        pm25Sum+=valueToAdd;
    }

    /**
     * Add to the overall quantity of CarbonDioxide recorded at this location.
     * @param valueToAdd Quantity of Carbon Dioxide to add (in tons).
     */
    public void addToCarbonDioxideSum(double valueToAdd) {
        carbonDioxideSum+=valueToAdd;
    }

    /**
     * Add to the overall quantities of the four recorded pollutants.
     * @param valueToAddNox Quantity of NOx to add (in tons)
     * @param valueToAddPm10 Quantity of PM10 particles to add (in tons).
     * @param valueToAddPm25 Quantity of PM2.5 to add (in tons).
     * @param valueToAddcarbonDioxide Quantity of Carbon Dioxide to add (in tons).
     */
    public void addToAllPollutants(double valueToAddNox, double valueToAddPm10, double valueToAddPm25, double valueToAddcarbonDioxide){
        noxSum += valueToAddNox;
        pm10Sum += valueToAddPm10;
        pm25Sum += valueToAddPm25;
        carbonDioxideSum += valueToAddcarbonDioxide;
    }

    /**
     * Computes the geometric mean of the quantities of the four pollutants.
     * The geometric mean represents a rudimentary approach to creating a value that indicates the overall degree of the pollution at this location.
     * A geometric mean is used to account for the great differences in the quantities of the pollutants.
     */
    public void computeGeometricMean(){
        double product = 1.0;

        int pollutantPresentCount = 4;

        // If a pollutant is 0, it's value is set to a value very close to 0 instead, so that it affects the geometric mean, but it doesn't make it 0, which would be unrepresentative
        // in case the values of the other pollutants are not 0.
        if (noxSum == 0)noxSum = 0.0000000001;
        if (pm10Sum == 0)pm10Sum = 0.0000000001;
        if (pm25Sum == 0)pm25Sum = 0.0000000001;
        if (carbonDioxideSum == 0)carbonDioxideSum =0.0000000001;

        product*=noxSum;
        product*=pm10Sum;
        product*=pm25Sum;
        product*=carbonDioxideSum;

        geometricMeanOfSums = Math.pow(product, 0.25);
    }

    /**
     * Returns the geometric mean of the quantities of the pollutants at this location.
     * This mean acts as a rudimentary way to represent the overall degree of the pollution at this location.
     * @return Geometric mean of pollutant quantities
     */
    public double getGeometricMean(){
        return geometricMeanOfSums;
    }

    /**
     * Returns Easting BNG coordinate for this location.
     * @return Easting value
     */
    public String getEasting(){
        return easting;
    }

    /**
     * Returns Northing BNG coordinate for this location.
     * @return Northing value
     */
    public String getNorthing(){
        return northing;
    }

    /**
     * Returns borough name for this location.
     * @return Borough name
     */
    public String getBorough(){
        return borough;
    }

    /**
     * Returns a string detailing the information captured for this location/ all that is contained within this datapoint.
     * @return String with the object's stored information.
     */
    public String toString(){
        String text = "Easting: " + easting + " Northing: " + northing + " Borough: " + borough + " noxSum: " + noxSum + " pm25Sum: " + pm25Sum + " pm10Sum: " + pm10Sum + " carbonDioxideSum: " + carbonDioxideSum + " geometricMean: " + geometricMeanOfSums;
        return text;
    }
}
