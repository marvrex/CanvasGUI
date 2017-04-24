package com.canvasgui.canvasgui.activities;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.canvasgui.canvasgui.DownloadXMLTask;
import com.canvasgui.canvasgui.GUIElementDescription;
import com.canvasgui.canvasgui.R;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.utils.UrlBeaconUrlCompressor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.accent_systems.ibks_sdk.scanner.ASResultParser.byteArrayToHex;

/*
 * The responsibility of this activity lies in scanning for nearby beacons
 * and provide the user with an option to retrieve a recognized beacon's URL.
 * This URL should then be used to dispatch a task to retrieve a layout XML.
 */
public class MainActivity extends AppCompatActivity implements BeaconConsumer, RangeNotifier{
    protected static final String TAG = "MainActivity";
    private BeaconManager beaconManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onResume() {
        super.onResume();


        beaconManager = BeaconManager.getInstanceForApplication(this.getApplicationContext());
        // detect url frame
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        // bind context to manager
        beaconManager.bind(this);
    }

    public void retrieveLayout(View view) {

        ArrayList<GUIElementDescription> guiElements = new ArrayList<>();
        try {
            guiElements = new DownloadXMLTask().execute(this).get();
        } catch (InterruptedException e) {
            Log.e("MainActivity", e.getClass() + ": " + e.getMessage());
        } catch (ExecutionException e) {
            Log.e("MainActivity", e.getClass() + ": " + e.getMessage());
        }
        //put list into intent
        Intent intent = new Intent(this, DisplayXmlActivity.class);
        intent.putParcelableArrayListExtra("components", guiElements);
        startActivity(intent);
    }

    @Override
    public void onBeaconServiceConnect() {
        Region region = new Region("all-beacons-region", null, null, null);

        try {
            beaconManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
           e.printStackTrace();
        }

        beaconManager.addRangeNotifier(this);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
        for (Beacon beacon : beacons) {
                String url = UrlBeaconUrlCompressor.uncompress(beacon.getId1().toByteArray());
                Log.d(TAG, "I see a beacon transmitting a url: " + url +
                        " approximately " + beacon.getDistance() + " meters away.");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        beaconManager.unbind(this);
    }

}
