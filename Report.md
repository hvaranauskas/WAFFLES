# CS126 WAFFLES Coursework Report [2026135]

## CustomerStore
### Overview
* I used a hashmap to store customers because it was constant time to add and retrieve customers from it, and customers would likely be added often
* I used Merge Sort to sort customers by whatever the methods wanted me to sort them by because it is efficient
* I used an array list to store the blacklisted IDs because it uses very little memory

### Space Complexity
Store         | Worst Case | Description
------------- | ---------- | -----------
CustomerStore | O(n)       | I used a `Hash Map` to store customers. <br>Where `n` is total customers in the store

### Time Complexity
Method                           | Average Case     | Description
-------------------------------- | ---------------- | -----------
addCustomer(Customer c)          | O(1)             | Hashmap add is constant time
addCustomer(Customer[] c)        | O(n)             | Calls first `addCustomer` n times <br>`n` is the length of the input array
getCustomer(Long id)             | O(1)             | Hashmap retrieval is constant time <br>Customers are stored in hashmap using their ID as the key
getCustomers()                   | O(n*log(n))      | MergeSort <br>`n` is the number of customers in the store
getCustomers(Customer[] c)       | O(n*log(n))      | MergeSort <br>`n` is the length of the input array
getCustomersByName()             | O(n*log(n))      | MergeSort <br>`n` is total customers in the store
getCustomersByName(Customer[] c) | O(n*log(n))      | MergeSort <br>`n` is length of the input array
getCustomersContaining(String s) | O(n*log(n)+s*m)  | MergeSort and convertAccentsFaster is called for each customer in the store <br>`n` is the number of customers which contain the search term, `s` is the length of the search term and `m` is  the number of customers in the store

<div style="page-break-after: always;"></div>

## FavouriteStore
### Overview
* I have used an array list to store favourites because it uses very little memory and is easy to add customers to
* Although removing elements from an array list is costly, I thought that elements would not be removed from favourite store as often as the others since the oldest favourite is kept, and therefore the extra memory would be worth it
* I used Merge Sort to sort the favourites in the methods because it is efficent
* I used an `idCounter` object in the methods with the form `getTop...ByFavouriteCount` because it made sorting the favourites using my Merge Sort much easier
* I used an array list to store the blacklisted IDs because it uses very little memory
* I used an array list to store the replaced favourites because it uses very little memory

### Space Complexity
Store          | Worst Case | Description
-------------- | ---------- | -----------
FavouriteStore | O(n)       | I have used an `array list` to store favourites <br>Where `n` is the number of favourites in the store

### Time Complexity
Method                                                          | Average Case     | Description
--------------------------------------------------------------- | ---------------- | -----------
addFavourite(Favourite f)                                       | O(n)             | For loops <br>`n` is the number of favourites in the store
addFavourite(Favourite[] f)                                     | O(m*n)           | First `addFavourite` is called m times <br>`m` is the length of the given array
getFavourite(Long id)                                           | O(n)             | Loops through all favourites in the store <br>`n` is the number of favourites in the store
getFavourites()                                                 | O(n*log(n))      | MergeSort <br>`n` is the number of favourites in the store
getFavourites(Favourite[] f)                                    | -                | -
getFavouritesByCustomerID(Long id)                              | O(n+m*log(m))    | MergeSort and for loop <br>`n` is the number of favourites in the store, `m` is the number of favourites which have the same customer ID as the given ID
getFavouritesByRestaurantID(Long id)                            | O(n+m*log(m))    | MergeSort and for loop <br>`n` is the number of favourites in the store, `m` is the number of favourites which have the same restaurant ID as the given ID
getCommonFavouriteRestaurants(<br>&emsp; Long id1, Long id2)    | O(n*m)           | Nested for loop <br>`n` is the number of restaurants which customer1 has favourited, `m` is the number of restaurants which customer2 has favourited
getMissingFavouriteRestaurants(<br>&emsp; Long id1, Long id2)   | O(n*m)           | Nested for loop <br>`n` is the number of restaurants which customer1 has favourited, `m` is the number of restaurants which customer2 has favourited
getNotCommonFavouriteRestaurants(<br>&emsp; Long id1, Long id2) | O(n*m)           | Nested for loop <br>`n` is the number of restaurants which customer1 has favourited, `m` is the number of restaurants which customer2 has favourited
getTopCustomersByFavouriteCount()                               | O(n*log(n)+m*log(m))| MergeSort is called twice <br>`n` is the number of favourites in the store, `m` is the number of unique customer IDs in the store
getTopRestaurantsByFavouriteCount()                             | O(n*log(n)+m*log(m))| MergeSort is called twice <br>`n` is the number of favourites in the store, `m` is the number of unique restaurants IDs in the store 

<div style="page-break-after: always;"></div>

## RestaurantStore
### Overview
* I have used a hashmap to store restaurants because adding and retrieving from a hashmap is constant time
* I used Merge Sort to sort the restaurants in the methods because it is efficient
* I used an array list to store the blacklisted IDs because it uses very little memory

