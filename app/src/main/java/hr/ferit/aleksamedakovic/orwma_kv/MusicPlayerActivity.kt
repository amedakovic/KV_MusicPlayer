package hr.ferit.aleksamedakovic.orwma_kv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.content.Intent
import android.media.Image
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.provider.ContactsContract
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import java.lang.Exception

class MusicPlayerActivity : AppCompatActivity() {

    var rootNode : FirebaseDatabase = FirebaseDatabase.getInstance("https://kvmusicplayer-f33ad-default-rtdb.europe-west1.firebasedatabase.app")
    var reference : DatabaseReference = rootNode.getReference("Songs")

    lateinit var song_name : TextView
    lateinit var artist_name : TextView
    lateinit var duration_played : TextView
    lateinit var duration_total : TextView
    lateinit var cover_art : ImageView
    lateinit var play_pause_btn: ImageView
    lateinit var play_next: ImageView
    lateinit var play_previous: ImageView
    lateinit var backBtn : ImageView
    lateinit var seek_bar : SeekBar
    var position: Int = -1
    lateinit var uri : Uri
    private var song_list: List<AudioData> = ArrayList()
    private var handler: Handler = Handler()
    companion object{ var media_player : MediaPlayer? = null}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)

        initViews()
        getIntentMethod()

        seek_bar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if( media_player != null && p2)
                    media_player?.seekTo(p1 * 1000)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
        this@MusicPlayerActivity.runOnUiThread(object : Runnable{
            override fun run() {
                if(media_player != null){
                    var currentPostion: Int = media_player!!.currentPosition/1000
                    seek_bar.setProgress(currentPostion)
                    duration_played.text = (formattedTime(currentPostion))
                }
                handler.postDelayed(this, 1000)
            }
        })


        play_pause_btn.setOnClickListener{
            if(media_player?.isPlaying == true) {
                play_pause_btn.setImageResource(R.drawable.ic_play_arrow)
                media_player?.pause()
            }else{
                play_pause_btn.setImageResource(R.drawable.ic_pause)
                media_player?.start()
            }
        }

        play_next.setOnClickListener {
            if(position != MusicListAdapter.items.size){
                position += 1
                uri = Uri.parse(song_list.get(position).path)
                playMusicPlayer()
            }
        }

        play_previous.setOnClickListener {
            if(position != 0){
                position -= 1
                uri = Uri.parse(song_list.get(position).path)
                playMusicPlayer()
            }
        }

        backBtn.setOnClickListener {
            finish()
        }


    }

    private fun formattedTime(currentPostion: Int):String{
        var totalout: String = ""
        var totalWithZero: String = ""
        var seconds: String = (currentPostion % 60).toString()
        var minutes: String = (currentPostion / 60).toString()
        totalout = "${minutes}:${seconds}"
        totalWithZero = "${minutes}:0${seconds}"
        if(seconds.length == 1)return totalWithZero

        return  totalout

    }
    
    private fun getIntentMethod(){
        position = intent.getIntExtra("position", -1)
        song_list = MusicListAdapter.items
        if(song_list != null){
            uri = Uri.parse(song_list.get(position).path)
        }
        playMusicPlayer()

    }

    private fun initViews(){
        song_name = findViewById(R.id.MP_song_name)
        artist_name = findViewById(R.id.MP_artist_name)
        duration_played = findViewById(R.id.MP_duration_played)
        duration_total = findViewById(R.id.MP_duration_total)
        cover_art = findViewById(R.id.MP_cover_art)
        play_pause_btn = findViewById(R.id.MP_play_pause)
        seek_bar = findViewById(R.id.MP_seek_bar)
        play_next = findViewById(R.id.MP_skip_next)
        play_previous = findViewById(R.id.MP_skip_previous)
        backBtn = findViewById(R.id.MP_back_btn)
    }

    private  fun playMusicPlayer(){
        play_pause_btn.setImageResource(R.drawable.ic_pause)
        if(media_player?.isPlaying == true){
            media_player?.stop()
            media_player?.release()
            media_player = MediaPlayer.create(this, uri)
            media_player?.start()
        }
        else{
            media_player = MediaPlayer.create(this, uri)
            media_player?.start()
        }
        seek_bar.max = media_player!!.duration / 1000
        duration_total.text = formattedTime(seek_bar.max)

        sendToFirebase(song_list.get(position).name, song_list.get(position).artist,"1",song_list.get(position).albumImagePath.toString())

        setSongData()

    }

    private fun sendData(name : String, artist : String, nbPlayed: String, songPath: String){
        reference.child(name + artist).child("ArtPath").setValue(songPath)
        reference.child(name + artist).child("nbPlayed").setValue(nbPlayed)
        reference.child(name + artist).child("artist").setValue(artist)
        reference.child(name + artist).child("name").setValue(name)
    }

    private fun sendToFirebase(name : String, artist : String, nbPlayed: String, songPath: String){
        reference.orderByKey().equalTo(name + artist).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onDataChange(dataSnapshot: DataSnapshot){
                if(dataSnapshot.exists()){
                    var newNbPlayed =  dataSnapshot.child(name + artist).child("nbPlayed").value.toString()
                    newNbPlayed = (newNbPlayed.toInt() + 1).toString()
                    sendData(name, artist, newNbPlayed, songPath)
                }
                else sendData(name, artist, nbPlayed, songPath)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


    private fun setSongData(){
        cover_art.setImageURI(song_list.get(position).albumImagePath)
        song_name.text = song_list.get(position).name
        artist_name.text = song_list.get(position).artist
    }
}


