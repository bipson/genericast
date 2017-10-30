package at.praich.audiostreamcast

import java.net.URL

/**
 * Created by philipp on 30.10.17.
 */

class ChannelListModel
(val channelName: String, val url: URL, val mediaType: String){
    var image: String? = ""
    var description: String? = ""
}