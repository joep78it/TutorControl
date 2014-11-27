package com.projects.tutorcontrol;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import java.util.Date;
import java.util.Locale;


public class TripActivity extends FragmentActivity implements ActionBar.TabListener,
        GooglePlayServicesClient.ConnectionCallbacks,
        GooglePlayServicesClient.OnConnectionFailedListener,
        LocationListener {

    private interface MyLocationListener{
        void onLocationChanged(Location location);
    }

    private MyLocationListener myListener;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;


    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 5;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    // Define an object that holds accuracy and frequency parameters
    private LocationRequest mLocationRequest;
    private boolean mUpdatesRequested;
    private LocationClient mLocationClient;
    private SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                * Thrown if Google Play services canceled the original
                * PendingIntent
                */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            //showErrorDialog(connectionResult.getErrorCode());
            showErrorDialog();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        // If already requested, start periodic updates
        if (mUpdatesRequested) {
            mLocationClient.requestLocationUpdates(mLocationRequest, this);
        }

        //getLocation();

        try {
            Location mCurrentLocation = mLocationClient.getLastLocation();
            /*
            String msg = "Updated Location: " +
                    Double.toString(mCurrentLocation.getLatitude()) + "," +
                    Double.toString(mCurrentLocation.getLongitude()) + "\nSpeed:" + Float.toString(mCurrentLocation.getSpeed());

            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            */
            if(myListener != null)
            {
                myListener.onLocationChanged(mCurrentLocation);
            }
        }
        catch(Exception e1)
        {
            Toast.makeText(this, e1.getMessage(), Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onDisconnected() {

    }

    @Override
    public void onLocationChanged(Location location) {
        /*
        String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude()) + "\nSpeed:" + Float.toString(location.getSpeed());
        */
        if(myListener != null)
        {
            myListener.onLocationChanged(location);
        }

        //Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;

        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }

        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }

        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST:
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK:
                    /*
                     * Try the request again
                     */

                        break;
                }

        }
    }

    //private boolean showErrorDialog(int errorCode) {
    private boolean showErrorDialog() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason.
            // resultCode holds the error code.
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
                errorFragment.show(getSupportFragmentManager(),
                        "Location Updates");
            }
        }
        return false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trip);

        // Set up the action bar.
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        // When swiping between different sections, select the corresponding
        // tab. We can also use ActionBar.Tab#select() to do this if we have
        // a reference to the Tab.
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);
            }
        });

        // For each of the sections in the app, add a tab to the action bar.
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            // Create a tab with text corresponding to the page title defined by
            // the adapter. Also specify this Activity object, which implements
            // the TabListener interface, as the callback (listener) for when
            // this tab is selected.
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(this));
        }

        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);

        mPrefs = getSharedPreferences("SharedPreferences",
                Context.MODE_PRIVATE);
        // Get a SharedPreferences editor
        mEditor = mPrefs.edit();
        /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);
        // Start with updates turned on
        mUpdatesRequested = true;


    }



    @Override
    protected void onPause() {
        // Save the current setting for updates
        mEditor.putBoolean("KEY_UPDATES_ON", mUpdatesRequested);
        mEditor.commit();
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mLocationClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        /*
         * Get any previous setting for location updates
         * Gets "false" if an error occurs
         */
        if (mPrefs.contains("KEY_UPDATES_ON")) {
            mUpdatesRequested =
                    mPrefs.getBoolean("KEY_UPDATES_ON", false);

            // Otherwise, turn off location updates
        } else {
            mEditor.putBoolean("KEY_UPDATES_ON", false);
            mEditor.commit();
        }
    }

    @Override
    protected void onStop() {
        // If the client is connected
        if (mLocationClient.isConnected()) {
            /*
             * Remove location updates for a listener.
             * The current Activity is the listener, so
             * the argument is "this".
             */
            mLocationClient.removeLocationUpdates(this);
        }
        /*
         * After disconnect() is called, the client is
         * considered "dead".
         */
        mLocationClient.disconnect();
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.trip, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
        // When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a JourneyFragment (defined as a static inner class below).
            Fragment retFragment = null;

            if (position == 0) {
                retFragment = JourneyFragment.newInstance(position + 1);
            }
            myListener=(MyLocationListener) retFragment;
            return retFragment;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getResources().getText(R.string.lbl_head_journey).toString().toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class JourneyFragment extends Fragment implements com.projects.tutorcontrol.TripActivity.MyLocationListener {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        private static final String STA_STOP = "STOP";
        private static final String STA_RUNNING = "RUNNING";

        private Button btnStart;
        private Button btnStop;

        private Chronometer chrSplit;
        private Chronometer chrTotal;

        private String currentState = STA_STOP;

        private Location mCurrentLocation=null;
        private Location mPreviousLocation=null;

        private double startDistance=0;
        private double splitDistance=0;

        private long startTime=0;
        private long splitTime=0;

        private double maxSpeedStart=0;
        private double maxSpeedSplit=0;

        private double mediumSpeedSplit=0;
        private double mediumSpeedStart=0;

        private TextView txtVelIst;
        private TextView txtMedSpeedSplit;
        private TextView txtMaxSpeedSplit;
        private TextView txtMedSpeedStart;
        private TextView txtMaxSpeedStart;

        private TextView txtDistSplit;
        private TextView txtDistStart;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static JourneyFragment newInstance(int sectionNumber) {
            JourneyFragment fragment = new JourneyFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public JourneyFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_trip, container, false);

            btnStart = (Button) rootView.findViewById(R.id.btnStart);
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startMeasure();
                }
            });
            btnStop = (Button) rootView.findViewById(R.id.btnStop);
            btnStop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    stopMeasure();
                }
            });

            txtVelIst=(TextView) rootView.findViewById(R.id.txtVelIst);
            txtMedSpeedSplit=(TextView) rootView.findViewById(R.id.txtSpMedSpl);
            txtMaxSpeedSplit=(TextView) rootView.findViewById(R.id.txtSpMaxSpl);
            txtMedSpeedStart=(TextView) rootView.findViewById(R.id.txtSpMedSta);
            txtMaxSpeedStart=(TextView) rootView.findViewById(R.id.txtSpMaxSta);

            txtDistSplit=(TextView) rootView.findViewById(R.id.txtSplitDist);
            txtDistStart=(TextView) rootView.findViewById(R.id.txtStartDIst);

            chrSplit = (Chronometer) rootView.findViewById(R.id.chrSplit);
            /*chrSplit.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                public void onChronometerTick(Chronometer cArg) {
                    long t = SystemClock.elapsedRealtime() - cArg.getBase();
                    cArg.setText(DateFormat.format("hh:mm:ss", t));
                }
            });*/
            chrTotal = (Chronometer) rootView.findViewById(R.id.chrTotal);
            /*chrTotal.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                public void onChronometerTick(Chronometer cArg) {
                    long t = SystemClock.elapsedRealtime() - cArg.getBase();
                    cArg.setText(DateFormat.format("hh:mm:ss", t));
                }
            });*/

            if (savedInstanceState != null) {
                currentState = savedInstanceState.getString("currState", STA_STOP);

                startDistance=savedInstanceState.getDouble("startDistance", 0);
                splitDistance=savedInstanceState.getDouble("splitDistance", 0);

                startTime=savedInstanceState.getLong("startTime", 0);
                splitTime=savedInstanceState.getLong("splitTime", 0);

            }

            if (currentState == STA_STOP) {
                chrSplit.setBase(SystemClock.elapsedRealtime());
                chrTotal.setBase(SystemClock.elapsedRealtime());
            } else {
                btnStart.setText(getActivity().getResources().getText(R.string.lbl_split));
            }

            //chrSplit.setFormat("HH:MM:SS");
            //chrTotal.setFormat("HH:MM:SS");

            return rootView;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);

            outState.putString("currState", currentState);

            outState.putDouble("startDistance", startDistance);
            outState.putDouble("splitDistance", splitDistance);

            outState.putLong("startTime", startTime);
            outState.putLong("splitTime", splitTime);

        }

        private void stopMeasure() {

            if (currentState == STA_RUNNING) {
                chrSplit.stop();
                chrTotal.stop();

                currentState = STA_STOP;

                btnStart.setText(getActivity().getResources().getText(R.string.lbl_start));
            }

        }

        private void startMeasure() {

            if (currentState == STA_STOP) {
                chrSplit.setBase(SystemClock.elapsedRealtime());
                chrTotal.setBase(SystemClock.elapsedRealtime());

                chrSplit.start();
                chrTotal.start();

                currentState = STA_RUNNING;
                btnStart.setText(getActivity().getResources().getText(R.string.lbl_split));

                startDistance=0;
                startTime=new Date().getTime();
                maxSpeedStart=0;


                splitDistance=0;
                splitTime=startTime;
                maxSpeedSplit=0;

            } else if (currentState == STA_RUNNING) {
                chrSplit.stop();
                chrSplit.setBase(SystemClock.elapsedRealtime());
                chrSplit.start();

                splitDistance=0;
                splitTime=new Date().getTime();
                maxSpeedSplit=0;

            }



        }

        /*
        private Location getLocation()
        {
            if(mCurrentLocation == null && mLocationClient.isConnected())
            {
                mCurrentLocation = mLocationClient.getLastLocation();
                String msg = "Updated Location: " +
                        Double.toString(mCurrentLocation.getLatitude()) + "," +
                        Double.toString(mCurrentLocation.getLongitude());
            }

            return mCurrentLocation;
        }
        */

        @Override
        public void onLocationChanged(Location location) {

            double newDistance=0;
            long currentTime=new Date().getTime();
            double currentSpeed=0;

            if(currentState==STA_RUNNING)
            {
                mPreviousLocation = mCurrentLocation;
                mCurrentLocation = location;

                newDistance = getDistance();

                startDistance += newDistance;
                splitDistance += newDistance;

                currentSpeed = location.getSpeed() * 3.6;

                if (currentSpeed > maxSpeedStart)
                    maxSpeedStart = currentSpeed;

                if (currentSpeed > maxSpeedSplit)
                    maxSpeedSplit = currentSpeed;

                mediumSpeedSplit = 3.6 * (splitDistance / (currentTime - splitTime)) / 1000;
                mediumSpeedStart = 3.6 * (startDistance / (currentTime - startTime)) / 1000;

                Toast.makeText(getActivity(),String.format("%.0f",splitDistance)+"\n"+String.format("%.0f",currentTime)+"\n"+String.format("%.0f",splitTime),Toast.LENGTH_LONG);
                Toast.makeText(getActivity(),String.format("%.0f",startDistance)+"\n"+String.format("%.0f",currentTime)+"\n"+String.format("%.0f",startTime),Toast.LENGTH_LONG);

                txtVelIst.setText(String.format("%.0f Km/h", currentSpeed));

                txtMedSpeedSplit.setText(String.format("%.0f Km/h", mediumSpeedSplit));
                txtMaxSpeedSplit.setText(String.format("%.0f Km/h", maxSpeedSplit));
                txtMedSpeedStart.setText(String.format("%.0f Km/h", mediumSpeedStart));
                txtMaxSpeedStart.setText(String.format("%.0f Km/h", maxSpeedStart));

                txtDistSplit.setText(String.format("%.1f Km", splitDistance/1000));
                txtDistStart.setText(String.format("%.1f Km", startDistance/1000));
            }
        }

        private double getDistance()
        {
            //Calcola la distanza presente tra currentLocation e previousLocation
            double distance=0;
            double raggioSfera=6375000;   //Più o meno dalle nostre parti vale così...

            if(mCurrentLocation != null && mPreviousLocation != null)
            {
                double diffLatRad=Math.abs(Math.toRadians(mCurrentLocation.getLatitude()) - Math.toRadians(mPreviousLocation.getLatitude()));
                double diffLonRad=Math.abs(Math.toRadians(mCurrentLocation.getLongitude()) - Math.toRadians(mPreviousLocation.getLongitude()));

                double distLat=diffLatRad * raggioSfera;
                double distLon=diffLonRad * raggioSfera;

                distance=Math.sqrt(Math.pow(distLat,(double) 2)+Math.pow(distLon,(double) 2));
            }

            return distance;
        }
    }

}
