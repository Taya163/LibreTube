package xyz.btcland.libretube.obj

data class SearchItem(
    var url: String?,
    var title: String?,
    var thumbnail: String?,
    var uploaderName: String?,
    var uploaderUrl: String?,
    var uploaderAvatar: String?,
    var uploadedDate: String?,
    var duration: Long?,
    var views: Long?,
    var uploaderVerified: Boolean?,
//Channel and Playlist attributes
    var name: String? = null,
    var description: String? = null,
    var subscribers: Long? = -1,
    var videos: Long? = -1,
    var verified: Boolean? = null
){
    constructor() : this("","","","","","","",0,0,null)
}
