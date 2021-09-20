// package service.attractions;

// import com.amadeus.resources.Activity;
// import com.amadeus.resources.PointOfInterest;
// import org.junit.Before;
// import org.junit.Test;
// import service.core.ActivityItem;
// import service.core.Attraction;
// import service.core.Geocode;

// import java.util.Arrays;

// import static org.junit.Assert.*;
// public class AttractionsServiceTest {

//     private static AttractionsService service;

//     @Before
//     public void setUp() { service = new AttractionsService(); }

//     @Test
//     public void getDestinationGeocodeTest() {
//         assertNotNull("Error : A Geocode object should be returned", service.getDestinationGeocode("barcelona", "spain"));
//         assertNotNull("Error : A Geocode object should be returned", service.getDestinationGeocode("Garðabær", "iceland"));
//         assertEquals("Error : It should be a Geocode object", Geocode.class, service.getDestinationGeocode("malacca", "malaysia").getClass());
//     }

//     @Test
//     public void getAttractionsTest() {
//         PointOfInterest[] barcelona = service.getAttractions(41.39715, 2.160873); // Barcelona
//         System.out.println(Arrays.toString(barcelona) + " Length : " + barcelona.length);
//         assertNotNull("Error : There should have attraction instances available", barcelona);
//         assertNotEquals("Error : This list shouldn't be empty (Barcelona, Spain)", 0, barcelona.length);
//     }

//     @Test
//     public void getAttractionsWithQueriesOnSupportedCitiesTest() {
//         // Reference : https://github.com/amadeus4dev/data-collection/blob/master/data/pois.md

//         // Barcelona is one of the supported city
//         Attraction[] barcelona = service.getAttractionsWithQueries("barcelona", "spain");
//         assertNotNull("Error : It should return an array of ActivityItem objects", barcelona);
//         assertNotEquals("Error : The ActivityItem array shouldn't be empty since Barcelona is a valid location", 0, barcelona.length);

//         Attraction[] paris = service.getAttractionsWithQueries("paris", "france");
//         assertNotNull("Error : It should return an array of ActivityItem objects", paris);
//         assertNotEquals("Error : The ActivityItem array shouldn't be empty since Barcelona is a valid location", 0, paris.length);
//     }

//     @Test
//     public void getAttractionsWithQueriesOnUnsupportedCitiesTest() {
//         // Cities that are not supported by the API sandbox in Testing Environment
//         Attraction[] dublin = service.getAttractionsWithQueries("dublin", "ireland");
//         assertNotNull("Error : It should return an array of ActivityItem objects", dublin);
//         assertEquals("Error : The ActivityItem array should be empty since Dublin is an invalid location for this API", 0, dublin.length);

//         Attraction[] madrid = service.getAttractionsWithQueries("madrid", "spain");
//         assertNotNull("Error : It should return an array of ActivityItem objects", madrid);
//         assertEquals("Error : The ActivityItem array should be empty since Madrid is an invalid location for this API", 0, madrid.length);
//     }
// }
