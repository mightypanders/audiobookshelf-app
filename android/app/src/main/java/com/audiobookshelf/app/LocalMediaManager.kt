package com.audiobookshelf.app

import android.Manifest
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.database.Cursor
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.media.MediaMetadataCompat
import android.util.Log
import androidx.annotation.AnyRes
import androidx.core.content.ContextCompat
import androidx.core.net.toFile
import androidx.core.net.toUri
import com.bumptech.glide.Glide
import java.io.File
import java.io.IOException


class LocalMediaManager {
  private var ctx: Context
  val tag = "LocalAudioManager"

  constructor(ctx: Context) {
    this.ctx = ctx
  }

  data class LocalAudio(val uri: Uri,
                        val id: String,
                        val name: String,
                        val duration: Int,
                        val size: Int,
                        val coverUri: Uri?
  ) {

    fun toMediaMetadata(): MediaMetadataCompat {
      return MediaMetadataCompat.Builder().apply {
        putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, id)
        putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, name)
        putString(MediaMetadataCompat.METADATA_KEY_TITLE, name)

        if (coverUri != null) {
          putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, coverUri.toString())
        }
      }.build()
    }
  }
  val localAudioFiles = mutableListOf<LocalAudio>()

  /**
   * get uri to drawable or any other resource type if u wish
   * @param context - context
   * @param drawableId - drawable res id
   * @return - uri
   */
  fun getUriToDrawable(context: Context,
                       @AnyRes drawableId: Int): Uri {
    return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
      + "://" + context.resources.getResourcePackageName(drawableId)
      + '/' + context.resources.getResourceTypeName(drawableId)
      + '/' + context.resources.getResourceEntryName(drawableId))
  }

  fun loadLocalAudio() {
    localAudioFiles.clear()

    localAudioFiles += LocalAudio(Uri.parse("asset:///public/samples/Anthem/AnthemSample.m4b"), "anthem_sample", "Anthem", 60000, 10000, getUriToDrawable(ctx, R.drawable.exo_icon_localaudio))
    localAudioFiles += LocalAudio(Uri.parse("asset:///public/samples/Legend of Sleepy Hollow/LegendOfSleepyHollowSample.m4b"), "sleepy_hollow", "Legend of Sleepy Hollow", 60000, 10000, getUriToDrawable(ctx, R.drawable.exo_icon_localaudio))

    // TODO: No longer reading in local audio files - just use samples
//    if (ContextCompat.checkSelfPermission(ctx, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//      Log.e(tag, "Permission not granted to read from external storage")
//      return
//    }
//
//    val collection =
//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//        MediaStore.Audio.Media.getContentUri(
//          MediaStore.VOLUME_EXTERNAL
//        )
//      } else {
//        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//      }
//
//    val proj = arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media.SIZE)
//    val audioCursor: Cursor? = ctx.contentResolver.query(collection, proj, null, null, null)
//
//    audioCursor?.use { cursor ->
//      // Cache column indices.
//      val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
//      val nameColumn =
//        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)
//      val durationColumn =
//        cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
//      val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
//
//      while (cursor.moveToNext()) {
//        // Get values of columns for a given video.
//        val id = cursor.getLong(idColumn)
//        val name = cursor.getString(nameColumn)
//        val duration = cursor.getInt(durationColumn)
//        val size = cursor.getInt(sizeColumn)
//
//        val contentUri: Uri = ContentUris.withAppendedId(
//          MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
//          id
//        )
//        Log.d(tag, "Found local audio file $name")
//       localAudioFiles += LocalAudio(contentUri, id.toString(), name, duration, size, null)
//      }
//    }
//
//    Log.d(tag, "${localAudioFiles.size} Local Audio Files found")
  }
}
