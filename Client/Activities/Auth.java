// Активити авторизации/регистрации

package sstu_team.book;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class Auth extends Activity implements View.OnClickListener{

    Button reg;
    AutoCompleteTextView login;
    EditText password;
    public static String result = "Server denied";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        reg = (Button) findViewById(R.id.reg_button);
        login = (AutoCompleteTextView) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);

        reg.setOnClickListener(this);
        reg.setText("Войти или зарегистрироваться");
    }

    @Override
    public void onClick(View view) {



        new Thread(new Runnable() {
            @Override
            public void run() {
                final String log = login.getText().toString();
                final String pass = password.getText().toString();
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost http = new HttpPost("http://gt99.xyz/Book/Main.php");
                List nameValuePairs = new ArrayList(2);
                nameValuePairs.add(new BasicNameValuePair("Function", "Auth"));
                nameValuePairs.add(new BasicNameValuePair("Login", log));
                nameValuePairs.add(new BasicNameValuePair("Password", pass));
                try {
                    http.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //получаем ответ от сервера
                try {
                    result = (String) httpclient.execute(http, new BasicResponseHandler());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject json;
        String resp = "Error:You Pidor!";

        try{
             json = new JSONObject(result);
             resp = json.getString("Result");
        } catch (JSONException e){}


        if(resp.equals("Error")) {

            Snackbar.make(findViewById(R.id.reg_button), "Ошибка. Попробуйте ещё раз!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }

        else if (resp.equals("Good")) {
            Metadata.login = login.getText().toString();
            Metadata.password = password.getText().toString();
            Intent intent = new Intent(Auth.this, BookListActivity.class);
            startActivity(intent);
            login.setText("");
            password.setText("");
        }

    }
}


