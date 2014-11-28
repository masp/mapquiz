package com.asp.mapquiz;

import android.graphics.Region;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class State {
    private static final String TAG = "State";
    private LatLngBounds mBounds;
    private String mName;
    private List<LatLng> mPoints;

    private State(final String name, final List<LatLng> points) {
        mName = name;
        mPoints = new ArrayList<LatLng>(points);
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (LatLng point : points) {
            builder.include(point);
        }
        mBounds = builder.build();

    }

    public String getName() {
        return mName;
    }

    public List<LatLng> getPoints() {
        return Collections.unmodifiableList(mPoints);
    }
    public LatLng getRandomPoint(){

        double latMin=90;
        double lngMin=180;
        double latMax=-90;
        double lngMax=-180;
     // Find max and min Lat and Lng
        for (int i = 0; i < mPoints.size(); i++) {
            LatLng point =  mPoints.get(i);
            if(point.latitude>latMax)latMax=point.latitude;
            if(point.latitude<latMin)latMin=point.latitude;
            if(point.longitude>lngMax)lngMax=point.longitude;
            if(point.longitude<lngMin)lngMin=point.longitude;
        }
        Log.d(TAG, "LatMin: " + latMin + " LatMax: " + latMax +" LatMin: " + lngMin + " LngMax: " + lngMax);
     // Pick a random point within these ranges
        latMax=mBounds.northeast.latitude;
        latMin=mBounds.southwest.latitude;
        lngMax=mBounds.northeast.longitude;
        lngMin=mBounds.southwest.longitude;
        double lat=randomDouble(latMin, latMax);
        double lng=randomDouble(lngMin, lngMax);
        LatLng randomPoint=new LatLng(lat,lng);
        Log.d(TAG, "LatMin: " + latMin + " LatMax: " + latMax +" LatMin: " + lngMin + " LngMax: " + lngMax);
        Log.d(TAG, "Chosen Lat/Lng: " + randomPoint.latitude + "/" + randomPoint.longitude);
     // Check that point is within polygon of points
        if(!withinPolygon(randomPoint)){
            Log.d(TAG, "Outside of Polygon");
            return getRandomPoint();
        }
        return randomPoint;
    }
    private double randomDouble(double min, double max){
        double value=0;
        if(max>min) value=min + (Math.random() * ((max - min)));
        return value;
    }

    public LatLngBounds getBounds(){
        return mBounds;
    }

    public boolean withinPolygon(LatLng point){

        //method to check if a Coordinate is located in the polygon

            //this method uses the ray tracing algorithm to determine if the point is in the polygon
            int nPoints=mPoints.size();
            int j=-999;
            int i=-999;
            boolean locatedInPolygon=false;
            for(i=0;i<(nPoints);i++){
                //repeat loop for all sets of points
                if(i==(nPoints-1)){
                    //if i is the last vertex, let j be the first vertex
                    j= 0;
                }else{
                    //for all-else, let j=(i+1)th vertex
                    j=i+1;
                }

                double vertY_i= mPoints.get(i).longitude;
                double vertX_i= mPoints.get(i).latitude;
                double vertY_j= mPoints.get(j).longitude;
                double vertX_j= mPoints.get(j).latitude;
                double testX  = point.latitude;
                double testY  = point.longitude;

                // following statement checks if testPoint.Y is below Y-coord of i-th vertex
                boolean belowLowY=vertY_i>testY;
                // following statement checks if testPoint.Y is below Y-coord of i+1-th vertex
                boolean belowHighY=vertY_j>testY;

        /* following statement is true if testPoint.Y satisfies either (only one is possible)
        -->(i).Y < testPoint.Y < (i+1).Y        OR
        -->(i).Y > testPoint.Y > (i+1).Y

        (Note)
        Both of the conditions indicate that a point is located within the edges of the Y-th coordinate
        of the (i)-th and the (i+1)- th vertices of the polygon. If neither of the above
        conditions is satisfied, then it is assured that a semi-infinite horizontal line draw
        to the right from the testpoint will NOT cross the line that connects vertices i and i+1
        of the polygon
        */
                boolean withinYsEdges= belowLowY != belowHighY;

                if( withinYsEdges){
                    // this is the slope of the line that connects vertices i and i+1 of the polygon
                    double slopeOfLine   = ( vertX_j-vertX_i )/ (vertY_j-vertY_i) ;

                    // this looks up the x-coord of a point lying on the above line, given its y-coord
                    double pointOnLine   = ( slopeOfLine* (testY - vertY_i) )+vertX_i;

                    //checks to see if x-coord of testPoint is smaller than the point on the line with the same y-coord
                    boolean isLeftToLine= testX < pointOnLine;

                    if(isLeftToLine){
                        //this statement changes true to false (and vice-versa)
                        locatedInPolygon= !locatedInPolygon;
                    }//end if (isLeftToLine)
                }//end if (withinYsEdges
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

        public State build() {
            built = true;
            return new State(name, points);
        }
    }
}
