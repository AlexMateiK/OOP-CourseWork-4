import com.opencsv.CSVReader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Helper class used to load connectivity data from a .csv file.
 *
 * The dataset for the 2021 PTAL predictions is offered by Transport for London at https://tfl.gov.uk/info-for/open-data-users/api-documentation
 * I acknowledge the Transport Data Service Terms and Conditions and the WebCAT turns and conditions linked here:
 * https://tfl.gov.uk/corporate/terms-and-conditions/transport-data-service
 * https://tfl.gov.uk/corporate/terms-and-conditions/webcat
 * Therefore, the functionality of our application is Powered by TfL Open Data
 *
 * Contains OS data © Crown copyright and database rights 2016'
 * Geomni UK Map data © and database rights [2019]
 *
 * @Author Alexandru Matei, K20054925
 * On the basis of code provided in the start package for coursework 4.
 * @Version 7/04/2021
 */

public class ConnectivityDataLoader {

    public ConnectivityDataLoader(){
    }

    /**
     * Loads connectivity data into an arraylist of objects that contain individual connectivity data points and returns that arraylist.
     * @return ArrayList containing connectivity datapoints.
     */
    public ArrayList<PTALValueContainer> loadConnectivityData(){

        ArrayList<PTALValueContainer> PTALGridValues = new ArrayList<>();

        try{
            URL url = getClass().getResource("2021-ptals-grid-values.csv");
            CSVReader reader = new CSVReader(new FileReader(new File(url.toURI()).getAbsolutePath()));
            String [] line;
            //skip the first row (column headers)
            reader.readNext();
            while ((line = reader.readNext()) != null) {

                String Easting = line[1];
                String Northing = line[2];

                String PTAL = line[4];

                PTALValueContainer PTALValue = new PTALValueContainer(Easting,Northing,PTAL);
                PTALGridValues.add(PTALValue);
            }
        } catch(IOException | URISyntaxException e){
            System.out.println("Failure! Something went wrong");
            e.printStackTrace();
        }
        System.out.println("Success! Number of loaded records: " + PTALGridValues.size());
        return PTALGridValues;
    }

}
