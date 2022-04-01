package uk.ac.warwick.cs126.stores;

import uk.ac.warwick.cs126.interfaces.IRestaurantStore;
import uk.ac.warwick.cs126.models.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.IOUtils;

import uk.ac.warwick.cs126.util.MergeSort;
import uk.ac.warwick.cs126.structures.HashMap;
import uk.ac.warwick.cs126.structures.ArrayList;
import uk.ac.warwick.cs126.util.ConvertToPlace;
import uk.ac.warwick.cs126.util.DataChecker;
import uk.ac.warwick.cs126.util.HaversineDistanceCalculator;
import uk.ac.warwick.cs126.util.StringFormatter;

public class RestaurantStore implements IRestaurantStore {

    private HashMap<Long,Restaurant> restaurantArray;
    private DataChecker dataChecker;
    private ArrayList<Long> blacklist;
    private ConvertToPlace convertToPlace;
    private StringFormatter stringFormatter;

    public RestaurantStore() {
        // Initialise variables here
        restaurantArray = new HashMap<>(20);
        dataChecker = new DataChecker();
        blacklist  = new ArrayList<>();
        convertToPlace = new ConvertToPlace();
        stringFormatter = new StringFormatter();
    }

    public Restaurant[] loadRestaurantDataToArray(InputStream resource) {
        Restaurant[] restaurantArray = new Restaurant[0];

        try {
            byte[] inputStreamBytes = IOUtils.toByteArray(resource);
            BufferedReader lineReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int lineCount = 0;
            String line;
            while ((line = lineReader.readLine()) != null) {
                if (!("".equals(line))) {
                    lineCount++;
                }
            }
            lineReader.close();

            Restaurant[] loadedRestaurants = new Restaurant[lineCount - 1];

            BufferedReader csvReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            String row;
            int restaurantCount = 0;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            csvReader.readLine();
            while ((row = csvReader.readLine()) != null) {
                if (!("".equals(row))) {
                    String[] data = row.split(",");

                    Restaurant restaurant = new Restaurant(
                            data[0],
                            data[1],
                            data[2],
                            data[3],
                            Cuisine.valueOf(data[4]),
                            EstablishmentType.valueOf(data[5]),
                            PriceRange.valueOf(data[6]),
                            formatter.parse(data[7]),
                            Float.parseFloat(data[8]),
                            Float.parseFloat(data[9]),
                            Boolean.parseBoolean(data[10]),
                            Boolean.parseBoolean(data[11]),
                            Boolean.parseBoolean(data[12]),
                            Boolean.parseBoolean(data[13]),
                            Boolean.parseBoolean(data[14]),
                            Boolean.parseBoolean(data[15]),
                            formatter.parse(data[16]),
                            Integer.parseInt(data[17]),
                            Integer.parseInt(data[18]));

                    loadedRestaurants[restaurantCount++] = restaurant;
                }
            }
            csvReader.close();

            restaurantArray = loadedRestaurants;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return restaurantArray;
    }

    public boolean addRestaurant(Restaurant restaurant) {
        if (restaurant.equals(null)) return false;
        // If true ID can't be extracted from repeated ID, restaurant isn't added.
        // If true ID can be extracted from repeated ID, ID is set to the extracted ID
        if (dataChecker.extractTrueID(restaurant.getRepeatedID()) == null) return false;
        else restaurant.setID(dataChecker.extractTrueID(restaurant.getRepeatedID()));
        // If restaurant isn't valid, it isn't added
        if (dataChecker.isValid(restaurant) == false) return false;
        // If restaurant shares ID with an already added restaurant, ID is blacklisted and both restaurants are removed
        if (restaurantArray.get(restaurant.getID()) != null) {
            restaurantArray.remove(restaurant.getID());
            blacklist.add(restaurant.getID());
            return false;
        }
        // If restaurant ID is blacklisted, restaurant isn't added
        if (blacklist.contains(restaurant.getID())) return false;

        // If restaurant passes all the checks, it gets added to the store
        restaurantArray.add(restaurant.getID(), restaurant);
        return true;
    }

    public boolean addRestaurant(Restaurant[] restaurants) {
        if (restaurants.equals(null)) return false;
            boolean decision = true;
        for (Restaurant restaurant : restaurants) {
            if (addRestaurant(restaurant) == false)
                decision = false;
        }
        return decision;
    }

    public Restaurant getRestaurant(Long id) {
        if (id == null) return null;
        return restaurantArray.get(id);
    }

    public Restaurant[] getRestaurants() {
        Restaurant[] restaurants = restaurantArray.toArray(Restaurant.class);
        return getRestaurants(restaurants);
    }

    public Restaurant[] getRestaurants(Restaurant[] restaurants) {
        if (restaurants.equals(null)) return null;
        for (int i = 0; i < restaurants.length; i++) {
            restaurants[i].setID(dataChecker.extractTrueID(restaurants[i].getRepeatedID()));
        }
        String[] comparators = {"getID"};
        boolean[] ascending = {true};
        return (Restaurant[]) MergeSort.sort(restaurants,comparators,ascending);
    }

    public Restaurant[] getRestaurantsByName() {
        Restaurant[] restaurants = restaurantArray.toArray(Restaurant.class);
        for (int i = 0; i < restaurants.length; i++) {
            restaurants[i].setID(dataChecker.extractTrueID(restaurants[i].getRepeatedID()));
        }
        String[] comparators = {"getName","getID"};
        boolean[] ascending = {true,true};
        return (Restaurant[]) MergeSort.sort(restaurants,comparators,ascending);
    }

    public Restaurant[] getRestaurantsByDateEstablished() {
        Restaurant[] restaurants = restaurantArray.toArray(Restaurant.class);
        return getRestaurantsByDateEstablished(restaurants);
    }

    public Restaurant[] getRestaurantsByDateEstablished(Restaurant[] restaurants) {
        if (restaurants == null) return null;
        for (int i = 0; i < restaurants.length; i++) {
            restaurants[i].setID(dataChecker.extractTrueID(restaurants[i].getRepeatedID()));
        }
        String[] comparators = {"getDateEstablished","","getName","getID"};
        boolean[] ascending = {true,true,true};
        return (Restaurant[]) MergeSort.sort(restaurants,comparators,ascending);
    }

    public Restaurant[] getRestaurantsByWarwickStars() {
        Restaurant[] restaurants = restaurantArray.toArray(Restaurant.class);
        for (int i = 0; i < restaurants.length; i++) {
            restaurants[i].setID(dataChecker.extractTrueID(restaurants[i].getRepeatedID()));
        }
        String[] comparators = {"getWarwickStars","getName","getID"};
        boolean[] ascending = {true,true,true};
        return (Restaurant[]) MergeSort.sort(restaurants,comparators,ascending);
    }

    public Restaurant[] getRestaurantsByRating(Restaurant[] restaurants) {
        if (restaurants == null) return null;
        for (int i = 0; i < restaurants.length; i++) {
            restaurants[i].setID(dataChecker.extractTrueID(restaurants[i].getRepeatedID()));
        }
        String[] comparators = {"getCustomerRating","getName","getID"};
        boolean[] ascending = {false,true,true};
        return (Restaurant[]) MergeSort.sort(restaurants,comparators,ascending);
    }

    public RestaurantDistance[] getRestaurantsByDistanceFrom(float latitude, float longitude) {
        Restaurant[] restaurants = restaurantArray.toArray(Restaurant.class);
        return getRestaurantsByDistanceFrom(restaurants,latitude,longitude);
    }

    public RestaurantDistance[] getRestaurantsByDistanceFrom(Restaurant[] restaurants, float latitude, float longitude) {
        if (restaurants == null) return null;
        for (int i = 0; i < restaurants.length; i++) {
            restaurants[i].setID(dataChecker.extractTrueID(restaurants[i].getRepeatedID()));
        }
        RestaurantDistance[] restaurantDistances = new RestaurantDistance[restaurants.length];
        for (int i = 0; i < restaurants.length; i++) {
            restaurantDistances[i].setRestaurant(restaurants[i]);
            restaurantDistances[i].setDistance(HaversineDistanceCalculator.inKilometres(restaurants[i].getLatitude(),restaurants[i].getLongitude(),latitude,longitude));
        }
        String[] comparators = {"getCustomerRating","getName","getID"};
        boolean[] ascending = {false,true,true};
        return (RestaurantDistance[]) MergeSort.sort(restaurantDistances,comparators,ascending);
    }

    public Restaurant[] getRestaurantsContaining(String searchTerm) {
        // Converts all accents and makes all characters lower case in 'searchTerm'
        String convertedSearchTerm = stringFormatter.convertAccentsFaster(searchTerm).toLowerCase();
        // Removes all leading spaces, trailing spaces and turns all multiple spaces into single spaces in 'convertedSearchTerm'
        String formattedSearchTerm = convertedSearchTerm.replace("  "," ").trim();

        Restaurant[] restaurants = restaurantArray.toArray(Restaurant.class);
        Restaurant[] searchResults = new Restaurant[restaurants.length];
        int n = 0;
        for (int i = 0; i < restaurants.length; i++) {
            Place place = convertToPlace.convert(restaurants[i].getLatitude(),restaurants[i].getLatitude());
            Cuisine cuisine = restaurants[i].getCuisine();
            String convertedName = stringFormatter.convertAccentsFaster(restaurants[i].getName()).toLowerCase();
            if (place.getName().toLowerCase().contains(formattedSearchTerm)) {
                searchResults[n] = restaurants[i];
                n++;
            }
            else if (cuisine.toString().toLowerCase().contains(formattedSearchTerm)) {
                searchResults[n] = restaurants[i];
                n++;
            }
            else if (convertedName.contains(formattedSearchTerm)) {
                searchResults[n] = restaurants[i];
                n++;
            }
        }

        Restaurant[] temp = searchResults;
        searchResults = new Restaurant[n];
        for (int i = 0; i < searchResults.length; i++) {
            searchResults[i] = temp[i];
        }

        String[] comparators = {"getName","getID"};
        boolean[] ascending = {true,true};
        return (Restaurant[]) MergeSort.sort(searchResults,comparators,ascending);

    }
}
