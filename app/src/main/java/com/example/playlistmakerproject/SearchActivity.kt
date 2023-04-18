package com.example.playlistmakerproject

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.os.Bundle
import android.os.Parcelable
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

const val LIST_KEY = "key_for_list"

class SearchActivity : AppCompatActivity() {

    private val movieBaseUrl = "https://itunes.apple.com"


    private val interceptor = HttpLoggingInterceptor()
    private val okHttpClient = OkHttpClient.Builder().addInterceptor(interceptor)
        .build()
    private val retrofit = Retrofit.Builder().client(okHttpClient)
        .baseUrl(movieBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val trackService = retrofit.create(TrackApi::class.java)
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayout: LinearLayout
    private lateinit var inputEditText: EditText
    private lateinit var clearButton: ImageView
    private lateinit var buttonBack: ImageView
    private lateinit var buttonRefresh: Button
    private lateinit var placeholder: TextView
    private lateinit var text: TextView
    private lateinit var clearHistoryButton: Button
    private lateinit var nothingFound: String
    private lateinit var errorMessage: String
    private var image = R.drawable.error_not_found_dark
    private var tracks: ArrayList<Track> = ArrayList()
    var historyTracks: ArrayList<Track> = ArrayList()
    private val trackAdapter = TrackAdapter(tracks)
    private var historyAdapter = HistoryAdapter(historyTracks)

    companion object {

        private var input = ""
        const val TEXT_CONTENTS = "TEXT_CONTENTS"
        const val TRACK_LIST = "TRACK_LIST"
        const val HISTORY_TRACK_LIST = "HISTORY_TRACK_LIST"
        const val STATE_PLACEHOLDER_VISIBILITY = "STATE_PLACEHOLDER_VISIBILITY"
        const val STATE_BUTTON_VISIBILITY = "STATE_BUTTON_VISIBILITY"
        const val ERROR_MESSAGE = "ERROR_MESSAGE"
        const val IMAGE = "IMAGE"
        const val SEL_ITEM = "SEL_ITEM"

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        interceptor.level = HttpLoggingInterceptor.Level.BODY
        nothingFound = resources.getString(R.string.nothing_found)
        errorMessage = resources.getString(R.string.error_message)
        recyclerView = findViewById(R.id.recyclerView)
        linearLayout = findViewById(R.id.container)
        inputEditText = findViewById(R.id.inputEditText)
        clearButton = findViewById(R.id.clearIcon)
        buttonBack = findViewById(R.id.buttonBack)
        buttonRefresh = findViewById(R.id.buttonRefresh)
        placeholder = findViewById(R.id.placeholder)
        text = findViewById(R.id.text)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
        placeholder.text = nothingFound
        val context = this
        val sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        historyTracks = readFromPref(sharedPreferences)!!
        historyAdapter = HistoryAdapter(historyTracks)

        savedInstanceState?.let {
            image = it.getInt(IMAGE)
            placeholder.setDrawableTop(image)
        }
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = trackAdapter


        clearButton.setOnClickListener {
            inputEditText.setText("")
            val view: View? = this.currentFocus
            if (view != null) {
                val inputMethodManager =
                    getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
                tracks.clear()
                trackAdapter.notifyDataSetChanged()
            }
        }
        buttonBack.setOnClickListener {
            finish()
        }
        inputEditText.setOnEditorActionListener { _, actionId, _ ->

            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }
        inputEditText.setOnFocusChangeListener { view, hasFocus ->
            showHistory(context, "")
        }
        buttonRefresh.setOnClickListener {
            search()
        }
        clearHistoryButton.setOnClickListener {
            historyTracks.clear()
            clearHistoryButton.visibility = View.GONE
            text.visibility = View.GONE
            historyAdapter.notifyDataSetChanged()
        }


        trackAdapter.setOnItemClickListener { position ->

            val items = historyTracks

            if (!items.contains(tracks[position])) {
                if (items.size < 10) {
                    items.add(tracks[position])
                } else {
                    items.removeAt(0)
                    items.add(tracks[position])
                }
            } else {
                items.remove(tracks[position])
                items.add(tracks[position])
            }
        }



        val searchTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                input = s.toString()
                clearButton.visibility = clearButtonVisibility(s)
                showHistory(context, s)
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }
        inputEditText.addTextChangedListener(searchTextWatcher)


    }



    override fun onStop() {
        super.onStop()
        val sharedPreferences = getSharedPreferences(PREFERENCES, MODE_PRIVATE)
        writeToPref(sharedPreferences, historyTracks)
    }

