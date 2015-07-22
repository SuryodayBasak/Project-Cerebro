package bci.cerebro;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.neurosky.thinkgear.TGDevice;
import com.neurosky.thinkgear.TGEegPower;
import com.neurosky.thinkgear.TGRawMulti;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class RecorderActivity extends Activity {

    /*
    Move the bluetooth handling methods to the switch on/off block.
     */
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISABLE_BT = 0;
    int pairedFlag = 0;
    int fileCountFlag = 0;

    BluetoothAdapter mBluetoothAdapter;
    Switch btSelect;
    TextView pairedIndicator;
    Set<BluetoothDevice> pairedDevices;
    List<String> list;
    String filename = "newfile.csv";
    Button recordNow;

    TGRawMulti rawData;
    TGDevice tgDevice;
    TGEegPower fbands;
    List<TGEegPower> points;
    //EEGPoint current;
    String thought;
    final boolean rawEnabled = false;

    TextView mindWaveIndicator;
    TextView attentionIndicator;
    TextView meditationIndicator;
    TextView channel1;
    TextView channel2;
    TextView channel3;
    TextView channel4;
    TextView channel5;
    TextView channel6;
    TextView channel7;
    TextView channel8;
    TextView rawVal;

    int lowAlpha;
    int highAlpha;
    int lowBeta;
    int highBeta;
    int lowGamma;
    int midGamma;
    int Delta;
    int Theta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recorder);

        //points = new ArrayList<TGEegPower>();
        //db = new frequencyTable(this);
        //Bundle extras = getIntent().getExtras();
        //thought = extras.getString("thought");
        //current = new EEGPoint(thought);

        assignVariables();
        btSelect.setChecked(false);

        if (mBluetoothAdapter == null) {
            // Device does not support Bluetooth
            btSelect.setClickable(false);
            pairedIndicator.setText("Bluetooth not supported.");

        }

        else{
            btSelect.setChecked(true);
            tgDevice = new TGDevice(mBluetoothAdapter, handler);
            Toast.makeText(getApplicationContext(), "The handler shit is done...",Toast.LENGTH_SHORT).show();

            list = new ArrayList<>(); //The '<>' indicates String. No need of explicit declaration, it seems.

            pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0)
            {
                for (BluetoothDevice device : pairedDevices)
                {
                    list.add(device.getName() + "," + device.getAddress());
                    if(device.getName().startsWith("Mind")){
                        pairedIndicator.setText("MindWave Mobile is paired.");
                        pairedFlag=1;
                    }
                }

                if(pairedFlag==0){
                    pairedIndicator.setText("MindWave Mobile is not paired.");
                }
            }

            /*
            if (mBluetoothAdapter.isEnabled()) {
                btSelect.setChecked(true);
                tgDevice = new TGDevice(mBluetoothAdapter, handler);
                Toast.makeText(getApplicationContext(), "The handler shit is done...",Toast.LENGTH_SHORT).show();
            }*/

            if(tgDevice.getState() != TGDevice.STATE_CONNECTING && tgDevice.getState() != TGDevice.STATE_CONNECTED)
                tgDevice.connect(rawEnabled);



            btSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked){

                    if(isChecked){
                        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    }

                    else{
                        mBluetoothAdapter.disable();
                    }

                }
            });

            recordNow.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // get an image from the camera
                            if (mBluetoothAdapter.isEnabled()) {

                            }
                        }
                    }
            );
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


    @Override
    public void onDestroy() {
        super.onDestroy();
        tgDevice.close();
    }
    @Override
    public void onResume() {
        super.onResume();
    }

    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
                switch (msg.what) {
                    case TGDevice.MSG_STATE_CHANGE:
                        switch (msg.arg1) {
                            case TGDevice.STATE_IDLE:
                                break;
                            case TGDevice.STATE_CONNECTING:
                                //Toast.makeText(getApplicationContext(), "Connecting ...",Toast.LENGTH_SHORT).show();
                                //myTextView = (TextView) findViewById(R.id.connectStatus);
                                mindWaveIndicator.setText("Connecting ...");
                                break;
                            case TGDevice.STATE_CONNECTED:
                                //Toast.makeText(getApplicationContext(), "Connected",Toast.LENGTH_SHORT).show();
                                //myTextView = (TextView) findViewById(R.id.connectStatus);
                                mindWaveIndicator.setText("Connected");
                                tgDevice.start();
                                break;
                            case TGDevice.STATE_NOT_FOUND:
                                //Toast.makeText(getApplicationContext(), "Connection Not Found, reset and try again.",Toast.LENGTH_SHORT).show();
                                //myTextView = (TextView) findViewById(R.id.connectStatus);
                                mindWaveIndicator.setText("Connection Not Found, reset and try again.");
                                break;
                            case TGDevice.STATE_NOT_PAIRED:
                                //Toast.makeText(getApplicationContext(), "There is not Mindset paired to this device.",Toast.LENGTH_SHORT).show();
                                //myTextView = (TextView) findViewById(R.id.connectStatus);
                                mindWaveIndicator.setText("There is not Mindset paired to this device.");
                                break;
                            case TGDevice.STATE_DISCONNECTED:
                                //Toast.makeText(getApplicationContext(), "Disconnected",Toast.LENGTH_SHORT).show();
                                //myTextView = (TextView) findViewById(R.id.connectStatus);
                                mindWaveIndicator.setText("Disconnected");
                        }
                        break;
                    case TGDevice.MSG_POOR_SIGNAL:
                        //myTextView = (TextView) findViewById(R.id.signal);
                        if(msg.arg1 == 0)
                        {
                            //Toast.makeText(getApplicationContext(), "Great Signal!",Toast.LENGTH_SHORT).show();
                            //myTextView.setText("Great");
                        }
                        else if(msg.arg1 > 50)
                        {
                            //Toast.makeText(getApplicationContext(), "Poor signal; please adjust headset.",Toast.LENGTH_SHORT).show();
                            //myTextView.setText("Poor, please readjust headset.");
                        }
                        break;
                    case TGDevice.MSG_ATTENTION:
                        //mProgress = (ProgressBar) findViewById(R.id.attentionBar);
                        //mProgress.setProgress(msg.arg1);
                        //Toast.makeText(getApplicationContext(), "Attention:"+msg.arg1,Toast.LENGTH_SHORT).show();
                        //attentionIndicator.setText("Attention: "+msg.arg1);
                        break;
                    case TGDevice.MSG_MEDITATION:
                        //meditationIndicator.setText("Meditation: "+msg.arg1);
                        break;
                    case TGDevice.MSG_BLINK:
                    //tv.append("Blink: " + msg.arg1 + "\n");
                        //Toast.makeText(getApplicationContext(), "Ha! You blinked :P",Toast.LENGTH_SHORT).show();
                        break;


                    case TGDevice.MSG_RAW_MULTI:
                        TGRawMulti rawM = (TGRawMulti)msg.obj;
                        //tv.append("Raw1: " + rawM.ch1 + "\nRaw2: " + rawM.ch2);
                        Toast.makeText(getApplicationContext(), "Got raw",Toast.LENGTH_SHORT).show();
                        case TGDevice.MSG_RAW_DATA:

                        //int rawValue = msg.arg1;
                        //break;

                    case TGDevice.MSG_EEG_POWER:
                        fbands = (TGEegPower)msg.obj;
                        //points.add(fbands);

                        //channel1.setText("Delta: " + fbands.delta);
                        Delta = fbands.delta;

                        //channel2.setText("High Alpha: " + fbands.highAlpha);
                        highAlpha = fbands.highAlpha;

                        //channel3.setText("High Beta: " + fbands.highBeta);
                        highBeta = fbands.highBeta;

                        //channel4.setText("Low Alpha: " + fbands.lowAlpha);
                        lowAlpha = fbands.lowAlpha;

                        //channel5.setText("Low Beta: " + fbands.lowBeta);
                        lowBeta = fbands.lowBeta;

                        //channel6.setText("Low Gamma: " + fbands.lowGamma);
                        lowGamma = fbands.lowGamma;

                        //channel7.setText("Mid Gamma: " + fbands.midGamma);
                        midGamma = fbands.midGamma;

                        //channel8.setText("Theta: " + fbands.theta);
                        Theta = fbands.theta;

                        saveData(lowAlpha,highAlpha,lowBeta,highBeta,lowGamma,midGamma,Delta,Theta);
                        break;
                    case TGDevice.MSG_LOW_BATTERY:
                        Toast.makeText(getApplicationContext(), "Low battery!",Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }

        }
    };


    public void saveData(int argLowAlpha, int argHighAlpha, int argLowBeta, int argHighBeta, int argLowGamma, int argMidGamma, int argDelta, int argTheta) {
        //String filename = "/sdcard/myfile.txt";
        //String filename = "file";
        //String fileext = ".txt";
        //String string = "1,2,3,4\n";
        //FileOutputStream outputStream;

        try {
            File sdCard = Environment.getExternalStorageDirectory();
            File dir = new File(sdCard.getAbsolutePath() + "/cerebro_files/");
            dir.mkdirs();

            if (fileCountFlag == 0) {
                File[] files = dir.listFiles();
                int numberOfFiles = files.length;
                String index = new Integer(numberOfFiles + 1).toString();
                Toast.makeText(getApplicationContext(), "Number of files is: "+index, Toast.LENGTH_SHORT).show();
                filename = "file" + index + ".csv";
                fileCountFlag = 1;
            }

            //File file = new File(dir, "filename1.csv"+index);
            File file = new File(dir, filename);
            //Toast.makeText(getApplicationContext(), "init file",Toast.LENGTH_SHORT).show();

            FileOutputStream f = new FileOutputStream(file, true);

            //Toast.makeText(getApplicationContext(), "got output stream",Toast.LENGTH_SHORT).show();

            //String strLowAlpha = new Integer(lowAlpha).toString();
            String strHighAlpha = new Integer(argHighAlpha).toString();
            String strLowBeta = new Integer(argLowBeta).toString();
            String strHighBeta = new Integer(argHighBeta).toString();
            String strLowGamma = new Integer(argLowGamma).toString();
            String strMidGamma = new Integer(argMidGamma).toString();
            String strDelta = new Integer(argDelta).toString();
            String strTheta = new Integer(argTheta).toString();

            //string1 = string1 + "\n";

            String strCSV = new Integer(argLowAlpha).toString();

            strCSV = strCSV+','+strHighAlpha+','+strLowBeta+','+strHighBeta+','+strLowGamma+','+strMidGamma+','+strDelta+','+strTheta+'\n';

            //f.write(string.getBytes());
            //bf.write(passData.getBytes());
            f.write(strCSV.getBytes());
            //Toast.makeText(getApplicationContext(), "Done writing",Toast.LENGTH_SHORT).show();
            f.close();
            //Toast.makeText(getApplicationContext(), "File buffer closed",Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "failed to write",Toast.LENGTH_SHORT).show();
        }
    }

    void assignVariables(){
        btSelect = (Switch) findViewById(R.id.btSwitch);
        pairedIndicator = (TextView) findViewById(R.id.pairedStatus);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        mindWaveIndicator = (TextView) findViewById(R.id.mindwaveStatus);

        recordNow = (Button) findViewById(R.id.recordButton);
        /*
        attentionIndicator = (TextView) findViewById(R.id.attentionLabel);
        meditationIndicator = (TextView) findViewById(R.id.meditationLabel);

        channel1 = (TextView) findViewById(R.id.ch1);
        channel2 = (TextView) findViewById(R.id.ch2);
        channel3 = (TextView) findViewById(R.id.ch3);
        channel4 = (TextView) findViewById(R.id.ch4);
        channel5 = (TextView) findViewById(R.id.ch5);
        channel6 = (TextView) findViewById(R.id.ch6);
        channel7 = (TextView) findViewById(R.id.ch7);
        channel8 = (TextView) findViewById(R.id.ch8);

        rawVal = (TextView) findViewById(R.id.raw);
        */
    }
}
