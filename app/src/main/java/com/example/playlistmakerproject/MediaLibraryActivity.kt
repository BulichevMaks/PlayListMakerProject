package com.example.playlistmakerproject


import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView

class MediaLibraryActivity : AppCompatActivity() {
    private var relativeLayout: RelativeLayout? = null

    private var lastButtonIndex = 0
    private var lastButtonId: Int = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_media_library)


    }
}
class NewsViewHolder(parentView: View) : RecyclerView.ViewHolder(parentView) {

    private val sourceName: TextView = parentView.findViewById(R.id.sourceName)
    private val text: TextView = parentView.findViewById(R.id.text)

    fun bind(model: News) {
        sourceName.text = model.name
        text.text = model.content
    }
}
data class News(val id: Int, val name: String, val content: String)

class NewsAdapter(
    private val news: List<News>,
    onItemClicked: () -> Unit
) : RecyclerView.Adapter<NewsViewHolder> () {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.news_view, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        holder.bind(news[position])
    }

    override fun getItemCount() = news.size
}