package codia.in.th.transcriptionusers;

import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import eu.livotov.labs.android.camview.ScannerLiveView;
import eu.livotov.labs.android.camview.camera.CameraController;

/**
 * Created by mynameismaxz on 9/12/15 AD.
 */

public class QRScannerCode extends AppCompatActivity {

    private ScannerLiveView camera;
    private CameraController controller;
    private boolean flashStatus;

    public CustomTabsServiceConnection customTabsServiceConnection;
    public CustomTabsSession customTabsSession;
    public Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_scanner);

        camera = (ScannerLiveView) findViewById(R.id.camview);
        camera.setScannerViewEventListener(new ScannerLiveView.ScannerViewEventListener() {
            @Override
            public void onCodeScanned(String data) {
                Log.d("QR CODE :", data);
//                MainActivity main = new MainActivity();
//                main.openPadFromURL(data);
//                Toast.makeText(QRScannerCode.this, data, Toast.LENGTH_LONG).show();
                openPadFromURL(data);
                connectCustomTabsService();
            }
        });
        camera.startScanner();
    }

    @Override
    protected void onDestroy() {
        camera.stopScanner();
        super.onDestroy();
        if (customTabsServiceConnection != null){
            unbindService(customTabsServiceConnection);
        }
    }

    public void connectCustomTabsService() {
        String chromePck = "com.android.chrome";
        customTabsServiceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName componentName, CustomTabsClient customTabsClient) {
                createCustomTabSession(customTabsClient);
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d("TEST", "EXIT ON CUSTOMTABS METHOD");
            }
        };
        CustomTabsClient.bindCustomTabsService(this, chromePck, customTabsServiceConnection);
    }

    public void createCustomTabSession(CustomTabsClient customTabsClient){
        customTabsClient.warmup(0);
        customTabsSession = customTabsClient.newSession(new CustomTabsCallback(){
            @Override
            public void onNavigationEvent(int navigationEvent, Bundle extras) {
                super.onNavigationEvent(navigationEvent, extras);
            }
        });
        customTabsSession.mayLaunchUrl(uri, null, null);
    }

    public void openPadFromURL(String padID) {
        Intent intent = new Intent(Intent.ACTION_SEND);
//        uri = Uri.parse(getResources().getString(R.string.url_etherpad_lite) + padID);
        uri = Uri.parse(padID);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(customTabsSession);
        builder.setShowTitle(true);
        builder.setToolbarColor(Color.rgb(0, 121, 107));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, uri);
    }

    public void toggleFlash() {
        flashStatus = !flashStatus;
        controller.switchFlashlight(flashStatus);
    }
}
