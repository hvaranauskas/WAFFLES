package uk.ac.warwick.cs126.stores;

import uk.ac.warwick.cs126.interfaces.IFavouriteStore;
import uk.ac.warwick.cs126.models.Favourite;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.IOUtils;

import uk.ac.warwick.cs126.structures.ArrayList;
import uk.ac.warwick.cs126.util.MergeSort;
import uk.ac.warwick.cs126.structures.IdCounter;
import uk.ac.warwick.cs126.util.DataChecker;

public class FavouriteStore implements IFavouriteStore {

    private ArrayList<Favourite> favouriteArray;
    private DataChecker dataChecker;
    private ArrayList<Long> blacklist;
    private ArrayList<Favourite> replacedFavourites;

    public FavouriteStore() {
        // Initialise variables here
        favouriteArray = new ArrayList<>();
        dataChecker = new DataChecker();
        blacklist = new ArrayList<>();
        replacedFavourites = new ArrayList<>();
    }

    public Favourite[] loadFavouriteDataToArray(InputStream resource) {
        Favourite[] favouriteArray = new Favourite[0];

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

            Favourite[] loadedFavourites = new Favourite[lineCount - 1];

            BufferedReader csvReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int favouriteCount = 0;
            String row;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            csvReader.readLine();
            while ((row = csvReader.readLine()) != null) {
                if (!("".equals(row))) {
                    String[] data = row.split(",");
                    Favourite favourite = new Favourite(
                            Long.parseLong(data[0]),
                            Long.parseLong(data[1]),
                            Long.parseLong(data[2]),
                            formatter.parse(data[3]));
                    loadedFavourites[favouriteCount++] = favourite;
                }
            }
            csvReader.close();

            favouriteArray = loadedFavourites;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return favouriteArray;
    }

    public boolean addFavourite(Favourite favourite) {
        if (favourite.equals(null)) return false;

        if (dataChecker.isValid(favourite) == false) return false;

        Long ID = favourite.getID();
        for (int i = 0; i < favouriteArray.size(); i++) {
            if (favouriteArray.get(i).getID().equals(favourite.getID())) {
                favouriteArray.remove(i);
                blacklist.add(ID);
                // If a favourite is removed, checks to see if there's an newer valid favourite with the same customer and restaurant ID which can replace it
                Favourite[] newerFavourites = replacedFavourites.toArray(Favourite.class);
                for (i = 0; i < newerFavourites.length; i++) {
                    if (newerFavourites[i].getCustomerID().equals(favourite.getCustomerID()) && newerFavourites[i].getRestaurantID().equals(favourite.getRestaurantID())) {
                        // ID of newer favourite is removed from blacklist so that it can be added to the favourite store
                        blacklist.remove(newerFavourites[i].getID());
                        // Cycles through all replaced favourites in array, so if it initially adds a more recent favourite but there exists an older one in the array, it will eventually replace the newer favourite with the older favourite
                        if (addFavourite(newerFavourites[i]) == true) {
                            // If the older favourite gets added to store, it is removed from the array of newer favourites to avoid complications
                            replacedFavourites.remove(newerFavourites[i]);
                        }
                    }
                }
                return false;
            }
        }


        if (blacklist.contains(ID)) return false;

        // Checks if the customer currently has a favourite of the restaurant in the store
        Favourite[] allFavourites = favouriteArray.toArray(Favourite.class);
        for (int i = 0; i < allFavourites.length; i++) {
            if (allFavourites[i].getCustomerID().equals(favourite.getCustomerID()) && allFavourites[i].getRestaurantID().equals(favourite.getRestaurantID())) {
                // If the customer already has a favourite of the restaurant currently in the store which is older than the one trying to be added now, the older one is kept and the newer one is blacklisted and not added
                if (favourite.getDateFavourited().compareTo(allFavourites[i].getDateFavourited()) > 0) {
                    blacklist.add(ID);
                    replacedFavourites.add(favourite);
                    return false;
                }
                // If the customer already has a favourite of the restaurant currently in the store which is newer than the one trying to be added, it is replaced with this older favourite and the newer favourite is blacklisted
                else {
                    blacklist.add(allFavourites[i].getID());
                    replacedFavourites.add(allFavourites[i]);
                    favouriteArray.remove(i);
                    favouriteArray.add(favourite);
                    return true;
                }
            }
        }
        // If the favourite is valid and there is no favourite from the customer for the restaurant currently in the store, the favourite is added
        favouriteArray.add(favourite);
        return true;
    }

    public boolean addFavourite(Favourite[] favourites) {
        if (favourites == null) return false;

        boolean decision = true;
        for (int i = 0; i < favourites.length; i++) {
            if (addFavourite(favourites[i]) == false) {
                decision = false;
            }
        }
        return decision;
    }

    public Favourite getFavourite(Long id) {
        Favourite[] favourites = favouriteArray.toArray(Favourite.class);
        for (int i = 0; i < favourites.length; i++) {
            if (favourites[i].getID().equals(id)) return favourites[i];
        }
        return null;
    }

