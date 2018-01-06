//Вспомогательный класс для формирования списка книг

package sstu_team.book.dummy;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sstu_team.book.Metadata;

public class DummyContent {


    public List<DummyItem> ITEMS = new ArrayList<DummyItem>();


    public static final Map<String, DummyItem> ITEM_MAP = new HashMap<String, DummyItem>();
    public static String result;

    public static int COUNT = Metadata.count;

    public DummyContent() {

        for (int i = 1; i <= Metadata.count; i++) {
            addItem(createDummyItem(i));
        }
    }

    private  void addItem(DummyItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position) {
        return new DummyItem(String.valueOf(position), Metadata.names[position-1]+"\nby "+Metadata.editors[position-1], makeDetails(position));
    }

    private static String makeDetails(int position) {
        StringBuilder builder = new StringBuilder();
        builder.append("Book id: ");
        builder.append("\n"+Metadata.currentId);
        //builder.append("\n"+Metadata.names[Integer.valueOf(Metadata.currentNum)-1]);

        return builder.toString();
    }

    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
