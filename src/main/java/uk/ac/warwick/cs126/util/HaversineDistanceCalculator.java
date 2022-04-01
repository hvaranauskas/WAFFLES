package uk.ac.warwick.cs126.util;

public class HaversineDistanceCalculator {

    private final static float R = 6372.8f;
    private final static float kilometresInAMile = 1.609344f;

    public static float inKilometres(float lat1, float lon1, float lat2, float lon2) {
        lat1 = (float) Math.toRadians(lat1);
        lat2 = (float) Math.toRadians(lat2);
        lon1 = (float) Math.toRadians(lon1);
        lon2 = (float) Math.toRadians(lon2);

        float a = (float) (Math.pow(Math.sin((lat2-lat1)/2),2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin((lon2-lon1)/2),2));
        float c = (float) (2 * Math.asin(Math.sqrt(a)));
        float d = R * c;
        // Rounds d to 1dp
        d = Math.round(d * 10);
        d = d / 10;
        return d;
    }

    public static float inMiles(float lat1, float lon1, float lat2, float lon2) {
        lat1 = (float) Math.toRadians(lat1);
        lat2 = (float) Math.toRadians(lat2);
        lon1 = (float) Math.toRadians(lon1);
        lon2 = (float) Math.toRadians(lon2);

        float a = (float) (Math.pow(Math.sin((lat2-lat1)/2),2) + Math.cos(lat1) * Math.cos(lat2) * Math.pow(Math.sin((lon2-lon1)/2),2));
        float c = (float) (2 * Math.asin(Math.sqrt(a)));
        float d = R * c;
        float dInMiles = d/kilometresInAMile;
        // Rounds dInMiles to 1dp
        dInMiles = Math.round(dInMiles * 10);
        dInMiles = dInMiles/10;
        return dInMiles;
    }

}