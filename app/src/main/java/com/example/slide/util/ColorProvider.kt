package com.example.slide.util

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object ColorProvider {

    private const val WHITE_COLOR = "#FFFFFF"

    private const val BLACK_COLOR = "#000000"

    private const val BLUE_GRAY_50_COLOR = "#ECEFF1"

    private const val BLUE_GRAY_100_COLOR = "#CFD8DC"

    private const val BLUE_GRAY_200_COLOR = "#B0BEC5"

    private const val BLUE_GRAY_300_COLOR = "#90A4AE"

    private const val BLUE_GRAY_400_COLOR = "#78909C"

    private const val BLUE_GRAY_500_COLOR = "#607D8B"

    private const val BLUE_GRAY_600_COLOR = "#546E7A"

    private const val BLUE_GRAY_700_COLOR = "#455A64"

    private const val BLUE_GRAY_800_COLOR = "#37474F"

    private const val BLUE_GRAY_900_COLOR = "#263238"

    private const val DEEP_ORANGE_50_COLOR = "#FBE9E7"

    private const val DEEP_ORANGE_100_COLOR = "#FFCCBC"

    private const val DEEP_ORANGE_200_COLOR = "#FFAB91"

    private const val DEEP_ORANGE_300_COLOR = "#FF8A65"

    private const val DEEP_ORANGE_400_COLOR = "#FF7043"

    private const val DEEP_ORANGE_500_COLOR = "#FF5722"

    private const val DEEP_ORANGE_600_COLOR = "#F4511E"

    private const val DEEP_ORANGE_700_COLOR = "#E64A19"

    private const val DEEP_ORANGE_800_COLOR = "#D84315"

    private const val DEEP_ORANGE_900_COLOR = "#BF360C"

    private const val DEEP_ORANGE_A100_COLOR = "#FF9E80"

    private const val DEEP_ORANGE_A200_COLOR = "#FF6E40"

    private const val DEEP_ORANGE_A400_COLOR = "#FF3D00"

    private const val DEEP_ORANGE_A700_COLOR = "#DD2C00"

    private const val BROWN_50_COLOR = "#EFEBE9"

    private const val BROWN_100_COLOR = "#D7CCC8"

    private const val BROWN_200_COLOR = "#BCAAA4"

    private const val BROWN_300_COLOR = "#A1887F"

    private const val BROWN_400_COLOR = "#8D6E63"

    private const val BROWN_500_COLOR = "#795548"

    private const val BROWN_600_COLOR = "#6D4C41"

    private const val BROWN_700_COLOR = "#5D4037"

    private const val BROWN_800_COLOR = "#4E342E"

    private const val BROWN_900_COLOR = "#3E2723"

    private const val GRAY_50_COLOR = "#FAFAFA"

    private const val GRAY_100_COLOR = "#F5F5F5"

    private const val GRAY_200_COLOR = "#EEEEEE"

    private const val GRAY_300_COLOR = "#E0E0E0"

    private const val GRAY_400_COLOR = "#BDBDBD"

    private const val GRAY_500_COLOR = "#9E9E9E"

    private const val GRAY_600_COLOR = "#757575"

    private const val GRAY_700_COLOR = "#616161"

    private const val GRAY_800_COLOR = "#424242"

    private const val GRAY_900_COLOR = "#212121"

    private const val YELLOW_50_COLOR = "#FFFDE7"

    private const val YELLOW_100_COLOR = "#FFF9C4"

    private const val YELLOW_200_COLOR = "#FFF59D"

    private const val YELLOW_300_COLOR = "#FFF176"

    private const val YELLOW_400_COLOR = "#FFEE58"

    private const val YELLOW_500_COLOR = "#FFEB3B"

    private const val YELLOW_600_COLOR = "#FDD835"

    private const val YELLOW_700_COLOR = "#FBC02D"

    private const val YELLOW_800_COLOR = "#F9A825"

    private const val YELLOW_900_COLOR = "#F57F17"

    private const val YELLOW_A100_COLOR = "#FFFF8D"

    private const val YELLOW_A200_COLOR = "#FFFF00"

    private const val YELLOW_A300_COLOR = "#FFEA00"

    private const val YELLOW_A400_COLOR = "#FFD600"

    private const val AMBER_50_COLOR = "#FFF8E1"

    private const val AMBER_100_COLOR = "#FFECB3"

    private const val AMBER_200_COLOR = "#FFE082"

    private const val AMBER_300_COLOR = "#FFD54F"

    private const val AMBER_400_COLOR = "#FFCA28"

    private const val AMBER_500_COLOR = "#FFC107"

    private const val AMBER_600_COLOR = "#FFB300"

    private const val AMBER_700_COLOR = "#FFA000"

    private const val AMBER_800_COLOR = "#FF8F00"

    private const val AMBER_900_COLOR = "#FF6F00"

    private const val AMBER_A100_COLOR = "#FFE57F"

    private const val AMBER_A200_COLOR = "#FFD740"

    private const val AMBER_A300_COLOR = "#FFC400"

    private const val AMBER_A400_COLOR = "#FFAB00"

    private const val ORANGE_50_COLOR = "#FFF3E0"

    private const val ORANGE_100_COLOR = "#FFE0B2"

    private const val ORANGE_200_COLOR = "#FFCC80"

    private const val ORANGE_300_COLOR = "#FFB74D"

    private const val ORANGE_400_COLOR = "#FFA726"

    private const val ORANGE_500_COLOR = "#FF9800"

    private const val ORANGE_600_COLOR = "#FB8C00"

    private const val ORANGE_700_COLOR = "#F57C00"

    private const val ORANGE_800_COLOR = "#EF6C00"

    private const val ORANGE_900_COLOR = "#E65100"

    private const val ORANGE_A100_COLOR = "#FFD180"

    private const val ORANGE_A200_COLOR = "#FFAB40"

    private const val ORANGE_A300_COLOR = "#FF9100"

    private const val ORANGE_A400_COLOR = "#FF6D00"

    private const val GREEN_50_COLOR = "#E8F5E9"

    private const val GREEN_100_COLOR = "#C8E6C9"

    private const val GREEN_200_COLOR = "#A5D6A7"

    private const val GREEN_300_COLOR = "#81C784"

    private const val GREEN_400_COLOR = "#66BB6A"

    private const val GREEN_500_COLOR = "#4CAF50"

    private const val GREEN_600_COLOR = "#43A047"

    private const val GREEN_700_COLOR = "#388E3C"

    private const val GREEN_800_COLOR = "#2E7D32"

    private const val GREEN_900_COLOR = "#1B5E20"

    private const val GREEN_A100_COLOR = "#B9F6CA"

    private const val GREEN_A200_COLOR = "#69F0AE"

    private const val GREEN_A300_COLOR = "#00E676"

    private const val GREEN_A400_COLOR = "#00C853"

    private const val LIGHT_GREEN_50_COLOR = "#F1F8E9"

    private const val LIGHT_GREEN_100_COLOR = "#DCEDC8"

    private const val LIGHT_GREEN_200_COLOR = "#C5E1A5"

    private const val LIGHT_GREEN_300_COLOR = "#AED581"

    private const val LIGHT_GREEN_400_COLOR = "#9CCC65"

    private const val LIGHT_GREEN_500_COLOR = "#8BC34A"

    private const val LIGHT_GREEN_600_COLOR = "#7CB342"

    private const val LIGHT_GREEN_700_COLOR = "#689F38"

    private const val LIGHT_GREEN_800_COLOR = "#558B2F"

    private const val LIGHT_GREEN_900_COLOR = "#33691E"

    private const val LIGHT_GREEN_A100_COLOR = "#CCFF90"

    private const val LIGHT_GREEN_A200_COLOR = "#B2FF59"

    private const val LIGHT_GREEN_A300_COLOR = "#76FF03"

    private const val LIGHT_GREEN_A400_COLOR = "#64DD17"

    private const val LIME_50_COLOR = "#F9FBE7"

    private const val LIME_100_COLOR = "#F0F4C3"

    private const val LIME_200_COLOR = "#E6EE9C"

    private const val LIME_300_COLOR = "#DCE775"

    private const val LIME_400_COLOR = "#D4E157"

    private const val LIME_500_COLOR = "#CDDC39"

    private const val LIME_600_COLOR = "#C0CA33"

    private const val LIME_700_COLOR = "#AFB42B"

    private const val LIME_800_COLOR = "#9E9D24"

    private const val LIME_900_COLOR = "#827717"

    private const val LIME_A100_COLOR = "#F4FF81"

    private const val LIME_A200_COLOR = "#EEFF41"

    private const val LIME_A300_COLOR = "#C6FF00"

    private const val LIME_A400_COLOR = "#AEEA00"

    private const val LIGHT_BLUE_50_COLOR = "#E1F5FE"

    private const val LIGHT_BLUE_100_COLOR = "#B3E5FC"

    private const val LIGHT_BLUE_200_COLOR = "#81D4FA"

    private const val LIGHT_BLUE_300_COLOR = "#4FC3F7"

    private const val LIGHT_BLUE_400_COLOR = "#29B6F6"

    private const val LIGHT_BLUE_500_COLOR = "#03A9F4"

    private const val LIGHT_BLUE_600_COLOR = "#039BE5"

    private const val LIGHT_BLUE_700_COLOR = "#0288D1"

    private const val LIGHT_BLUE_800_COLOR = "#0277BD"

    private const val LIGHT_BLUE_900_COLOR = "#01579B"

    private const val LIGHT_BLUE_A100_COLOR = "#80D8FF"

    private const val LIGHT_BLUE_A200_COLOR = "#40C4FF"

    private const val LIGHT_BLUE_A300_COLOR = "#00B0FF"

    private const val LIGHT_BLUE_A400_COLOR = "#0091EA"

    private const val CYAN_50_COLOR = "#E0F7FA"

    private const val CYAN_100_COLOR = "#B2EBF2"

    private const val CYAN_200_COLOR = "#80DEEA"

    private const val CYAN_300_COLOR = "#4DD0E1"

    private const val CYAN_400_COLOR = "#26C6DA"

    private const val CYAN_500_COLOR = "#00BCD4"

    private const val CYAN_600_COLOR = "#00ACC1"

    private const val CYAN_700_COLOR = "#0097A7"

    private const val CYAN_800_COLOR = "#00838F"

    private const val CYAN_900_COLOR = "#006064"

    private const val CYAN_A100_COLOR = "#84FFFF"

    private const val CYAN_A200_COLOR = "#18FFFF"

    private const val CYAN_A300_COLOR = "#00E5FF"

    private const val CYAN_A400_COLOR = "#00B8D4"

    private const val TEAL_50_COLOR = "#E0F2F1"

    private const val TEAL_100_COLOR = "#B2DFDB"

    private const val TEAL_200_COLOR = "#80CBC4"

    private const val TEAL_300_COLOR = "#4DB6AC"

    private const val TEAL_400_COLOR = "#26A69A"

    private const val TEAL_500_COLOR = "#009688"

    private const val TEAL_600_COLOR = "#00897B"

    private const val TEAL_700_COLOR = "#00796B"

    private const val TEAL_800_COLOR = "#00695C"

    private const val TEAL_900_COLOR = "#004D40"

    private const val TEAL_A100_COLOR = "#A7FFEB"

    private const val TEAL_A200_COLOR = "#64FFDA"

    private const val TEAL_A300_COLOR = "#1DE9B6"

    private const val TEAL_A400_COLOR = "#00BFA5"

    private const val DEEP_PURPLE_50_COLOR = "#EDE7F6"

    private const val DEEP_PURPLE_100_COLOR = "#D1C4E9"

    private const val DEEP_PURPLE_200_COLOR = "#B39DDB"

    private const val DEEP_PURPLE_300_COLOR = "#9575CD"

    private const val DEEP_PURPLE_400_COLOR = "#7E57C2"

    private const val DEEP_PURPLE_500_COLOR = "#673AB7"

    private const val DEEP_PURPLE_600_COLOR = "#5E35B1"

    private const val DEEP_PURPLE_700_COLOR = "#512DA8"

    private const val DEEP_PURPLE_800_COLOR = "#4527A0"

    private const val DEEP_PURPLE_900_COLOR = "#311B92"

    private const val DEEP_PURPLE_A100_COLOR = "#B388FF"

    private const val DEEP_PURPLE_A200_COLOR = "#7C4DFF"

    private const val DEEP_PURPLE_A300_COLOR = "#651FFF"

    private const val DEEP_PURPLE_A400_COLOR = "#6200EA"

    private const val INDIGO_50_COLOR = "#E8EAF6"

    private const val INDIGO_100_COLOR = "#C5CAE9"

    private const val INDIGO_200_COLOR = "#9FA8DA"

    private const val INDIGO_300_COLOR = "#7986CB"

    private const val INDIGO_400_COLOR = "#5C6BC0"

    private const val INDIGO_500_COLOR = "#3F51B5"

    private const val INDIGO_600_COLOR = "#3949AB"

    private const val INDIGO_700_COLOR = "#303F9F"

    private const val INDIGO_800_COLOR = "#283593"

    private const val INDIGO_900_COLOR = "#1A237E"

    private const val INDIGO_A100_COLOR = "#8C9EFF"

    private const val INDIGO_A200_COLOR = "#536DFE"

    private const val INDIGO_A300_COLOR = "#3D5AFE"

    private const val INDIGO_A400_COLOR = "#304FFE"

    private const val BLUE_50_COLOR = "#E3F2FD"

    private const val BLUE_100_COLOR = "#BBDEFB"

    private const val BLUE_200_COLOR = "#90CAF9"

    private const val BLUE_300_COLOR = "#64B5F6"

    private const val BLUE_400_COLOR = "#42A5F5"

    private const val BLUE_500_COLOR = "#2196F3"

    private const val BLUE_600_COLOR = "#1E88E5"

    private const val BLUE_700_COLOR = "#1976D2"

    private const val BLUE_800_COLOR = "#1565C0"

    private const val BLUE_900_COLOR = "#0D47A1"

    private const val BLUE_A100_COLOR = "#82B1FF"

    private const val BLUE_A200_COLOR = "#448AFF"

    private const val BLUE_A300_COLOR = "#2979FF"

    private const val BLUE_A400_COLOR = "#2962FF"

    private const val RED_50_COLOR = "#FFEBEE"

    private const val RED_100_COLOR = "#FFCDD2"

    private const val RED_200_COLOR = "#EF9A9A"

    private const val RED_300_COLOR = "#E57373"

    private const val RED_400_COLOR = "#EF5350"

    private const val RED_500_COLOR = "#F44336"

    private const val RED_600_COLOR = "#E53935"

    private const val RED_700_COLOR = "#D32F2F"

    private const val RED_800_COLOR = "#C62828"

    private const val RED_900_COLOR = "#B71C1C"

    private const val RED_A100_COLOR = "#FF8A80"

    private const val RED_A200_COLOR = "#FF5252"

    private const val RED_A300_COLOR = "#FF1744"

    private const val RED_A400_COLOR = "#D50000"

    private const val PINK_50_COLOR = "#FCE4EC"

    private const val PINK_100_COLOR = "#F8BBD0"

    private const val PINK_200_COLOR = "#F48FB1"

    private const val PINK_300_COLOR = "#F06292"

    private const val PINK_400_COLOR = "#EC407A"

    private const val PINK_500_COLOR = "#E91E63"

    private const val PINK_600_COLOR = "#D81B60"

    private const val PINK_700_COLOR = "#C2185B"

    private const val PINK_800_COLOR = "#AD1457"

    private const val PINK_900_COLOR = "#880E4F"

    private const val PINK_A100_COLOR = "#FF80AB"

    private const val PINK_A200_COLOR = "#FF4081"

    private const val PINK_A300_COLOR = "#F50057"

    private const val PINK_A400_COLOR = "#C51162"

    private const val PURPLE_50_COLOR = "#F3E5F5"

    private const val PURPLE_100_COLOR = "#E1BEE7"

    private const val PURPLE_200_COLOR = "#CE93D8"

    private const val PURPLE_300_COLOR = "#BA68C8"

    private const val PURPLE_400_COLOR = "#AB47BC"

    private const val PURPLE_500_COLOR = "#9C27B0"

    private const val PURPLE_600_COLOR = "#8E24AA"

    private const val PURPLE_700_COLOR = "#7B1FA2"

    private const val PURPLE_800_COLOR = "#6A1B9A"

    private const val PURPLE_900_COLOR = "#4A148C"

    private const val PURPLE_A100_COLOR = "#EA80FC"

    private const val PURPLE_A200_COLOR = "#E040FB"

    private const val PURPLE_A300_COLOR = "#D500F9"

    private const val PURPLE_A400_COLOR = "#AA00FF"

    // Texture

    private const val TEXTURE_1 = "text_texture/1.jpg"

    private const val TEXTURE_2 = "text_texture/2.jpg"

    private const val TEXTURE_3 = "text_texture/3.jpg"

    private const val TEXTURE_4 = "text_texture/4.jpg"

    private const val TEXTURE_5 = "text_texture/5.jpg"

    private const val TEXTURE_6 = "text_texture/6.jpg"

    private const val TEXTURE_7 = "text_texture/7.jpg"

    private const val TEXTURE_8 = "text_texture/8.jpg"

    private const val TEXTURE_9 = "text_texture/9.jpg"

    private const val TEXTURE_10 = "text_texture/10.jpg"

    private const val TEXTURE_11 = "text_texture/11.jpg"

    private const val TEXTURE_12 = "text_texture/12.jpg"

    private const val TEXTURE_13 = "text_texture/13.jpg"

    private const val TEXTURE_14 = "text_texture/14.jpg"

    private const val TEXTURE_15 = "text_texture/15.jpg"

    fun colors(): ArrayList<String> {
        return arrayListOf(
            WHITE_COLOR,
            BLACK_COLOR,
            BLUE_GRAY_500_COLOR,
            DEEP_ORANGE_500_COLOR,
            BROWN_500_COLOR,
            GRAY_500_COLOR,
            YELLOW_500_COLOR,
            AMBER_500_COLOR,
            ORANGE_500_COLOR,
            GREEN_500_COLOR,
            LIME_500_COLOR,
            LIGHT_BLUE_500_COLOR,
            CYAN_500_COLOR,
            TEAL_500_COLOR,
            DEEP_PURPLE_500_COLOR,
            INDIGO_500_COLOR,
            BLUE_500_COLOR,
            RED_500_COLOR,
            PINK_500_COLOR,
            PURPLE_500_COLOR,
            BLUE_GRAY_50_COLOR,
            BLUE_GRAY_100_COLOR,
            BLUE_GRAY_200_COLOR,
            BLUE_GRAY_300_COLOR,
            BLUE_GRAY_400_COLOR,
            BLUE_GRAY_500_COLOR,
            BLUE_GRAY_600_COLOR,
            BLUE_GRAY_700_COLOR,
            BLUE_GRAY_800_COLOR,
            BLUE_GRAY_900_COLOR,
            DEEP_ORANGE_50_COLOR,
            DEEP_ORANGE_100_COLOR,
            DEEP_ORANGE_200_COLOR,
            DEEP_ORANGE_300_COLOR,
            DEEP_ORANGE_400_COLOR,
            DEEP_ORANGE_500_COLOR,
            DEEP_ORANGE_600_COLOR,
            DEEP_ORANGE_700_COLOR,
            DEEP_ORANGE_800_COLOR,
            DEEP_ORANGE_900_COLOR,
            DEEP_ORANGE_A100_COLOR,
            DEEP_ORANGE_A200_COLOR,
            DEEP_ORANGE_A400_COLOR,
            DEEP_ORANGE_A700_COLOR,
            BROWN_50_COLOR,
            BROWN_100_COLOR,
            BROWN_200_COLOR,
            BROWN_300_COLOR,
            BROWN_400_COLOR,
            BROWN_500_COLOR,
            BROWN_600_COLOR,
            BROWN_700_COLOR,
            BROWN_800_COLOR,
            BROWN_900_COLOR,
            GRAY_50_COLOR,
            GRAY_100_COLOR,
            GRAY_200_COLOR,
            GRAY_300_COLOR,
            GRAY_400_COLOR,
            GRAY_500_COLOR,
            GRAY_600_COLOR,
            GRAY_700_COLOR,
            GRAY_800_COLOR,
            GRAY_900_COLOR,
            YELLOW_50_COLOR,
            YELLOW_100_COLOR,
            YELLOW_200_COLOR,
            YELLOW_300_COLOR,
            YELLOW_400_COLOR,
            YELLOW_500_COLOR,
            YELLOW_600_COLOR,
            YELLOW_700_COLOR,
            YELLOW_800_COLOR,
            YELLOW_900_COLOR,
            YELLOW_A100_COLOR,
            YELLOW_A200_COLOR,
            YELLOW_A300_COLOR,
            YELLOW_A400_COLOR,
            AMBER_50_COLOR,
            AMBER_100_COLOR,
            AMBER_200_COLOR,
            AMBER_300_COLOR,
            AMBER_400_COLOR,
            AMBER_500_COLOR,
            AMBER_600_COLOR,
            AMBER_700_COLOR,
            AMBER_800_COLOR,
            AMBER_900_COLOR,
            AMBER_A100_COLOR,
            AMBER_A200_COLOR,
            AMBER_A300_COLOR,
            AMBER_A400_COLOR,
            ORANGE_50_COLOR,
            ORANGE_100_COLOR,
            ORANGE_200_COLOR,
            ORANGE_300_COLOR,
            ORANGE_400_COLOR,
            ORANGE_500_COLOR,
            ORANGE_600_COLOR,
            ORANGE_700_COLOR,
            ORANGE_800_COLOR,
            ORANGE_900_COLOR,
            ORANGE_A100_COLOR,
            ORANGE_A200_COLOR,
            ORANGE_A300_COLOR,
            ORANGE_A400_COLOR,
            GREEN_50_COLOR,
            GREEN_100_COLOR,
            GREEN_200_COLOR,
            GREEN_300_COLOR,
            GREEN_400_COLOR,
            GREEN_500_COLOR,
            GREEN_600_COLOR,
            GREEN_700_COLOR,
            GREEN_800_COLOR,
            GREEN_900_COLOR,
            GREEN_A100_COLOR,
            GREEN_A200_COLOR,
            GREEN_A300_COLOR,
            GREEN_A400_COLOR,
            LIGHT_GREEN_50_COLOR,
            LIGHT_GREEN_100_COLOR,
            LIGHT_GREEN_200_COLOR,
            LIGHT_GREEN_300_COLOR,
            LIGHT_GREEN_400_COLOR,
            LIGHT_GREEN_500_COLOR,
            LIGHT_GREEN_600_COLOR,
            LIGHT_GREEN_700_COLOR,
            LIGHT_GREEN_800_COLOR,
            LIGHT_GREEN_900_COLOR,
            LIGHT_GREEN_A100_COLOR,
            LIGHT_GREEN_A200_COLOR,
            LIGHT_GREEN_A300_COLOR,
            LIGHT_GREEN_A400_COLOR,
            LIME_50_COLOR,
            LIME_100_COLOR,
            LIME_200_COLOR,
            LIME_300_COLOR,
            LIME_400_COLOR,
            LIME_500_COLOR,
            LIME_600_COLOR,
            LIME_700_COLOR,
            LIME_800_COLOR,
            LIME_900_COLOR,
            LIME_A100_COLOR,
            LIME_A200_COLOR,
            LIME_A300_COLOR,
            LIME_A400_COLOR,
            LIGHT_BLUE_50_COLOR,
            LIGHT_BLUE_100_COLOR,
            LIGHT_BLUE_200_COLOR,
            LIGHT_BLUE_300_COLOR,
            LIGHT_BLUE_400_COLOR,
            LIGHT_BLUE_500_COLOR,
            LIGHT_BLUE_600_COLOR,
            LIGHT_BLUE_700_COLOR,
            LIGHT_BLUE_800_COLOR,
            LIGHT_BLUE_900_COLOR,
            LIGHT_BLUE_A100_COLOR,
            LIGHT_BLUE_A200_COLOR,
            LIGHT_BLUE_A300_COLOR,
            LIGHT_BLUE_A400_COLOR,
            CYAN_50_COLOR,
            CYAN_100_COLOR,
            CYAN_200_COLOR,
            CYAN_300_COLOR,
            CYAN_400_COLOR,
            CYAN_500_COLOR,
            CYAN_600_COLOR,
            CYAN_700_COLOR,
            CYAN_800_COLOR,
            CYAN_900_COLOR,
            CYAN_A100_COLOR,
            CYAN_A200_COLOR,
            CYAN_A300_COLOR,
            CYAN_A400_COLOR,
            TEAL_50_COLOR,
            TEAL_100_COLOR,
            TEAL_200_COLOR,
            TEAL_300_COLOR,
            TEAL_400_COLOR,
            TEAL_500_COLOR,
            TEAL_600_COLOR,
            TEAL_700_COLOR,
            TEAL_800_COLOR,
            TEAL_900_COLOR,
            TEAL_A100_COLOR,
            TEAL_A200_COLOR,
            TEAL_A300_COLOR,
            TEAL_A400_COLOR,
            DEEP_PURPLE_50_COLOR,
            DEEP_PURPLE_100_COLOR,
            DEEP_PURPLE_200_COLOR,
            DEEP_PURPLE_300_COLOR,
            DEEP_PURPLE_400_COLOR,
            DEEP_PURPLE_500_COLOR,
            DEEP_PURPLE_600_COLOR,
            DEEP_PURPLE_700_COLOR,
            DEEP_PURPLE_800_COLOR,
            DEEP_PURPLE_900_COLOR,
            DEEP_PURPLE_A100_COLOR,
            DEEP_PURPLE_A200_COLOR,
            DEEP_PURPLE_A300_COLOR,
            DEEP_PURPLE_A400_COLOR,
            INDIGO_50_COLOR,
            INDIGO_100_COLOR,
            INDIGO_200_COLOR,
            INDIGO_300_COLOR,
            INDIGO_400_COLOR,
            INDIGO_500_COLOR,
            INDIGO_600_COLOR,
            INDIGO_700_COLOR,
            INDIGO_800_COLOR,
            INDIGO_900_COLOR,
            INDIGO_A100_COLOR,
            INDIGO_A200_COLOR,
            INDIGO_A300_COLOR,
            INDIGO_A400_COLOR,
            BLUE_50_COLOR,
            BLUE_100_COLOR,
            BLUE_200_COLOR,
            BLUE_300_COLOR,
            BLUE_400_COLOR,
            BLUE_500_COLOR,
            BLUE_600_COLOR,
            BLUE_700_COLOR,
            BLUE_800_COLOR,
            BLUE_900_COLOR,
            BLUE_A100_COLOR,
            BLUE_A200_COLOR,
            BLUE_A300_COLOR,
            BLUE_A400_COLOR,
            RED_50_COLOR,
            RED_100_COLOR,
            RED_200_COLOR,
            RED_300_COLOR,
            RED_400_COLOR,
            RED_500_COLOR,
            RED_600_COLOR,
            RED_700_COLOR,
            RED_800_COLOR,
            RED_900_COLOR,
            RED_A100_COLOR,
            RED_A200_COLOR,
            RED_A300_COLOR,
            RED_A400_COLOR,
            PINK_50_COLOR,
            PINK_100_COLOR,
            PINK_200_COLOR,
            PINK_300_COLOR,
            PINK_400_COLOR,
            PINK_500_COLOR,
            PINK_600_COLOR,
            PINK_700_COLOR,
            PINK_800_COLOR,
            PINK_900_COLOR,
            PINK_A100_COLOR,
            PINK_A200_COLOR,
            PINK_A300_COLOR,
            PINK_A400_COLOR,
            PURPLE_50_COLOR,
            PURPLE_100_COLOR,
            PURPLE_200_COLOR,
            PURPLE_300_COLOR,
            PURPLE_400_COLOR,
            PURPLE_500_COLOR,
            PURPLE_600_COLOR,
            PURPLE_700_COLOR,
            PURPLE_800_COLOR,
            PURPLE_900_COLOR,
            PURPLE_A100_COLOR,
            PURPLE_A200_COLOR,
            PURPLE_A300_COLOR,
            PURPLE_A400_COLOR
        )
    }

    private fun textures(): ArrayList<String> {
        return arrayListOf(
            TEXTURE_1,
            TEXTURE_2,
            TEXTURE_3,
            TEXTURE_4,
            TEXTURE_5,
            TEXTURE_6,
            TEXTURE_7,
            TEXTURE_8,
            TEXTURE_9,
            TEXTURE_10,
            TEXTURE_11,
            TEXTURE_12,
            TEXTURE_13,
            TEXTURE_14,
            TEXTURE_15,
        )
    }

    suspend fun getTextureBitmaps(context: Context): ArrayList<Bitmap> =
        withContext(Dispatchers.IO) {
            val bitmaps = ArrayList<Bitmap>()
            textures().forEach {
                BitmapUtil.getBitmapFromAsset(context, it)?.let {bitmap->
                    bitmaps.add(bitmap)
                }
            }
            return@withContext bitmaps
        }

}