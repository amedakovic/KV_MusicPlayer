package hr.ferit.aleksamedakovic.orwma_kv

import android.Manifest

import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.SearchView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity(), SearchView.OnQueryTextListener{

   // private val READ_EXTERNAL_STORAGE : Int = 1


    companion object{var items: ArrayList<AudioData> = ArrayList()}

    lateinit var bottomNavView : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        bottomNavView = findViewById(R.id.bottomNavigationView)

        checkPermission()
        items = loadAudioFiles()
        val SongFragment = SongsFragment()
        val FavFragment = FavoritesFragment()
        FavFragment.loadFavSongs()
        setCurrentFragment(SongFragment)


         bottomNavView.setOnNavigationItemSelectedListener {
                 when (it.itemId) {
                     R.id.nav_bar_songs -> setCurrentFragment(SongFragment)
                     R.id.nav_bar_favorites -> setCurrentFragment(FavFragment)
                 }
                 true
         }


    }



    private fun setCurrentFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            replace(R.id.flFragment,fragment)
            commit()
        }
    }


     private fun checkPermission(){
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)==
                PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        }
    }



    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search, menu)
        val menuItem : MenuItem = menu!!.findItem(R.id.searchOption)
        val searchView : SearchView = menuItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        TODO("Not yet implemented")
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        var userInput : String = p0!!.toLowerCase()
        var files : ArrayList<AudioData> = ArrayList()
        for(song in items){
            if(song.name.toLowerCase().contains(userInput))
                files.add(song)
        }
        SongsFragment.AudioAdapter.update(files)
        return true
    }


    private fun  loadAudioFiles(): ArrayList<AudioData> {
        val tempArr: ArrayList<AudioData> = ArrayList()

        val uri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ARTIST, MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION)

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

