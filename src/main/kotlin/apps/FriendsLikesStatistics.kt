package apps

import com.vk.api.sdk.objects.photos.Photo
import com.vk.api.sdk.objects.users.UserMin
import com.vk.api.sdk.queries.likes.LikesType
import temp.AuthData
import temp.Counter
import temp.Requests
import temp.UserIds

fun main(args: Array<String>) {
    val code = AuthData.getCode()
    val actor = AuthData.getActor(code)
//    val users: IntArray = Requests.getFriendsOfUser(actor, UserIds.asMap["eneustroeva"]!!)
//    val users: IntArray = Requests.getMembersOfGroup(actor, "76477009")

    val userId = UserIds.asMap["vshefer"]!!

    val photos: MutableList<Photo> = Requests.vk
            .photos()
            .get(actor)
            .albumId("profile")
            .ownerId(userId)
            .execute()
            .items

    val counter = Counter<String>()

    photos.map { it.id }.forEach { photoId ->
        //    val data = Requests.getGroupsOfUsers(actor, *UserIds.asMap.values.toIntArray())
        val likes: MutableList<UserMin> = Requests.vk
                .likes()
                .getListExtended(actor, LikesType.PHOTO)
                .ownerId(userId)
                .itemId(photoId)
                .execute()
                .items
        val likesNames = likes.map {
            """${it.firstName}_${it.lastName}"""
        }
        counter.put(*likesNames.toTypedArray())

        Thread.sleep(500)
    }

    counter.print()
}


