package dk.alroe.apps.octopub;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

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

    public class JThread {
        public final String title;
        public final String id;
        public final int length;
        public JThread(String title, String id, int length) {
            this.title = title;
            this.id = id;
            this.length = length;
        }
    }

    public interface GetThreads {
        @GET("/getThreads")
        Call<List<JThread>> threads();
    } //@Path("title") String title, @Path("id") String id,@Path("length") int length

    private WebRequestHandler() {
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    public ArrayList<Thread> getThreads() throws IOException {
        GetThreads getsThreads = retrofit.create(GetThreads.class);
        Call<List<JThread>> call = getsThreads.threads();
        List<JThread> JThreads = call.execute().body();
        ArrayList<Thread> Threads = new ArrayList<>();
        for (JThread JThread : JThreads){
            Threads.add(new Thread(JThread.title,JThread.id,JThread.length));
        }
        return Threads;
    }
}
