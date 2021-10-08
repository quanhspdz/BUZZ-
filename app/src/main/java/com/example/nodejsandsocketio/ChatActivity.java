package com.example.nodejsandsocketio;

import static com.example.nodejsandsocketio.SocketManager.mSocket;
import static com.example.nodejsandsocketio.UserManager.currentUser;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

public class ChatActivity extends AppCompatActivity {

    ListView listMessage;
    EditText edtMessage;
    ImageButton btnSend;
    ArrayList<String> arrayMessage;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_activivy);

        listMessage = findViewById(R.id.list_message);
        edtMessage = findViewById(R.id.edt_message);
        btnSend = findViewById(R.id.imgBtn_send);

        edtMessage.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        //send request to server to get message list
        mSocket.emit("client-request-message-list", "now");

        arrayMessage = new ArrayList<>();
        adapter = new ArrayAdapter<String>(ChatActivity.this, android.R.layout.simple_list_item_1, arrayMessage);
        listMessage.setAdapter(adapter);
        //get message list from server
        mSocket.on("server-send-message-list", onRetrieveMessage);

        //user send message
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = edtMessage.getText().toString();
                if (!edtMessage.getText().toString().trim().equals("")) {
                    message = currentUser + ": " + message;
                    mSocket.emit("client-send-message", message);
                    //send request to server to get message list
                    mSocket.emit("client-request-message-list", "now");
                    edtMessage.setText("");
                }
            }
        });

    }
    Emitter.Listener onRetrieveMessage = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONArray jsonArray = (JSONArray) object.getJSONArray("messageList");
                        arrayMessage.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            arrayMessage.add(jsonArray.getString(i));
                        }
                        adapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

}