package com.example.nodejsandsocketio;

import static com.example.nodejsandsocketio.SocketManager.mSocket;
import static com.example.nodejsandsocketio.UserManager.currentUser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import io.socket.emitter.Emitter;

public class WelcomeActivity extends AppCompatActivity {

    TextView txtWelcome, txtNumberUser;
    ListView listUsernameOnline;
    Button btnGo;
    ArrayAdapter adapter;
    ArrayList<String> arrayUserOnline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellcome);

        txtWelcome = findViewById(R.id.txt_welcome);
        txtNumberUser = findViewById(R.id.text_list_user);
        listUsernameOnline = findViewById(R.id.list_username);
        btnGo = findViewById(R.id.btn_go);

        txtWelcome.setText("Welcome to BUZZ,\n" + currentUser + "!");

        arrayUserOnline = new ArrayList<>();
        adapter = new ArrayAdapter(WelcomeActivity.this, android.R.layout.simple_list_item_1, arrayUserOnline);
        listUsernameOnline.setAdapter(adapter);

        //request server to send list user
        mSocket.emit("client-request-user-list", "now");
        //listen server send user list to client & add to list view
        mSocket.on("server-send-user-list", onRetrieveUserList);

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(WelcomeActivity.this, ChatActivity.class);
                startActivity(intent);
            }
        });


    }

    //get user list from server
    Emitter.Listener onRetrieveUserList = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        JSONArray jsonArray = (JSONArray) object.getJSONArray("userList");
                        arrayUserOnline.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            arrayUserOnline.add(jsonArray.getString(i));
                        }
                        adapter.notifyDataSetChanged();
                        txtNumberUser.setText("Currently " + arrayUserOnline.size() + " user(s):");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });
        }
    };

}