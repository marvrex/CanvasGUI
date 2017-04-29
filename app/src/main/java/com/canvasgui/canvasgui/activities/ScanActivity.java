package com.canvasgui.canvasgui.activities;

import android.content.Intent;
import android.os.RemoteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;


import com.canvasgui.canvasgui.ScannedCanvasAppContainer;
import com.canvasgui.canvasgui.connector.FetchXMLTask;
import com.canvasgui.canvasgui.connector.XMLToGUIDescriptionConverterTask;
import com.canvasgui.canvasgui.GUIElementDescription;
import com.canvasgui.canvasgui.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;
import org.xmlpull.v1.XmlPullParser;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/*
 * The app's launch activity.
 * When this activity is displayed it initializes a service to scan for nearby beacons
 * and populates valid urls from the received broadcasts in a list view.
 */
public class ScanActivity extends AppCompatActivity implements RangeNotifier, BeaconConsumer {

    //extends ListActivity

    protected static final String TAG = "ScanActivity";

    private BeaconManager beaconManager;
    // The region in which to parse broadcasts
    private Region acceptedRegion;

    // All detected beacons
    private Set<Beacon> scannedBeacons;
    private ScannedCanvasAppContainer canvasAppContainer;

    // Currently available Button ids
    private List<Integer> buttonIds;

    //DEBUG
    private List<URL> validLayoutUrls;
    private ArrayAdapter<URL> listAdapter;

    private boolean isScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        canvasAppContainer = new ScannedCanvasAppContainer(this);
        scannedBeacons = new HashSet<>();
        buttonIds = new ArrayList<>();

        isScanning = false;



        //DEBUG
        validLayoutUrls = new ArrayList<>();

      //  initListAdapter();
        initRegion();
        initBeaconManager();
    }

    @Override
    public void onResume() {
        super.onResume();
        initBeaconManager();
    }

    @Override
    public void onPause() {
        super.onPause();
        // Disable scanning to save resources
        this.disableScanning();
    }

    /*
     * Configures the region used by the {@BeaconManager} instance to set a range in which to accept beacon broadcasts.
     * Afterwards
     */
    @Override
    public void onBeaconServiceConnect() {
        startScanning();
        beaconManager.addRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

        this.scannedBeacons.addAll(beacons);
        canvasAppContainer.processBeacons(beacons);

        //DEBUG add textviews for each url
        for (Beacon beacon : scannedBeacons) {
            try {
                String location = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                URL url = new URL(location);

                if (!validLayoutUrls.contains(url)) {
                    validLayoutUrls.add(url);

                    /*
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                        listAdapter.notifyDataSetChanged();
                        }
                    });
                    */
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
    }

    private void initListAdapter() {
        listAdapter = new ArrayAdapter(this,
                android.R.layout.simple_list_item_1,
                validLayoutUrls);
       // setListAdapter(listAdapter);
    }

    /*
     * Configures the region of the notifier accept all broadcasts, since all broadcasts
     * transmitting a Eddytone URL could be a potential candidate
     */
    private void initRegion() {
        String regionName = "all-beacons-region";

        // Pass null values for the id parameters, so every beacon in range broadcasting a url is detected
        this.acceptedRegion = new Region(regionName, null, null, null);
    }

    /*
     * Retrieves the applications BeaconManager instance and assigns it to this class' reference variable.
     * Additionally the beacon manager is configured to parse only Eddystone URL frames from broadcasting devices, since only thoise are allowed.
     */
    private void initBeaconManager() {
        beaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // Only parse url frames
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        // Bind context to manager
        beaconManager.bind(this);
    }

    public void toggleScan(View view) {
        if (isScanning)
           stopScanning();
        else
            startScanning();
    }

    /*
     * Starts ranging for beacons broadcasting within this class' acceptedRegion's range
     */
    public void startScanning() {
        try {
            beaconManager.startRangingBeaconsInRegion(acceptedRegion);
            isScanning = true;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /*
     * Stops ranging for beacons broadcasting within this class' acceptedRegion's range
     */
    public void stopScanning() {
        try {
            beaconManager.stopRangingBeaconsInRegion(acceptedRegion);
            isScanning = false;
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        // Process the new detected beacons
        canvasAppContainer.processBeacons(scannedBeacons);
        updateButtons();
    }

    private void updateButtons() {
        Map<Button, URL> apps = canvasAppContainer.getApps();

        removeButtonsFromPreviousScan(buttonIds);
        addScanResultAsButton(apps);
    }

    private void addScanResultAsButton(final Map<Button, URL> apps) {
        //This activities' layout
        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_main);

        for (final Button button : apps.keySet()) {
            View.OnClickListener clickListener = new View.OnClickListener() {
                URL url = apps.get(button);
                @Override
                public void onClick(View v) {
                    try {
                        XmlPullParser parser = new FetchXMLTask().execute(url).get();
                        ArrayList<GUIElementDescription> guiComponents = new XMLToGUIDescriptionConverterTask(getApplicationContext(), parser).execute().get();
                        openCanvasApp(guiComponents);
                    } catch (ExecutionException | InterruptedException e) {
                        Log.e(TAG, e.getClass() + ": " + e.getMessage());
                    }

                }
            };

            button.setOnClickListener(clickListener);
            buttonIds.add(button.getId());
            layout.addView(button);
        }
    }

    private void removeButtonsFromPreviousScan(List<Integer> oldButtonIds) {
        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_main);

        for (int id : oldButtonIds) {
            Button button = (Button) findViewById(id);
            layout.removeView(button);
        }
    }

    /*
     * Unbinds this BeaconConsumer from the global beacon manager.
     * To temporarily stop scanning, use stopScanning() as its more efficient to restart the service afterwards.
     */
    public void disableScanning() {
        beaconManager.unbind(this);
    }

    public void openCanvasApp(ArrayList<GUIElementDescription> guiItems) {
        // Create an intent for the new activity
        Intent intent = new Intent(this, DisplayXmlActivity.class);
        // Bind the gui components to this intent
        intent.putParcelableArrayListExtra("components", guiItems);
        // Start the new activity
        startActivity(intent);
    }

    /*
    // DEBUG
    public void openExampleLayout(View view) {

        ArrayList<GUIElementDescription> guiElements = new ArrayList<>();
        try {
            guiElements = new XMLToGUIDescriptionConverterTask().execute(this).get();
        } catch (InterruptedException e) {
            Log.e("ScanActivity", e.getClass() + ": " + e.getMessage());
        } catch (ExecutionException e) {
            Log.e("ScanActivity", e.getClass() + ": " + e.getMessage());
        }

        openCanvasApp(guiElements);
    }
*/

}