    fun showHistory(context: Context, s: CharSequence?) {
        if (inputEditText.hasFocus() && s?.isEmpty() == true && historyTracks.isNotEmpty()
        ) {
            placeholder.visibility = View.GONE
            buttonRefresh.visibility = View.GONE
            recyclerView.adapter = historyAdapter
            clearHistoryButton.visibility = View.VISIBLE
            text.visibility = View.VISIBLE
            recyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, true)
        } else {
            recyclerView.layoutManager =
                LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
            recyclerView.adapter = trackAdapter
            clearHistoryButton.visibility = View.GONE
            text.visibility = View.GONE
        }
    }

    private fun search() {
        if (input.isNotEmpty()) {
            trackService.search(input)
                .enqueue(object : Callback<TrackResponse> {
                    override fun onResponse(
                        call: Call<TrackResponse>,
                        response: Response<TrackResponse>
                    ) {
                        when (response.code()) {
                            200 -> {
                                if (response.body()?.results?.isNotEmpty() == true) {
                                    tracks.clear()
                                    tracks.addAll(response.body()?.results!!)
                                    trackAdapter.notifyDataSetChanged()
                                    showMessage("", Event.SUCCESS)
                                } else {
                                    showMessage(nothingFound, Event.NOTHING_FOUND)
                                }
                            }
                            404 -> showMessage(nothingFound, Event.NOTHING_FOUND)
                            401 -> showMessage("You are not authorisation", Event.ERROR)
                            else -> showMessage(errorMessage, Event.ERROR)
                        }
                    }

                    override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                        showMessage(errorMessage, Event.SERVER_ERROR)
                    }
                })
        }
    }

    private fun showMessage(text: String, event: Event) {
        placeholder = findViewById(R.id.placeholder)
        when (event) {
            Event.SUCCESS -> {
                placeholder.visibility = View.GONE
                buttonRefresh.visibility = View.GONE
            }
            Event.NOTHING_FOUND -> {
                tracks.clear()
                trackAdapter.notifyDataSetChanged()
                placeholder.text = text
                placeholder.visibility = View.VISIBLE
                buttonRefresh.visibility = View.GONE
                image = if (isDarkTheme()) {
                    placeholder.setDrawableTop(R.drawable.error_not_found_dark)
                    R.drawable.error_not_found_dark
                } else {
                    placeholder.setDrawableTop(R.drawable.error_not_found_light)
                    R.drawable.error_not_found_light
                }
            }
            Event.ERROR -> {
                tracks.clear()
                trackAdapter.notifyDataSetChanged()
                placeholder.text = text
            }
            Event.SERVER_ERROR -> {
                tracks.clear()
                trackAdapter.notifyDataSetChanged()
                placeholder.text = text
                placeholder.visibility = View.VISIBLE
                buttonRefresh.visibility = View.VISIBLE
                image = if (isDarkTheme()) {
                    placeholder.setDrawableTop(R.drawable.error_enternet_dark)
                    R.drawable.error_enternet_dark

                } else {
                    placeholder.setDrawableTop(R.drawable.error_enternet_light)
                    R.drawable.error_enternet_light
                }
            }
        }
    }

    private fun isDarkTheme(): Boolean {
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        return currentNightMode == Configuration.UI_MODE_NIGHT_YES
    }

    private fun TextView.setDrawableTop(iconId: Int) {
        val icon = this.context?.resources?.getDrawable(iconId)
        this.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null)
    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_CONTENTS, input)
        outState.putInt(STATE_PLACEHOLDER_VISIBILITY, placeholder.visibility)
        outState.putInt(STATE_BUTTON_VISIBILITY, buttonRefresh.visibility)
        outState.putInt(IMAGE, image)
        outState.putString(ERROR_MESSAGE, placeholder.text.toString())
        if (tracks.isNotEmpty()) {
            outState.putParcelableArrayList(
                TRACK_LIST,
                tracks as ArrayList<out Parcelable?>?
            )
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        input = savedInstanceState.getString(TEXT_CONTENTS, "")
        image = savedInstanceState.getInt(IMAGE)
        placeholder.text = savedInstanceState.getString(ERROR_MESSAGE, "")
        placeholder.visibility =
            savedInstanceState.getInt(STATE_PLACEHOLDER_VISIBILITY, View.INVISIBLE)
        buttonRefresh.visibility =
            savedInstanceState.getInt(STATE_BUTTON_VISIBILITY, View.INVISIBLE)
        if (tracks.isNotEmpty()) {
            tracks.addAll(savedInstanceState.getParcelableArrayList<Parcelable>(TRACK_LIST) as ArrayList<Track>)
        }
    }

    private fun writeToPref(sharedPreferences: SharedPreferences, user: ArrayList<Track>) {
        val json = Gson().toJson(user)
        sharedPreferences.edit()
            .putString(LIST_KEY, json)
            .apply()
    }

    private fun readFromPref(sharedPreferences: SharedPreferences): ArrayList<Track>? {
        val json = sharedPreferences.getString(LIST_KEY, null) ?: return arrayListOf()
        return Gson().fromJson(json, Array<Track>::class.java)?.let { ArrayList(it.toList()) }

    }
}