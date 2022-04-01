package uk.ac.warwick.cs126.stores;

import uk.ac.warwick.cs126.interfaces.IReviewStore;
import uk.ac.warwick.cs126.models.Review;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import org.apache.commons.io.IOUtils;

import uk.ac.warwick.cs126.structures.ArrayList;
import uk.ac.warwick.cs126.structures.AverageReviewCounter;
import uk.ac.warwick.cs126.structures.HashMap;
import uk.ac.warwick.cs126.structures.IdCounter;
import uk.ac.warwick.cs126.structures.KeywordCounter;
import uk.ac.warwick.cs126.util.DataChecker;
import uk.ac.warwick.cs126.util.KeywordChecker;
import uk.ac.warwick.cs126.util.MergeSort;
import uk.ac.warwick.cs126.util.StringFormatter;

public class ReviewStore implements IReviewStore {

    private HashMap<Long,Review> reviewHashMap;
    private DataChecker dataChecker;
    private ArrayList<Long> blacklist;
    private KeywordChecker keywordChecker;
    private ArrayList<Review> replacedReviews;

    public ReviewStore() {
        // Initialise variables here
        reviewHashMap = new HashMap<>(20);
        dataChecker = new DataChecker();
        blacklist = new ArrayList<>();
        replacedReviews = new ArrayList<>();
    }

    public Review[] loadReviewDataToArray(InputStream resource) {
        Review[] reviewArray = new Review[0];

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

            Review[] loadedReviews = new Review[lineCount - 1];

            BufferedReader tsvReader = new BufferedReader(new InputStreamReader(
                    new ByteArrayInputStream(inputStreamBytes), StandardCharsets.UTF_8));

            int reviewCount = 0;
            String row;
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

            tsvReader.readLine();
            while ((row = tsvReader.readLine()) != null) {
                if (!("".equals(row))) {
                    String[] data = row.split("\t");
                    Review review = new Review(
                            Long.parseLong(data[0]),
                            Long.parseLong(data[1]),
                            Long.parseLong(data[2]),
                            formatter.parse(data[3]),
                            data[4],
                            Integer.parseInt(data[5]));
                    loadedReviews[reviewCount++] = review;
                }
            }
            tsvReader.close();

            reviewArray = loadedReviews;

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return reviewArray;
    }

    public boolean addReview(Review review) {
        if (review.equals(null)) return false;

        if (dataChecker.isValid(review) == false) return false;

        Long ID = review.getID();
        if (reviewHashMap.get(ID) != null) {
            reviewHashMap.remove(ID);
            blacklist.add(ID);
            // If a review is removed, checks to see if there's an older valid review with the same customer and restaurant ID which can replace it
            Review[] olderReviews = replacedReviews.toArray(Review.class);
            for (int i = 0; i < olderReviews.length; i++) {
                if (olderReviews[i].getCustomerID().equals(review.getCustomerID()) && olderReviews[i].getRestaurantID().equals(review.getRestaurantID())) {
                    // ID of older review is removed from blacklist so that it can be added to the review store
                    blacklist.remove(olderReviews[i].getID());
                    // Cycles through all replaced reviews in array, so if it initially adds an older review but there exists a newer one in the array, it will eventually replace the older review with the newer review
                    if (addReview(olderReviews[i]) == true) {
                        // If the older review gets added to store, it is removed from the array of older reviews to avoid complications
                        replacedReviews.remove(olderReviews[i]);
                    }
                }
            }
            return false;
        }

        if (blacklist.contains(ID)) return false;

        // Checks if the customer currently has a review of the restaurant in the store
        Review[] allReviews = reviewHashMap.toArray(Review.class);
        for (int i = 0; i < allReviews.length; i++) {
            if (allReviews[i].getCustomerID().equals(review.getCustomerID()) && allReviews[i].getRestaurantID().equals(review.getRestaurantID())) {
                // If the customer already has a review of the restaurant currently in the store which is newer than the one trying to be added now, the newer one is kept and the older one is blacklisted and not added
                if (review.getDateReviewed().compareTo(allReviews[i].getDateReviewed()) < 0) {
                    blacklist.add(ID);
                    replacedReviews.add(review);
                    return false;
                }
                // If the customer already has a review of the restaurant currently in the store which is older than the one trying to be added, it is replaced with this new review and the older review is blacklisted
                else {
                    blacklist.add(allReviews[i].getID());
                    replacedReviews.add(allReviews[i]);
                    reviewHashMap.remove(allReviews[i].getID());
                    reviewHashMap.add(ID, review);
                    return true;
                }
            }
        }
        // If the review is valid and there is no review from the customer for the restaurant currently in the store, the review is added
        reviewHashMap.add(ID,review);
        return true;
    }

