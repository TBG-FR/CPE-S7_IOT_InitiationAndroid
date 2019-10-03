package cpe.iot.tbg.ex03_networkcomm;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class SendMessageActivity extends AppCompatActivity {

    private final String IP = "192.168.1.129";
    private final int PORT = 10000;
    private InetAddress address;
    private DatagramSocket UDPSocket;

    public EditText et_send_message;
    public TextView tv_get_message;
    public Button btn_send_message;
    public Button btn_get_message;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_message);

        et_send_message = findViewById(R.id.et_send_message);
        btn_send_message = findViewById(R.id.btn_send_message);
        btn_get_message = findViewById(R.id.btn_get_message);
        tv_get_message = findViewById(R.id.tv_get_message);

        btn_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                sendMessage(et_send_message.getText().toString());
            }
        });

        btn_get_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                new ReceiverTask(UDPSocket, tv_get_message).execute();
            }
        });

        // Permission : pas besoin de la demander au runtime ici (réseau only)

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        try
        {
            UDPSocket = new DatagramSocket(10000);
            address = InetAddress.getByName(IP);
        }
        catch(IOException ex)
        {
            Log.e("UDPSocketCreation", "onResume: ", ex);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        address = null;
        UDPSocket = null;
    }

    public void sendMessage(String message)
    {
        MessageThread mtd = new MessageThread(address, PORT, UDPSocket, message, true);
        mtd.start();
    }
}

class MessageThread extends Thread {

    InetAddress Address;
    int Port;
    DatagramSocket Socket;
    String Message;
    boolean Send;

    MessageThread(InetAddress address, int port, DatagramSocket socket, String message, boolean send) {
        super();
        Address = address;
        Port = port;
        Socket = socket;
        Message = message;
        Send = send;
    }

    @Override
    public void run() {
        //super.run();
        try
        {
            if(Send)
            {
                byte[] data = Message.getBytes("UTF-8");

                DatagramPacket packet = new DatagramPacket(data, data.length, Address, Port);
                Socket.send(packet);
            }
        }
        catch(Exception ex) // getBytes or send
        {
            ex.printStackTrace();
        }
    }
}

class ReceiverTask extends AsyncTask<Void, byte[], Void> {

    DatagramSocket Socket;
    TextView TextView;

    ReceiverTask(DatagramSocket socket, TextView textView) {
        super();
        Socket = socket;
        TextView = textView;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        while(true){
            byte[] data = new byte [1024]; // Espace de réception des données.
            DatagramPacket packet = new DatagramPacket(data, data.length);
            try
            {
                Socket.receive(packet);
            }
            catch (IOException ex)
            {
                ex.printStackTrace();
            }
            int size = packet.getLength();
            publishProgress(java.util.Arrays.copyOf(data, size));
        }
    }

    @Override
    protected void onProgressUpdate(byte[]... values) {
        super.onProgressUpdate(values);

        String message = "";
        try
        {
            message = new String(values[0], "UTF-8");
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
            message = "Erreur à la réception";
        }
        finally
        {
            TextView.setText(message);
        }
    }
}
