package sstu_team.book;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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

/**
 * An activity representing a single Book detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link BookListActivity}.
 */
public class BookDetailActivity extends AppCompatActivity {

    String name = "";
    String author = "";
    String year = "";
    String editor = "";

    TextView info;
    public static String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.detail_toolbar);
        setSupportActionBar(toolbar);

        info = findViewById(R.id.info);



        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost http = new HttpPost("http://gt99.xyz/Book/Main.php");
                List nameValuePairs = new ArrayList(2);
                nameValuePairs.add(new BasicNameValuePair("Function", "GetBook"));
                nameValuePairs.add(new BasicNameValuePair("Login", Metadata.login));
                nameValuePairs.add(new BasicNameValuePair("Password", Metadata.password));
                nameValuePairs.add(new BasicNameValuePair("BookId", Metadata.currentId));
                try {
                    http.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                //получаем ответ от сервера
                try {
                    res = (String) httpclient.execute(http, new BasicResponseHandler());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject json;

        try{
            json = new JSONObject(res);
           name = json.getJSONObject("Book").getString("Name");
           author = json.getJSONObject("Book").getString("Author");
           year = json.getJSONObject("Book").getString("Year");
           editor = json.getJSONObject("Book").getString("WhoAdded");
           Metadata.EditName = name;
           Metadata.EditAuthor = author;
           Metadata.EditYear = year;
        } catch (JSONException e){}


        FloatingActionButton edit_button = (FloatingActionButton) findViewById(R.id.edit_button);
        edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("DEBUG", Metadata.EditAuthor);
                Intent intent = new Intent(BookDetailActivity.this, EditBookActivity.class);
                startActivity(intent);
            }
        });

        FloatingActionButton delete_button = (FloatingActionButton) findViewById(R.id.delete_button);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        HttpClient httpclient = new DefaultHttpClient();
                        HttpPost http = new HttpPost("http://gt99.xyz/Book/Main.php");
                        List nameValuePairs = new ArrayList(2);
                        nameValuePairs.add(new BasicNameValuePair("Function", "BookDelete"));
                        nameValuePairs.add(new BasicNameValuePair("Login", Metadata.login));
                        nameValuePairs.add(new BasicNameValuePair("Password", Metadata.password));
                        nameValuePairs.add(new BasicNameValuePair("BookId", Metadata.currentId));
                        try {
                            http.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                        //получаем ответ от сервера
                        try {
                            res = (String) httpclient.execute(http, new BasicResponseHandler());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();


                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                Intent intent = new Intent(BookDetailActivity.this, BookListActivity.class);

                startActivity(intent);
            }
        });

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail fragment and add it to the activity
            // using a fragment transaction.
            Bundle arguments = new Bundle();
            arguments.putString(BookDetailFragment.ARG_ITEM_ID,
                    getIntent().getStringExtra(BookDetailFragment.ARG_ITEM_ID));
            BookDetailFragment fragment = new BookDetailFragment();
            fragment.setArguments(arguments);
           getSupportFragmentManager().beginTransaction()
                    .add(R.id.book_detail_container, fragment)
                    .commit();
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Название: " + name);
        builder.append("\n\nАвтор: " + author);
        builder.append("\n\nГод выпуска: " + year);
        builder.append("\n\nДобавил: " + editor);
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. Use NavUtils to allow users
            // to navigate up one level in the application structure. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            NavUtils.navigateUpTo(this, new Intent(this, BookListActivity.class));
            return true;
        }
        else if(item.getTitle().equals("Выйти из аккаунта")) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost http = new HttpPost("http://gt99.xyz/Book/Main.php");
                    List nameValuePairs = new ArrayList(2);
                    nameValuePairs.add(new BasicNameValuePair("Function", "DeAuth"));
                    nameValuePairs.add(new BasicNameValuePair("Login", Metadata.login));
                    nameValuePairs.add(new BasicNameValuePair("Password", Metadata.password));

                    try {
                        http.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    //получаем ответ от сервера
                    try {
                        String result = (String) httpclient.execute(http, new BasicResponseHandler());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Intent intent = new Intent(BookDetailActivity.this, Auth.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {


        menu.add("Выйти из аккаунта");

        return super.onCreateOptionsMenu(menu);
    }


}
