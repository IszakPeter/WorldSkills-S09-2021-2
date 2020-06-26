package com.example.worldskills_2021

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_dashboard.*
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.*
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import kotlin.concurrent.thread
import kotlin.system.exitProcess
import kotlinx.coroutines.delay as delay1


/* "források":
        https://github.com/tarikul-app-dev/RestAPIOkhttpExKotlin/blob/master/app/src/main/java/limited/it/planet/callingrestapikotlin/NetworkClient.kt
        https://stackoverflow.com/questions/5161951/android-only-the-original-thread-that-created-a-view-hierarchy-can-touch-its-vi
        https://stackoverflow.com/questions/56893945/how-to-use-okhttp-to-make-a-post-request-in-kotlin
        https://better-coding.com/solved-android-cannot-send-data-to-the-server-cleartext-communication-to-not-permitted-by-network-security-policy/
        https://www.journaldev.com/309/android-alert-dialog-using-kotlin
        https://www.appsdeveloperblog.com/create-button-kotlin-programmatically/
        https://medium.com/@hissain.khan/parsing-with-google-gson-library-in-android-kotlin-7920e26f5520
        https://bezkoder.com/kotlin-android-read-json-file-assets-gson/
        https://stackoverflow.com/questions/1337424/android-spinner-get-the-selected-item-change-event
        https://android--code.blogspot.com/2018/03/android-kotlin-imageview-set-image.html?m=1

*/


public class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment)
        val appBarConfiguration = AppBarConfiguration(setOf( R.id.add_user_button, R.id.taking_orders, R.id.view_products))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        val mainLooper = Looper.getMainLooper()
        // kapcsolat ellenörzése
        val client = OkHttpClient().newBuilder()
            .build()
        val request: Request = Request.Builder()
            .url("http://10.1.1.13:3000/")
            .method("GET", null)
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(mainLooper).post {
                    Toast.makeText(
                        navView.context,
                        "Nem sikerül csatlakozni szerverhez\n" +
                                "Az alkalmazás automatikusan bezáródik",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Thread.sleep(3000)
                exitProcess(0)
                        }
            override fun onResponse(call: Call, response: Response) {
                Handler(mainLooper).post {
                    Toast.makeText(
                        navView.context,
                        "Sikerült csatlakozni a szerverhez",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        })

    }

fun  order(view: View){

}
}


