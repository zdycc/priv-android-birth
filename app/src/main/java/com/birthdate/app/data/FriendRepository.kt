package com.birthdate.app.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.util.concurrent.atomic.AtomicLong

class FriendRepository(context: Context) {

    private val appContext = context.applicationContext
    private val gson = Gson()
    private val dataFile = File(appContext.filesDir, "friends.json")
    private val avatarDir = File(appContext.filesDir, "avatars").apply { mkdirs() }

    private val _friends = MutableStateFlow<List<Friend>>(emptyList())
    val friends: StateFlow<List<Friend>> = _friends.asStateFlow()

    private val idCounter = AtomicLong(0)

    init {
        _friends.value = load()
        idCounter.set((_friends.value.maxOfOrNull { it.id } ?: 0L) + 1)
    }

    private fun load(): List<Friend> = try {
        if (!dataFile.exists()) emptyList()
        else {
            val type = object : TypeToken<List<Friend>>() {}.type
            val list = gson.fromJson<List<Friend>>(dataFile.readText(), type) ?: emptyList()
            list.filter { it.name.isNotBlank() && it.lunarMonth in 1..12 && it.lunarDay in 1..30 }
        }
    } catch (e: Exception) {
        Log.e(TAG, "load failed", e)
        emptyList()
    }

    private fun persist(list: List<Friend>) {
        try {
            dataFile.writeText(gson.toJson(list))
        } catch (e: Exception) {
            Log.e(TAG, "persist failed", e)
        }
    } 

    fun addFriend(
        name: String,
        birthYear: Int?,
        lunarMonth: Int,
        lunarDay: Int,
        isLeapMonth: Boolean,
        avatarPath: String?
    ): Long {
        val id = idCounter.getAndIncrement()
        val friend = Friend(
            id = id,
            name = name.trim(),
            avatarPath = avatarPath,
            birthYear = birthYear,
            lunarMonth = lunarMonth,
            lunarDay = lunarDay,
            isLeapMonth = isLeapMonth
        )
        val updated = _friends.value + friend
        _friends.value = updated
        persist(updated)
        return id
    }

    fun updateFriend(friend: Friend) {
        val updated = _friends.value.map { if (it.id == friend.id) friend else it }
        _friends.value = updated
        persist(updated)
    }

    fun getFriend(id: Long): Friend? = _friends.value.find { it.id == id }

    fun deleteFriend(id: Long) {
        val target = _friends.value.find { it.id == id }
        val updated = _friends.value.filter { it.id != id }
        _friends.value = updated
        persist(updated)
        if (target?.avatarPath != null) deleteAvatarFile(target.avatarPath)
    }

    fun deleteFriends(ids: Collection<Long>) {
        if (ids.isEmpty()) return
        val idSet = ids.toSet()
        val targets = _friends.value.filter { it.id in idSet }
        val updated = _friends.value.filter { it.id !in idSet }
        _friends.value = updated
        persist(updated)
        targets.forEach { if (it.avatarPath != null) deleteAvatarFile(it.avatarPath) }
    }

    fun copyAvatar(uri: Uri): String {
        val out = File(avatarDir, "avatar_${System.currentTimeMillis()}.jpg")
        try {
            appContext.contentResolver.openInputStream(uri)?.use { input ->
                out.outputStream().use { input.copyTo(it) }
            }
        } catch (e: Exception) {
            Log.e(TAG, "copyAvatar failed", e)
        }
        return out.absolutePath
    }

    fun deleteAvatarFile(path: String?) {
        if (path == null) return
        try {
            File(path).delete()
        } catch (e: Exception) {
            Log.e(TAG, "deleteAvatarFile failed", e)
        }
    }

    private companion object {
        const val TAG = "FriendRepository"
    }
}
