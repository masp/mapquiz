package com.asp.placequiz.state;

import com.asp.placequiz.question.Option;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StateOption implements Option {
    private static final String TAG = "StateOption";

    private LatLngBounds mBounds;
    private String mName;
    private List<LatLng> mPoints;

    /**
     * @param name Name of the State
     * @param points Vertices of the border of the State
     */
    private StateOption(final String name, final List<LatLng> points) {
        mName = name;
        mPoints = new ArrayList<LatLng>(points);
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (LatLng point : points) {
            builder.include(point);
        }
        mBounds = builder.build();

    }

    /**
     * @return The name of the State
     */
    public String getName() {
        return mName;
    }

    /**
     * Returns the points representing the bounds of the State
     * @return The vertices of the State
     */
    public List<LatLng> getPoints() {
        return Collections.unmodifiableList(mPoints);
    }

    /**
     * Finds a random location within the bounds of the StateOption
     * @return A latitude/longitude of a location within the bounds of the StateOption
     */
    public LatLng getRandomPoint() {
        LatLng randomPoint = getRandomCoordInBounds(mBounds);
        if(!withinPolygon(randomPoint)){
            // If outside, try again
            return getRandomPoint();
        }
        return randomPoint;
    }

    /**
     * Given bounds returns a random coordinate therewithin
     * @param bounds The bounds to generate within
     * @return The random point generated within the bounds
     */
    private LatLng getRandomCoordInBounds(LatLngBounds bounds) {
        double latMax = bounds.northeast.latitude;
        double latMin = bounds.southwest.latitude;
        double lngMax = bounds.northeast.longitude;
        double lngMin = bounds.southwest.longitude;
        double lat = randomDouble(latMin, latMax);
        double lng = randomDouble(lngMin, lngMax);
        return new LatLng(lat, lng);
    }

    /**
     * Generates a random double within the range
     * @param min Minimum value that the random num can be (inc)
     * @param max Maximum value that the random num can be (inc)
     * @return The randomly generated double value
     */
    private double randomDouble(double min, double max){
        double value = 0;
        if (max > min) value = min + (Math.random() * ((max - min)));
        return value;
    }

    public LatLngBounds getBounds() {
        return mBounds;
    }

    /**
     * Uses raytracing method to test whether point is within polygon
     * @param point LatLng point to test
     * @return True if point is within polygon, false if not
     */
    public boolean withinPolygon(LatLng point) {
        int nPoints = mPoints.size();
        int j;
        int i;
        boolean locatedInPolygon = false;
        // Iterate through every edge of the polygon
        for(i = 0; i < nPoints; i++) {
            if (i == (nPoints - 1)) {
                // If i is the last vertex of the polygon, let the second part of the edge
                //  be the first vertex of the polygon
                j = 0;
            } else {
                // For all-else, let the vertex after be the vertex
                j = i + 1;
            }

            final LatLng firstVertex = mPoints.get(i);
            final LatLng secondVertex = mPoints.get(j);
            boolean belowLowY =  firstVertex.longitude > point.longitude;
            boolean belowHighY = secondVertex.longitude > point.longitude;
            boolean withinYsEdges = belowLowY != belowHighY;
            // if firstVertex.y < point.y < secondVertex.y then a line drawn to the right
            //  will intersect the edge of the polygon
            if (withinYsEdges) {
                // Slope of the edge being checked
                // x = my + ix;
                double invertedSlopeOfEdge = (secondVertex.latitude - firstVertex.latitude) /
                                     (secondVertex.longitude - firstVertex.longitude);
                // If the value of x = my + ix is to the left of the point
                double pointOnLine = invertedSlopeOfEdge * (point.longitude - firstVertex.longitude)
                        + firstVertex.latitude;
                boolean isLeftToLine= point.latitude < pointOnLine;

                if (isLeftToLine) {
                    // By inverting this value, an odd number of edge intersections
                    // will yield true, an even number will yield false
                    locatedInPolygon= !locatedInPolygon;
                }
            }
        }

        return locatedInPolygon;
    }

    public static class StateBuilder {
        private boolean built;
        private String name;
        private List<LatLng> points;

        public StateBuilder(String name) {
            points = new ArrayList<LatLng>();
            this.name = name;
        }

        public void addPoint(LatLng point) {
            if (built) throw new IllegalStateException("State object has already been built!");
            points.add(point);
        }

        public StateOption build() {
            built = true;
            return new StateOption(name, points);
        }
    }
}
