package service.recommendations;

import com.amadeus.resources.Activity;
import org.junit.Before;
import org.junit.Test;
import service.core.ActivityItem;
import service.core.Geocode;

import static org.junit.Assert.*;

public class ActivitiesRecommenderServiceTest {

    private static ActivitiesRecommenderService recommender;

    @Before
    public void setUp() {
        recommender = new ActivitiesRecommenderService();
    }

    @Test
    public void isValidLocationNameWithValidInputTest() {
        assertTrue("Error : 'Dublin' should be a valid city name", recommender.isValidLocationName("Dublin"));
        assertTrue("Error : 'dublin' should be a valid city name", recommender.isValidLocationName("dublin"));
        assertTrue("Error : 'San Fransisco' should be a valid city name", recommender.isValidLocationName("San Fransisco"));
        assertTrue("Error : 'Val-d'Or' should be a valid city name", recommender.isValidLocationName("Val-d'Or"));
        assertTrue("Error : 'Düsseldorf' should be a valid city name", recommender.isValidLocationName("Düsseldorf"));
        assertTrue("Error : 'Provence-Alpes-Côte d'Azur' should be a valid city name", recommender.isValidLocationName("Provence-Alpes-Côte d'Azur"));
        assertTrue("Error : 'Garðabær' should be a valid city name", recommender.isValidLocationName("Garðabær"));
    }

    @Test
    public void isValidLocationNameWithInvalidInputTest() {
        // Any whitespace character besides space bar is considered invalid
        assertFalse("Error : 'San\tFransisco' shouldn't be a valid city name", recommender.isValidLocationName("San\tFransisco"));
        assertFalse("Error : 'd#ublin' shouldn't be a valid city name", recommender.isValidLocationName("d#ublin"));
        assertFalse("Error : 'dublin@' shouldn't be a valid city name", recommender.isValidLocationName("dublin@"));
        assertFalse("Error : 'dublin;' shouldn't be a valid city name", recommender.isValidLocationName("dublin;"));
        assertFalse("Error : 'dublin;' shouldn't be a valid city name", recommender.isValidLocationName("//dublin"));
        assertFalse("Error : 'Val-d'Or' should be a valid city name", recommender.isValidLocationName("Val--d'Or"));
        assertFalse("Error : '\");' shouldn't be a valid city name", recommender.isValidLocationName("\");"));
    }

    @Test
    public void getDestinationWithValidQueriesTest() {
        assertNotNull("Error : A Geocode object should be returned", recommender.getDestinationGeocode("dublin", "ireland"));
        assertNotNull("Error : A Geocode object should be returned", recommender.getDestinationGeocode("Garðabær", "iceland"));
        assertEquals("Error : It should be a Geocode object", Geocode.class, recommender.getDestinationGeocode("malacca", "malaysia").getClass());
    }
    @Test
    public void getDestinationWithInvalidQueriesTest() {
        assertNull("Error : City and Country Code are considered invalid queries, full name required", recommender.getDestinationGeocode("dub", "ire"));
        assertNull("Error : Nothing should be returned for invalid location", recommender.getDestinationGeocode("wtf", "wtf"));
        assertNull("Error : Nothing should be returned for blank parameter", recommender.getDestinationGeocode("wtf", ""));
        assertNull("Error : Nothing should be returned for blank parameters", recommender.getDestinationGeocode("", ""));
    }

     @Test
     public void getActivitiesValidTest() {

         Activity[] barcelona = recommender.getActivities(41.39715, 2.160873); // Barcelona
         assertNotNull("Error : There should have activity instances available", barcelona);
         assertNotEquals("Error : This list shouldn't be empty (Barcelona, Spain)", 0, barcelona.length);

         Geocode dest = recommender.getDestinationGeocode("dublin", "ireland");
         Activity[] dublin = recommender.getActivities(dest.getLatitude(), dest.getLongitude());
         assertNotNull("Error : There should have activity instances available", dublin);
         assertNotEquals("Error : This list should be not empty (Dublin, Ireland))", 0, dublin.length);

         dest = recommender.getDestinationGeocode("Düsseldorf", "Germany");
         Activity[] dusseldorf = recommender.getActivities(dest.getLatitude(), dest.getLongitude());
         assertNotNull("Error : There should have activity instances available", dusseldorf);
         assertNotEquals("Error : This list should be not empty (Düsseldorf, Germany))", 0, dusseldorf.length);
     }

    @Test
    public void getActivitiesInvalidTest() {

        // Cities in China are not supported by Amadeus API -> It is possible some countries aren't supported
        Geocode dest = recommender.getDestinationGeocode("beijing", "china");
        Activity[] beijing = recommender.getActivities(dest.getLatitude(), dest.getLongitude());
        assertNotNull("Error : The activity array instance should be returned", beijing);
        assertEquals("Error : This list should be empty since this location is not supported ()", 0, beijing.length);

        dest = recommender.getDestinationGeocode("Pyongyang", "North Korea");
        Activity[] pyongyang = recommender.getActivities(dest.getLatitude(), dest.getLongitude());
        assertNotNull("Error : The activity array instance should be returned", pyongyang);
        assertEquals("Error : This list should be empty since this location is not supported", 0, pyongyang.length);
    }

    @Test
    public void getActivitiesWithQueriesTest() {

        ActivityItem[] dublin = recommender.getActivitiesWithQueries("dublin", "ireland");
        assertNotNull("Error : It should return an array of ActivityItem objects", dublin);
        assertNotEquals("Error : The ActivityItem array shouldn't be empty since it is a valid location", 0, dublin.length);

        ActivityItem[] invalid = recommender.getActivitiesWithQueries("dublin", "china");
        assertNotNull("Error : It should return an array of ActivityItem objects", invalid);
        assertEquals("Error : The ActivityItem array should be empty since the invalid location", 0, invalid.length);

        ActivityItem[] unsupported = recommender.getActivitiesWithQueries("beijing", "china");
        assertNotNull("Error : It should return an array of ActivityItem objects", unsupported);
        assertEquals("Error : The ActivityItem array should be empty since it is an unsupported location", 0, unsupported.length);
    }
}