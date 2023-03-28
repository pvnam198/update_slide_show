package com.example.slide.ui.video.video_preview

import com.example.slide.R
import com.example.slide.music_engine.DefaultMusic
import com.example.slide.videolib.NewMaskBitmap
import java.io.Serializable

object ThemeProvider {

    fun getThemeById(id: Int): ThemeProvider.Theme {
        return THEMES.find {
            it.id == id
        } ?: SHUFFLE_EFFECT
    }

    val SHINE_EFFECT =
        Theme(
            0,
            R.drawable.ic_effect_shine,
            R.string.shine,
            MusicProvider.SUNNY,
            Theme.STYLE_PRESET,
            true
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.Whole3D_TB)
            list.add(NewMaskBitmap.EFFECT.Whole3D_BT)
            list.add(NewMaskBitmap.EFFECT.Whole3D_LR)
            list.add(NewMaskBitmap.EFFECT.Whole3D_RL)
            list.add(NewMaskBitmap.EFFECT.SepartConbine_TB)
            list.add(NewMaskBitmap.EFFECT.SepartConbine_BT)
            list.add(NewMaskBitmap.EFFECT.SepartConbine_LR)
            list.add(NewMaskBitmap.EFFECT.SepartConbine_RL)
            list
        }

    private val PUSH_EFFECT =
        Theme(
            1,
            R.drawable.ic_effect_push,
            R.string.push,
            MusicProvider.CLEARDAY,
            Theme.STYLE_PRESET
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.ROLL_2D_TB)
            list.add(NewMaskBitmap.EFFECT.Roll2D_BT)
            list.add(NewMaskBitmap.EFFECT.Roll2D_LR)
            list.add(NewMaskBitmap.EFFECT.Roll2D_RL)
            list.add(NewMaskBitmap.EFFECT.DIAMOND_ZOOM_IN)
            list.add(NewMaskBitmap.EFFECT.DIAMOND_ZOOM_OUT)
            list
        }

    private val CIRCLE_EFFECT =
        Theme(
            2,
            R.drawable.ic_effect_circle,
            R.string.circle,
            MusicProvider.TENDERNESS,
            Theme.STYLE_PRESET
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.CIRCLE_LEFT_TOP)
            list.add(NewMaskBitmap.EFFECT.CIRCLE_RIGHT_TOP)
            list.add(NewMaskBitmap.EFFECT.CIRCLE_LEFT_BOTTOM)
            list.add(NewMaskBitmap.EFFECT.CIRCLE_RIGHT_BOTTOM)
            list.add(NewMaskBitmap.EFFECT.CIRCLE_IN)
            list.add(NewMaskBitmap.EFFECT.CIRCLE_OUT)
            list.add(NewMaskBitmap.EFFECT.ECLIPSE_IN)
            list
        }

    private val SQUARE_EFFECT =
        Theme(
            3,
            R.drawable.ic_effect_square,
            R.string.square,
            MusicProvider.ENDLESSMOTION,
            Theme.STYLE_PRESET
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.SQUARE_IN)
            list.add(NewMaskBitmap.EFFECT.SQUARE_OUT)
            list.add(NewMaskBitmap.EFFECT.CROSS_IN)
            list.add(NewMaskBitmap.EFFECT.CROSS_OUT)
            list.add(NewMaskBitmap.EFFECT.ROW_SPLIT)
            list.add(NewMaskBitmap.EFFECT.COL_SPLIT)
            list
        }

    private val FENCE_EFFECT =
        Theme(
            4,
            R.drawable.ic_effect_fence,
            R.string.fence,
            MusicProvider.THEJAZZPIANO,
            Theme.STYLE_PRESET,
            true
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.VERTICAL_RECT)
            list.add(NewMaskBitmap.EFFECT.HORIZONTAL_RECT)
            list.add(NewMaskBitmap.EFFECT.Jalousie_BT)
            list.add(NewMaskBitmap.EFFECT.DIAMOND_ZOOM_IN)
            list.add(NewMaskBitmap.EFFECT.DIAMOND_ZOOM_OUT)
            list.add(NewMaskBitmap.EFFECT.SKEW_LEFT_MERGE)
            list.add(NewMaskBitmap.EFFECT.SKEW_RIGHT_MERGE)
            list.add(NewMaskBitmap.EFFECT.SKEW_LEFT_SPLIT)
            list.add(NewMaskBitmap.EFFECT.SKEW_RIGHT_SPLIT)
            list
        }

    private val FADE_EFFECT =
        Theme(
            5,
            R.drawable.ic_effect_fade,
            R.string.fade,
            MusicProvider.ADVENTURE,
            Theme.STYLE_PRESET
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.Erase_Slide)
            list.add(NewMaskBitmap.EFFECT.Erase)
            list.add(NewMaskBitmap.EFFECT.Crossfade)
            list.add(NewMaskBitmap.EFFECT.Pixel_effect)
            list
        }

    val SHUFFLE_EFFECT =
        Theme(
            6,
            R.drawable.ic_effect_shufle,
            R.string.shuffle,
            MusicProvider.ADVENTURE,
            Theme.STYLE_PRESET
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.RECT_RANDOM)
            list.add(NewMaskBitmap.EFFECT.CLOCK)
            list.add(NewMaskBitmap.EFFECT.LEAF)
            list.add(NewMaskBitmap.EFFECT.PIN_WHEL)
            list.add(NewMaskBitmap.EFFECT.OPEN_DOOR)
            list.add(NewMaskBitmap.EFFECT.FOUR_TRIANGLE)
            list.add(NewMaskBitmap.EFFECT.VERTICAL_RECT)
            list.add(NewMaskBitmap.EFFECT.HORIZONTAL_RECT)
            list.add(NewMaskBitmap.EFFECT.Flip_Page_Right)
            list.add(NewMaskBitmap.EFFECT.Tilt_Drift)
            list.add(NewMaskBitmap.EFFECT.HORIZONTAL_COLUMN_DOWNMASK)
            list.add(NewMaskBitmap.EFFECT.DIAMOND_ZOOM_IN)
            list.add(NewMaskBitmap.EFFECT.DIAMOND_ZOOM_OUT)
            list
        }

    private val DIVIDED_EFFECT =
        Theme(
            7,
            R.drawable.ic_effect_divided,
            R.string.divided,
            MusicProvider.EPIC,
            Theme.STYLE_PRESET,
            true
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.RollInTurn_TB)
            list.add(NewMaskBitmap.EFFECT.RollInTurn_BT)
            list.add(NewMaskBitmap.EFFECT.RollInTurn_LR)
            list.add(NewMaskBitmap.EFFECT.RollInTurn_RL)
            list.add(NewMaskBitmap.EFFECT.Jalousie_BT)
            list.add(NewMaskBitmap.EFFECT.Jalousie_LR)
            list.add(NewMaskBitmap.EFFECT.SepartConbine_TB)
            list.add(NewMaskBitmap.EFFECT.SepartConbine_BT)
            list.add(NewMaskBitmap.EFFECT.SepartConbine_LR)
            list.add(NewMaskBitmap.EFFECT.SepartConbine_RL)
            list
        }

    private val ERASE_EFFECT =
        Theme(
            8,
            R.drawable.ic_effect_erase,
            R.string.erase,
            MusicProvider.ADVENTURE
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.Erase)
            list
        }

    val CROSS_IN_EFFECT =
        Theme(
            9,
            R.drawable.ic_effect_cross_in,
            R.string.cross_in,
            MusicProvider.CLEARDAY
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.CROSS_IN)
            list
        }

    private val CROSS_OUT_EFFECT =
        Theme(
            10,
            R.drawable.ic_effect_cross_out,
            R.string.cross_out,
            MusicProvider.ENDLESSMOTION
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.CROSS_OUT)
            list
        }

    private val PIXEL_EFFECT =
        Theme(
            11,
            R.drawable.ic_effect_pixel_effect,
            R.string.pixel_effect,
            MusicProvider.ENERGY
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.PIN_WHEL)
            list
        }

    private val ROLLINTURN_TB_EFFECT =
        Theme(
            12,
            R.drawable.ic_effect_rollinturn_tb,
            R.string.rollinturn_tb,
            MusicProvider.EPIC,
            Theme.STYLE_PRESET
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.RollInTurn_TB)
            list
        }

    private val ROLLINTURN_BT_EFFECT =
        Theme(
            13,
            R.drawable.ic_effect_rollinturn_bt,
            R.string.rollinturn_bt,
            MusicProvider.FUNNYSONG,
            Theme.STYLE_3D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.RollInTurn_BT)
            list
        }

    private val ROLLINTURN_LR_EFFECT =
        Theme(
            14,
            R.drawable.ic_effect_rollinturn_lr,
            R.string.rollinturn_lr,
            MusicProvider.GOINGHIGHER,
            Theme.STYLE_3D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.RollInTurn_LR)
            list
        }

    private val ROLLINTURN_RL_EFFECT =
        Theme(
            15,
            R.drawable.ic_effect_rollinturn_rl,
            R.string.rollinturn_rl,
            MusicProvider.INSPIRE,
            Theme.STYLE_3D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.RollInTurn_RL)
            list
        }

    private val ERASE_SLIDE_EFFECT =
        Theme(
            16,
            R.drawable.ic_effect_erase_slide,
            R.string.erase_slide,
            MusicProvider.A_LITTLE_STORY,
            Theme.STYLE_3D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.Erase_Slide)
            list
        }

    private val WHOLE3D_TB_EFFECT =
        Theme(
            17,
            R.drawable.ic_effect_whole3d_tb,
            R.string.whole3d_tb,
            MusicProvider.LOVE,
            Theme.STYLE_3D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.Whole3D_TB)
            list
        }

    private val WHOLE3D_BT_EFFECT =
        Theme(
            18,
            R.drawable.ic_effect_whole3d_bt,
            R.string.whole3d_bt,
            MusicProvider.ONCEAGAIN,
            Theme.STYLE_3D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.Whole3D_BT)
            list
        }

    private val WHOLE3D_LR_EFFECT =
        Theme(
            19,
            R.drawable.ic_effect_whole3d_lr,
            R.string.whole3d_lr,
            MusicProvider.PERCEPTION,
            Theme.STYLE_3D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.Whole3D_LR)
            list
        }

    private val WHOLE3D_RL_EFFECT =
        Theme(
            20,
            R.drawable.ic_effect_whole3d_rl,
            R.string.whole3d_rl,
            MusicProvider.RETROSOUL,
            Theme.STYLE_3D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.Whole3D_RL)
            list
        }

    private val SEPARTCONBINE_TB_EFFECT =
        Theme(
            21,
            R.drawable.ic_effect_separtconbine_tb,
            R.string.separtconbine_tb,
            MusicProvider.RUMBLE,
            Theme.STYLE_3D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.SepartConbine_TB)
            list
        }

    private val SEPARTCONBINE_BT_EFFECT =
        Theme(
            22,
            R.drawable.ic_effect_separtconbine_bt,
            R.string.separtconbine_bt,
            MusicProvider.SMILE,
            Theme.STYLE_3D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.SepartConbine_BT)
            list
        }

    private val SEPARTCONBINE_LR_EFFECT =
        Theme(
            23,
            R.drawable.ic_effect_separtconbine_lr,
            R.string.separtconbine_lr,
            MusicProvider.STRAIGHT,
            Theme.STYLE_3D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.SepartConbine_LR)
            list
        }

    private val SEPARTCONBINE_RL_EFFECT =
        Theme(
            24,
            R.drawable.ic_effect_separtconbine_rl,
            R.string.separtconbine_rl,
            MusicProvider.SUNNY,
            Theme.STYLE_3D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.SepartConbine_RL)
            list
        }

    private val JALOUSIE_BT_EFFECT =
        Theme(
            25,
            R.drawable.ic_effect_jalousie_bt,
            R.string.jalousie_bt,
            MusicProvider.SWEET,
            Theme.STYLE_3D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.Jalousie_BT)
            list
        }

    private val JALOUSIE_LR_EFFECT =
        Theme(
            26,
            R.drawable.ic_effect_jalousie_lr,
            R.string.jalousie_lr,
            MusicProvider.TENDERNESS
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.Jalousie_LR)
            list
        }

    private val CROSSFADE_LR_EFFECT =
        Theme(
            27,
            R.drawable.ic_effect_crossfade,
            R.string.crossfade,
            MusicProvider.THEJAZZPIANO
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.Crossfade)
            list
        }

    private val LEAF_EFFECT =
        Theme(
            28,
            R.drawable.ic_effect_leaf,
            R.string.leaf,
            MusicProvider.ADVENTURE
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.LEAF)
            list
        }

    private val FLIP_PAGE_RIGHT_EFFECT =
        Theme(
            29,
            R.drawable.ic_effect_flip_page_right,
            R.string.flip_page_right,
            MusicProvider.CLEARDAY
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.Flip_Page_Right)
            list
        }

    private val ROLL_2D_TB_EFFECT =
        Theme(
            30,
            R.drawable.ic_effect_roll_2d_tb,
            R.string.roll_2d_tb,
            MusicProvider.ENDLESSMOTION
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.ROLL_2D_TB)
            list
        }

    private val ROLL_2D_BT_EFFECT =
        Theme(
            31,
            R.drawable.ic_effect_roll2d_bt,
            R.string.roll_2d_bt,
            MusicProvider.ENERGY
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.Roll2D_BT)
            list
        }

    private val ROLL_2D_LR_EFFECT =
        Theme(
            32,
            R.drawable.ic_effect_roll2d_lr,
            R.string.roll_2d_lr,
            MusicProvider.EPIC
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.Roll2D_LR)
            list
        }

    private val ROLL_2D_RL_EFFECT =
        Theme(
            33,
            R.drawable.ic_effect_roll2d_rl,
            R.string.roll_2d_rl,
            MusicProvider.FUNNYSONG
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.Roll2D_RL)
            list
        }

    private val RECT_RANDOM_EFFECT =
        Theme(
            34,
            R.drawable.ic_effect_rect_random,
            R.string.rect_random,
            MusicProvider.GOINGHIGHER
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.RECT_RANDOM)
            list
        }

    private val PIN_WHEL_EFFECT =
        Theme(
            35,
            R.drawable.ic_effect_pin_whel,
            R.string.pin_whel,
            MusicProvider.INSPIRE
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.PIN_WHEL)
            list
        }

    private val FOUR_TRIANGLE_EFFECT =
        Theme(
            36,
            R.drawable.ic_effect_four_triangle,
            R.string.four_triangle,
            MusicProvider.A_LITTLE_STORY
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.FOUR_TRIANGLE)
            list
        }

    private val DIAMOND_ZOOM_IN_EFFECT =
        Theme(
            37,
            R.drawable.ic_effect_diamond_zoom_in,
            R.string.diamond_zoom_in,
            MusicProvider.LOVE
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.DIAMOND_ZOOM_IN)
            list
        }

    private val DIAMOND_ZOOM_OUT_EFFECT =
        Theme(
            38,
            R.drawable.ic_effect_diamond_zoom_out,
            R.string.diamond_zoom_out,
            MusicProvider.ONCEAGAIN
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.DIAMOND_ZOOM_OUT)
            list
        }

    private val SKEW_LEFT_MERGE_EFFECT =
        Theme(
            39,
            R.drawable.ic_effect_skew_left_merge,
            R.string.skew_left_merge,
            MusicProvider.PERCEPTION
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.SKEW_LEFT_MERGE)
            list
        }

    private val SKEW_RIGHT_MERGE_EFFECT =
        Theme(
            40,
            R.drawable.ic_effect_skew_right_merge,
            R.string.skew_right_merge,
            MusicProvider.RETROSOUL
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.SKEW_RIGHT_MERGE)
            list
        }

    private val SKEW_LEFT_SPLIT_EFFECT =
        Theme(
            41,
            R.drawable.ic_effect_skew_left_split,
            R.string.skew_left_split,
            MusicProvider.RUMBLE
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.SKEW_LEFT_SPLIT)
            list
        }

    private val SKEW_RIGHT_SPLIT_EFFECT =
        Theme(
            42,
            R.drawable.ic_effect_skew_right_split,
            R.string.skew_right_split,
            MusicProvider.SMILE
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.SKEW_RIGHT_SPLIT)
            list
        }

    private val ECLIPSE_IN_EFFECT =
        Theme(
            43,
            R.drawable.ic_effect_eclipse_in,
            R.string.eclipse_in,
            MusicProvider.STRAIGHT
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.ECLIPSE_IN)
            list
        }

    private val CIRCLE_LEFT_TOP_EFFECT =
        Theme(
            44,
            R.drawable.ic_effect_circle_left_top,
            R.string.circle_left_top,
            MusicProvider.SUNNY
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.CIRCLE_LEFT_TOP)
            list
        }

    private val CIRCLE_RIGHT_TOP_EFFECT =
        Theme(
            45,
            R.drawable.ic_effect_circle_right_top,
            R.string.circle_right_top,
            MusicProvider.SWEET
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.CIRCLE_RIGHT_TOP)
            list
        }

    private val CIRCLE_LEFT_BOTTOM_EFFECT =
        Theme(
            46,
            R.drawable.ic_effect_circle_left_bottom,
            R.string.circle_left_bottom,
            MusicProvider.TENDERNESS
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.CIRCLE_LEFT_BOTTOM)
            list
        }

    private val CIRCLE_RIGHT_BOTTOM_EFFECT =
        Theme(
            47,
            R.drawable.ic_effect_circle_right_bottom,
            R.string.circle_right_bottom,
            MusicProvider.THEJAZZPIANO
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.CIRCLE_RIGHT_BOTTOM)
            list
        }


    private val CIRCLE_IN_EFFECT =
        Theme(
            48,
            R.drawable.ic_effect_circle_in,
            R.string.circle_in,
            MusicProvider.ADVENTURE
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.CIRCLE_IN)
            list
        }


    private val CIRCLE_OUT_EFFECT =
        Theme(
            49,
            R.drawable.ic_effect_circle_out,
            R.string.circle_out,
            MusicProvider.CLEARDAY
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.CIRCLE_OUT)
            list
        }


    private val CLOCK_EFFECT =
        Theme(
            50,
            R.drawable.ic_effect_clock,
            R.string.clock,
            MusicProvider.ENDLESSMOTION
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.CLOCK)
            list
        }


    private val OPEN_DOOR_EFFECT =
        Theme(
            51,
            R.drawable.ic_effect_open_door,
            R.string.open_door,
            MusicProvider.ENERGY
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.OPEN_DOOR)
            list
        }


    private val CROSS_SHUTTER_EFFECT =
        Theme(
            52,
            R.drawable.ic_effect_cross_shutter_1,
            R.string.cross_shutter,
            MusicProvider.EPIC
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.CROSS_SHUTTER_1)
            list
        }


    private val SQUARE_IN_EFFECT =
        Theme(
            53,
            R.drawable.ic_effect_square_in,
            R.string.square_in,
            MusicProvider.FUNNYSONG
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.SQUARE_IN)
            list
        }


    private val SQUARE_OUT_EFFECT =
        Theme(
            54,
            R.drawable.ic_effect_square_out,
            R.string.square_out,
            MusicProvider.GOINGHIGHER
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.SQUARE_OUT)
            list
        }


    private val HORIZONTAL_COLUMN_DOWNMASK_EFFECT =
        Theme(
            55,
            R.drawable.ic_effect_horizontal_column_downmask,
            R.string.horizontal_column_downmask,
            MusicProvider.INSPIRE
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.HORIZONTAL_COLUMN_DOWNMASK)
            list
        }


    private val VERTICAL_RECT_EFFECT =
        Theme(
            56,
            R.drawable.ic_effect_vertical_rect,
            R.string.vertical_rect,
            MusicProvider.A_LITTLE_STORY
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.VERTICAL_RECT)
            list
        }


    private val HORUZONTAL_RECT_EFFECT =
        Theme(
            57,
            R.drawable.ic_effect_horuzontal_rect,
            R.string.horizontal_rect,
            MusicProvider.LOVE
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.HORIZONTAL_RECT)
            list
        }


    private val ROW_SPLIT_EFFECT =
        Theme(
            58,
            R.drawable.ic_effect_row_split,
            R.string.row_split,
            MusicProvider.ONCEAGAIN
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.ROW_SPLIT)
            list
        }


    private val COL_SPLIT_EFFECT =
        Theme(
            59,
            R.drawable.ic_effect_col_split,
            R.string.col_split,
            MusicProvider.PERCEPTION
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.COL_SPLIT)
            list
        }


    private val TILT_DRIFT_EFFECT =
        Theme(
            60,
            R.drawable.ic_effect_tilt_drift,
            R.string.tilt_drift,
            MusicProvider.RETROSOUL,
            Theme.STYLE_3D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.Tilt_Drift)
            list
        }

    private val NONE_EFFECT =
        Theme(
            61,
            R.drawable.ic_effect_tilt_drift,
            R.string.none,
            MusicProvider.RETROSOUL,
            Theme.STYLE_2D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.NONE)
            list
        }

    private val ZOOM_IN_CROSS_FADE_EFFECT =
        Theme(
            62,
            R.drawable.ic_effect_62_zoom_in,
            R.string.zoom_and_fade,
            MusicProvider.RETROSOUL,
            Theme.STYLE_2D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.ZOOM_IN_CROSS_FADE)
            list
        }

    private val ROLL_DOWN_CURTAIN_EFFECT =
        Theme(
            63,
            R.drawable.ic_effect_63_roll_down_curtain,
            R.string.roll_down_curtain,
            MusicProvider.RETROSOUL,
            Theme.STYLE_2D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.OPEN_WINDOW)
            list
        }

    private val PLANKS_EFFECT =
        Theme(
            64,
            R.drawable.ic_effect_planks,
            R.string.planks,
            MusicProvider.AMBIENT_ATMOSPHERIC,
            Theme.STYLE_2D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.Jalousie_BT)
            list.add(NewMaskBitmap.EFFECT.Jalousie_LR)
            list.add(NewMaskBitmap.EFFECT.SKEW_LEFT_MERGE)
            list.add(NewMaskBitmap.EFFECT.SKEW_RIGHT_MERGE)
            list.add(NewMaskBitmap.EFFECT.SKEW_LEFT_SPLIT)
            list.add(NewMaskBitmap.EFFECT.SKEW_RIGHT_SPLIT)
            list.add(NewMaskBitmap.EFFECT.HORIZONTAL_COLUMN_DOWNMASK)
            list
        }

    private val SCREEN_EFFECT =
        Theme(
            65,
            R.drawable.ic_effect_screen,
            R.string.screen,
            MusicProvider.FREEDOM_INSPIRED_CINEMATIC_BACKGROUND_MUSIC_FOR_VIDEO,
            Theme.STYLE_2D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.PIN_WHEL)
            list.add(NewMaskBitmap.EFFECT.Jalousie_BT)
            list.add(NewMaskBitmap.EFFECT.Jalousie_LR)
            list.add(NewMaskBitmap.EFFECT.CROSS_SHUTTER_1)
            list.add(NewMaskBitmap.EFFECT.VERTICAL_RECT)
            list.add(NewMaskBitmap.EFFECT.HORIZONTAL_RECT)
            list.add(NewMaskBitmap.EFFECT.ROW_SPLIT)
            list
        }

    private val DISSOLVE_EFFECT =
        Theme(
            66,
            R.drawable.ic_effect_dissolve,
            R.string.dissolve,
            MusicProvider.DISSOLVE,
            Theme.STYLE_2D
        ) {
            val list: ArrayList<NewMaskBitmap.EFFECT> = ArrayList()
            list.add(NewMaskBitmap.EFFECT.Erase)
            list.add(NewMaskBitmap.EFFECT.Crossfade)
            list.add(NewMaskBitmap.EFFECT.Pixel_effect)
            list.add(NewMaskBitmap.EFFECT.Flip_Page_Right)
            list
        }

    val THEMES =
        arrayOf(
            NONE_EFFECT,
            DISSOLVE_EFFECT,
            SCREEN_EFFECT,
            PLANKS_EFFECT,
            SHUFFLE_EFFECT,
            PUSH_EFFECT,
            CIRCLE_EFFECT,
            SHINE_EFFECT,
            SQUARE_EFFECT,
            FENCE_EFFECT,
            FADE_EFFECT,
            DIVIDED_EFFECT,
            ZOOM_IN_CROSS_FADE_EFFECT,
            ROLL_DOWN_CURTAIN_EFFECT,
            ERASE_EFFECT,
            CROSS_IN_EFFECT,
            CROSS_OUT_EFFECT,
            PIXEL_EFFECT,
            ROLLINTURN_TB_EFFECT,
            ROLLINTURN_BT_EFFECT,
            ROLLINTURN_LR_EFFECT,
            ROLLINTURN_RL_EFFECT,
            ERASE_SLIDE_EFFECT,
            WHOLE3D_TB_EFFECT,
            WHOLE3D_BT_EFFECT,
            WHOLE3D_LR_EFFECT,
            WHOLE3D_RL_EFFECT,
            SEPARTCONBINE_TB_EFFECT,
            SEPARTCONBINE_BT_EFFECT,
            SEPARTCONBINE_LR_EFFECT,
            SEPARTCONBINE_RL_EFFECT,
            JALOUSIE_BT_EFFECT,
            JALOUSIE_LR_EFFECT,
            CROSSFADE_LR_EFFECT,
            LEAF_EFFECT,
            FLIP_PAGE_RIGHT_EFFECT,
            ROLL_2D_TB_EFFECT,
            ROLL_2D_BT_EFFECT,
            ROLL_2D_LR_EFFECT,
            ROLL_2D_RL_EFFECT,
            RECT_RANDOM_EFFECT,
            PIN_WHEL_EFFECT,
            FOUR_TRIANGLE_EFFECT,
            DIAMOND_ZOOM_IN_EFFECT,
            DIAMOND_ZOOM_OUT_EFFECT,
            SKEW_LEFT_MERGE_EFFECT,
            SKEW_RIGHT_MERGE_EFFECT,
            SKEW_LEFT_SPLIT_EFFECT,
            SKEW_RIGHT_SPLIT_EFFECT,
            ECLIPSE_IN_EFFECT,
            CIRCLE_LEFT_TOP_EFFECT,
            CIRCLE_RIGHT_TOP_EFFECT,
            CIRCLE_LEFT_BOTTOM_EFFECT,
            CIRCLE_RIGHT_BOTTOM_EFFECT,
            CIRCLE_IN_EFFECT,
            CIRCLE_OUT_EFFECT,
            CLOCK_EFFECT,
            OPEN_DOOR_EFFECT,
            CROSS_SHUTTER_EFFECT,
            SQUARE_IN_EFFECT,
            SQUARE_OUT_EFFECT,
            HORIZONTAL_COLUMN_DOWNMASK_EFFECT,
            VERTICAL_RECT_EFFECT,
            HORUZONTAL_RECT_EFFECT,
            ROW_SPLIT_EFFECT,
            COL_SPLIT_EFFECT,
            TILT_DRIFT_EFFECT
        )

    class Theme(
        var id: Int,
        var image: Int,
        var nameRes: Int,
        var defaultMusic: DefaultMusic,
        var style: Int = STYLE_2D,
        var isPremium: Boolean = false,
        private val animationCreator: () -> ArrayList<NewMaskBitmap.EFFECT>
    ) : Serializable {

        companion object {

            const val STYLE_2D = 0

            const val STYLE_3D = 1

            const val STYLE_PRESET = 2

        }

        private val animations: ArrayList<NewMaskBitmap.EFFECT> = animationCreator.invoke()

        fun getEffectByPos(pos: Int): NewMaskBitmap.EFFECT {
            return animations[pos % animations.size]
        }

    }
}