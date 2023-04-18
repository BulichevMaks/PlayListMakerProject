package com.example.playlistmakerproject

import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmakerproject.SearchActivity.Companion.SEL_ITEM
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class PlayerViewActivity : AppCompatActivity() {

    private lateinit var imageAlbum: ImageView
    private lateinit var buttonBack: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var duration: TextView
    private lateinit var collectionName: TextView
    private lateinit var releaseDate: TextView
    private lateinit var primaryGenreName: TextView
    private lateinit var country: TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.player_view)

        imageAlbum = findViewById(R.id.imageAlbum)
        buttonBack = findViewById(R.id.buttonBack)
        trackName = findViewById(R.id.trackName)
        artistName = findViewById(R.id.artistName)
        duration = findViewById(R.id.duration)
        collectionName = findViewById(R.id.collectionName)
        releaseDate = findViewById(R.id.releaseDate)
        primaryGenreName = findViewById(R.id.primaryGenreName)
        country = findViewById(R.id.country)

        trackName.text = intent.getStringExtra("trackName")
        artistName.text = intent.getStringExtra("artistName")
        duration.text = intent.getStringExtra("duration")
        collectionName.text = intent.getStringExtra("collectionName")

        val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
        val date = intent.getStringExtra("releaseDate")?.let { dateFormat.parse(it) }
        val calendar = Calendar.getInstance().apply {
            if (date != null) {
                time = date
            }
        }
        releaseDate.text = calendar.get(Calendar.YEAR).toString()
        primaryGenreName.text = intent.getStringExtra("primaryGenreName")
        country.text = intent.getStringExtra("country")

        Glide.with(this)
            .load(intent.getStringExtra(SEL_ITEM)?.replaceAfterLast('/',"512x512bb.jpg")!!)
            .placeholder(R.drawable.plaseholder_player)
            .error(R.drawable.plaseholder_player)
            .transform(RoundedCorners(dpToPx(8))) // как правильно указать значение вот тут?
            .into(imageAlbum)

        buttonBack.setOnClickListener {
            finish()
        }
    }
}

fun dpToPx(dp: Int): Int {
    val density = Resources.getSystem().displayMetrics.density
    return (dp * density).roundToInt()
}
