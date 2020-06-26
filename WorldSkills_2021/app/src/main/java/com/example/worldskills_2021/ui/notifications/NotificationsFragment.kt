package com.example.worldskills_2021.ui.notifications

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.worldskills_2021.DownLoadImageTask
import com.example.worldskills_2021.OwnFunctions
import com.example.worldskills_2021.Product
import com.example.worldskills_2021.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_notifications.*

import okhttp3.*
import java.io.IOException
import kotlin.system.exitProcess


class NotificationsFragment : Fragment() {

    var productsClassList: ArrayList<Product> = ArrayList<Product>()
    private lateinit var notificationsViewModel: NotificationsViewModel

    @SuppressLint("ResourceType")
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        notificationsViewModel =
                ViewModelProviders.of(this).get(NotificationsViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notifications, container, false)
        val mainLooper = Looper.getMainLooper()
        val spinner = root.findViewById(R.id.look_products) as Spinner
        val clientProd = OkHttpClient().newBuilder()
            .build()
        val requestProd: Request = Request.Builder()
            .url("http://10.1.1.13:3000/products")
            .method("GET", null)
            .build()
        clientProd.newCall(requestProd).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(mainLooper).post {
                    Toast.makeText(
                        root.context,
                        "Nem sikerül csatlakozni a serverhez\n" +
                                "Az alkalmazás automatikusan bezáródik",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Thread.sleep(3000)
                exitProcess(0)
            }
            override fun onResponse(call: Call, response: Response) {
                val xd = OwnFunctions().responseBodyToString(response)
                Handler(mainLooper).post {
                    val gson = Gson()
                    val listPersonType = object : TypeToken<List<Product>>() {}.type
                    productsClassList = gson.fromJson(xd, listPersonType)
                    var productList = ArrayList<String>()
                    for (o in productsClassList) {
                        productList.add(o.name )
                    }
                    val adapter = ArrayAdapter(
                        root.context,
                        android.R.layout.simple_spinner_item,
                        productList
                    )
                    spinner.adapter = adapter
                }
            }
        })
        val image  = root.findViewById(R.id.imageView) as ImageView
        spinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long) {
                val product = productsClassList[look_products.selectedItemId.toInt()]
                val url: String = product.icon
                name.text = product.name
                description.text ="Leírás:\n${product.description.replace("<br><br>","\n\n")
                    .replace("<groupLimit>","")
                    .replace("</groupLimit>","")
                    .replace("<unique>","")
                    .replace("</unique>","")
                    .replace("<stats>","")
                    .replace("</stats>","")
                    .replace("<mana>","")
                    .replace("</mana>","")
                    .replace(":",":\n")
                    .replace("- ",":\n")
                }"
                plaintext.text ="${product.plaintext}"
                purchasable.text = "A termék ára: ${product.price} pénz"
                if (product.purchasable) price.text = "Vásárolható"
                else price.text = "Nincs Készleten"
                println("\n $url")
                var ico = view!!.findViewById(R.id.imageView) as ImageView
                DownLoadImageTask(ico).execute(url)
            }
            override fun onNothingSelected(parentView: AdapterView<*>?) {
            }
        }

         return root
    }
}

