import static org.junit.Assert.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.stream.Collectors;

/**
 * Unit testing for PropertiesAvailabilityIndicator class. It checks if update method is
 * handling correctly all possible inputs. It also checks if the list containing each borough
 * properties contains only the properties from the right borough.
 *
 * @author Mateusz Adamski K20063061.
 * @version 1.2
 */
class PropertiesAvailabilityIndicatorTest {
    public PropertiesAvailabilityIndicator propertiesAvailabilityIndicator;
    ArrayList<AirbnbListing> airbnbListings;

    @Before
    void setUp() {
        airbnbListings = new AirbnbDataLoader().load();
        propertiesAvailabilityIndicator = new PropertiesAvailabilityIndicator(
                1000, 1000, airbnbListings);
    }

    @After
    void tearDown() {
    }

    @Test
    void updateNullList() {
        propertiesAvailabilityIndicator.update(null);
        for (BoroughInformation boroughInformation : propertiesAvailabilityIndicator.getBoroughsInfoList()) {
            assertTrue(boroughInformation.getProperties().isEmpty());
        }
    }

    @Test
    void updateNewList() {
        ArrayList<AirbnbListing> temp = new ArrayList<>();
        propertiesAvailabilityIndicator.update(temp);
        for (BoroughInformation boroughInformation : propertiesAvailabilityIndicator.getBoroughsInfoList()) {
            assertTrue(boroughInformation.getProperties().isEmpty());
        }
    }

    @Test
    void updateEmptiedList() {
        ArrayList<AirbnbListing> temp = airbnbListings;
        temp = (ArrayList) temp.stream().filter(v -> v.getPrice() < 0).collect(Collectors.toList());
        propertiesAvailabilityIndicator.update(temp);
        for (BoroughInformation boroughInformation : propertiesAvailabilityIndicator.getBoroughsInfoList()) {
            assertTrue(boroughInformation.getProperties().isEmpty());
        }
    }

    @Test
    void updateFilteredList() {
        ArrayList<AirbnbListing> temp = airbnbListings;
        temp = (ArrayList) temp.stream().filter(v -> v.getPrice() < 100 && v.getPrice() > 20).collect(Collectors.toList());
        propertiesAvailabilityIndicator.update(temp);
        for (BoroughInformation boroughInformation : propertiesAvailabilityIndicator.getBoroughsInfoList()) {
            assertFalse(boroughInformation.getProperties().isEmpty());
        }
    }

    @Test
    void updateFullList() {
        propertiesAvailabilityIndicator.update(airbnbListings);
        for (BoroughInformation boroughInformation : propertiesAvailabilityIndicator.getBoroughsInfoList()) {
            assertFalse(boroughInformation.getProperties().isEmpty());
        }
    }

    @Test
    void getBoroughsInfoListNullList() {
        propertiesAvailabilityIndicator.update(null);
        ArrayList<BoroughInformation> temp = propertiesAvailabilityIndicator.getBoroughsInfoList();
        for (BoroughInformation boroughInformation : temp) {
            for (AirbnbListing airbnbListing : boroughInformation.getProperties()) {
                assertTrue(boroughInformation.getName().equals(airbnbListing.getNeighbourhood()));
            }
        }
    }

    @Test
    void getBoroughsInfoListNewList() {
        ArrayList<AirbnbListing> update = new ArrayList<>();
        propertiesAvailabilityIndicator.update(update);
        ArrayList<BoroughInformation> temp = propertiesAvailabilityIndicator.getBoroughsInfoList();
        for (BoroughInformation boroughInformation : temp) {
            for (AirbnbListing airbnbListing : boroughInformation.getProperties()) {
                assertTrue(boroughInformation.getName().equals(airbnbListing.getNeighbourhood()));
            }
        }
    }

    @Test
    void getBoroughsInfoListEmptiedList() {
        ArrayList<AirbnbListing> update = airbnbListings;
        update = (ArrayList) update.stream().filter(v -> v.getPrice() < 0).collect(Collectors.toList());
        propertiesAvailabilityIndicator.update(update);
        ArrayList<BoroughInformation> temp = propertiesAvailabilityIndicator.getBoroughsInfoList();
        for (BoroughInformation boroughInformation : temp) {
            for (AirbnbListing airbnbListing : boroughInformation.getProperties()) {
                assertTrue(boroughInformation.getName().equals(airbnbListing.getNeighbourhood()));
            }
        }
    }

    @Test
    void getBoroughsInfoListFilteredList() {
        ArrayList<AirbnbListing> update = airbnbListings;
        update = (ArrayList) update.stream().filter(v -> v.getPrice() < 100 && v.getPrice() > 20).collect(Collectors.toList());
        propertiesAvailabilityIndicator.update(update);
        ArrayList<BoroughInformation> temp = propertiesAvailabilityIndicator.getBoroughsInfoList();
        for (BoroughInformation boroughInformation : temp) {
            for (AirbnbListing airbnbListing : boroughInformation.getProperties()) {
                assertTrue(boroughInformation.getName().equals(airbnbListing.getNeighbourhood()));
            }
        }
    }

    @Test
    void getBoroughsInfoListFullList() {
        ArrayList<BoroughInformation> temp = propertiesAvailabilityIndicator.getBoroughsInfoList();
        for (BoroughInformation boroughInformation : temp) {
            for (AirbnbListing airbnbListing : boroughInformation.getProperties()) {
                assertTrue(boroughInformation.getName().equals(airbnbListing.getNeighbourhood()));
            }
        }
    }
}
