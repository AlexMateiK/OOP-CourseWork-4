import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Helper class used to load information on pollutant emissions in 2016.
 *
 * Data contained in EmissionsByGridExactCut2016FromLAEI2016EmissionsSummaryStaticValues.csv
 * and EmissionsByGridExactCut2016FromLAEI2016EmissionsSummary
 * extracted from The Greater London Authority's London DataStore
 * in accordance with UK Open Government Licence V2 (http://www.nationalarchives.gov.uk/doc/open-government-licence/version/2/)
 * NOTICE: The Greater London Authority cannot warrant the quality or the accuaracy of the extracted data.
 *
 * @author Alexandru Matei, K20054925
 * @version 7/04/2021
 */

public class PollutionDataLoader {

    /**
     * Creates a PollutionDataLoader type object which may then be used to load and then process pollution-related data.
     */
    public PollutionDataLoader(){

    }

    /*
     * Loads data from a dataset containing quantities for different pollutants and various sources for locations in London specified using BNG coordinates.
     * All the quantities for the various sources are summed, creating at the end, for each provided location, a total sum for the quantities of each pollutant.
     * These overall quantities then will be used for creating a geometric mean, used to compare the overall pollution of the various locations.
     */
    /**
     * Load the pollution information.
     * @return ArrayList containing pollution data points
     */
    public ArrayList<PollutionDataPoint> loadPollutionData(){

        ArrayList<PollutionDataPoint> pollutionDataList = new ArrayList<>();

        PollutionDataPoint spaceHolder = new PollutionDataPoint("","","");
        pollutionDataList.add(spaceHolder);

        /*
        First, go through a file that contains only the various coordinates and id's for all of the location for which data was recorded into the dataset,
        basically creating the containers for the sums.
         */
        try{
            URL url = getClass().getResource("EmissionsByGridExactCut2016FromLAEI2016EmissionsSummaryStaticValues.csv");
            CSVReader reader = new CSVReader(new FileReader(new File(url.toURI()).getAbsolutePath()));
            String [] line;
            //skip the first row (column headers)
            reader.readNext();
            while ((line = reader.readNext()) != null) {

                String locID = line[0];
                String easting = line[1];
                String northing = line[2];
                String borough = line[3];

                PollutionDataPoint polDataPoint = new PollutionDataPoint(easting, northing, borough);
                pollutionDataList.add(Integer.parseInt(locID),polDataPoint);



            }
        } catch(IOException | URISyntaxException e){
            System.out.println("Failure! Something went wrong");
            e.printStackTrace();
        }
        System.out.println("Success! Number of loaded records: " + pollutionDataList.size());

        /*
        Then, go through the dataset and add the quantities of pollutants to their respective sums.
         */
        try{
            URL url = getClass().getResource("EmissionsByGridExactCut2016FromLAEI2016EmissionsSummary.csv");
            CSVReader reader = new CSVReader(new FileReader(new File(url.toURI()).getAbsolutePath()));
            String [] line;
            //skip the first row (column headers)
            reader.readNext();
            while ((line = reader.readNext()) != null) {

                String lineString = "-";
                // Certain rows contains a line for pollutant quantities, which is interpreted as the lack of detection of that pollutant, thus the assigning of "0"
                String locID = line[2];
                String noxValue = line[10];
                if (lineString.equals(noxValue))noxValue="0";
                double numericNoxValue = Double.parseDouble(noxValue);
                String pm10Value = line[11];
                if (lineString.equals(pm10Value))pm10Value="0";
                double numericPm10Value = Double.parseDouble(pm10Value);
                String pm25Value = line[12];
                if (lineString.equals(pm25Value))pm25Value="0";
                double numericPm25Value = Double.parseDouble(pm25Value);
                String carbonDioxideValue = line[13];
                if (lineString.equals(carbonDioxideValue))carbonDioxideValue="0";
                double numericCarbonDioxideValue = Double.parseDouble(carbonDioxideValue);

                int pollutionDataPointIndex = Integer.parseInt(locID);
                PollutionDataPoint pollutionDataPoint = pollutionDataList.get(pollutionDataPointIndex);

                pollutionDataPoint.addToAllPollutants(numericNoxValue, numericPm10Value, numericPm25Value, numericCarbonDioxideValue);
            }
        } catch(IOException | URISyntaxException e){
            System.out.println("Failure! Something went wrong");
            e.printStackTrace();
        }

        computeGeometricMeans(pollutionDataList);


        return pollutionDataList;
    }

    /**
     * Goes through all of the data points and makes them compute the geometric mean of their pollutant quantities.
     * @param pollutionList the ArrayList containing all of the pollution datapoints
     */
    private void computeGeometricMeans(ArrayList<PollutionDataPoint> pollutionList){
        for (PollutionDataPoint dataPoint : pollutionList)
            dataPoint.computeGeometricMean();
    }

}
