package hr.ferit.aleksamedakovic.orwma_kv

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*


class FavoritesFragment : Fragment() {


    var reference : DatabaseReference = FirebaseDatabase.
    getInstance("https://kvmusicplayer-f33ad-default-rtdb.europe-west1.firebasedatabase.app").
    getReference("Songs")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                MainActivity.FavAdapter.deleteItems()
                for(tempSnap in snapshot.children){
                    val data = tempSnap.getValue(FavoritesData::class.java)
                    MainActivity.FavAdapter.AddItem(data!!)
                }
                /*val artist = snapshot.child("artist").value.toString()
                val name = snapshot.child("name").value.toString()
                val nbPlayed = snapshot.child("nbPlayed").value.toString()
                val ArtPath = snapshot.child("ArtPath").value.toString()
                MainActivity.FavAdapter.AddItem(FavoritesData(ArtPath, nbPlayed, artist, name))*/
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        var view : View = inflater.inflate(R.layout.fragment_favorites, container, false)
        var recyclerView = view.findViewById<RecyclerView>(R.id.recyclerViewFavorites)
        recyclerView.adapter = MainActivity.FavAdapter
        recyclerView.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        return view
    }


    }
