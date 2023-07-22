package com.example.apod

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import java.net.URL
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun customDate(view: View) {
        val cal = Calendar.getInstance()
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH)
        val day = cal.get(Calendar.DAY_OF_MONTH)

        // Maximum date allowed to be selected
        val maxCalendar = Calendar.getInstance()

        val datePicker = DatePickerDialog(this,
            DatePickerDialog.OnDateSetListener{ view, year, monthOfYear, dayOfMonth ->
                val selectedDate = Calendar.getInstance()
                selectedDate.set(year, monthOfYear, dayOfMonth)

                //Validate the date
                if(selectedDate.after(maxCalendar)) {
                    Toast.makeText(this, "Invalid date! Select a date that's not in the future.",
                        Toast.LENGTH_SHORT).show()
                }
                else {
                    fetchDateInfo(year, monthOfYear+1, dayOfMonth)
                }
            },
            year,
            month,
            day
        )
        datePicker.datePicker.maxDate = maxCalendar.timeInMillis
        datePicker.show()
    }

    private fun fetchDateInfo(year: Int, month: Int, day: Int) {
        val title = findViewById<TextView>(R.id.imageTitle)
        val copyright = findViewById<TextView>(R.id.imageCopyRight)
        val desc = findViewById<TextView>(R.id.imageDescription)
        val picture = findViewById<ImageView>(R.id.imageView)

        title.text = "Fetching"
        copyright.text = ""
        desc.text = ""
        picture.setImageResource(android.R.color.transparent)

        // APOD api key
        val url = "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY&date=$year-$month-$day"

        val queue = Volley.newRequestQueue(this)

        // World's largest variable initialization
        val jsonRequest = JsonObjectRequest(Request.Method.GET, url,
            null,
            Response.Listener{ response ->
                title.text = response.getString("title")
                desc.text = response.getString("explanation")

                // Check for copyright in the entry
                if(response.has("copyright")) {
                    copyright.text = response.getString("copyright")
                } else {
                    copyright.text = ""
                }

                // If the entry is a video type, then display a notice
                val mediaType = response.getString("media_type")
                if(mediaType == "video") {
                    desc.append("\n\n This APOD entry is a video, which isn't supported.")
                }
                else {
                    // Get the image url from the api
                    val imageUrl = response.getString("url")
                    try {
                        // Convert http links to https
                        val httpsUrl = URL(imageUrl.replace("http://", "https://"))
                        // Now display the image
                        Glide.with(this)
                            .load(httpsUrl)
                            .into(picture)
                    } catch (e: Exception) {
                        desc.text = "Error loading image: ${e.message}"
                    }
                }
            },
            Response.ErrorListener{ error ->
                // Check for errors (such as hitting the api request limit)
                if(error.networkResponse != null) {
                    when (error.networkResponse.statusCode) {
                        429 -> desc.text = "Request limit reached"
                        else -> desc.text = "Error: ${error.networkResponse.statusCode}"
                    }
                }
                desc.text = "That didn't work: ${error.localizedMessage}"
            })

        queue.add(jsonRequest)
    }


    fun fetchInfo(view: View) {
        val title = findViewById<TextView>(R.id.imageTitle)
        val copyright = findViewById<TextView>(R.id.imageCopyRight)
        val desc = findViewById<TextView>(R.id.imageDescription)
        val picture = findViewById<ImageView>(R.id.imageView)

        title.text = "Fetching"
        copyright.text = ""
        desc.text = ""
        picture.setImageResource(android.R.color.transparent)

        // APOD api key
        val url = "https://api.nasa.gov/planetary/apod?api_key=DEMO_KEY"

        val queue = Volley.newRequestQueue(this)

        // World's largest variable initialization
        val jsonRequest = JsonObjectRequest(Request.Method.GET, url,
            null,
            Response.Listener{ response ->
                title.text = response.getString("title")
                desc.text = response.getString("explanation")

                // Check for copyright in the entry
                if(response.has("copyright")) {
                    copyright.text = response.getString("copyright")
                } else {
                    copyright.text = ""
                }

                // If the entry is a video type, then display a notice
                val mediaType = response.getString("media_type")
                if(mediaType == "video") {
                    desc.append("\n\n This APOD entry is a video, which isn't supported.")
                }
                else {
                    // Get the image url from the api
                    val imageUrl = response.getString("url")
                    try {
                        // Convert http links to https
                        val httpsUrl = URL(imageUrl.replace("http://", "https://"))
                        // Now display the image
                        Glide.with(this)
                            .load(httpsUrl)
                            .into(picture)
                    } catch (e: Exception) {
                        desc.text = "Error loading image: ${e.message}"
                    }
                }
                             },
            Response.ErrorListener{ error ->
                // Check for errors (such as hitting the api request limit)
                if(error.networkResponse != null) {
                    when (error.networkResponse.statusCode) {
                        429 -> desc.text = "Request limit reached"
                        else -> desc.text = "Error: ${error.networkResponse.statusCode}"
                    }
                }
                desc.text = "That didn't work: ${error.localizedMessage}"
            })

        queue.add(jsonRequest)
    }
}