    public Favourite[] getFavourites() {
        Favourite[] favourites = favouriteArray.toArray(Favourite.class);
        String[] comparators = {"getID"};
        boolean[] ascending = {true};
        return (Favourite[]) MergeSort.sort(favourites,comparators,ascending);
    }

    public Favourite[] getFavouritesByCustomerID(Long id) {
        Favourite[] favourites = favouriteArray.toArray(Favourite.class);
        Favourite[] customerFavourites = new Favourite[favourites.length];
        int n = 0;
        for (int i = 0; i < favourites.length; i++) {
            if (favourites[i].getCustomerID().equals(id)) {
                customerFavourites[n] = favourites[i];
                n++;
            }
        }

        // Removes null elements from 'customerFavourites'
        Favourite[] temp = customerFavourites;
        customerFavourites = new Favourite[n];
        for (int i = 0; i < customerFavourites.length; i++) {
            customerFavourites[i] = temp[i];
        }

        String[] comparators = {"getDateFavourited","getID"};
        boolean[] ascending = {false,true};
        return (Favourite[]) MergeSort.sort(customerFavourites,comparators,ascending);
    }

    public Favourite[] getFavouritesByRestaurantID(Long id) {
        Favourite[] favourites = favouriteArray.toArray(Favourite.class);
        Favourite[] restaurantFavourites = new Favourite[favourites.length];
        int n = 0;
        for (int i = 0; i < favourites.length; i++) {
            if (favourites[i].getRestaurantID().equals(id)) {
                restaurantFavourites[n] = favourites[i];
                n++;
            }
        }

        // Removes null elements from 'restaurantFavourites'
        Favourite[] temp = restaurantFavourites;
        restaurantFavourites = new Favourite[n];
        for (int i = 0; i < restaurantFavourites.length; i++) {
            restaurantFavourites[i] = temp[i];
        }

        String[] comparators = {"getDateFavourited","getID"};
        boolean[] ascending = {false,true};
        return (Favourite[]) MergeSort.sort(restaurantFavourites,comparators,ascending);
    }

    public Long[] getCommonFavouriteRestaurants(Long customer1ID, Long customer2ID) {
        // Don't need to sort the array in this method before returning it as the method 'getFavouritesByCustomerID' already gives the arrays to us sorted in our desired order
        Favourite[] customer1Faves = getFavouritesByCustomerID(customer1ID);
        Favourite[] customer2Faves = getFavouritesByCustomerID(customer2ID);

        Favourite[] commonFavourites = new Favourite[customer1Faves.length];
        int n = 0;
        for (int i = 0; i < customer1Faves.length; i++) {
            for (int j = i; j < customer2Faves.length; j++) {
                if (customer1Faves[i].getRestaurantID().equals(customer2Faves[j].getRestaurantID())) {
                    if (customer1Faves[i].getDateFavourited().compareTo(customer2Faves[i].getDateFavourited()) > 0) {
                        commonFavourites[n] = customer1Faves[i];
                        n++;
                    } else {
                        commonFavourites[n] = customer2Faves[j];
                        n++;
                    }
                }
            }
        }

        // Removes null elements from 'commonFavourites'
        Favourite[] temp = commonFavourites;
        for (int i = 0; i < commonFavourites.length; i++) {
            commonFavourites[i] = temp[i];
        }

        Long[] idFavourites = new Long[commonFavourites.length];
        for (int i = 0; i < commonFavourites.length; i++) {
            idFavourites[i] = commonFavourites[i].getRestaurantID();
        }
        return idFavourites;
    }

    public Long[] getMissingFavouriteRestaurants(Long customer1ID, Long customer2ID) {
        // Don't need to sort the array in this method before returning it as the method 'getFavouritesByCustomerID' already gives the arrays to us sorted in our desired order
        Favourite[] customer1Faves = getFavouritesByCustomerID(customer1ID);
        Favourite[] customer2Faves = getFavouritesByCustomerID(customer2ID);

        Favourite[] missingFavourites = new Favourite[customer1Faves.length];
        int n = 0;
        for (int i = 0; i < customer1Faves.length; i++) {
            for (int j = i+1; j < customer2Faves.length; j++) {
                if (customer1Faves[i].getRestaurantID().equals(customer2Faves[j].getRestaurantID()) == false) {
                    missingFavourites[n] = customer1Faves[i];
                    n++;
                }
            }
        }

        // Removes null elements from 'missingFavourites'
        Favourite[] temp = missingFavourites;
        for (int i = 0; i < missingFavourites.length; i++) {
            missingFavourites[i] = temp[i];
        }

        Long[] idFavourites = new Long[missingFavourites.length];
        for (int i = 0; i < missingFavourites.length; i++) {
            idFavourites[i] = missingFavourites[i].getRestaurantID();
        }
        return idFavourites;
    }

