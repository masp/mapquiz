package com.asp.mapquiz;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.Xml;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LocationSelectActivity extends FragmentActivity {
    private static final String TAG = "LocationSelectActivity";

    private SupportMapFragment mMaps;
    private List<State> mStates;
    Button mButton1;
    Button mButton2;
    Button mButton3;
    Button mButton4;
    Button mButton5;
    TextView mText1;
    List<State> randStates;
    int RandomState;
    State selectState;
    LatLngBounds mBounds;
    AlertDialog.Builder builder1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_select);
        mButton1=(Button)findViewById(R.id.button1);
        mButton2=(Button)findViewById(R.id.button2);
        mButton3=(Button)findViewById(R.id.button3);
        mButton4=(Button)findViewById(R.id.button4);
        mButton5=(Button)findViewById(R.id.button5);
        mText1=(TextView)findViewById(R.id.result);

        mMaps = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.location_select_map_fragment);



        XmlPullParser xmlParser = getResources().getXml(R.xml.states);
        try {
            State.StateBuilder currState = null;
            int eventType = xmlParser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag = xmlParser.getName();
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        mStates = new ArrayList<State>();
                        break;
                    case XmlPullParser.START_TAG:
                        if ("state".equals(tag)) {
                            currState = new State.StateBuilder(xmlParser.getAttributeValue(0));
                        } else if (currState != null && "point".equals(tag)) {
                            double lat = Double.parseDouble(xmlParser.getAttributeValue(0));
                            double lng = Double.parseDouble(xmlParser.getAttributeValue(1));
                            currState.addPoint(new LatLng(lat, lng));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if (currState != null && "state".equals(tag)) {
                            mStates.add(currState.build());
                        }
                        break;
                }
                eventType = xmlParser.next();
            }
        } catch (XmlPullParserException ex) {
            ex.printStackTrace();
        } catch (IOException io) {
            io.printStackTrace();
        }

        makeNewMap();


    }
    private void dialogAnswer(String message){
        mMaps.getMap().animateCamera(CameraUpdateFactory.newLatLngBounds(mBounds,50));
  //      mMaps.getMap().addTileOverlay(new TileOverlayOptions().)

        mButton1.setVisibility(View.GONE);
        mButton2.setVisibility(View.GONE);
        mButton3.setVisibility(View.GONE);
        mButton4.setVisibility(View.GONE);
        mButton5.setVisibility(View.VISIBLE);
        mText1.setText(message);
        mText1.setVisibility(View.VISIBLE);

    }

    private void makeNewMap(){
        final Random random = new Random();
        randStates = getRandomStates(random);
        RandomState=random.nextInt(randStates.size());
        selectState = randStates.get(RandomState);
        mButton1.setVisibility(View.VISIBLE);
        mButton2.setVisibility(View.VISIBLE);
        mButton3.setVisibility(View.VISIBLE);
        mButton4.setVisibility(View.VISIBLE);
        mButton5.setVisibility(View.GONE);
        mText1.setVisibility(View.GONE);
//      Populate buttons with names of random states
        mButton1.setText(randStates.get(0).getName());
        mButton2.setText(randStates.get(1).getName());
        mButton3.setText(randStates.get(2).getName());
        mButton4.setText(randStates.get(3).getName());

//      Select state and choose location within state to display
        float zoomLevel=9;
        LatLng loc=selectState.getRandomPoint();
        final Iterable<LatLng> outline = selectState.getPoints();
        mBounds=selectState.getBounds();

        mMaps.getMap().clear();
        mMaps.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(loc,zoomLevel));

        mMaps.getMap().addMarker(new MarkerOptions()
                .title("Where is This?")
                .snippet("In what state is this marker positioned?")
                .position(loc));

        mMaps.getMap().addPolygon(new PolygonOptions()
                .strokeColor(Color.RED)
                .strokeWidth(5)
                .addAll(outline));

        startTime=


        mButton1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(RandomState==0){
                    dialogAnswer("Correct!");
                    long duration=endTime-startTime;
                    Score score=new Score(1,true,selectState,randStates,duration );

                }else {
                    dialogAnswer("Wrong!");
                }
            }
        });
        mButton2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(RandomState==1){
                    dialogAnswer("Correct!");
                }else {
                    dialogAnswer("Wrong!");
                }
            }
        });
        mButton3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(RandomState==2){
                    dialogAnswer("Correct!");
                }else {
                    dialogAnswer("Wrong!");
                }
            }
        });
        mButton4.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(RandomState==3){
                    dialogAnswer("Correct!");
                }else {
                    dialogAnswer("Wrong!");
                }
            }
        });
        mButton5.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                makeNewMap();
            }
        });
    }

    public List<State> getRandomStates(Random random) {
        List<State> states = new ArrayList<State>();
        List<Integer> chosenIndices = new ArrayList<Integer>();
        chosenIndices.add(-1);
        for (int i = 0; i < 4; i++) {
            int randIndex = -1;
            while (chosenIndices.contains(randIndex))
                randIndex = random.nextInt(mStates.size());
            chosenIndices.add(randIndex);
            State state = mStates.get(randIndex);
            states.add(state);
            Log.d(TAG, "Chosen State: " + state.getName());
        }
        return states;
    }
}