    public boolean addReview(Review[] reviews) {
        if (reviews.equals(null)) return false;

        boolean decision = true;
        for (Review review : reviews) {
            if (addReview(review) == false) {
                decision = false;
            }
        }
        return decision;
    }

    public Review getReview(Long id) {
        return reviewHashMap.get(id);
    }

    public Review[] getReviews() {
        Review[] reviews = reviewHashMap.toArray(Review.class);
        String[] comparators = {"getID"};
        boolean[] ascending = {true};
        return (Review[]) MergeSort.sort(reviews,comparators,ascending);
    }

    public Review[] getReviewsByDate() {
        Review[] reviews = reviewHashMap.toArray(Review.class);
        String[] comparators = {"getDateReviewed","getID"};
        boolean[] ascending = {false,true};
        return (Review[]) MergeSort.sort(reviews,comparators,ascending);
    }

    public Review[] getReviewsByRating() {
        Review[] reviews = reviewHashMap.toArray(Review.class);
        String[] comparators = {"getRating","getDateReviewed","getID"};
        boolean[] ascending = {false,false,true};
        return (Review[]) MergeSort.sort(reviews,comparators,ascending);
    }

    public Review[] getReviewsByCustomerID(Long id) {
        if (id == null) return null;

        Review[] allReviews = reviewHashMap.toArray(Review.class);
        Review[] reviewsByCustomer = new Review[allReviews.length];
        int n = 0;
        for (int i = 0; i < allReviews.length; i++) {
            if (allReviews[i].getCustomerID().equals(id)) {
                reviewsByCustomer[n] = allReviews[i];
                n++;
            }
        }

        // Makes it so that there are no null elements in 'reviewsByCustomer'
        Review[] temp = reviewsByCustomer;
        reviewsByCustomer = new Review[n];
        for (int i = 0; i < reviewsByCustomer.length; i++) {
            reviewsByCustomer[i] = temp[i];
        }

        String[] comparators = {"getDateReviewed", "getID"};
        boolean[] ascending = {false,true};
        return (Review[]) MergeSort.sort(reviewsByCustomer, comparators, ascending);
    }

    public Review[] getReviewsByRestaurantID(Long id) {
        if (id == null) return null;

        Review[] allReviews = reviewHashMap.toArray(Review.class);
        Review[] reviewsForRestaurant = new Review[allReviews.length];
        int n = 0;
        for (int i = 0; i < allReviews.length; i++) {
            if (allReviews[i].getRestaurantID().equals(id)) {
                reviewsForRestaurant[n] = allReviews[i];
                n++;
            }
        }

        // Makes it so that there are no null elements in 'reviewsForRestaurant'
        Review[] temp = reviewsForRestaurant;
        reviewsForRestaurant = new Review[n];
        for (int i = 0; i < reviewsForRestaurant.length; i++) {
            reviewsForRestaurant[i] = temp[i];
        }

        String[] comparators = {"getDateReviewed", "getID"};
        boolean[] ascending = {false,true};
        return (Review[]) MergeSort.sort(reviewsForRestaurant, comparators, ascending);
    }

    public float getAverageCustomerReviewRating(Long id) {
        if (id == null) return 0.0f;

        Review[] reviewsByCustomer = getReviewsByCustomerID(id);
        float sumOfRatings = 0;
        for (int i = 0; i < reviewsByCustomer.length; i++) {
            sumOfRatings += reviewsByCustomer[i].getRating();
        }

        float average = sumOfRatings / reviewsByCustomer.length;
        // Rounds average to 1dp
        average = Math.round(average * 10);
        average = average/10;

        return average;
    }

