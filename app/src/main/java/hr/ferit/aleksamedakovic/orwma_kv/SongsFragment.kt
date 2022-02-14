package hr.ferit.aleksamedakovic.orwma_kv

import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SongsFragment : Fragment() {
    companion object{val AudioAdapter = MusicListAdapter()}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        var view : View = inflater.inflate(R.layout.fragment_songs, container, false)
        var recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)

        recyclerView.adapter = AudioAdapter
        AudioAdapter.postItemsList(MainActivity.items)
        recyclerView.layoutManager = LinearLayoutManager(this.context, RecyclerView.VERTICAL, false)
        return view


    }



}