    public Long[] getNotCommonFavouriteRestaurants(Long customer1ID, Long customer2ID) {
        // Don't need to sort the array in this method before returning it as the method 'getFavouritesByCustomerID' already gives the arrays to us sorted in our desired order
        Favourite[] customer1Faves = getFavouritesByCustomerID(customer1ID);
        Favourite[] customer2Faves = getFavouritesByCustomerID(customer2ID);

        Favourite[] notCommonFavourites = new Favourite[customer1Faves.length];
        int n = 0;
        for (int i = 0; i < customer1Faves.length; i++) {
            for (int j = i+1; j < customer2Faves.length; j++) {
                if (customer1Faves[i].getRestaurantID().equals(customer2Faves[j].getRestaurantID()) == false) {
                    notCommonFavourites[n] = customer1Faves[i];
                    notCommonFavourites[n++] = customer2Faves[j];
                    n++;
                }
            }
        }

        // Removes null elements from 'notCommonFavourites'
        Favourite[] temp = notCommonFavourites;
        for (int i = 0; i < notCommonFavourites.length; i++) {
            notCommonFavourites[i] = temp[i];
        }

        Long[] idFavourites = new Long[notCommonFavourites.length];
        for (int i = 0; i < notCommonFavourites.length; i++) {
            idFavourites[i] = notCommonFavourites[i].getRestaurantID();
        }
        return idFavourites;
    }

    // Final two methods work essentially the same as the 'getTop...ByReviewCount' methods in review store, so won't repeat explanation to avoid repetition
    public Long[] getTopCustomersByFavouriteCount() {
        Favourite[] favourites = favouriteArray.toArray(Favourite.class);
        String[] comparators = {"getCustomerID","getDateReviewed"};
        boolean[] ascending = {true,false};
        Favourite[] sortedByID = (Favourite[]) MergeSort.sort(favourites,comparators,ascending);

        IdCounter[] idCounter = new IdCounter[sortedByID.length];
        int n = 0;
        idCounter[n] = new IdCounter(sortedByID[n].getCustomerID(),sortedByID[n].getDateFavourited());
        for (int i = 0; i < sortedByID.length; i++) {
            if (idCounter[n].getID().equals(sortedByID[i].getCustomerID()) == false) {
                n++;
                idCounter[n] = new IdCounter(sortedByID[i].getCustomerID(),sortedByID[i].getDateFavourited());
            }
            idCounter[n].incrementTally();
        }

        // Removes null elements from 'idCounter'
        IdCounter[] temp = idCounter;
        idCounter = new IdCounter[n];
        for (int i = 0; i < idCounter.length; i++) {
            idCounter[i] = temp[i];
        }

        comparators = new String[] {"getTally","getFinalReviewDate","getID"};
        ascending = new boolean[] {false,true,true};
        IdCounter[] sortedByFavouriteCount = (IdCounter[]) MergeSort.sort(idCounter,comparators,ascending);

        Long[] topCustomers = new Long[20];
        for (int i = 0; i < sortedByFavouriteCount.length; i++) {
            topCustomers[i] = sortedByFavouriteCount[i].getID();
            if (i >=19) return topCustomers;
        }
        return topCustomers;
    }

    public Long[] getTopRestaurantsByFavouriteCount() {
        Favourite[] favourites = favouriteArray.toArray(Favourite.class);
        String[] comparators = {"getRestaurantID","getDateReviewed"};
        boolean[] ascending = {true,false};
        Favourite[] sortedByID = (Favourite[]) MergeSort.sort(favourites,comparators,ascending);

        IdCounter[] idCounter = new IdCounter[sortedByID.length];
        int n = 0;
        idCounter[n] = new IdCounter(sortedByID[n].getRestaurantID(),sortedByID[n].getDateFavourited());
        for (int i = 0; i < sortedByID.length; i++) {
            if (idCounter[n].getID().equals(sortedByID[i].getRestaurantID()) == false) {
                n++;
                idCounter[n] = new IdCounter(sortedByID[i].getRestaurantID(),sortedByID[i].getDateFavourited());
            }
            idCounter[n].incrementTally();
        }

        // Removes null elements from 'idCounter'
        IdCounter[] temp = idCounter;
        idCounter = new IdCounter[n];
        for (int i = 0; i < idCounter.length; i++) {
            idCounter[i] = temp[i];
        }

        comparators = new String[] {"getTally","getFinalReviewDate","getID"};
        ascending = new boolean[] {false,true,true};
        IdCounter[] sortedByFavouriteCount = (IdCounter[]) MergeSort.sort(idCounter,comparators,ascending);

        Long[] topRestaurants = new Long[20];
        for (int i = 0; i < sortedByFavouriteCount.length; i++) {
            topRestaurants[i] = sortedByFavouriteCount[i].getID();
            if (i >=19) return topRestaurants;
        }
        return topRestaurants;
    }
}
