// Активити деталей конкретной книги

package sstu_team.book;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.widget.TextView;

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

public class BookDetailActivity extends AppCompatActivity {

    String name = "";
    String author = "";
    String year = "";
    String editor = "";
    String time = "";

    TextView info;
    Toolbar toolbar;
    FloatingActionButton edit_button;
    FloatingActionButton delete_button;
    public static String res;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        info = (TextView) findViewById(R.id.info);
        edit_button = (FloatingActionButton) findViewById(R.id.edit_book);
        delete_button = (FloatingActionButton) findViewById(R.id.delete_book);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString(BookDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(BookDetailFragment.ARG_ITEM_ID));
            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.book_detail_container, fragment)
                    .commit();
        }

        PostRequest example = new PostRequest();
        RequestBody requestBody = formRequestBody(Metadata.login,Metadata.password,
                Metadata.currentId);
        Callback callback = formCallback();

        try {
            example.post(Metadata.url, requestBody, callback);
        } catch (IOException e) {
            e.printStackTrace();
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
        else if(item.getTitle().equals("Выйти")) {

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
                        Intent intent = new Intent(BookDetailActivity.this,Auth.class);
                        startActivity(intent);
                    } else {
                        Log.w("RESPONSE", "No response");
                    }
                }
            });
        }

        return super.onOptionsItemSelected(item);

    }



    public void onEditBookClick(View view) {
        Log.w("DEBUG", Metadata.EditAuthor);
        Intent intent = new Intent(BookDetailActivity.this, EditBookActivity.class);
        startActivity(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onDeleteBookClick(View view) {

        PostRequest example = new PostRequest();
        RequestBody requestBody = formRequestBodyForDelete(Metadata.login, Metadata.password,
                Metadata.currentId);
        Callback callback = formCallbackForDelete();
        try {
            example.post(Metadata.url, requestBody, callback);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private RequestBody formRequestBodyForDelete(String log,String pass,String id) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("Function", "BookDelete")
                .addFormDataPart("Login", log)
                .addFormDataPart("Password", pass)
                .addFormDataPart("BookId", id)
                .build();
        return requestBody;
    }

    private Callback formCallbackForDelete() {
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
                    Intent intent = new Intent(BookDetailActivity.this, BookListActivity.class);
                    startActivity(intent);
                } else {
                    Log.w("RESPONSE", "No response");
                }
            }
        };
    }


    private RequestBody formRequestBody(String log,String pass,String id) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("Function", "GetBook")
                .addFormDataPart("Login", log)
                .addFormDataPart("Password", pass)
                .addFormDataPart("BookId", id)
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
            name = json.getJSONObject("Book").getString("Name");
            author = json.getJSONObject("Book").getString("Author");
            year = json.getJSONObject("Book").getString("Year");
            editor = json.getJSONObject("Book").getString("WhoAdded");
            time = json.getJSONObject("Book").getString("Time");
            response = json.getString("Result");
            Log.w("RESULT",response);
        } catch (JSONException e){}

        if(response.equals("Error")) {
            Intent intent = new Intent(BookDetailActivity.this, BookListActivity.class);
            startActivity(intent);
        }

        else if (response.equals("Good")) {
            Metadata.EditName = name;
            Metadata.EditAuthor = author;
            Metadata.EditYear = year;

            final StringBuilder builder = new StringBuilder();
            builder.append("Название: " + name);
            builder.append("\n\nАвтор: " + author);
            builder.append("\n\nГод выпуска: " + year);
            builder.append("\n\nДобавил: " + editor);
            builder.append("\n\nДата изменения: " + time);

            runOnUiThread(new Runnable(){
                public void run(){
                    info.setText(builder);

                    if(!editor.equals(Metadata.login)) {
                        delete_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                Snackbar.make(view, "Вы не являетесь создателем этой записи.", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();

                            }
                        });
                        edit_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Snackbar.make(view, "Вы не являетесь создателем этой записи.", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            }
                        });
                    }

                }
            });
        }
    }
}






