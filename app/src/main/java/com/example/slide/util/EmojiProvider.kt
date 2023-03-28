package com.example.slide.util

import android.content.Context
import com.example.slide.ui.edit_image.model.EmojiSticker
import org.json.JSONArray


object EmojiProvider {

    private const val JSON_EMOJI_FILE = "emojis.json"

    fun getEmojiStickers(context: Context): ArrayList<EmojiSticker> {
        val jsonFileString =
            context.assets.open(JSON_EMOJI_FILE).bufferedReader().use { it.readText() }
        val jsonArr = JSONArray(jsonFileString)
        val emojiStickers = ArrayList<EmojiSticker>()
        for (i in 0 until jsonArr.length()) {
            val jObject = jsonArr.getJSONObject(i)
            val isShow = jObject.getBoolean("isShow")
            if (isShow) {
                val path = jObject.getString("emojiPath")
                val represent = jObject.getString("represent")
                val imageJsons = jObject.getJSONArray("emojis")
                val isVip = jObject.getBoolean("isVip")
                val images = Array<String>(imageJsons.length()) { imageJsons.getString(it) }
                emojiStickers.add(EmojiSticker(path, represent, images, isVip))
            }
        }
        return emojiStickers
    }
}