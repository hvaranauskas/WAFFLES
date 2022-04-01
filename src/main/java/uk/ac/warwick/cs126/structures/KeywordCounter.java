package uk.ac.warwick.cs126.structures;

public class KeywordCounter {

    int tally;
    String keyword;

    public KeywordCounter(String keyword) {
        this.keyword = keyword;
    }

    public void incrementTally() { tally++; }

    public int getTally() { return tally; }

    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getKeyword() { return keyword; }

}
