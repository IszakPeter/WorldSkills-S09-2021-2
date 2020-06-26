package com.example.worldskills_2021

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.AsyncTask
import android.widget.ImageView
import android.widget.Toast
import okhttp3.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL

class OwnFunctions  {
    public final fun responseBodyToString(response: Response): String {
        val bufferedReader = BufferedReader(InputStreamReader(response.body!!.byteStream()))
        val stringBuilder = StringBuilder()
        bufferedReader.forEachLine { stringBuilder.append(it) }
        return stringBuilder.toString()
    }
}

public data class Customer(
        val id :Int,
        val name: String,
        val email: String,
        val phone: String
){}
public data class Product (
    val name: String,
    val  description: String,
    val plaintext: String,
    val id: Int,
    val icon: String,
    val price: Int,
    val purchasable: Boolean
){}
public  data class Order(
    val customerId :Int,
    val products :ArrayList<OrderedProduct>
){
    override fun toString(): String {
        var index=0
       var s = "{\n \"customerId\": $customerId, \n \"products\": [\n"
        for (obj in products){
            s+=obj.toString()
            if(index<products.size-1)  s+=","
        index++
        }
        s+="\n]\n}"
        return s
    }

}
public  data class OrderedProduct(
    val id :Int,
    val cnt: Int
){
    override fun toString(): String {
        return "{\n \"id\": $id,\n \"cnt\": $cnt \n }";
    }
}

public class DownLoadImageTask(internal val imageView: ImageView) : AsyncTask<String, Void, Bitmap?>() {
    override fun doInBackground(vararg urls: String): Bitmap? {
        val urlOfImage = urls[0]
        return try {
            val inputStream = URL(urlOfImage).openStream()
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) { // Catch the download exception
            e.printStackTrace()
            null
        }
    }
    override fun onPostExecute(result: Bitmap?) {
        if(result!=null){
            // Display the downloaded image into image view
           // Toast.makeText(imageView.context,"download success", Toast.LENGTH_SHORT).show()
            imageView.setImageBitmap(result)
        }else{
            Toast.makeText(imageView.context,"Error downloading", Toast.LENGTH_SHORT).show()
        }
    }
}
