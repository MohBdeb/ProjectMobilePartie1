package mohamedBenkhalfa1877541.StudentStacker.reseau;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class JustineAPIClient {

    private static Retrofit retrofit = null;
    private static OkHttpClient client = null;
    //    private static String adresse = "192.168.4.36";
    private static String adresse = "192.168.2.13";
    private static String server_url = "http://"+adresse+":8888/";

    public static Retrofit getRetrofit(){
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        if (client == null){
            client = new OkHttpClient.Builder()
                    .readTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .addInterceptor(interceptor).build();
        }

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(server_url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build();
        }

        return retrofit;
    }
}