### Space Complexity
Store           | Worst Case | Description
--------------- | ---------- | -----------
RestaurantStore | O(n)       | I have used a `hash map` to store restaurants <br>Where `n` is the number of restaurants in the store

### Time Complexity
Method                                                                        | Average Case     | Description
----------------------------------------------------------------------------- | ---------------- | -----------
addRestaurant(Restaurant r)                                                   | O(1)             | Adding elements to a hashmap is constant time
addRestaurant(Restaurant[] r)                                                 | O(n)             | Feeds all elements from array into the first `addRestaurant` method <br>Length of array is `n`
getRestaurant(Long id)                                                        | O(1)             | Hashmap retrieval is constant time <br>Restaurants are stored in hasmap using their ID as the key
getRestaurants()                                                              | O(n*log(n))      | MergeSort <br>`n` is the number of restaurants in the store
getRestaurants(Restaurant[] r)                                                | O(n*log(n))      | MergeSort <br>`n` is the length of the array
getRestaurantsByName()                                                        | O(n*log(n))      | MergeSort <br>`n` is the number of restaurants in the store
getRestaurantsByDateEstablished()                                             | O(n*log(n))      | MergeSort <br>`n` is the number of restaurants in the store
getRestaurantsByDateEstablished(<br>&emsp; Restaurant[] r)                    | O(n*log(n))      | MergeSort <br>`n` is the number of restaurants in the store
getRestaurantsByWarwickStars()                                                | O(n*log(n))      | MergeSort <br>`n` is the length of the array
getRestaurantsByRating(Restaurant[] r)                                        | O(n*log(n))      | MergeSort <br>`n` is the number of restaurants in the store
getRestaurantsByDistanceFrom(<br>&emsp; float lat, float lon)                 | O(n*log(n))      | MergeSort <br>`n` is the number of restaurants in the store
getRestaurantsByDistanceFrom(<br>&emsp; Restaurant[] r, float lat, float lon) | O(n*log(n))      | MergeSort <br>`n` is the length of the array
getRestaurantsContaining(String s)                                            | O(n*log(n)+s*n)  | MergeSort and convertAccentsFaster in for loop <br>`n` is the number of restaurants in the store and `s` is the length of the string s

<div style="page-break-after: always;"></div>

## ReviewStore
### Overview
* I have used a hash map to store reviews because adding and retrieving elements from it is constant time
* I used Merge Sort to sort the reviews because it is efficient
* I used `idCounter` objects in the methods of the form 'getTop...ByReviewCount' because it made sorting the customers and restaurants by review count using my Merge Sort much easier
* Similarly to the `getTop...ReviewCount` methods, I used an `AverageReviewCounter` object in `getTopRatedRestaurants` because it made sorting the restaurants by average rating using my Merge Sort much easier
* Similarly again, I used a `KeywordCounter` object in the `getTopKeywordsForRestaurant` method because it made tallying the keywords and then sorting the keywords by their frequency using my Merge Sort much easier

### Space Complexity
Store           | Worst Case | Description
--------------- | ---------- | -----------
ReviewStore     | O(n)       | I have used a `hash map` to store reviews <br>Where `n` is the number of reviews in the store

### Time Complexity
Method                                     | Average Case     | Description
------------------------------------------ | ---------------- | -----------
addReview(Review r)                        | O(n+o)           | For loops, adding to hashmap is constant time <br>`n` is the number of reviews in the store and `o` is the number of elements in 'olderReviews'
addReview(Review[] r)                      | O(a*n + a*o)     | Feeds all elements from array into the first `addReview` method <br>Length of array is `a`, number of reviews in store is `n` and length of 'olderReviews' is `o`
getReview(Long id)                         | O(1)             | Hashmap retrieval is constant time <br>Reviews are stored in the hashmap using their ID as the key
getReviews()                               | O(n*log(n))      | MergeSort <br>`n` is the number of reviews in the store
getReviews(Review[] r)                     | O(n*log(n))      | MergeSort <br>`n` is the length of the given array
getReviewsByDate()                         | O(n*log(n))      | MergeSort <br>`n` is the number of reviews in the store
getReviewsByRating()                       | O(n*log(n))      | MergeSort <br>`n` is the number of reviews in the store
getReviewsByRestaurantID(Long id)          | O(n + m*log(m))  | For loop and MergeSort <br>`n` is the number of reviews in the store and `m` is the number of reviews with the given ID
getReviewsByCustomerID(Long id)            | O(n + m*log(m))  | For loop and MergeSort <br>`n` is the number of reviews in the store and `m` is the number of reviews with the given ID
getAverageCustomerReviewRating(Long id)    | O(n)             | For loop <br>`n` is the number of reviews in the store
getAverageRestaurantReviewRating(Long id)  | O(n)             | For loop <br>`n` is the number of reviews in the store
getCustomerReviewHistogramCount(Long id)   | O(n+m)           | For loop and call to `getReviewsByCustomerID` <br>`n` is the number of reviews in the store, `m` is the length of the array returned by `getReviewsByCustomerID`
getRestaurantReviewHistogramCount(Long id) | O(n+m)           | For loop and call to `getReviewsByRestaurantID` <br>`n` is the number of reviews in the store, `m` is the length of the array returned by `getReviewsByRestaurantID`
getTopCustomersByReviewCount()             | O(n*log(n)+m*log(m))| MergeSort is called twice <br>`n` is the number of reviews in the store, `m` is the number of unique customer IDs in the store
getTopRestaurantsByReviewCount()           | O(n*log(n)+m*log(m))| MergeSort is called twice <br>`n` is the number of reviews in the store, `m` is the number of unique restaurant IDs in the store
getTopRatedRestaurants()                   | O(n*log(n)+m*log(m))| MergeSort is called twice <br>`n` is the number of reviews in the store, `m` is the number of unique restaurant IDs in the store 
getTopKeywordsForRestaurant(Long id)       | O(n+m+k*log(k)+a*log(a))| For loops and MergeSort <br>`n` is the number of reviews in the store, `m` is the total number of words in every review combined,`k` is the number of times a keyword was used in all reviews and `a` is the number of keywords used in reviews
getReviewsContaining(String s)             | O(n*s + m*log(m))| MergeSort and `convertAccentsFaster` is called for every review in the store <br>`n` is the number of reviews in the store and `m` is the number of reviews which contain the search term

