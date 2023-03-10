package com.practicum.playlistmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.RoundedCorner
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {
    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
    }

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(iTunesApi::class.java)
    private lateinit var inputText: EditText
    var savedText: String = ""


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, savedText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        savedText = savedInstanceState.getString(SEARCH_TEXT, "")
        inputText = findViewById(R.id.search_edit_text)
        inputText.setText(savedText)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        val clearButton = findViewById<ImageView>(R.id.clear_text)
        inputText = findViewById(R.id.search_edit_text)
        val image = findViewById<ImageView>(R.id.back_icon_search)
        val recycler = findViewById<RecyclerView>(R.id.recycler_view)
        val noResult = findViewById<FrameLayout>(R.id.iw_no_result_layout)
        val noConnection = findViewById<FrameLayout>(R.id.iw_no_connection_layout)
        val reloadButton = findViewById<Button>(R.id.bt_update)
        inputText.requestFocus()
        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            layoutManager = LinearLayoutManager(this@SearchActivity)
        }
        val trackList = ArrayList<Track>()
        val trackAdapter = TrackAdapter(trackList)
        recyclerView.adapter = trackAdapter

        image.setOnClickListener {
            finish()
        }
        clearButton.setOnClickListener {
            inputText.setText("")
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(clearButton.windowToken, 0)
            recycler.visibility = View.GONE
            noResult.visibility = View.GONE
            noConnection.visibility = View.GONE
        }
        fun clearButtonVisibility(s: CharSequence?): Int {
            return if (s.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        fun responseTrack(searchText: String) {
            if (searchText.isNotEmpty()) {
                iTunesService.search(searchText).enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        if (response.code() == 200) {
                            trackList.clear()
                            if (response.body()?.results?.isNotEmpty() == true) {
                                trackList.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                                recycler.visibility = View.VISIBLE
                                noResult.visibility = View.GONE
                                noConnection.visibility = View.GONE
                            }
                            if (trackList.isEmpty()) {
                                recycler.visibility = View.GONE
                                noResult.visibility = View.VISIBLE
                                noConnection.visibility = View.GONE
                            }
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        recycler.visibility = View.GONE
                        noResult.visibility = View.GONE
                        noConnection.visibility = View.VISIBLE
                        reloadButton.setOnClickListener {
                            responseTrack(savedText)
                        }
                    }

                })
            }
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearButton.visibility = clearButtonVisibility(p0)
                savedText = p0.toString()
                inputText.setOnEditorActionListener { _, actionId, _ ->
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        responseTrack(savedText)
                        true
                    }
                    false
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        }

        inputText.addTextChangedListener(textWatcher)
    }

}