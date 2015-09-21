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
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText txtInputNew;
    FloatingActionButton qrScanBtn;
    Button submitBtn;

    public CustomTabsServiceConnection customTabsServiceConnection;
    public CustomTabsSession customTabsSession;
    public Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initURIParse();
        initQRBtn();
    }

    private void initURIParse() {
        txtInputNew = (EditText) findViewById(R.id.padInputTxt);
        submitBtn = (Button) findViewById(R.id.btnSubmit);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO : Push function to URL's Website
//                Intent i = new Intent(MainActivity.this, intentToWeb.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("pad_id",txtInputNew.getText().toString());
//                i.putExtras(bundle);
//                startActivity(i);

                String getPadTxt = txtInputNew.getText().toString();
                openPadFromURL(getPadTxt);
                connectCustomTabsService();

//                OpenPadOnWeb pad = new OpenPadOnWeb();
//                pad.openPadFromURL(txtInputNew.getText().toString());
//                pad.connectCustomTabsService();
//                Toast.makeText(MainActivity.this, txtInputNew.getText().toString(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void initQRBtn() {
        qrScanBtn = (FloatingActionButton) findViewById(R.id.qrScanBtn);
        qrScanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, QRScannerCode.class);
                startActivity(i);
            }
        });
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
        uri = Uri.parse(getResources().getString(R.string.url_etherpad_lite) + padID);
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder(customTabsSession);
        builder.setShowTitle(true);
        builder.setToolbarColor(Color.rgb(0, 121, 107));
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(this, uri);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (customTabsServiceConnection != null){
            unbindService(customTabsServiceConnection);
            Log.d("TEST","EXIT ON DESTROY METHOD");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
