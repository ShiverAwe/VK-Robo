package temp

import com.vk.api.sdk.objects.users.UserXtrCounters

object Utils {

    fun userListToMapById(users: MutableList<UserXtrCounters>): HashMap<Int, String> {
        val map = HashMap<Int, String>()
        users.forEach {
            map.put(it.id, "${it.firstName}_${it.lastName}")
        }
        return map
    }

}