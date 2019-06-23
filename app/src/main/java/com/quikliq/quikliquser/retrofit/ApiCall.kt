package retrofit


import com.quikliq.quikliquser.utilities.Prefs
import com.quikliq.quikliquser.constants.Constant
import java.util.concurrent.TimeUnit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Created by Navdeep Machine
 */
class ApiCall {

    fun apiCall(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.connectTimeout(1, TimeUnit.MINUTES)
        httpClient.readTimeout(1, TimeUnit.MINUTES)
        httpClient.writeTimeout(1, TimeUnit.MINUTES)
        httpClient.addInterceptor { chain ->
            val original = chain.request()

            val request = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .method(original.method(), original.body())
                    .build()

            chain.proceed(request)
        }
        httpClient.addInterceptor(logging)
        return Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()

    }

    fun apiCallWithHeader(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.connectTimeout(1, TimeUnit.MINUTES)
        httpClient.readTimeout(1, TimeUnit.MINUTES)
        httpClient.writeTimeout(1, TimeUnit.MINUTES)
        if (Prefs.getBoolean(Constant.IS_LOGGED_IN, false)) {
            httpClient.addInterceptor { chain ->
                val original = chain.request()

                val request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer "+Prefs.getString("access_token",""))
                        .method(original.method(), original.body())
                        .build()

                chain.proceed(request)
            }
        } else {
            httpClient.addInterceptor { chain ->
                val original = chain.request()

                val request = original.newBuilder()
                        .header("Content-Type", "application/json")
                        .header("Authorization", "Bearer 6ZPRcEh9bpNJu9SUPCnQBa3QdaJINT")
                        .method(original.method(), original.body())
                        .build()

                chain.proceed(request)
            }
        }

        httpClient.addInterceptor(logging)
        return Retrofit.Builder()
                .baseUrl(Constant.GIFT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()

    }

    fun loginApiCallWithHeader(): Retrofit {
        val logging = HttpLoggingInterceptor()
        logging.level = HttpLoggingInterceptor.Level.BODY
        val httpClient = OkHttpClient.Builder()
        httpClient.connectTimeout(1, TimeUnit.MINUTES)
        httpClient.readTimeout(1, TimeUnit.MINUTES)
        httpClient.writeTimeout(1, TimeUnit.MINUTES)
        httpClient.addInterceptor { chain ->
            val original = chain.request()

            val request = original.newBuilder()
                    .header("Content-Type", "application/json")
                    .header("Authorization", Constant.LOGIN_BEARER)
                    .method(original.method(), original.body())
                    .build()

            chain.proceed(request)
        }
        httpClient.addInterceptor(logging)
        return Retrofit.Builder()
                .baseUrl(Constant.GIFT_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build()

    }
}
