package com.example.networkapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var requestQueue: RequestQueue
    lateinit var titleTextView: TextView
    lateinit var descriptionTextView: TextView
    lateinit var numberEditText: EditText
    lateinit var showButton: Button
    lateinit var comicImageView: ImageView

    private val PREFS_NAME = "ComicPrefs"
    private val KEY_LAST_COMIC = "last_comic"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        requestQueue = Volley.newRequestQueue(this)

        titleTextView = findViewById<TextView>(R.id.comicTitleTextView)
        descriptionTextView = findViewById<TextView>(R.id.comicDescriptionTextView)
        numberEditText = findViewById<EditText>(R.id.comicNumberEditText)
        showButton = findViewById<Button>(R.id.showComicButton)
        comicImageView = findViewById<ImageView>(R.id.comicImageView)

        showButton.setOnClickListener {
            val id = numberEditText.text.toString()
            if (id.isNotEmpty()) {
                downloadComic(id)
            } else {
                Toast.makeText(this, "Please enter a comic ID", Toast.LENGTH_SHORT).show()
            }
        }
        loadSavedComic()
    }
    private fun downloadComic(comicId: String) {
        val url = "https://xkcd.com/$comicId/info.0.json"

        val request = JsonObjectRequest(url,
            { response ->
                showComic(response)
                saveComic(response) // TODO (2: Save when downloaded)
            },
            {
                // TODO (1: Fix any bugs - added error handling)
                Toast.makeText(this, "Error fetching comic. Check ID or connection.", Toast.LENGTH_SHORT).show()
            }
        )
        requestQueue.add(request)
    }
    private fun showComic(comicObject: JSONObject) {
        titleTextView.text = comicObject.getString("title")
        descriptionTextView.text = comicObject.getString("alt")
        Picasso.get().load(comicObject.getString("img")).into(comicImageView)
    }
    private fun saveComic(comicObject: JSONObject) {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString(KEY_LAST_COMIC, comicObject.toString())
            apply()
        }
    }
    private fun loadSavedComic() {
        val sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        val savedJson = sharedPreferences.getString(KEY_LAST_COMIC, null)

        if (savedJson != null) {
            try {
                val comicObject = JSONObject(savedJson)
                showComic(comicObject)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}