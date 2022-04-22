import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Class that implements the whole of the logic behind the statistics.
 * Note: In this class, I make use of an online tool offered by the British Geological Survey (https://www.bgs.ac.uk/)
 * in order to convert coordinates to British National Grid easting and northing values.
 * This tool is offered publicly.
 * Consider all results of the conversion from coordinates to BNG values as being
 * based upon the tool offered at https://webapps.bgs.ac.uk/data/webservices/convertForm.cfm, with the permission of the British Geological Survey.
 *
 * @author Alexandru Matei, k20059425
 * @version 7/04/2021
 */

public class StatLogicHelper {

    // array of ints which'll store 0's and 1's to express whether a given stat is "taken"

    private int[] statisticsTakenStatus;

    private ArrayList<AirbnbListing> filteredListings;
    private static ArrayList<AirbnbListing> favoriteListings;

    private ArrayList<PTALValueContainer> PTALGridValues;

    // I decided to make fields for String containing the stats
    // So that they dont have to be computed repeatedly

    private String avgNumberOfReviews;
    private String numberOfAvailableProperties;
    private String numberOfHomesAndAppartments;
    private String nameMostExpensiveBorough;
    private String mostConnectFavPropertyText;
    private String leastPollutedFavPropertyText;
    private String favCheapestPropertyDescription;
    private String favBoroughText;

    private ArrayList<PollutionDataPoint> pollutionDataPointList;

    private HashMap<String,Integer> favBoroughMap;

    public StatLogicHelper(ArrayList<AirbnbListing> filteredListings){

        this.filteredListings = filteredListings;

        // placeholder until I get a fav array to get passed here
        favoriteListings = new ArrayList<>();


        statisticsTakenStatus = new int[8];
        statisticsTakenStatus[0] = 1;
        statisticsTakenStatus[1] = 1;
        statisticsTakenStatus[2] = 1;
        statisticsTakenStatus[3] = 1;
        // the default initial state is that the first 4 stats are "taken"

        favBoroughMap = new HashMap<>();


        avgNumberOfReviews = "unknown";
        numberOfAvailableProperties = "unknown";
        numberOfHomesAndAppartments = "unknown";
        nameMostExpensiveBorough = "unknown";
        mostConnectFavPropertyText = "unknown";
        leastPollutedFavPropertyText = "unknown";
        favCheapestPropertyDescription = "unknown";
        favBoroughText = "unknown";

        ConnectivityDataLoader connecDataLoader = new ConnectivityDataLoader();
        PTALGridValues = connecDataLoader.loadConnectivityData();

        PollutionDataLoader polDataLoader = new PollutionDataLoader();
        pollutionDataPointList = polDataLoader.loadPollutionData();

        updateStats(filteredListings);
    }

    /**
     * Adds provided listing to favorites
     * @param listing
     */
    public static void addToFavorites(AirbnbListing listing){
        favoriteListings.add(listing);
        //System.out.println(favoriteListings.size());
    }

    /**
     * Removes provided listing from favorites
     * @param listing
     */
    public static void removeFromFavorites(AirbnbListing listing){
        Iterator iterator = favoriteListings.iterator();
        AirbnbListing temp;
        while (iterator.hasNext()){
            temp = (AirbnbListing) iterator.next();
            if (temp == listing)
                iterator.remove();
        }

    }

    /**
     * Checks if a provided listing is in the user's favorites
     * @param listing
     * @return
     */
    public static boolean checkIfFavorite(AirbnbListing listing){
        boolean flag = false;
        for (AirbnbListing temp : favoriteListings)
            if (listing == temp)
            {   flag = true; break ;}
        return flag;
    }

    /**
     * Unoccupies the stat for the passed index, looks for the nearest stat index that is smaller than the given int and that is not taken.
     * @param currentStat The current index
     * @return Unoccupied previous stat index.
     */
    public int getPreviousStatIndex(int currentStat){
        statisticsTakenStatus[currentStat]=0;

        currentStat--;
        if (currentStat<0)currentStat=7;
        while (statisticsTakenStatus[currentStat]==1)
        {
            currentStat--;
            if (currentStat<0)currentStat=7;
        }

        statisticsTakenStatus[currentStat]=1;
        return currentStat;
    }

    /**
     * Unoccupies the stat for the passed index, looks for the nearest stat index that is bigger than the given int and that is not taken.
     * @param currentStat The current index
     * @return Next unoccupied stat index
     */
    public int getNextStatIndex(int currentStat){
        statisticsTakenStatus[currentStat]=0;

        currentStat = (currentStat+1)%8;
        while (statisticsTakenStatus[currentStat]==1)
        {
            currentStat = (currentStat+1)%8;
        }

        statisticsTakenStatus[currentStat]=1;
        return currentStat;
    }

    /**
     * Computes all of the statistics.
     */
    public void updateStats(ArrayList<AirbnbListing> list){

        filteredListings = list;

        computeBaseStats();
        computeCheapestFavProperty();
        computeFavouriteBorough();
        computeLeastPollutedFavProperty();
        computeMostConnectedFavProperty();
    }

    /**
     * Only computes the statistics related to the user's favorite properties.
     */
    public void updateOnlyFavoriteStats(){
        computeCheapestFavProperty();
        computeFavouriteBorough();
        computeLeastPollutedFavProperty();
        computeMostConnectedFavProperty();
    }

    /**
     * Returns a string containing the information to be displayed for a specified statistic.
     * @param statIndex The index specific to the statistic.
     * @return The text describing that statistic.
     */
    public String getStatTextByIndex(int statIndex){

        switch (statIndex){
            case 0:
                return getAvgNoOfReviewsStat();
            case 1:
                return getNumberOfAvailableProperties();
            case 2:
                return getNumberOfHomesAndAppartments();
            case 3:
                return getMostExpensiveBorough();
            case 4:
                return getMostConnectedFavProperty();
            case 5:
                return getLeastPollutedFavProperty();
            case 6:
                return getFavouriteBorough();
            case 7:
                return getCheapestFavProperty();

        }

        return "";
    }

    /**
     * Computes the four basic stats, that depend only on the user's price range selection.
     *
     */
    private void computeBaseStats() {

        int totalNumberOfReviews = 0;
        int totalNumberOfProperties = filteredListings.size();
        // because I am using a filteredListings array, the size of it is actually the number of available properties
        // and that number will be used for the available properties stat

        int numberOfHomesAndAppartments = 0;

        HashMap<String, Integer> boroughToNumberOfProps = new HashMap<>();
        HashMap<String, Integer> boroughToSum = new HashMap<>();
        // Maps to associate a borough name with how many properties I count for that borough
        // and, respectively, to associate a borough name to the sum of the prices of the properties I find for that borough.

        for (AirbnbListing listing: filteredListings){
            totalNumberOfReviews += listing.getNumberOfReviews();

            if (!listing.getRoom_type().toLowerCase().equals("private room"))numberOfHomesAndAppartments++; // Increases the counter of homes and appartments by 1

            Integer currentNo = boroughToNumberOfProps.get(listing.getNeighbourhood());
            if ( currentNo == null ) currentNo = 1; // If no properties have been found so far for this property's borough, set the counter to 1
            else
            {
                currentNo++;
            }
            boroughToNumberOfProps.put(listing.getNeighbourhood(), currentNo); // After having increased the number of properties for the current property's borough by 1, update the value in the map.

            Integer currentSum = boroughToSum.get(listing.getNeighbourhood());
            if (currentSum == null) currentSum = 0; // If this is the first time I encounter a property for this borough, the .get method will return null, so the Integer variable needs to be initialized here
            currentSum += listing.getPrice()*listing.getMinimumNights();
            boroughToSum.put(listing.getNeighbourhood(), currentSum);
        }


        Set keys = boroughToNumberOfProps.keySet();
        String mostExpBorough = "unknown";

        /*
        This for loop goes through each borough and calculates the average price for the properties it encountered,
        and checks each average to see if its greater than the current maximum average.
        If it is, we have found a more expensive borough ( as far as the user-given price range goes )
         */
        float maxAvgPrice = 0;
        for ( Object boroughName : keys ){
            float noOfProps = boroughToNumberOfProps.get(boroughName);
            float sumOfPrices = boroughToSum.get(boroughName);
            float avgPriceForBorough;
            avgPriceForBorough = sumOfPrices/noOfProps;
            if ( avgPriceForBorough > maxAvgPrice ){
                maxAvgPrice = avgPriceForBorough;
                mostExpBorough = (String) boroughName;
            }

        }

        String avgHolder = "" + (double) totalNumberOfReviews/totalNumberOfProperties;
        int ind = avgHolder.indexOf(".");
        avgHolder = avgHolder.substring(0, ind+2);

        avgNumberOfReviews = "" + avgHolder;

        numberOfAvailableProperties = "" + totalNumberOfProperties;

        this.numberOfHomesAndAppartments = "" + numberOfHomesAndAppartments;

        nameMostExpensiveBorough = mostExpBorough;

    }

    /**
     * Finds the most connected property ( as far as moving around the city goes ) out of those favorited by the user
     */
    private void computeMostConnectedFavProperty(){



        AirbnbListing mostConnectedFavArray = null;

        // Notice: the PTAL values increase in significance lexicographically ( 6b>6a, for example)
        String MaxPTAL = "";

        for (AirbnbListing favListing : favoriteListings){
            String latitude = String.valueOf(favListing.getLatitude());
            String longitude = String.valueOf(favListing.getLongitude());

            String url = "https://webapps.bgs.ac.uk/data/webservices/CoordConvert_LL_BNG.cfc?method=LatLongToBNG&lat=" + latitude + "&lon=" + longitude;
            // The property's coordinates need to be converted into BNG (British National Grid) values

            String easting = "", northing = "";

            try {

                URLConnection connection = new URL(url).openConnection();


                InputStream response = connection.getInputStream();
                try (Scanner scanner = new Scanner(response)) {

                    String responseBody = scanner.useDelimiter("\\A").next();

                    int startIndex = responseBody.indexOf("EASTING"); // I use the positions of the literals EASTING and NORTHING in the response to reliably find the BNG values
                    startIndex += 9;
                    int endIndex = startIndex + 6;
                    easting = responseBody.substring(startIndex,endIndex);

                    startIndex = responseBody.indexOf("NORTHING");
                    startIndex += 10;
                    endIndex = startIndex + 6;
                    northing =  responseBody.substring(startIndex,endIndex);

                    //System.out.println("Northing: "+ northing + " Easting: " + easting + responseBody);

                }
            }
            catch (IOException exceptie) {
                System.out.print("Coordinate to BNG conversion url access failed");

            }

            /*
            Here, I need to modify the BNG values, because the resolution ( location/coordinate wise ) of the coursework dataset
            is greater than that of the connectivity dataset.
            The connectivity dataset only contains easting and northing values ending in 45 and 52 respectively.
            In other words, I need to round up/down and get the nearest approximation of the connectivity grade for my property.
             */
            int lastDigitsNorthing = Integer.parseInt(northing.substring(3,6));
            if (lastDigitsNorthing%100 > 2) northing = northing.substring(0,4) + "52";
            else {
                lastDigitsNorthing-=100; lastDigitsNorthing/=100; lastDigitsNorthing*=100; lastDigitsNorthing+=52;
                String temporary = "" + lastDigitsNorthing;
                northing = northing.substring(0,3) + "" + temporary;
            }

            int lastDigitsEasting = Integer.parseInt(easting.substring(3,6));
            if (lastDigitsEasting%100 < 95) easting = easting.substring(0,4) + "45";
            else {
                lastDigitsEasting+=100; lastDigitsEasting/=100; lastDigitsEasting*=100; lastDigitsEasting+=45;
                String temporary = "" + lastDigitsEasting;
                easting = easting.substring(0,3) + "" + temporary;
            }

            //System.out.println("New northing: " + northing + " New easting: "+ easting);

            /*
            After I have rounded the BNG values for my property's location to some values that exist within the
            connectivity dataset, I search the dataset for my location and get the PTAL value for it.
             */
            String PTAL = "";
            int count = 0;
            for (PTALValueContainer valCont : PTALGridValues){
                count++;
                if (northing.equals(valCont.getNorthing()) && easting.equals(valCont.getEasting()))
                {
                    PTAL = valCont.getPTAL();
                    break;
                }
            }

            /*
            Finally, I compare the PTAL for the current propety to the greatest Public Travel Accesibility Level grade found so far.
             */
            //System.out.println("PTAL VALUE: " + PTAL);
            if ( MaxPTAL.compareTo(PTAL) < 0 ){
                MaxPTAL = PTAL;
                mostConnectedFavArray = favListing;
                //System.out.println("Fav listing id: " + favListing.getId());
            }

        }

        if (mostConnectedFavArray == null) mostConnectFavPropertyText =  "We can't find the best connected \nproperty from your favourites. \n \nTry pressing 'favorite' \non some properties!";
        else mostConnectFavPropertyText = "The best connected property \nfrom your favourites: \n" + mostConnectedFavArray.getName() + "\nOffered by:" + mostConnectedFavArray.getHost_name() + "\nin " + mostConnectedFavArray.getNeighbourhood();

    }

    /*
    In order to find the least polluted property out of the user's favorites I
    go through the list of favorite properties,
    taking each one and converting its coordinates to BNG
    then rounding up those values to the closest ones that I will be able to find in  my pollution dataset
    then searching through my datapoint collection which I created from the dataset
    for a datapoint which has matching coordinates.
    The geometric mean value contained within that matching data point is what I use as an indicator of the approximate degree
    of pollution in the property's area.
     */
    /**
     * Finds the property in the least polluted area, out of the properties favorited by the user.
     */
    private void computeLeastPollutedFavProperty() {

        double minGeometricMean = 1000000.0;
        AirbnbListing leastPollutedFavProperty = null;

        for (AirbnbListing favListing : favoriteListings) {
            String latitude = String.valueOf(favListing.getLatitude());
            String longitude = String.valueOf(favListing.getLongitude());

            String url = "https://webapps.bgs.ac.uk/data/webservices/CoordConvert_LL_BNG.cfc?method=LatLongToBNG&lat=" + latitude + "&lon=" + longitude;
            // The property's coordinates need to be converted into BNG (British National Grid) values

            String easting = "", northing = "";

            try {

                URLConnection connection = new URL(url).openConnection();


                InputStream response = connection.getInputStream();
                try (Scanner scanner = new Scanner(response)) {

                    String responseBody = scanner.useDelimiter("\\A").next();

                    int startIndex = responseBody.indexOf("EASTING");
                    startIndex += 9;
                    int endIndex = startIndex + 6;
                    easting = responseBody.substring(startIndex, endIndex);

                    startIndex = responseBody.indexOf("NORTHING");
                    startIndex += 10;
                    endIndex = startIndex + 6;
                    northing = responseBody.substring(startIndex, endIndex);



                }
            } catch (IOException exceptie) {
                System.out.print("Coordinate to BNG conversion url access failed");

            }

            /*
            Here, I need to modify the BNG values, because the resolution ( location/coordinate wise ) of the coursework dataset
            is greater than that of the pollution dataset.
            The connectivity dataset only contains easting and northing values ending in 500
            In other words, I need to round up/down to the nearest five-hundredth in order to match the property's location to a location on which information is offered in the dataset.
             */
            int northingNumericalForm = Integer.parseInt(northing);
            int eastingNumericalForm = Integer.parseInt(easting);

            if (northingNumericalForm%1000 > 0 ) northingNumericalForm = northingNumericalForm/1000*1000+500;
            else northingNumericalForm = (northingNumericalForm/1000-1)*1000+500;

            if (eastingNumericalForm%1000 > 0 ) eastingNumericalForm = eastingNumericalForm/1000*1000+500;
            else eastingNumericalForm = (eastingNumericalForm/1000-1)*1000+500;

            northing = ""+northingNumericalForm;
            easting = ""+eastingNumericalForm;

            //System.out.println("Northing: " + northing + " Easting: " + easting);

            /*
            Then, in this for loop, I search for a pollution data point that has the BNG coordinates that resulted from the earlier rounding, in order to approximate the degree of pollution at this property's location.
             */
            for ( PollutionDataPoint polDataPoint : pollutionDataPointList ){
                if ( polDataPoint.getNorthing().equals(northing) && polDataPoint.getEasting().equals(easting) && ( polDataPoint.getBorough().contains(favListing.getNeighbourhood()) || favListing.getNeighbourhood().contains(polDataPoint.getBorough()) ))
                {
                    // System.out.println("Geometric mean: " + polDataPoint.getGeometricMean()); used
                    if ( polDataPoint.getGeometricMean() < minGeometricMean ){ // When I find the corresponding datapoint, I compare it to the value of the least polluted property so far
                        minGeometricMean = polDataPoint.getGeometricMean();
                        // System.out.println(polDataPoint.toString());
                        leastPollutedFavProperty = favListing;
                        break;
                    }
                }

            }

        }

        if (leastPollutedFavProperty!=null) leastPollutedFavPropertyText = "The least polluted property\nout of your favourites is:\n" + leastPollutedFavProperty.getName() + "\noffered by " + leastPollutedFavProperty.getHost_name() + "\nin " + leastPollutedFavProperty.getNeighbourhood() ;
        else leastPollutedFavPropertyText = "We do not know\nwhich of your favourite\nproperties is located\nin the area with the\nleast pollution.\nTry adding some favorites!";

    }

    /**
     * Method that finds the borough that has the most properties favorited by the user.
     */
    private void computeFavouriteBorough(){
        Integer countMax = 0;
        String favBorough = "unknown";

        for ( AirbnbListing listing: favoriteListings ){
            String boroughName = listing.getNeighbourhood();
            Integer count = favBoroughMap.get(boroughName);
            if (count == null){
                count = 1;
                favBoroughMap.put(boroughName, count);
            }
            else {
                count++;
                favBoroughMap.put(boroughName,count);
                if (countMax<=count){countMax = count; favBorough = boroughName;}
            }
        }

        if(favoriteListings.size()==1)favBorough = favoriteListings.get(0).getNeighbourhood();

        if (favBorough.equals("unknown")) favBoroughText = "We couldn't find your\nmost favorited borough.\nFavorite some more properties!";
        else favBoroughText = "The borough you have\nThe highest number of\nfavorited properties in is\n" + favBorough;
    }

    /**
     * Finds the cheapest property out of the user's favorites.
     */
    private void computeCheapestFavProperty(){

        int minPrice = 1000000000;
        favCheapestPropertyDescription = "We couldn't find\nthe cheapest of your\nfavorite properties.\nTry adding some favorites!.";

        for ( AirbnbListing listing: favoriteListings ){
            if (minPrice > listing.getPrice()*listing.getMinimumNights() ) {
                minPrice = listing.getPrice()*listing.getMinimumNights();
                favCheapestPropertyDescription = "Out of your favorites\nThe cheapest property is\n" + listing.getName() + "\nprovided by " + listing.getHost_name() + "\nin " +listing.getNeighbourhood();
            }

        }

    }

    /**
     * Returns the string containing the statistic on the average number of reviews per propety.
     * @return Statistic on the average number of reviews per property
     */
    private String getAvgNoOfReviewsStat(){
        return "Average number of reviews\n per property: "+avgNumberOfReviews;
    }

    /**
     * Returns a string detailing the number of available properties within the specified price range.
     * @return String containing the number of available properties.
     */
    private String getNumberOfAvailableProperties(){
        return "Number of available properties:\n" + numberOfAvailableProperties;
    }

    /**
     * Returns a string detailing the number of homes and appartments within the specified price range.
     * @return String containing the number of homes and appartments.
     */
    private String getNumberOfHomesAndAppartments() {
        return "Number of homes and appartments:\n" + numberOfHomesAndAppartments;
    }

    /**
     * Returns a string specifying which borough contains the most expensive properties on average. ( Out of those that are within the user's price range )
     * @return String containing the most expensive borough
     */
    private String getMostExpensiveBorough(){
        return "Most expensive borough:\n" + nameMostExpensiveBorough;
    }

    /**
     * Returns a string specifying the most well-connected property out of those favorited by the user.
     * @return String containing the best connected favorite property.
     */
    private String getMostConnectedFavProperty() {
        return mostConnectFavPropertyText;
    }

    /**
     * Returns a string specifying which of the user's favorite properties is in the least polluted area.
     * @return String containing the property that is least polluted out of the user's favorite properties.
     */
    private String getLeastPollutedFavProperty(){
        return leastPollutedFavPropertyText;
    }

    /**
     * Returns a string specifying the cheapest property out of the user's favorite properties.
     * @return String containing the cheapest property out of the user's favorites.
     */
    private String getCheapestFavProperty(){
        return favCheapestPropertyDescription;
    }

    /**
     * Returns a string specifying the borough that contains the most properties favorited by the user.
     * @return The user's favorite borough.
     */
    private String getFavouriteBorough(){
        return favBoroughText;
    }
}
