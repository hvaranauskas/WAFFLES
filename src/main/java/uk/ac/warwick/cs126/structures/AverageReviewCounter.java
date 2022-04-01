package uk.ac.warwick.cs126.structures;

import java.util.Date;

public class AverageReviewCounter {

    private Long ID;
    private Date finalReviewDate;
    private int sumOfRatings;
    private int numOfRatings;

    public AverageReviewCounter(Long ID, Date finalReviewDate) {
        this.ID = ID;
        this.finalReviewDate = finalReviewDate;
    }

    public void addToSumOfRating(int rating) { sumOfRatings += rating; }

    public int getSumOfRatings() { return sumOfRatings; }

    public void incrementNumOfRatings() { numOfRatings++; }

    public float getAverageRating() { return (sumOfRatings/numOfRatings); }

    public Long getID() { return ID; }

    public void setID(long ID) { this.ID = ID; }

    public Date getFinalReviewDate() { return finalReviewDate; }

    public void setFinalReviewDate(Date date) { finalReviewDate = date; }
}
