package com.example.dexel.autoplayer.views


import androidx.databinding.DataBindingUtil
import android.support.v4.media.MediaBrowserCompat.MediaItem
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.example.dexel.autoplayer.R
import com.example.dexel.autoplayer.databinding.FragmentSongBinding
import com.example.dexel.autoplayer.viewModels.MusicServiceViewModel
import com.example.dexel.autoplayer.viewModels.ViewModelFactory
import javax.inject.Inject


class MySongRecyclerViewAdapter(private val listener: OnClickListener):
        RecyclerView.Adapter<MySongRecyclerViewAdapter.ViewHolder>() {

    interface OnClickListener {
        fun onClick(mediaItem: MediaItem)
    }

    private var songs: List<MediaItem> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: FragmentSongBinding = DataBindingUtil
                .inflate(LayoutInflater.from(parent.context), R.layout.fragment_song, parent, false)
        binding.listener = listener
        return ViewHolder(binding)
    }

    fun setValues(values: List<MediaItem>) {
        Log.d(TAG, values.toString())
        songs = values
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = songs[position]
        holder.binding.data = item
        holder.itemView.findViewById<ImageView>(R.id.image_item)
                .setImageBitmap(item.description.iconBitmap)
    }

    override fun getItemCount(): Int = songs.size

    inner class ViewHolder(val binding: FragmentSongBinding): RecyclerView.ViewHolder(binding.root)

    companion object {
        private val TAG = MySongRecyclerViewAdapter::class.simpleName
    }
}
