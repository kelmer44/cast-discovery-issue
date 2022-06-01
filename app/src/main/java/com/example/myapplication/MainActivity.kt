package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.mediarouter.app.MediaRouteButton
import androidx.mediarouter.media.MediaRouter
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaLoadOptions
import com.google.android.gms.cast.framework.CastButtonFactory
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.framework.CastSession
import com.google.android.gms.cast.framework.SessionManagerListener
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.TimeUnit

const val TAG = "ChromecastTest"

class MainActivity : AppCompatActivity() {

    private val compositeDisposable = CompositeDisposable()
    private val triggerDiscovery by lazy { findViewById<Button>(R.id.discovery_trigger) }
    private val playContent by lazy { findViewById<Button>(R.id.play_content) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onStart() {
        super.onStart()
        initCast()
    }

    private fun initCast() {
        val disposable = Completable.complete()
            .delay(2000L, TimeUnit.MILLISECONDS, Schedulers.computation())
            .observeOn(AndroidSchedulers.mainThread())
            .andThen(
                Single.fromCallable {
                    initSdk()
                }
            )
            .subscribe(
                { initComponents(it) },
                { Log.e(TAG, "Error!", it) }
            )
        compositeDisposable.add(disposable)
    }

    private fun initComponents(castContext: CastContext) {
        Log.e(TAG, "Initializing other components")
        val mediaRouteButton = findViewById<MediaRouteButton>(R.id.media_button)
        CastButtonFactory.setUpMediaRouteButton(applicationContext, mediaRouteButton)

        castContext.mergedSelector?.let { selector ->
            triggerDiscovery.isEnabled = true
            triggerDiscovery.setOnClickListener {
                MediaRouter.getInstance(applicationContext)
                    .addCallback(
                        selector,
                        object : MediaRouter.Callback() {},
                        MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY
                    )
            }
        }


        playContent.isEnabled = true
        playContent.setOnClickListener {
            castContext.sessionManager.currentCastSession
                ?.remoteMediaClient?.load(
                    MediaInfo.Builder(
                        "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
                    )
                        .build(),
                    MediaLoadOptions.Builder().build()
                ) ?: Toast.makeText(applicationContext, "Player not ready!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initSdk(): CastContext {
        Log.i(TAG, "Initializing cast SDK")
        return CastContext.getSharedInstance(applicationContext)
    }

    override fun onStop() {
        super.onStop()
        compositeDisposable.dispose()
    }
}
