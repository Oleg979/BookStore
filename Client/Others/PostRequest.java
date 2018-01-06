// Вспомогательный класс для формирования HTTP-запроса

package sstu_team.book;

import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class PostRequest {

    OkHttpClient client = new OkHttpClient();

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void post(String url, RequestBody body, Callback callback) throws IOException {

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        Call call = client.newCall(request);
        call.enqueue(callback);
    }
}
