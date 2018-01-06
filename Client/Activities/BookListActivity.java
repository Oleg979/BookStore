// Активити списка всех книг

package sstu_team.book;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;
import sstu_team.book.dummy.DummyContent;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class BookListActivity extends AppCompatActivity {

    private boolean mTwoPane;
    public static String result;
    DummyContent dum;
    FloatingActionButton fab;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());

        if (findViewById(R.id.book_detail_container) != null)
            mTwoPane = true;


        String func = "";
        if (Metadata.type == 1) func = "GetUserBooks";
        else if (Metadata.type == 0) func = "GetAllBooks";

        PostRequest example = new PostRequest();
        RequestBody requestBody = formRequestBody(func,Metadata.login,Metadata.password);
        Callback callback = formCallback();

        try {
            example.post(Metadata.url, requestBody, callback);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private RequestBody formRequestBody(String func,String log, String pass) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("Function", func)
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
        int len = 0;

        try {
            json = new JSONObject(response);
            len = json.getJSONArray("Books").length();
            JSONArray arr = json.getJSONArray("Books");

            String name[] = new String[arr.length()];
            String ids[] = new String[arr.length()];
            String editors[] = new String[arr.length()];
            String times[] = new String[arr.length()];

            for (int i = 0; i < arr.length(); i++) {
                name[i] = arr.getJSONObject(i).getString("Name");
                ids[i] = arr.getJSONObject(i).getString("Id");
                editors[i] = arr.getJSONObject(i).getString("WhoAdded");
                times[i] = arr.getJSONObject(i).getString("Time");
            }

            Metadata.names = name;
            Metadata.ids = ids;
            Metadata.editors = editors;
            Metadata.times = times;
        }
        catch (JSONException e) {
            e.printStackTrace();
        }

        Metadata.count = len;
        dum = new DummyContent();

        final int finalLen = len;
        runOnUiThread(new Runnable(){
            public void run(){
                if (finalLen == 0) {
                    Snackbar.make(findViewById(R.id.fab), "Ваша библиотека пуста. Добавьте новую книгу!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                Log.w("DEBUG",String.valueOf(finalLen));

                dum = new DummyContent();
                View recyclerView = findViewById(R.id.book_list);
                assert recyclerView != null;
                setupRecyclerView((RecyclerView) recyclerView);

            }
        });

    }

    public void onFabClick(View view) {

        Intent intent = new Intent(BookListActivity.this,AddBookActivity.class);
        startActivity(intent);

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {

        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(this, dum.ITEMS, mTwoPane));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getTitle().equals("Только мои книги")) {

            String func = "";

                    if (Metadata.type == 0) {
                        Metadata.type = 1;
                        func = "GetUserBooks";
                        Snackbar.make(findViewById(R.id.fab), "Теперь отображаются только Ваши книги.",
                                Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();

                    }
                    else if (Metadata.type == 1) {
                        Metadata.type = 0;
                        func = "GetAllBooks";
                        Snackbar.make(findViewById(R.id.fab), "Теперь отображаются все книги.",
                                Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                    }

                   PostRequest example = new PostRequest();
                   RequestBody requestBody = formChangeRequestBody(func,Metadata.login,Metadata.password);
                   Callback callback = formChangeCallback();

                    try {
                        example.post(Metadata.url, requestBody, callback);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
        }

        else if(item.getTitle().equals("Поиск")) {
            Snackbar.make(findViewById(R.id.fab), "Данная функция доступна только в платной версии приложения.", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
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
                        Intent intent = new Intent(BookListActivity.this,Auth.class);
                        startActivity(intent);
                    } else {
                        Log.w("RESPONSE", "No response");
                    }
                }
            });
        }
        return super.onOptionsItemSelected(item);
    }

    private RequestBody formChangeRequestBody(String func,String log, String pass) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("Function", func)
                .addFormDataPart("Login", log)
                .addFormDataPart("Password", pass)
                .build();
        return requestBody;
    }

    private  Callback formChangeCallback() {
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
                    checkChangeResponse(responseStr);
                } else {
                    Log.w("RESPONSE", "No response");
                }

            }
        };
    }

    private void checkChangeResponse(String response) {


        JSONObject json;
        int len = 0;

        try{
            json = new JSONObject(response);
            len = json.getJSONArray("Books").length();

            JSONArray arr = json.getJSONArray("Books");

            String name[] = new String[arr.length()];
            String ids[] = new String[arr.length()];
            String editors[] = new String[arr.length()];
            String times[] = new String[arr.length()];

            for(int i = 0; i < arr.length(); i++) {
                name[i] = arr.getJSONObject(i).getString("Name");
                ids[i] = arr.getJSONObject(i).getString("Id");
                editors[i] = arr.getJSONObject(i).getString("WhoAdded");
                times[i] = arr.getJSONObject(i).getString("Time");
            }

            Metadata.names = name;
            Metadata.ids = ids;
            Metadata.editors = editors;
            Metadata.times = times;

        } catch (JSONException e){}

        Metadata.count = len;

        dum = new DummyContent();

        final View recyclerView = findViewById(R.id.book_list);
        assert recyclerView != null;
        runOnUiThread(new Runnable(){
            public void run(){
                setupRecyclerView((RecyclerView) recyclerView);
            }
        });

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
                Metadata.currentTime = Metadata.times[Integer.valueOf(item.id) - 1];

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


}







