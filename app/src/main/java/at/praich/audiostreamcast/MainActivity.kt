package at.praich.audiostreamcast

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.cast.framework.*

import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaInfo.UNKNOWN_DURATION


private const val RADIO_URL = "http://www.radioswissjazz.ch/live/aacp.m3u"
private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {

    private var mCastSession: CastSession? = null
    private var mSessionManager: SessionManager? = null

    private val mSessionManagerListener = SessionManagerListenerImpl()

    private inner class SessionManagerListenerImpl() : SessionManagerListener<Session> {
        override fun onSessionResumeFailed(session: Session?, p1: Int) {
            Log.d(TAG, "onSessionResumeFailed")
        }

        override fun onSessionStarting(session: Session?) {
            Log.d(TAG, "onSessionStarting")
        }

        override fun onSessionEnding(session: Session?) {
            Log.d(TAG, "onSessionEnding")
        }

        override fun onSessionSuspended(session: Session?, p1: Int) {
            Log.d(TAG, "onSessionSuspended")
        }

        override fun onSessionStartFailed(session: Session?, p1: Int) {
            Log.d(TAG, "onSessionStartFailed")
        }

        override fun onSessionResuming(p0: Session?, p1: String?) {
            Log.d(TAG, "onSessionResuming")
        }

        override fun onSessionStarted(session: Session, sessionId: String) {
            invalidateOptionsMenu()
        }

        override fun onSessionResumed(session: Session, wasSuspended: Boolean) {
            invalidateOptionsMenu()
        }

        override fun onSessionEnded(session: Session, error: Int) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val castContext = CastContext.getSharedInstance(this)
        mSessionManager = CastContext.getSharedInstance(this).sessionManager

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener {
            val mediaInfo = MediaInfo.Builder(RADIO_URL)
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setContentType("audio/aacp")
                    //.setMetadata(movieMetadata)
                    .setStreamDuration(UNKNOWN_DURATION)
                    .build()
            val remoteMediaClient = mCastSession?.remoteMediaClient
            remoteMediaClient?.load(mediaInfo, true, 0)


//            view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show()
        }
    }

    override fun onResume() {
        mCastSession = mSessionManager?.currentCastSession
        mSessionManager?.addSessionManagerListener(mSessionManagerListener)
        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        CastButtonFactory.setUpMediaRouteButton(applicationContext,
                menu,
                R.id.media_route_menu_item);
        return true
    }

    override fun onPause() {
        super.onPause()
        mSessionManager?.removeSessionManagerListener(mSessionManagerListener)
        mCastSession = null
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
