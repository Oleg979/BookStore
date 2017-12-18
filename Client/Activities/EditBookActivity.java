// Активити редактирования книги

package sstu_team.book;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
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

import static android.Manifest.permission.READ_CONTACTS;


public class EditBookActivity extends AppCompatActivity implements View.OnClickListener {

    AutoCompleteTextView name;
    AutoCompleteTextView author;
    AutoCompleteTextView year;
    Button add;

    public static String res;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        name = (AutoCompleteTextView) findViewById(R.id.BookName);
        author = (AutoCompleteTextView) findViewById(R.id.BookAuthor);
        year = (AutoCompleteTextView) findViewById(R.id.BookYear);
        add = (Button) findViewById(R.id.add_button);

        author.setText(Metadata.EditAuthor);
        name.setText(Metadata.EditName);
        year.setText(Metadata.EditYear);

        add.setText("Изменить");
        add.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                String bookName = name.getText().toString();
                String bookAuthor = author.getText().toString();
                String bookYear = year.getText().toString();
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost http = new HttpPost("http://gt99.xyz/Book/Main.php");
                List nameValuePairs = new ArrayList(2);
                nameValuePairs.add(new BasicNameValuePair("Function", "EditBook"));
                nameValuePairs.add(new BasicNameValuePair("Login", Metadata.login));
                nameValuePairs.add(new BasicNameValuePair("Password", Metadata.password));
                nameValuePairs.add(new BasicNameValuePair("BookId", Metadata.currentId));
                nameValuePairs.add(new BasicNameValuePair("BookName", bookName));
                nameValuePairs.add(new BasicNameValuePair("BookAuthor", bookAuthor));
                nameValuePairs.add(new BasicNameValuePair("BookYear", bookYear));
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
        String resp = "Error:You Pidor!";

        try{
            json = new JSONObject(res);
            resp = json.getString("Result");
        } catch (JSONException e){}

        if(resp.equals("Error")) {
            Snackbar.make(findViewById(R.id.add_button), "Ошибка! Вы не заполнили поля или использовали недопустимые символы!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        else if (resp.equals("Good")) {
            Intent intent = new Intent(EditBookActivity.this, BookListActivity.class);
            startActivity(intent);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add("Выйти из аккаунта");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

         if(item.getTitle().equals("Выйти из аккаунта")) {
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

            Intent intent = new Intent(EditBookActivity.this, Auth.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
}
