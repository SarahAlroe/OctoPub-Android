package dk.alroe.apps.octopub;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dk.alroe.apps.octopub.model.Message;
import dk.alroe.apps.octopub.model.Thread;
import dk.alroe.apps.octopub.model.UploadResponse;
import dk.alroe.apps.octopub.model.UserId;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * Created by silasa on 12/6/16.
 */
public class WebRequestHandler {
    private static final String BASE_URL = "https://api.octopub.cf/";
    private static WebRequestHandler ourInstance = new WebRequestHandler();
    private Retrofit retrofit;

    private WebRequestHandler() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public static WebRequestHandler getInstance() {
        return ourInstance;
    }

    public ArrayList<Thread> getThreads() throws IOException {
        OctoPub octoPub = retrofit.create(OctoPub.class);
        Call<List<Thread>> call = octoPub.threads();
        return (ArrayList<Thread>) call.execute().body();
    }

    public UserId newID() throws IOException {
        OctoPub octoPub = retrofit.create(OctoPub.class);
        Call<UserId> call = octoPub.id();
        return call.execute().body();
    }

    public Thread getThread(String id) throws IOException {
        OctoPub octoPub = retrofit.create(OctoPub.class);
        Call<Thread> call = octoPub.thread(id);
        return call.execute().body();
    }

    public ArrayList getMessagesFrom(String thread, int number) throws IOException {
        OctoPub octoPub = retrofit.create(OctoPub.class);
        Call<ArrayList<Message>> call = octoPub.messagesFrom(thread, number);
        return call.execute().body();
    }

    public ArrayList getHistory(String thread) throws IOException {
        OctoPub octoPub = retrofit.create(OctoPub.class);
        Call<ArrayList<Message>> call = octoPub.history(thread);
        return call.execute().body();
    }

    public boolean addMessage(String thread, String text, UserId id) throws IOException {
        OctoPub octoPub = retrofit.create(OctoPub.class);
        Call<String> call = octoPub.addMessage(thread, text, id.getId(), id.getHash());
        return call.execute().isSuccessful();
    }

    public String getHelp() throws IOException {
        OctoPub octoPub = retrofit.create(OctoPub.class);
        Call<String> call = octoPub.help();
        return call.execute().body();
    }

    public UserId addThread(Thread thread) throws IOException {
        OctoPub octoPub = retrofit.create(OctoPub.class);
        Call<UserId> call = octoPub.addThread(thread.getTitle(), thread.getText());
        return call.execute().body();
    }

    public String uploadFromUri(Uri fileUri, Context context) throws IOException {
        OctoPub octoPub = retrofit.create(OctoPub.class);
        File file = new File(getRealPathFromURI(fileUri, context));
        String mimetype = context.getContentResolver().getType(fileUri);
        if (mimetype == null) {
            String extension = MimeTypeMap.getFileExtensionFromUrl(fileUri.getPath());
            mimetype = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        RequestBody requestBody = RequestBody.create(MediaType.parse(mimetype), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("Media", file.getName(), requestBody);
        RequestBody name = RequestBody.create(okhttp3.MultipartBody.FORM, file.getName());
        Call<UploadResponse> call = octoPub.upload(file.getName(), requestBody);
        return call.execute().body().result.cleanFileName;
    }

    private String getRealPathFromURI(Uri contentURI, Context context) {
        String result;
        Cursor cursor = context.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    public interface OctoPub {
        @GET("/newID")
        Call<UserId> id();

        @GET("/getThreads")
        Call<List<Thread>> threads();

        @GET("/getThread/")
        Call<Thread> thread(@Query("thread") String id);

        @GET("/getMessagesFrom/")
        Call<ArrayList<Message>> messagesFrom(@Query("thread") String id, @Query("fromNumber") int number);

        @GET("/getHistory/")
        Call<ArrayList<Message>> history(@Query("thread") String id);

        @FormUrlEncoded
        @POST("/addMessage/")
        Call<String> addMessage(@Field("thread") String thread, @Field("text") String text, @Field("id") String id, @Field("hash") String hash);

        @FormUrlEncoded
        @POST("/addThread/")
        Call<UserId> addThread(@Field("title") String title, @Field("text") String text);

        @GET("/getHelp/")
        Call<String> help();

        @POST("https://octopub.tk/upload.php")
        Call<UploadResponse> upload(@Query("name") String name,
                                    @Body RequestBody file
        );
    }
}
