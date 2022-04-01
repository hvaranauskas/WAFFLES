package uk.ac.warwick.cs126.structures;

import java.util.Date;

public class IdCounter {

    private Long ID;
    private int tally = 0;
    private Date finalReviewDate;

    public IdCounter(Long ID, Date finalReviewDate) {
        this.ID = ID;
        this.finalReviewDate = finalReviewDate;
    }


    public void incrementTally() { this.tally++; }

    public void setTally(int tally) { this.tally = tally; }

    public int getTally() { return tally; }

    public Long getID() { return ID; }

    public void setID(long ID) { this.ID = ID; }

    public Date getFinalReviewDate() { return finalReviewDate; }

    public void setFinalReviewDate(Date date) { finalReviewDate = date; }
}
