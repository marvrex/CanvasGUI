package com.canvasgui.canvasgui.activities;

import android.content.Intent;
import android.os.RemoteException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;


import com.canvasgui.canvasgui.ScannedCanvasAppContainer;
import com.canvasgui.canvasgui.GUIElementDescription;
import com.canvasgui.canvasgui.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.apache.commons.io.FilenameUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * The app's launch activity.
 * When this activity is displayed it initializes a service to scan for nearby beacons
 * and populates valid urls from the received broadcasts in a list view.
 */
public class ScanActivity extends AppCompatActivity implements RangeNotifier, BeaconConsumer {

    private BeaconManager beaconManager;
    // The region in which to parse broadcasts
    private Region acceptedRegion;

    // All detected beacons
    private Set<Beacon> scannedBeacons;
    private ScannedCanvasAppContainer canvasAppContainer;

    // Currently available Button ids
    private List<Integer> buttonIds;

    private boolean isScanning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        canvasAppContainer = new ScannedCanvasAppContainer(this);
        scannedBeacons = new HashSet<>();
        buttonIds = new ArrayList<>();

        isScanning = false;

        // Set up scanning button
        ImageButton scanButton = (ImageButton) findViewById(R.id.buttonToggleScan);
        scanButton.setBackground(getResources().getDrawable(R.drawable.ic_scan_button));

        // Initialize beacon components
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

    // Called when BLE-Beacons are detected, that broadcast in the defined region
    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {

        this.scannedBeacons.addAll(beacons);
        canvasAppContainer.processBeacons(beacons);
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
        ImageButton scanButton = (ImageButton) findViewById(R.id.buttonToggleScan);
        scanButton.setImageResource(R.drawable.ic_scan_button);

        try {
            beaconManager.startRangingBeaconsInRegion(acceptedRegion);
            isScanning = true;
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /*
     * Stops ranging for beacons broadcasting within this class' acceptedRegion's range
     * After a scan has been stopped any valid beacons that have been found while scanning
     * will be processed.
     */
    public void stopScanning() {
        ImageButton scanButton = (ImageButton) findViewById(R.id.buttonToggleScan);
        scanButton.setImageResource(R.drawable.ic_scan_button_cancel);

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

    /*
     * Gets the apps of the ScannedCanvasAppContainer of this activity.
     * Removes all buttons and thus apps from a previous scan and creates
     * new buttons for the newly detected ones.
     */
    private void updateButtons() {
        Map<ArrayList<GUIElementDescription>,String> apps = canvasAppContainer.getApps();

        removeButtonsFromPreviousScan(buttonIds);
        addScanResultAsButton(apps);
    }

    /*
     * Creates and adds Buttons for this layout using
     * informationen of the app definition obtained via a previous scan
     */
    private void addScanResultAsButton(Map<ArrayList<GUIElementDescription>,String> apps) {
        // Disable placeholder text
        removePlaceholderText();
        //This activities' layout
        LinearLayout layout = (LinearLayout) findViewById(R.id.activity_main);

        for (final ArrayList<GUIElementDescription> gui : apps.keySet()) {
            int currentButtonId = 0;
            Button button = createNewAppButton(apps.get(gui), currentButtonId);

            View.OnClickListener clickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        openCanvasApp(gui);
                }
            };

            button.setOnClickListener(clickListener);
            buttonIds.add(button.getId());
            layout.addView(button);

            currentButtonId++;
        }
    }

    private Button createNewAppButton(String text, int id) {
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext()) ;

        Button button = (Button)inflater.inflate(R.layout.canvas_app_button, null);
        button.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        button.setText(text);
        button.setId(id);
        buttonIds.add(id);

        return button;
    }

    private void removePlaceholderText() {
        RelativeLayout layoutWrapper = (RelativeLayout) findViewById(R.id.activity_main_wrapper);
        layoutWrapper.removeView(findViewById(R.id.scan_placeholder_text));
    }

    /*
     * Removes any existing buttons and thus apps provided by this application
     */
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

    /*
     * Starts a new activity and transmits the defined GUI components of an app definition
     */
    public void openCanvasApp(ArrayList<GUIElementDescription> guiItems) {
        // Create an intent for the new activity
        Intent intent = new Intent(this, DisplayXmlActivity.class);
        // Bind the gui components to this intent
        intent.putParcelableArrayListExtra("components", guiItems);
        // Start the new activity
        startActivity(intent);
    }

}
