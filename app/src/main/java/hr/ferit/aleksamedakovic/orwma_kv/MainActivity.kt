package hr.ferit.aleksamedakovic.orwma_kv

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils.replace
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.util.*


class MainActivity : AppCompatActivity() {

   // private val READ_EXTERNAL_STORAGE : Int = 1

    companion object{
        lateinit var AudioAdapter: MusicListAdapter
        lateinit var FavAdapter : FavoritesListAdapter
    }

    lateinit var bottomNavView : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavView = findViewById(R.id.bottomNavigationView)

        checkPermission()
        FavAdapter = FavoritesListAdapter()
        AudioAdapter = MusicListAdapter()
        AudioAdapter.postItemsList(loadAudioFiles())
        val SongFragment = SongsFragment()
        val FavFragment = FavoritesFragment()

        setCurrentFragment(SongFragment)




         bottomNavView.setOnNavigationItemSelectedListener {
                 when (it.itemId) {
                     R.id.nav_bar_songs -> setCurrentFragment(SongFragment)
                     R.id.nav_bar_favorites -> setCurrentFragment(FavFragment)
                 }
                 true
         }

        //val recyclerView : RecyclerView = findViewById(R.id.recyclerView)
        //initView()

        //recyclerView.setAdapter(MusicListAdapter())
    }

   /* override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == READ_EXTERNAL_STORAGE){
            if(grantResults.size > 8 && grantResults[0] == PackageManager.PERMISSION_DENIED)
        }
    }*/

    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
    }
     /*fun initView() {
        findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = AudioAdapter
        }
    }*/

     private fun checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)==
                PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }

     private fun  loadAudioFiles(): ArrayList<AudioData> {
        val tempArr: ArrayList<AudioData> = ArrayList()

        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION)
      /*  val projection : Array<String> = {
            MediaStore.Audio.AudioColumns.TITLE
            MediaStore.Audio.AudioColumns.DATA

        };*/
        val cursor: Cursor? = this.contentResolver.query(uri, projection, null, null, null)
        cursor?.moveToFirst()
        if(cursor!=null){
            while (cursor.moveToNext()){
                tempArr.add(AudioData(cursor.getString(0), cursor.getString(1),cursor.getString(2), cursor.getString(3),
                    cursor.getLong(4), cursor.getInt(5)))
            }
        }
        cursor?.close()
        return tempArr
    }

}

