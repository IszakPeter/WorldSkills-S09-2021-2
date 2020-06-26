package com.example.worldskills_2021.ui.home

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.worldskills_2021.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.fragment_home.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException


class HomeFragment : Fragment() {
    private lateinit var homeViewModel: HomeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val mainLooper = Looper.getMainLooper()
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_home, container, false)
        val addUserButton = root.findViewById(R.id.add_user_button) as FloatingActionButton
        addUserButton.setOnClickListener(
            View.OnClickListener {
                if (userName.text?.isEmpty()!! || emailAddress.text?.isEmpty()!! || phoneNumber.text?.isEmpty()!!){
                    Handler(mainLooper).post{
                        Toast.makeText(
                            root.context,
                            "Hiányzik néhány ügyfél adat",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
                else {
                    val client = OkHttpClient().newBuilder()
                        .build()
                    val mediaType: MediaType? = "application/x-www-form-urlencoded".toMediaTypeOrNull()
                    val body: RequestBody = RequestBody.create(
                        mediaType,
                        "name=" + userName.text + "&email=" + emailAddress.text + "&phone=" + phoneNumber.text
                    )
                    val request: Request = Request.Builder()
                        .url("http://10.1.1.13:3000/customers")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded")
                        .build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Handler(mainLooper).post {
                                Handler(mainLooper).post{
                                    Toast.makeText(
                                        root.context,
                                        "Sikertelen ügyfél felvétel",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        override fun onResponse(call: Call, response: Response) {
                            Handler(mainLooper).post {
                                Handler(mainLooper).post{
                                    userName.setText("")
                                    emailAddress.setText("")
                                    phoneNumber.setText("")
                                    Toast.makeText(
                                        root.context,
                                        "Sikeres ügyfél felvétel",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    })
                }
        })
        return root
    }
}