<div style="page-break-after: always;"></div>

## Util
### Overview
* **ConvertToPlace** 
    * I sorted `places` array in the class constructor by their latitude and longitude using Merge Sort so I could use Binary Bearch on the array
    * Although linearly seaching through the array may be more efficient for the first few uses of the class, if we use the class a lot, eventually sorting it in the beginning and then using binary search onwards will end up being more efficient
* **DataChecker**
    * Assesses the validity the given objects according to the criteria given in the guide.
* **HaversineDistanceCalculator (HaversineDC)**
    * Uses the equations given in the guide to work out the distance between two distances in the given unit
* **KeywordChecker**
    * I used Binary Search to search through the array of keywords because it is more efficient than linearly searching through it
    * I didn't need to sort the array of keywords beforehand because it is already sorted
* **StringFormatter**
    * Stored all the accents in an array so I could use Binary Search on it because Binary Search is more efficient than linear search
    * In the same way as ConvertToPlace, my implementation may initially be less efficient, but it will eventually be more efficient after a number of uses

### Space Complexity
Util               | Worst Case | Description
-------------------| ---------- | -----------
ConvertToPlace     | O(1)       | Array used to store places is constant and merge sort is done on a constant number of places
DataChecker        | O(1)       | All data structures used in this class have a constant size
HaversineDC        | O(1)       | I used no data structure in this class
KeywordChecker     | O(1)       | Length of array used to store the keywords is constant
StringFormatter    | O(n)       | Binary Search is performed `n` times. <br>`n` is the number of accents in the word

### Time Complexity
Util              | Method                                                                             | Average Case     | Description
----------------- | ---------------------------------------------------------------------------------- | ---------------- | -----------
ConvertToPlace    | convert(float lat, float lon)                                                      | O(1)             | BinarySearch through an array of constant length
DataChecker       | extractTrueID(String[] repeatedID)                                                 | O(1)             | Nested for loop <br>Loops a constant number of times
DataChecker       | isValid(Long id)                                                                   | O(1)             | Linear search through constant number of digits in ID <br>Length of ID must be 16 to do linear search
DataChecker       | isValid(Customer customer)                                                         | O(1)             | Number of methods in every customer is constant <br>Checking if ID is valid is constant for each customer
DataChecker       | isValid(Favourite favourite)                                                       | O(1)             | Number of methods in every favourite is constant <br>Checking if ID is valid is constant for each favourite
DataChecker       | isValid(Restaurant restaurant)                                                     | O(1)             | Number of methods in every restaurant is constant <br>Checking if ID is valid is constant for each restaurant
DataChecker       | isValid(Review review)                                                             | O(1)             | Number of methods in every review is constant <br>Checking if ID is valid is constant for each review
HaversineDC       | inKilometres(<br>&emsp; float lat1, float lon1, <br>&emsp; float lat2, float lon2) | O(1)             | Constant number of calculations done every time
HaversineDC       | inMiles(<br>&emsp; float lat1, float lon1, <br>&emsp; float lat2, float lon2)      | O(1)             | Constant number of calculations done every time
KeywordChecker    | isAKeyword(String s)                                                               | O(1)             | BinarySearch through an array of constant length
StringFormatter   | convertAccentsFaster(String s)                                                     | O(n)             | Linear search through string of length n and Binary search through an array of constant length