    public float getAverageRestaurantReviewRating(Long id) {
        if (id == null) return 0.0f;

        Review[] reviewsForRestaurant = getReviewsByRestaurantID(id);
        float sumOfRatings = 0;
        for (int i = 0; i < reviewsForRestaurant.length; i++) {
            sumOfRatings += reviewsForRestaurant[i].getRating();
        }

        float average = sumOfRatings / reviewsForRestaurant.length;
        // Rounds average to 1dp
        average = Math.round(average * 10);
        average = average/10;

        return average;
    }

    public int[] getCustomerReviewHistogramCount(Long id) {
        if (id == null) return null;

        int[] histogram = new int[5];
        Review[] reviewsByCustomer = getReviewsByCustomerID(id);
        for (int i = 0; i < reviewsByCustomer.length; i++) {
            histogram[reviewsByCustomer[i].getRating()-1]++;
        }
        return histogram;
    }

    public int[] getRestaurantReviewHistogramCount(Long id) {
        if (id == null) return null;

        int[] histogram = new int[5];
        Review[] reviewsForRestaurant = getReviewsByRestaurantID(id);
        for (int i = 0; i < reviewsForRestaurant.length; i++) {
            histogram[reviewsForRestaurant[i].getRating()-1]++;
        }
        return histogram;
    }

    public Long[] getTopCustomersByReviewCount() {
        Review[] allReviews = reviewHashMap.toArray(Review.class);
        // Sort the reviews initially so it's easier to tally up the number of reviews by each customer and find each customers latest review date later on
        String[] comparators = {"getCustomerID","getDateReviewed"};
        boolean[] ascending = {true,false};
        Review[] sortedByID = (Review[]) MergeSort.sort(allReviews,comparators,ascending);

        // Makes a new object of type ReviewCounter, which contains the fields that are used to sort the customer IDs, to allow us to sort it using the 'MergeSort.sort' method
        // Initially sorting the array of reviews pays off here, as whenever we encounter a new customer ID, we can assume that we are done tallying up the previous customer's reviews, and that the first review in the array for each ID is that ID's newest review
        IdCounter[] idCounter = new IdCounter[sortedByID.length];
        int n = 0;
        idCounter[n] = new IdCounter(sortedByID[n].getCustomerID(),sortedByID[n].getDateReviewed());
        for (int i = 0; i < sortedByID.length; i++) {
            if (idCounter[n].getID().equals(sortedByID[i].getCustomerID()) == false) {
                n++;
                idCounter[n] = new IdCounter(sortedByID[i].getCustomerID(),sortedByID[i].getDateReviewed());
            }
            idCounter[n].incrementTally();
        }

        // Gets rid of all null elements in 'reviewCounter' to avoid errors when sorting it
        IdCounter[] temp = idCounter;
        idCounter = new IdCounter[n];
        for (int i = 0; i < idCounter.length; i++) {
            idCounter[i] = temp[i];
        }

        comparators = new String[] {"getTally","getFinalReviewDate","getID"};
        ascending = new boolean[] {false,true,true};
        IdCounter[] sortedByReviewCount = (IdCounter[]) MergeSort.sort(idCounter,comparators,ascending);

        Long[] topCustomers = new Long[20];
        for (int i = 0; i < sortedByReviewCount.length; i++) {
            topCustomers[i] = sortedByReviewCount[i].getID();
            if (i >=19) return topCustomers;
        }
        return topCustomers;
    }

