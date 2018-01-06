// Вспомогательный класс для отправки запроса на деавторизацию

package sstu_team.book;


import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeAuthRequest {

    Callback callback;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public DeAuthRequest(Callback callback) {

        this.callback = callback;
        PostRequest example = new PostRequest();
        RequestBody requestBody = formRequestBody(Metadata.login, Metadata.password);

        try {
            example.post(Metadata.url, requestBody, callback);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private RequestBody formRequestBody(String log, String pass) {
        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("Function", "DeAuth")
                .addFormDataPart("Login", log)
                .addFormDataPart("Password", pass)
                .build();
        return requestBody;
    }





}
