package hr.ferit.aleksamedakovic.orwma_kv

import android.content.ContentUris
import android.net.Uri

class AudioData (_name:String, _path:String, _artist: String, _album: String, _albumId: Long, _duration: Int) {
    var name: String = "defaultValue"
    var path: String = "defaultPath"
    var artist: String = "defaultValue"
    var album: String = "defaultValue"
    var albumImagePath: Uri = Uri.parse( "content://media/external/audio/albumart")
    var duration : Int = -1

    init{
        this.name = _name
        this.path = _path
        this.album = _album
        this.artist = _artist
        this.albumImagePath = ContentUris.withAppendedId(albumImagePath, _albumId)
        duration = _duration
    }
}