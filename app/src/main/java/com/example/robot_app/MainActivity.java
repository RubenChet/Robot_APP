package com.example.robot_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.ubtechinc.cruzr.sdk.ros.RosRobotApi;
import com.ubtechinc.cruzr.serverlibutil.interfaces.InitListener;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MainActivity extends AppCompatActivity {


    private Button btn;
    private static final String TAG = "MyTag";
    private String topic, clientID;
    private MqttAndroidClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
        inite();
    }

    private void init() {
        btn = findViewById(R.id.btn_sub);
        clientID = "xxx";
        topic = "testtopic/rauren";
        client =
                new MqttAndroidClient(this.getApplicationContext(), "tcp://broker.hivemq.com:1883",
                        clientID);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectX();
            }
        });
    }

    public void inite() {
        RosRobotApi.get().initializ(this, new InitListener() {
            @Override
            public void onInit() {
                Toast.makeText(MainActivity.this, "SDK", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public int ledSetColor(int r, int g, int b, int times) {
        return RosRobotApi.get().ledSetColor(r, g, b, times);
    }
    public int ledSetOnOff(boolean onOff) {
        return RosRobotApi.get().ledSetOnOff(onOff);
    }


    private void connectX() {
        ledSetOnOff(true);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(MainActivity.this, "Connected", Toast.LENGTH_SHORT).show();
                    sub();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(MainActivity.this, "NoConnected", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private void sub() {
        try {
            client.subscribe(topic, 0);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Toast.makeText(MainActivity.this, new String(message.getPayload()), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        }catch (MqttException e) {

        }
    }
}