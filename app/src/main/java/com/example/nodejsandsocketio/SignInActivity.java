package com.example.nodejsandsocketio;

import static com.example.nodejsandsocketio.SocketManager.mSocket;
import static com.example.nodejsandsocketio.UserManager.currentUser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.WrapperListAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.emitter.Emitter;

public class SignInActivity extends AppCompatActivity {

    EditText edtUserName, edtPassword;
    Button btnGo;
    TextView txtSwitchToSignUp, txtWarning;
    String USERNAME = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        edtPassword = findViewById(R.id.edt_password_confirm);
        edtUserName = findViewById(R.id.edt_UserName);
        btnGo = findViewById(R.id.btn_Submit);
        txtSwitchToSignUp = findViewById(R.id.txt_switchToSignUp);
        txtWarning = findViewById(R.id.textWarning);

        //connect socket
        mSocket.connect();

        txtSwitchToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                finish();
                startActivity(intent);
            }
        });

        btnGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = "";
                if (!edtUserName.getText().toString().equals(""))
                    username = edtUserName.getText().toString();
                String password = "";
                if (!edtPassword.getText().toString().equals(""))
                    password = edtPassword.getText().toString();

                if (username.equals("") || password.equals("")) {
                    //user has not filled all info yet
                    txtWarning.setText("Please fill in all information!");
                } else {
                    USERNAME = username;
                    //send to server username and password
                    mSocket.emit("client-send-login-username", username);
                    mSocket.emit("client-send-login-password", password);
                }
            }
        });

        //listen server send login result
        mSocket.on("server-send-login-result", onRetrieveLoginResult);

    }

    //listen server send login result
    Emitter.Listener onRetrieveLoginResult = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        String result = object.getString("status");
                        if (result.equals("1")) {
                            //user name & password is corrected
                            Intent intent  = new Intent(SignInActivity.this, WelcomeActivity.class);
                            currentUser = USERNAME;
                            startActivity(intent);
                        } else {
                            txtWarning.setText("Sign in fail! Please check your user name or password!");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

}