package com.example.playlistmakerproject


import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import java.text.SimpleDateFormat
import java.util.*


class TrackViewHolder(private val item: View) : RecyclerView.ViewHolder(item) {

    private val tvTrackName: TextView = item.findViewById(R.id.trackName)
    private val tvArtistName: TextView = item.findViewById(R.id.artistName)
    private val tvTrackTime: TextView = item.findViewById(R.id.trackTime)
    private val tvTrackImageScr: ImageView = item.findViewById(R.id.trackImageScr)

    fun bind(model: Track) {
        tvTrackName.text = model.trackName
        tvArtistName.text = model.artistName.trimEnd()
        tvTrackTime.text = model.trackTimeMillis?.let {
            SimpleDateFormat("mm:ss", Locale.getDefault()).format(it.toLong())
        }

        Glide.with(item.context)
            .load(model.artworkUrl100)
            .placeholder(R.drawable.ic_holder)
            .centerCrop()
            .transform(RoundedCorners(itemView.resources.getDimensionPixelSize(R.dimen.track_icon_radius)))
            .into(tvTrackImageScr)
    }
}