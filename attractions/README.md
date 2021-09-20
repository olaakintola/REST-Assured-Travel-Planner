# Attraction Recommender

A recommender that returns a list of attraction available in a given city, with the integration of Amadeus API

## About this module

This module has the similar structrue as `activities` module, which also requires the full name of both city and country of the destination.

Given 2 Parameters (CityName, CountryName) as the location -> An array of Attraction objects which represent individual activities available in that location

## APIs and Libraries used

- [JSON Simple Library](https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple/1.1.1)
- [Amadeus API](https://developers.amadeus.com)
- [Nominatim Search API](https://nominatim.org/release-docs/develop/api/Search/) (Free + Open-Source, No registration required)

### Limitiation

Due to the limitation of Amadeus API in a testing environment, there is location restriction which the fetching of `PointOfInterested` object is only available in the following 7 cities (city, country) :

- Bangalore, India
- Barcelona, Spain
- Berlin, Germany
- Dallas, United States
- New York, United States
- San Franciso, United States
- London, United Kingdom

__Reference__ : <https://github.com/amadeus4dev/data-collection/blob/master/data/pois.md>

_Note_ : Although Activity class is also mentioned in the documentation from the link above, however the testing result shows that most of the cities are supported by `Activity` API method (except _China and North Korea_) but __NOT__ the case of `PointOfInterest` API method.

### References

- [Amadeus Java SDK](https://github.com/amadeus4dev/amadeus-java)
- [Amadeus API Java SDK Documentation](https://amadeus4dev.github.io/amadeus-java/reference/packages.html)
- [Amadeus Points Of Interest API Reference](https://developers.amadeus.com/self-service/category/destination-content/api-doc/points-of-interest/api-reference)
- [Amadeus for Developers Documentation](https://documenter.getpostman.com/view/2672636/RWEcPfuJ?version=latest)
- [GPS Coordinates Checker](https://www.gps-coordinates.net)
- [Nominatim : Open-Source search based on OpeenStreetMap data](https://nominatim.org/release-docs/develop/api/Search/)
- [Tutorial : How to Parse JSON Data From a REST API Using a Simple JSON Library](https://dzone.com/articles/how-to-parse-json-data-from-a-rest-api-using-simpl)
- [Regular Expression for City Name](https://stackoverflow.com/questions/11757013/regular-expressions-for-city-name)