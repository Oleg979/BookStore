// Активити списка всех книг

package sstu_team.book;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import sstu_team.book.dummy.DummyContent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class BookListActivity extends AppCompatActivity {


    private boolean mTwoPane;
    public static String result;
    DummyContent dum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);



        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost http = new HttpPost("http://gt99.xyz/Book/Main.php");
                List nameValuePairs = new ArrayList(2);
                nameValuePairs.add(new BasicNameValuePair("Function", "GetAllBooks"));
                nameValuePairs.add(new BasicNameValuePair("Login", Metadata.login));
                nameValuePairs.add(new BasicNameValuePair("Password", Metadata.password));

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
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        JSONObject json;
        int len = 0;

        try{
            json = new JSONObject(result);
            len = json.getJSONArray("Books").length();

            JSONArray arr = json.getJSONArray("Books");

            String name[] = new String[arr.length()];
            String ids[] = new String[arr.length()];
            String editors[] = new String[arr.length()];

            for(int i = 0; i < arr.length(); i++) {
                name[i] = arr.getJSONObject(i).getString("Name");
                ids[i] = arr.getJSONObject(i).getString("Id");
                editors[i] = arr.getJSONObject(i).getString("WhoAdded");
            }

            Metadata.names = name;
            Metadata.ids = ids;
            Metadata.editors = editors;

        } catch (JSONException e){}

        Metadata.count = len;
        dum = new DummyContent();

        if (len == 0) {
            Snackbar.make(findViewById(R.id.fab), "Ваша библиотека пуста. Добавьте новую книгу!", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
        Log.d("DEBUG",String.valueOf(len));

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(BookListActivity.this,AddBookActivity.class);
                startActivity(intent);
            }
        });

        if (findViewById(R.id.book_detail_container) != null) {
            mTwoPane = true;
        }

        View recyclerView = findViewById(R.id.book_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);
    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, dum.ITEMS, mTwoPane));
    }

    public static class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final BookListActivity mParentActivity;
        private final List<DummyContent.DummyItem> mValues;
        private final boolean mTwoPane;
        private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {




                DummyContent.DummyItem item = (DummyContent.DummyItem) view.getTag();


                Metadata.currentNum = item.id;
                Log.d("CURRENT NUMBER", Metadata.ids[Integer.valueOf(item.id) - 1]);
                Metadata.currentId = Metadata.ids[Integer.valueOf(item.id) - 1];
                Metadata.currentEditor = Metadata.editors[Integer.valueOf(item.id) - 1];

                if (mTwoPane) {
                    Bundle arguments = new Bundle();

                    arguments.putString(BookDetailFragment.ARG_ITEM_ID, item.id);

                    BookDetailFragment fragment = new BookDetailFragment();
                    fragment.setArguments(arguments);
                    mParentActivity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.book_detail_container, fragment)
                            .commit();
                } else {
                    Context context = view.getContext();
                    Intent intent = new Intent(context, BookDetailActivity.class);
                    intent.putExtra(BookDetailFragment.ARG_ITEM_ID, item.id);

                    context.startActivity(intent);
                }
            }
        };

        SimpleItemRecyclerViewAdapter(BookListActivity parent,
                                      List<DummyContent.DummyItem> items,
                                      boolean twoPane) {
            mValues = items;
            mParentActivity = parent;
            mTwoPane = twoPane;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.book_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mIdView.setText(mValues.get(position).id);
            holder.mContentView.setText(mValues.get(position).content);

            holder.itemView.setTag(mValues.get(position));
            holder.itemView.setOnClickListener(mOnClickListener);
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mIdView;
           final TextView mContentView;

            ViewHolder(View view) {
                super(view);
                mIdView = (TextView) view.findViewById(R.id.id_text);
                mContentView = (TextView) view.findViewById(R.id.content);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.add("Только мои книги");
        menu.add("Выйти из аккаунта");

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getTitle().equals("Только мои книги")) {

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
                        result = (String) httpclient.execute(http, new BasicResponseHandler());
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

            Intent intent = new Intent(BookListActivity.this,Auth.class);
            startActivity(intent);
        }
            return super.onOptionsItemSelected(item);
    }
}
