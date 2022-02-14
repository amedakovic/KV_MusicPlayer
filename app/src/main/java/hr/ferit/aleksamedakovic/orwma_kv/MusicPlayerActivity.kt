package hr.ferit.aleksamedakovic.orwma_kv

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import com.google.firebase.database.*

class MusicPlayerActivity : AppCompatActivity() {

    var rootNode : FirebaseDatabase = FirebaseDatabase.getInstance("https://kvmusicplayer-f33ad-default-rtdb.europe-west1.firebasedatabase.app")
    var reference : DatabaseReference = rootNode.getReference("Songs")

    private lateinit var songName : TextView
    private lateinit var artistName : TextView
    private lateinit var durationPlayed : TextView
    private lateinit var durationTotal : TextView
    private lateinit var coverArt : ImageView
    private lateinit var playPauseBtn: ImageView
    private lateinit var playNext: ImageView
    private lateinit var playPrevious: ImageView
    private lateinit var backBtn : ImageView
    private lateinit var seekBar : SeekBar
    private var position: Int = -1
    private lateinit var uri : Uri
    private var songList: List<AudioData> = ArrayList()
    private var handler: Handler = Handler()
    companion object{ var mediaPlayer : MediaPlayer? = null}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_player)

        initViews()
        getIntentMethod()

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                if( mediaPlayer != null && p2)
                    mediaPlayer?.seekTo(p1 * 1000)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
        this@MusicPlayerActivity.runOnUiThread(object : Runnable{
            override fun run() {
                if(mediaPlayer != null){
                    var currentPostion: Int = mediaPlayer!!.currentPosition/1000
                    seekBar.setProgress(currentPostion)
                    durationPlayed.text = (formattedTime(currentPostion))
                }
                handler.postDelayed(this, 1000)
            }
        })


        playPauseBtn.setOnClickListener{
            if(mediaPlayer?.isPlaying == true) {
                playPauseBtn.setImageResource(R.drawable.ic_play_arrow)
                mediaPlayer?.pause()
            }else{
                playPauseBtn.setImageResource(R.drawable.ic_pause)
                mediaPlayer?.start()
            }
        }

        playNext.setOnClickListener {
            if(position != MainActivity.items.size){
                position += 1
                uri = Uri.parse(songList.get(position).path)
                playMusicPlayer()
            }
        }

        playPrevious.setOnClickListener {
            if(position != 0){
                position -= 1
                uri = Uri.parse(songList.get(position).path)
                playMusicPlayer()
            }
        }

        backBtn.setOnClickListener {
            finish()
        }


    }

    private fun formattedTime(currentPostion: Int):String{
        var totalout: String
        var totalWithZero: String
        var seconds: String = (currentPostion % 60).toString()
        var minutes: String = (currentPostion / 60).toString()
        totalout = "${minutes}:${seconds}"
        totalWithZero = "${minutes}:0${seconds}"
        if(seconds.length == 1)return totalWithZero

        return  totalout

    }
    
    private fun getIntentMethod(){
        position = intent.getIntExtra("position", -1)
        songList = MainActivity.items
        if(songList != null){
            uri = Uri.parse(songList.get(position).path)
        }
        playMusicPlayer()

    }

    private fun initViews(){
        songName = findViewById(R.id.MP_song_name)
        artistName = findViewById(R.id.MP_artist_name)
        durationPlayed = findViewById(R.id.MP_duration_played)
        durationTotal = findViewById(R.id.MP_duration_total)
        coverArt = findViewById(R.id.MP_cover_art)
        playPauseBtn = findViewById(R.id.MP_play_pause)
        seekBar = findViewById(R.id.MP_seek_bar)
        playNext = findViewById(R.id.MP_skip_next)
        playPrevious = findViewById(R.id.MP_skip_previous)
        backBtn = findViewById(R.id.MP_back_btn)
    }

    private  fun playMusicPlayer(){
        playPauseBtn.setImageResource(R.drawable.ic_pause)
        if(mediaPlayer?.isPlaying == true){
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = MediaPlayer.create(this, uri)
            mediaPlayer?.start()
        }
        else{
            mediaPlayer = MediaPlayer.create(this, uri)
            mediaPlayer?.start()
        }
        seekBar.max = mediaPlayer!!.duration / 1000
        durationTotal.text = formattedTime(seekBar.max)

        sendToFirebase(songList.get(position).name, songList.get(position).artist,"1",songList.get(position).albumImagePath.toString())

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
        coverArt.setImageURI(songList.get(position).albumImagePath)
        songName.text = songList.get(position).name
        artistName.text = songList.get(position).artist
    }
}


