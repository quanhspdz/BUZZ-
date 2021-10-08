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

import org.json.JSONException;
import org.json.JSONObject;

import java.awt.font.TextAttribute;

import io.socket.emitter.Emitter;

public class SignUpActivity extends AppCompatActivity {

    TextView txtWarning, txtSwitchToSignIn;
    EditText edtUsername, edtPassword, edtConfirmPassword;
    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        txtWarning = findViewById(R.id.textWarning);
        txtSwitchToSignIn = findViewById(R.id.txt_switchToSignIn);
        edtUsername = findViewById(R.id.edt_UserName_new);
        edtPassword = findViewById(R.id.edt_password);
        edtConfirmPassword = findViewById(R.id.edt_password_confirm);
        btnSubmit = findViewById(R.id.btn_Submit);

        //switch to sign in activity
        txtSwitchToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, SignInActivity.class);
                finish();
                startActivity(intent);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = "";
                if (!edtUsername.getText().toString().equals(""))
                    userName = edtUsername.getText().toString();

                String password = "";
                if (!edtPassword.getText().toString().equals(""))
                    password = edtPassword.getText().toString();

                String confirmPassword = "";
                if (!edtConfirmPassword.getText().toString().equals(""))
                    confirmPassword = edtConfirmPassword.getText().toString();

                //check input
                if (userName.equals("") || password.equals("") || confirmPassword.equals("")) {
                    txtWarning.setText("Please fill in all information!");
                } else {
                    //check password confirm
                    if (password.equals(confirmPassword)) {
                        txtWarning.setText("");
                        mSocket.emit("client-register-user", userName);
                        mSocket.emit("client-register-user-password", password);
                    } else {
                        txtWarning.setText("Incorrect confirm password!");
                    }
                }

            }
        });

        //check user name existed or not
        mSocket.on("server-send-register-result", onRetrieveRegisterResult);

    }

    Emitter.Listener onRetrieveRegisterResult = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    JSONObject object = (JSONObject) args[0];
                    try {
                        String exist = object.getString("result");
                        if (exist.equals("1")) {
                            txtWarning.setText("This user name has already taken by someone else!");
                        } else {
                            txtWarning.setText("");
                            currentUser = edtUsername.getText().toString();
                            Intent intent = new Intent(SignUpActivity.this, WelcomeActivity.class);
                            startActivity(intent);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    };

}