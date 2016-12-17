package dk.alroe.apps.octopub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Field;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by silasa on 12/6/16.
 */
public class WebRequestHandler {
    private static WebRequestHandler ourInstance = new WebRequestHandler();

    public static WebRequestHandler getInstance() {
        return ourInstance;
    }

    private static final String BASE_URL = "http://api.octopub.tk/";
    private Retrofit retrofit;

    private WebRequestHandler() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public interface OctoPub {
        @GET("/newID")
        Call<ID> id();

        @GET("/getThreads")
        Call<List<Thread>> threads();

        @GET("/getThread/")
        Call<Thread> thread(@Query("thread") String id);

        @GET("/getMessagesFrom/")
        Call<ArrayList<Message>> messagesFrom(@Query("thread") String id, @Query("fromNumber") int number);

        @GET("/getHistory/")
        Call<ArrayList<Message>> history(@Query("thread") String id);

        @POST("/addMessage/")
        Call<String> addMessage(@Field("thread") String thread, @Field("text") String text, @Field("id") String id, @Field("hash") String hash);

        @POST("/addThread/")
        Call<String> addThread(@Field("title") String title, @Field("text") String text);
    }

    public ArrayList<Thread> getThreads() throws IOException {
        OctoPub octoPub = retrofit.create(OctoPub.class);
        Call<List<Thread>> call = octoPub.threads();
        ArrayList<Thread> Threads = (ArrayList<Thread>) call.execute().body();
        return Threads;
    }

    public ID newID() throws IOException{
        OctoPub octoPub = retrofit.create(OctoPub.class);
        Call<ID> call = octoPub.id();
        return call.execute().body();
    }

    public Thread getThread(String id) throws IOException{
        OctoPub octoPub = retrofit.create(OctoPub.class);
        Call<Thread> call = octoPub.thread(id);
        return call.execute().body();
    }

    public ArrayList getMessagesFrom(String thread, int number) throws IOException{
        OctoPub octoPub = retrofit.create(OctoPub.class);
        Call<ArrayList<Message>> call = octoPub.messagesFrom(thread, number);
        return call.execute().body();
    }
    public ArrayList getHistory(String thread) throws IOException{
        OctoPub octoPub = retrofit.create(OctoPub.class);
        Call<ArrayList<Message>> call = octoPub.history(thread);
        return call.execute().body();
    }
    public boolean addMessage (String thread, String text, ID id) throws IOException {
        OctoPub octoPub = retrofit.create(OctoPub.class);
        Call<String> call = octoPub.addMessage(thread, text, id.getId(), id.getHash());
        return call.execute().isSuccessful();
    }
}