    // Essentially the same as the above method, so I won't go into detail on how it works to avoid repetition
    public Long[] getTopRestaurantsByReviewCount() {
        Review[] allReviews = reviewHashMap.toArray(Review.class);
        String[] comparators = {"getRestaurantID","getDateReviewed"};
        boolean[] ascending = {true,false};
        Review[] sortedByID = (Review[]) MergeSort.sort(allReviews,comparators,ascending);

        IdCounter[] idCounter = new IdCounter[sortedByID.length];
        int n = 0;
        idCounter[n] = new IdCounter(sortedByID[n].getRestaurantID(),sortedByID[n].getDateReviewed());
        for (int i = 0; i < sortedByID.length; i++) {
            if (idCounter[n].getID().equals(sortedByID[i].getRestaurantID()) == false) {
                n++;
                idCounter[n] = new IdCounter(sortedByID[i].getRestaurantID(),sortedByID[i].getDateReviewed());
            }
            idCounter[n].incrementTally();
        }

        IdCounter[] temp = idCounter;
        idCounter = new IdCounter[n];
        for (int i = 0; i < idCounter.length; i++) {
            idCounter[i] = temp[i];
        }

        comparators = new String[] {"getTally","getFinalReviewDate","getID"};
        ascending = new boolean[] {false,true,true};
        IdCounter[] sortedByReviewCount = (IdCounter[]) MergeSort.sort(idCounter,comparators,ascending);

        Long[] topRestaurants = new Long[20];
        for (int i = 0; i < sortedByReviewCount.length; i++) {
            topRestaurants[i] = sortedByReviewCount[i].getID();
            if (i >=19) return topRestaurants;
        }
        return topRestaurants;
    }

    public Long[] getTopRatedRestaurants() {
        Review[] allReviews = reviewHashMap.toArray(Review.class);
        // Similarly to the ReviewCount methods, sort the array initially to make it easier to get the sum of all reviews later on
        String[] comparators = {"getRestaurantID","getDateReviewed"};
        boolean[] ascending = {true,false};
        Review[] sortedReviews = (Review[]) MergeSort.sort(allReviews,comparators,ascending);

        // In the same way how the ReviewCount methods worked, whenever we encounter a review with a new restaurant ID in the array of reviews, we can assume that this review is the newest for the ID and we are done summing the previous ID's ratings
        // Also similarly to how we used a new object to help us count the number of reviews, we do the same thing here except it's a different object which helps us calculate and sort by the average
        AverageReviewCounter[] averageReviewCounter = new AverageReviewCounter[sortedReviews.length];
        int n = 0;
        averageReviewCounter[n] = new AverageReviewCounter(sortedReviews[n].getRestaurantID(),sortedReviews[n].getDateReviewed());
        for (int i = 0; i < sortedReviews.length; i++) {
            if (averageReviewCounter[n].getID().equals(sortedReviews[i].getRestaurantID()) == false) {
                n++;
                averageReviewCounter[n] = new AverageReviewCounter(sortedReviews[i].getRestaurantID(),sortedReviews[i].getDateReviewed());
            }
            averageReviewCounter[n].incrementNumOfRatings();
            averageReviewCounter[n].addToSumOfRating(sortedReviews[i].getRating());
        }

        // Removes all null elements from the array 'averageReviewCounter' to avoid errors when sorting it
        AverageReviewCounter[] temp = averageReviewCounter;
        averageReviewCounter = new AverageReviewCounter[n];
        for (int i = 0; i < averageReviewCounter.length; i++) {
            averageReviewCounter[i] = temp[i];
        }

        comparators = new String[] {"getAverageRating","getFinalReviewDate","getID"};
        ascending = new boolean[] {false,true,true};
        AverageReviewCounter[] sortedByAverageRating = (AverageReviewCounter[]) MergeSort.sort(averageReviewCounter,comparators,ascending);

        Long[] topRestaurants = new Long[20];
        for (int i = 0; i < sortedByAverageRating.length; i++) {
            topRestaurants[i] = sortedByAverageRating[i].getID();
            if (i >= 19) return topRestaurants;
        }
        return topRestaurants;
    }

