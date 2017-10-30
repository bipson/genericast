package at.praich.audiostreamcast

import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.google.android.gms.cast.framework.*

import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.cast.framework.SessionManagerListener
import com.google.android.gms.cast.framework.media.RemoteMediaClient
import com.google.android.gms.cast.MediaInfo
import com.google.android.gms.cast.MediaInfo.UNKNOWN_DURATION
import kotlinx.android.synthetic.main.content_main.*
import android.widget.TextView
import android.view.ViewGroup
import android.widget.BaseAdapter
import java.net.URL
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener




private const val RADIO_RSJ = "http://stream.srg-ssr.ch/m/rsj/aacp_96"
private const val RADIO_FM4 = "http://mp3stream1.apasf.apa.at:80"
private const val RADIO_FM4_alt = "http://mp3stream1.apasf.apa.at:8000/listen.pls"
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
            mCastSession = session as CastSession
        }

        override fun onSessionResumed(session: Session, wasSuspended: Boolean) {
            invalidateOptionsMenu()
        }

        override fun onSessionEnded(session: Session, error: Int) {
            invalidateOptionsMenu()
        }
    }

    private inner class ChannelListAdapter
    (val channelList :ArrayList<ChannelListModel>) : BaseAdapter() {

        override fun getItem(position: Int): ChannelListModel? {
            return channelList[position]
        }

        override fun getItemId(position: Int): Long {
            Log.d(TAG, "getItemId")
            return 0
        }

        override fun getCount(): Int {
            return channelList.size
        }

        // override other abstract methods here
        override fun getView(position: Int, convertView: View?, container: ViewGroup): View {
            var convertView = convertView
            if (convertView == null) {
                convertView = layoutInflater.inflate(R.layout.channel_listrow, container, false)
            }

            val item = getItem(position)

            var name = convertView!!.findViewById<TextView>(R.id.name)
            var url = convertView!!.findViewById<TextView>(R.id.url)
            var mimetype = convertView!!.findViewById<TextView>(R.id.mimetype)
            var description = convertView!!.findViewById<TextView>(R.id.description)

            name?.text = item?.channelName
            url?.text = item?.url.toString()
            mimetype?.text = item?.mediaType

            return convertView
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val castContext = CastContext.getSharedInstance(this)
        mSessionManager = CastContext.getSharedInstance(this).sessionManager
        var dataChannelList : ArrayList<ChannelListModel> = ArrayList()
        dataChannelList.add(ChannelListModel("RadioSwissJazz", URL(RADIO_RSJ), "audio/mp4"))
        dataChannelList.add(ChannelListModel("FM4", URL(RADIO_FM4), "application/mp3"))
        dataChannelList.add(ChannelListModel("FM4", URL(RADIO_FM4), "application/mpeg"))
        dataChannelList.add(ChannelListModel("FM4", URL(RADIO_FM4), "application/x-mpegurl"))
        dataChannelList.add(ChannelListModel("FM4", URL(RADIO_FM4_alt), "application/mp3"))
        dataChannelList.add(ChannelListModel("FM4", URL(RADIO_FM4_alt), "application/mpeg"))
        dataChannelList.add(ChannelListModel("FM4", URL(RADIO_FM4_alt), "application/x-mpegurl"))

        // Create a message handling object as an anonymous class.
        val mMessageClickedHandler = OnItemClickListener { _, _, position, _ ->

            val url = dataChannelList[position].url.toString()
            val mediaType = dataChannelList[position].mediaType

            val mediaInfo = MediaInfo.Builder(url)
                    .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                    .setContentType(mediaType)
                    //.setMetadata(movieMetadata)
                    .setStreamDuration(UNKNOWN_DURATION)
                    .build()
            val remoteMediaClient = mCastSession?.remoteMediaClient
            remoteMediaClient?.load(mediaInfo, true, 0)
        }

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        channelList.adapter = ChannelListAdapter(dataChannelList)

        channelList.setOnItemClickListener(mMessageClickedHandler)

        fab.setOnClickListener {
            view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
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
