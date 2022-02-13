package hr.ferit.aleksamedakovic.orwma_kv

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.*

class FavoritesListAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

        val items: ArrayList<FavoritesData> = ArrayList()


        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
                RecyclerView.ViewHolder {
            return FavoritesViewHolder(
                LayoutInflater.from(parent.context).inflate(R.layout.favorite_songs_layout, parent,
                    false)
            )
        }
        override fun onBindViewHolder(holder: RecyclerView.ViewHolder,
                                      position: Int) {

            when(holder) {
                is  FavoritesViewHolder-> {
                    holder.bind(items[position])

                }
            }
        }

        override fun getItemCount(): Int {
            return items.size
        }

        fun AddItem(data : FavoritesData){
            items.add(data)
        }

        fun deleteItems(){
            items.clear()
        }

        class FavoritesViewHolder constructor(
            itemView: View
        ): RecyclerView.ViewHolder(itemView){
            private val SongName : TextView = itemView.findViewById(R.id.SongNameFavorites)
            private val ArtistName : TextView = itemView.findViewById(R.id.ArtistNameFavorites)
            private val AlbumImage: ImageView = itemView.findViewById(R.id.AlbumImageFavorites)
            private val TimesPlayed: TextView = itemView.findViewById(R.id.textViewNbPlayed)


            fun bind(Data: FavoritesData){
                Glide
                    .with(itemView.context)
                    .load(Data.ArtPath)
                    .into(AlbumImage)
                SongName.text = Data.name
                ArtistName.text = Data.artist
                TimesPlayed.text = Data.nbPlayed
            }
        }
    }
