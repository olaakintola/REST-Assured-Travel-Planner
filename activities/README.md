# Travel Activities Recommender

A feature that generates a list of activities available in a given location. In ideal, this feature searches activities available in location given through user query on UI, and return a list of relevant activities.

## About this module

This module is meant to be an activity finder for users who have input both the city and country name as the location, and generate a list of activities according to that location. Here is the full design stages to provide the service :

| Step | TODO                               | Purpose + HOW TO                                                                  | Progress       |
| ---- | ---------------------------------- | --------------------------------------------------------------------------------- | -------------- |
| 1    | Get location query from the user   | Location queries (ie. city & country) are accepted through App UI                 | Not-applicable |
| 2    | Validate the query received        | Done with Regular Expression, to reject queries with unwanted characters or blank | Done           |
| 3    | Search for geocode of a location   | Finding the location through its geocode with Nominatim Search API                | Done           |
| 4    | Use the geocode to find activities | Required parameter : Geo-coordinate^                                              | Done           |
| 5    | Translate Actvity class            | Amadeus Java SDK Activity object -> Java Bean Activity object (Array)             | Done           |

Given 2 Parameters (CityName, CountryName) as the location -> A list of Activity objects which represent individual activities available in that location

Done by using a single Java method `getActivitiesWithQueries()` with _2 HTTP GET Method Calls from 2 different APIs_  

^ Reference [here](https://amadeus4dev.github.io/amadeus-java/reference/com/amadeus/shopping/Activities.html)

## Tools & Libraries

- IntelliJ IDEA Community Edition
- Maven
- Postman
- [JSON Simple Library](https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple/1.1.1)
- [Amadeus API](https://developers.amadeus.com)
- [Nominatim Search API](https://nominatim.org/release-docs/develop/api/Search/) (Free + Open-Source, No registration required)

### Notes

#### ~~API Credentials~~

- On Java class _ActivitiesRecommenderService_, the API credentials are not included due to [security practice](https://developers.amadeus.com/blog/best-practices-api-key-storage).
- Contact [me](mailto:vincentgoh1998@gmail.com) to get my API credential or using your own Amadeus Developer Credentials.
- The implementation here fetches those credentials from the machine ENVIRONMENT variables *AMADEUS_CLIENT_ID* and *AMADEUS_CLIENT_SECRET* with `System.getenv()`

#### API Response Examples

- An example of __Nominatim Search API response__ is attached (ie. _successful_location_search.json_), while it is basically in __JSON__ format in this documentation [link](https://nominatim.org/release-docs/develop/api/Output/)
- There is also an example of __Activty[] Response__ of __Amadeus Tours & Activities API__ in this module (ie. _successful_activity_list.json_). The `Activity` class in `core` module is almost the duplicate of `Activity` class of Amadeus Java SDK, while it is in _Java Bean_ format, and only important information are stored.

#### Query Validation

- City or country name with _non-ASCII character(s)_ are __accepted__, example Düsseldorf (Germany), Provence-Alpes-Côte d'Azur (France) and Garðabær (Iceland)
- Most of the special characters such as tab ('\t') ane newline ('\n') character are __unaccepted__ except _whitespace_

### Input Tests

Tests for `https://test.api.amadeus.com/v1/shopping/activities?latitude=&longitude=` :

| Test    | Input to test                                              | Example (latitude, longitude)        | Activities?     |
| ------- | ---------------------------------------------------------- | ------------------------------------ | --------------- |
| &#9745; | Geolocation of the supported location                      | Barcelona(41.397158, 2.160873)       | Yes             |
| &#9745; | Geolocation of the supported location                      | London(51.5073219,-0.1276474)        | Yes             |
| &#9745; | Geolocation of location doesn't included in the link above | Manchester(53.4794892,-2.2451148)    | Yes             |
| &#9745; | Geolocation of location doesn't included in the link above | Madrid(40.4167047, -3.7035825)       | Yes             |
| &#9745; | Geolocation of location doesn't included in the link above | Kuala Lumpur(3.1516964, 101.6942371) | Yes             |
| &#9745; | Geolocation of location doesn't included in the link above | Malacca(2.2245111, 102.2614662)      | Internal Error^ |
| &#9745; | Geolocation of location doesn't included in the link above | Beijing(39.9020803,116.7185213)      | No              |

It turns out _searching/representing location with their geolocation is the most efficient way_. The translation from location name to its geolocation is one by using __Nominatim API__

^___Note : It shows that there may have some issue of activities searching in certain geolocation (country)___

For more tests done, look into unit test cases in class `ActivitiesRecommenderServiceTest.java` (located in `/test` directory)

### References

- [Amadeus Java SDK](https://github.com/amadeus4dev/amadeus-java)
- [Amadeus API Java SDK Documentation](https://amadeus4dev.github.io/amadeus-java/reference/packages.html)
- [Amadeus Tours & Activities API Reference](https://developers.amadeus.com/self-service/category/destination-content/api-doc/tours-and-activities/api-reference)
- [Amadeus for Developers Documentation](https://documenter.getpostman.com/view/2672636/RWEcPfuJ?version=latest)
- [IATA Airline and Location Code Search](https://www.iata.org/en/publications/directories/code-search/)
- [GPS Coordinates Checker](https://www.gps-coordinates.net)
- [Nominatim : Open-Source search based on OpeenStreetMap data](https://nominatim.org/release-docs/develop/api/Search/)
- [Tutorial : How to Parse JSON Data From a REST API Using a Simple JSON Library](https://dzone.com/articles/how-to-parse-json-data-from-a-rest-api-using-simpl)
- [Regular Expression for City Name](https://stackoverflow.com/questions/11757013/regular-expressions-for-city-name)
