package com.example.worldskills_2021.ui.dashboard

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.example.worldskills_2021.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.fragment_dashboard.*
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import kotlin.system.exitProcess


class DashboardFragment : Fragment() {



    private lateinit var dashboardViewModel: DashboardViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? { dashboardViewModel = ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val mainLooper = Looper.getMainLooper()
        var customersClassList: ArrayList<Customer> = ArrayList<Customer>()
        var productsClassList: ArrayList<Product> = ArrayList<Product>()
        val orderedProductList = ArrayList<OrderedProduct>()

        //Vásárlók betöltése
        val client = OkHttpClient().newBuilder()
            .build()
        val request: Request = Request.Builder()
            .url("http://10.1.1.13:3000/customers")
            .method("GET", null)
            .addHeader("Content-Type", "application/x-www-form-urlencoded")
            .build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Handler(mainLooper).post {
                    Toast.makeText(
                        root.context,
                        "Nem sikerül csatlakozni a serverhez \n" +
                                "Az alkalmazás automatikusan bezáródik ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                Thread.sleep(3000)
                exitProcess(0)   }
            override fun onResponse(call: Call, response: Response) {
                val xd = OwnFunctions().responseBodyToString(response)
                Handler(mainLooper).post {
                    val gson = Gson()
                    val listCustomerType = object : TypeToken<List<Customer>>() {}.type
                    customersClassList = gson.fromJson(xd, listCustomerType)
                    var nameList = ArrayList<String>()
                    for (o in customersClassList) {
                        nameList.add(o.name)
                    }
                    val spinner = root.findViewById(R.id.names) as Spinner
                    val adapter = ArrayAdapter(
                        root.context,
                        android.R.layout.simple_spinner_item,
                        nameList
                    )
                    spinner.adapter = adapter
                }
            }
        })

        // termékek betöltése
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
                        productList.add(o.name + " Ára: " + o.price + " pénz/db")
                    }
                    val spinner = root.findViewById(R.id.order_products) as Spinner
                    val adapter = ArrayAdapter(
                        root.context,
                        android.R.layout.simple_spinner_item,
                        productList
                    )
                    spinner.adapter = adapter
                }
            }
        })

        // termék felvétele
        val addProductButton = root.findViewById(R.id.add_product_button) as FloatingActionButton
        addProductButton.setOnClickListener(
            View.OnClickListener {
                if (product_piece.text?.isEmpty()!!) {
                    Toast.makeText(
                    root.context,
                    "Nem álítotad be a darabszámot",
                    Toast.LENGTH_SHORT
                ).show()
                }
                else {
                    orderedProductList.add(
                        OrderedProduct(
                            customersClassList[order_products.selectedItemId.toInt()].id,
                            product_piece.text.toString().toInt()
                        )
                    )
                    product_piece.setText("")
                }
            })

        // rendelés felvétele
        val addOrderButton = root.findViewById(R.id.add_order_button) as FloatingActionButton
        addOrderButton.setOnClickListener(
            View.OnClickListener {
                if (orderedProductList.isEmpty()) {
                    Toast.makeText(
                        root.context,
                        "Nem rögzitetél egyetlen terméket sem ",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                else {
                    val client = OkHttpClient().newBuilder()
                        .build()
                    val mediaType = "application/json".toMediaTypeOrNull()
                    val body: RequestBody = RequestBody.create(
                        mediaType,
                        Order(
                            customersClassList[names.selectedItemId.toInt()].id,
                            orderedProductList
                        ).toString())
                    val request: Request = Request.Builder()
                        .url("http://10.1.1.13:3000/orders")
                        .method("POST", body)
                        .addHeader("Content-Type", "application/json")
                        .build()
                    client.newCall(request).enqueue(object : Callback {
                        override fun onFailure(call: Call, e: IOException) {
                            Handler(mainLooper).post {
                                Toast.makeText(
                                    root.context,
                                    "Hiba történt a rögzittés közben",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        override fun onResponse(call: Call, response: Response) {
                            Handler(mainLooper).post {
                                Toast.makeText(
                                    root.context,
                                    "Sikeres rendelés rögzités",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }}
                    })
                }
            })
        return root
    }
}
