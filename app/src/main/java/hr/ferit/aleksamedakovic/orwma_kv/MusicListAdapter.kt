package hr.ferit.aleksamedakovic.orwma_kv

import android.content.Intent
import android.provider.MediaStore

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide


class MusicListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    lateinit var items: List<AudioData>



    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            RecyclerView.ViewHolder {
       // items.find { it.path.equals("aaa")}?.path = "aaa"
        return AudioViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.music_list_layout, parent,
                false)
        )
    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder,
                                  position: Int) {

        when(holder) {
            is  AudioViewHolder-> {
                holder.bind(items[position])
                holder.itemView.setOnClickListener{
                    val intent = Intent(holder.itemView.context, MusicPlayerActivity::class.java)
                    intent.putExtra("position", position)
                    //intent.putExtra("SongList", items)
                    holder.itemView.context.startActivity(intent)
                }
            }
        }
        }

    override fun getItemCount(): Int {
        return items.size
    }
    fun postItemsList(data: ArrayList<AudioData>) {
        items = data
    }


    class AudioViewHolder constructor(
        itemView: View
    ): RecyclerView.ViewHolder(itemView){
        private val SongName : TextView = itemView.findViewById(R.id.SongName)
        private val ArtistName : TextView = itemView.findViewById(R.id.ArtistName)
        private val AlbumName : TextView = itemView.findViewById(R.id.AlbmuName)
        private val AlbumImage: ImageView = itemView.findViewById(R.id.AlbumImage)



        fun bind(audioData: AudioData){
            Glide
                .with(itemView.context)
                .load(audioData.albumImagePath)
                .into(AlbumImage)
            SongName.text = audioData.name
            ArtistName.text = audioData.artist
            AlbumName.text = audioData.album
        }
    }

    fun update(list : ArrayList<AudioData>){
        items = ArrayList()
        (items as MutableList<AudioData>).addAll(list)
        notifyDataSetChanged()
    }
}