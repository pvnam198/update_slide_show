package com.example.slide.ui.video.video_preview

import com.example.slide.R
import com.example.slide.music_engine.DefaultMusic

object MusicProvider {


    val ADVENTURE = DefaultMusic(
        0,
        R.string.adventure,
        "music/adventure.ogg",
        30188
    )

    val CLEARDAY = DefaultMusic(
        1,
        R.string.clear_day,
        "music/clearday.ogg",
        30241
    )

    val ENDLESSMOTION = DefaultMusic(
        2,
        R.string.endless_motion,
        "music/endlessmotion.ogg",
        30267
    )

    val ENERGY = DefaultMusic(
        3,
        R.string.energy,
        "music/energy.ogg",
        30084
    )

    val EPIC = DefaultMusic(
        4,
        R.string.epic,
        "music/epic.ogg",
        30371
    )

    val FUNNYSONG = DefaultMusic(
        5,
        R.string.funny_song,
        "music/funnysong.ogg",
        30162
    )

    val GOINGHIGHER = DefaultMusic(
        6,
        R.string.going_higher,
        "music/goinghigher.ogg",
        30058
    )

    val INSPIRE = DefaultMusic(
        7,
        R.string.inspire,
        "music/inspire.ogg",
        30214
    )

    val LOVE = DefaultMusic(
        8,
        R.string.love,
        "music/love.ogg",
        30475
    )

    val ONCEAGAIN = DefaultMusic(
        10,
        R.string.once_again,
        "music/onceagain.ogg",
        30162
    )

    val PERCEPTION = DefaultMusic(
        11,
        R.string.perception,
        "music/perception.ogg",
        30188
    )

    val RETROSOUL = DefaultMusic(
        12,
        R.string.retro_soul,
        "music/retrosoul.ogg",
        30032
    )

    val RUMBLE = DefaultMusic(
        13,
        R.string.rumble,
        "music/rumble.ogg",
        30032
    )

    val SMILE = DefaultMusic(
        14,
        R.string.smile,
        "music/smile.ogg",
        30058
    )

    val STRAIGHT = DefaultMusic(
        15,
        R.string.straight,
        "music/straight.ogg",
        30058
    )

    val SUNNY = DefaultMusic(
        16,
        R.string.sunny,
        "music/sunny.ogg",
        30058
    )

    val SWEET = DefaultMusic(
        17,
        R.string.sweet,
        "music/sweet.ogg",
        30058
    )

    val TENDERNESS = DefaultMusic(
        18,
        R.string.tenderness,
        "music/tenderness.ogg",
        30032
    )

    val THEJAZZPIANO = DefaultMusic(
        19,
        R.string.the_jazz_piano,
        "music/thejazzpiano.ogg",
        30032
    )

    val A_LITTLE_STORY = DefaultMusic(
            20,
            R.string.adventure,
            "music/a_little_story.ogg",
            73000
    )

    val AMBIENT_ATMOSPHERIC = DefaultMusic(
            21,
            R.string.ambient_atmospheric,
            "music/ambient_atmospheric.ogg",
            30000
    )

    val FREEDOM_INSPIRED_CINEMATIC_BACKGROUND_MUSIC_FOR_VIDEO = DefaultMusic(
            22,
            R.string.freedom_inspired_cinematic_background_music_for_video,
            "music/freedom_inspired_cinematic_background_music_for_video.ogg",
            30000
    )

    val DISSOLVE = DefaultMusic(
            23,
            R.string.dissolve,
            "music/in_the_forest2.ogg",
            30000
    )

    fun defaultMusics(): ArrayList<DefaultMusic> {
        return arrayListOf(
            DISSOLVE,
            FREEDOM_INSPIRED_CINEMATIC_BACKGROUND_MUSIC_FOR_VIDEO,
            AMBIENT_ATMOSPHERIC,
            A_LITTLE_STORY,
            ADVENTURE,
            CLEARDAY,
            ENDLESSMOTION,
            ENERGY,
            EPIC,
            FUNNYSONG,
            GOINGHIGHER,
            INSPIRE,
            LOVE,
            ONCEAGAIN,
            PERCEPTION,
            RETROSOUL,
            RUMBLE,
            SMILE,
            STRAIGHT,
            SUNNY,
            SWEET,
            TENDERNESS,
            THEJAZZPIANO
        )
    }

}