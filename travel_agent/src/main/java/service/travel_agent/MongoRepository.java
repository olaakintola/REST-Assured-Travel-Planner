package service.travel_agent;

import com.google.gson.Gson;

import static com.mongodb.client.model.Filters.eq;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;

import service.core.MongoBooking;

import java.util.ArrayList;

public class MongoRepository {
    protected MongoCollection<Document> collection;

    public void connectToCollection() {
        final MongoClient mongoClient = MongoClients.create("mongodb+srv://tanmayjoshi:tanmaypass@cluster-quaranteam.sr96d.mongodb.net/bookings?retryWrites=true&w=majority");
        final MongoDatabase database = mongoClient.getDatabase("bookings");
        this.collection = database.getCollection("booking");
    }

    public boolean insertBooking(MongoBooking booking) {
        try {
            final String json = new Gson().toJson(booking);
            final Document doc = Document.parse(json);
            collection.insertOne(doc);
            return true;
        } catch (final Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private MongoBooking createBookingFromMongoDocument(final Document document) {
        String referenceId = document.get("referenceId").toString();
        String flightDetails = document.get("flightDetails").toString();
        String hotelDetails = document.get("hotelDetails").toString(); 
        String activitiesDetails = document.get("activitiesDetails").toString(); 
        String attractionsDetails = document.get("attractionsDetails").toString(); 
        return new MongoBooking(referenceId, flightDetails, hotelDetails, activitiesDetails, attractionsDetails);
    }

    public MongoBooking getBookingFromMongo(final String search) throws NoSuchFieldException{
        try {
            final Document document = collection.find(eq("referenceId", search)).first();
            return createBookingFromMongoDocument(document);
        } catch (final Exception e) {
            return new MongoBooking();
        }
    }

}