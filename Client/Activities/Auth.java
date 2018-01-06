// Активити авторизации/регистрации

package sstu_team.book;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Auth extends Activity{

    Button reg;
    AutoCompleteTextView login;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        reg = (Button) findViewById(R.id.reg_button);
        login = (AutoCompleteTextView) findViewById(R.id.login);
        password = (EditText) findViewById(R.id.password);

        reg.setText("Войти или зарегистрироваться");

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onRegBtnClick(View view) {

        final String log = login.getText().toString();
        final String pass = password.getText().toString();

        PostRequest example = new PostRequest();
        RequestBody requestBody = formRequestBody(log,pass);
        Callback callback = formCallback();

        try {
            example.post(Metadata.url, requestBody, callback);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private RequestBody formRequestBody(String log, String pass) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("Function", "Auth")
                .addFormDataPart("Login", log)
                .addFormDataPart("Password", pass)
                .build();
        return requestBody;
    }

    private Callback formCallback() {
        return new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseStr = response.body().string();
                    Log.w("RESPONSE", responseStr);
                    checkResponse(responseStr);
                } else {
                    Log.w("RESPONSE", "No response");
                }

            }
        };
    }

    private void checkResponse(String response) {

        JSONObject json;

        try{
            json = new JSONObject(response);
            response = json.getString("Result");
            Log.w("RESULT",response);
        } catch (JSONException e){}

        if(response.equals("Error")) {
            Snackbar snack = Snackbar.make(findViewById(R.id.reg_button),
                    "Ошибка. Попробуйте ещё раз!", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null);
            View snackbarView = snack.getView();
            TextView snackTextView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
            snackTextView.setTextColor(Color.WHITE);
            snack.show();
        }

        else if (response.equals("Good")) {
            Metadata.type = 0;
            Metadata.login = login.getText().toString();
            Metadata.password = password.getText().toString();
            Intent intent = new Intent(Auth.this, BookListActivity.class);
            startActivity(intent);

        }
    }

}