    public String[] getTopKeywordsForRestaurant(Long id) {
        keywordChecker = new KeywordChecker();

        // Concatenates all words in all reviews for the restaurant into a single string
        Review[] restaurantReviews = getReviewsByRestaurantID(id);
        String allWordsInReviews = "";
        for (int i = 0; i < restaurantReviews.length; i++) {
            allWordsInReviews += restaurantReviews[i].getReview() + " ";
        }

        // Makes an array of strings of every word used in every review for the restaurant
        // Splits 'allWordsInReviews' into many strings of words by splitting the string whenever a character that isn't a letter, the number 0 or a '-' is encountered
        // 0 and '-' are exceptions because they are included in some keywords
        String[] arrayOfAllWords = allWordsInReviews.split("\\W&&[^-0]");

        // Makes every string in the array of strings that isn't a keyword null
        int n = 0;
        for (int i = 0; i < arrayOfAllWords.length; i++) {
            if (keywordChecker.isAKeyword(arrayOfAllWords[i]) == false) {
                arrayOfAllWords[i] = null;
                n++;
            }
        }

        // A new array 'allKeywords' is made, which is identical to 'arrayOfAllWords', except all null elements are removed
        // The array 'allKeywords' now is array of every instance of a keyword being used in reviews for restaurants
        String[] allKeywords = new String[arrayOfAllWords.length-n];
        for (int i = 0; i < arrayOfAllWords.length; i++) {
            if (arrayOfAllWords[i] != null) {
                allKeywords[i] = allKeywords[i];
            }
        }

        // Sorts the strings in the array 'allKeywords' alphabetically so they are easier to count
        String[] comparators = {""};
        boolean[] ascending = {true};
        String[] sortedKeyWords = (String[]) MergeSort.sort(allKeywords,comparators,ascending);

        // Object 'KeywordCounter' makes it easier to count how many times each keyword is used, similarly to how ReviewCounter and AverageReviewCounter were used in the above methods
        // Sorting the array prior to counting allows us to assume every time a new keyword is encountered, we are done tallying the number of uses of the previous keyword
        KeywordCounter[] keywordCounter = new KeywordCounter[sortedKeyWords.length];
        n = 0;
        keywordCounter[n] = new KeywordCounter(sortedKeyWords[n]);
        for (int i = 0; i < sortedKeyWords.length; i++) {
            if (keywordCounter[n].getKeyword().equals(sortedKeyWords[i]) == false) {
                n++;
                keywordCounter[n] = new KeywordCounter(sortedKeyWords[i]);
            }
                keywordCounter[n].incrementTally();
        }

        // Gets rid all null elements from 'keywordCounter' to avoid errors when sorting it
        KeywordCounter[] temp = keywordCounter;
        keywordCounter = new KeywordCounter[n];
        for (int i = 0; i < keywordCounter.length; i++) {
            keywordCounter[i] = temp[i];
        }

        comparators = new String[] {"getTally","getKeyword"};
        ascending = new boolean[] {false,true};
        KeywordCounter[] keywordsSortedByFrequency = (KeywordCounter[]) MergeSort.sort(keywordCounter,comparators,ascending);

        String[] topKeywords = new String[5];
        for (int i = 0; i < keywordsSortedByFrequency.length; i++) {
            topKeywords[i] = keywordsSortedByFrequency[i].getKeyword();
            if (i >= 4) return topKeywords;
        }
        return topKeywords;
    }

    public Review[] getReviewsContaining(String searchTerm) {
        if (searchTerm.equals("")) return new Review[0];

        String convertedSearchTerm = StringFormatter.convertAccentsFaster(searchTerm).toLowerCase();

        // Removes trailing and leading spaces in convertedSearchTerm, and replaces duplicate successive spaces with single spaces
        String formattedSearchTerm = convertedSearchTerm.replace("  "," ").trim();

        Review[] allReviews = reviewHashMap.toArray(Review.class);
        Review[] searchResults = new Review[allReviews.length];
        int n = 0;
        for (int i = 0; i < allReviews.length; i++) {
            String convertedReview = StringFormatter.convertAccentsFaster(allReviews[i].getReview()).toLowerCase();
            // In each 'convertedReview', trailing and leading spaces are removed and replaces duplicate successive spaces with single spaces
            String formattedReview = convertedReview.replace("  "," ").trim();
            if (formattedReview.contains(formattedSearchTerm)) {
                searchResults[n] = allReviews[i];
                n++;
            }
        }

        // Removes all null elements from 'searchResults' so there are no errors when sorting the array
        Review[] temp = searchResults;
        searchResults = new Review[n];
        for (int i = 0; i < searchResults.length; i++) {
            searchResults[i] = temp[i];
        }

        String[] comparators = {"getDateReviewed","getID"};
        boolean[] ascending = {false,true};
        Review[] sortedSearchResults = (Review[]) MergeSort.sort(searchResults,comparators,ascending);
        return sortedSearchResults;
    }
}
