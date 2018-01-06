// Активити редактирования книги

package sstu_team.book;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.Manifest.permission.READ_CONTACTS;

public class EditBookActivity extends AppCompatActivity {

    AutoCompleteTextView name;
    AutoCompleteTextView author;
    AutoCompleteTextView year;
    Button add;

    public static String res;

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_book);

        name = (AutoCompleteTextView) findViewById(R.id.BookName);
        author = (AutoCompleteTextView) findViewById(R.id.BookAuthor);
        year = (AutoCompleteTextView) findViewById(R.id.BookYear);
        add = (Button) findViewById(R.id.edit_button);

        author.setText(Metadata.EditAuthor);
        name.setText(Metadata.EditName);
        year.setText(Metadata.EditYear);

        add.setText("Изменить");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onEditBtnClick(View view) {

        String bookName = name.getText().toString();
        String bookAuthor = author.getText().toString();
        String bookYear = year.getText().toString();

        PostRequest example = new PostRequest();
        RequestBody requestBody = formRequestBody(Metadata.login, Metadata.password,
                Metadata.currentId, bookName, bookAuthor, bookYear);
        Callback callback = formCallback();

        try {
            example.post(Metadata.url, requestBody, callback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private RequestBody formRequestBody(String log, String pass, String id,
                                        String name, String author, String year) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("Function", "EditBook")
                .addFormDataPart("Login", log)
                .addFormDataPart("Password", pass)
                .addFormDataPart("BookId", id)
                .addFormDataPart("BookName", name)
                .addFormDataPart("BookAuthor", author)
                .addFormDataPart("BookYear", year)
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

        try {
            json = new JSONObject(response);
            response = json.getString("Result");
            Log.w("RESULT", response);
        } catch (JSONException e) {
        }

        if (response.equals("Error")) {
            Snackbar.make(findViewById(R.id.edit_button),
                    "Ошибка! Вы не заполнили поля или использовали недопустимые символы!", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
        } else if (response.equals("Good")) {
            Intent intent = new Intent(EditBookActivity.this, BookListActivity.class);
            startActivity(intent);
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_other, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpTo(this, new Intent(this, BookListActivity.class));
            return true;
        }

        else if (item.getTitle().equals("Выйти")) {
            DeAuthRequest deAuthRequest = new DeAuthRequest(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (response.isSuccessful()) {
                        String responseStr = response.body().string();
                        Log.w("RESPONSE", responseStr);
                        Intent intent = new Intent(EditBookActivity.this,Auth.class);
                        startActivity(intent);
                    } else {
                        Log.w("RESPONSE", "No response");
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }
}
