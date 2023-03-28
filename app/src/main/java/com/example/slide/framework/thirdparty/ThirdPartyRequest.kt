package com.example.slide.framework.thirdparty

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.text.TextUtils
import android.util.Log
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.example.slide.local.PreferencesHelper
import com.example.slide.util.Utils
import org.json.JSONArray
import org.json.JSONObject
import timber.log.Timber

object ThirdPartyRequest {

    var is_nav = false

    var is_dialog = false

    var is_native = false

    var is_banner = false

    var is_more_fragment = false

    var is_more_screen = false

    var more_app_text = 1

    val apps = ArrayList<NavApp>()

    val dialogs = ArrayList<DialogApp>()

    val banners = ArrayList<BannerApp>()

    private const val TAG = "more"

    var isLoaded = false

    fun getMoreApp(context: Context) {
        if (Utils.isDevMode(context)) return
        if (PreferencesHelper(context).isVip()) return
        val pm: PackageManager = context.packageManager
        if (!isLoaded) {
            val getRequest = JsonObjectRequest(
                Request.Method.GET, APIConstants.REDIRECT_URL, null,
                MyListener(pm), Response.ErrorListener {
                    Timber.d(it.toString())
                }
            )
            getRequest.tag = TAG
            VolleyHelper.getInstance(context).addToRequestQueue(getRequest)
        }
    }

    internal class MyListener(val pm: PackageManager) :
        Response.Listener<JSONObject> {

        override fun onResponse(response: JSONObject) {
            try {
                Timber.d(response.toString())
                is_nav = response.getInt("is_nav") == 1
                is_dialog = response.getInt("is_dialog") == 1
                is_native = response.getInt("is_native") == 1
                is_banner = response.getInt("is_banner") == 1
                is_more_fragment = response.getInt("is_more_fragment") == 1
                is_more_screen = response.getInt("is_more_screen") == 1
                more_app_text = response.getInt("more_app_text")
                val redirectJsonArrayString = response.getString("redirect")
                if (!TextUtils.isEmpty(redirectJsonArrayString)) {
                    val redirectJsonArray = JSONArray(redirectJsonArrayString)

                    for (i in 0 until redirectJsonArray.length()) {
                        val jsonObject = redirectJsonArray.getJSONObject(i)
                        val id = jsonObject.getInt("id")
                        val name = jsonObject.getString("name")
                        val content = jsonObject.getString("content")
                        val icon = jsonObject.getString("icon")
                        val bannerString = jsonObject.getString("banner")
                        val myPackage = jsonObject.getString("package")
                        val minSdk = jsonObject.getInt("minsdk")
                        val type = jsonObject.getInt("type")
                        val percent = jsonObject.getInt("percent")
                        if (!isPackageInstalled(myPackage, pm) && Build.VERSION.SDK_INT >= minSdk) {
                            if (type == TYPE_APP) {
                                val app =
                                    NavApp(name, myPackage, APIConstants.REDIRECT_FOLDER_URL + icon)
                                apps.add(app)
                            } else if (type == TYPE_BANNER) {
                                val banner = BannerApp(
                                    id,name, myPackage, APIConstants.REDIRECT_FOLDER_URL + icon,
                                    APIConstants.REDIRECT_FOLDER_URL + bannerString)
                                banners.add(banner)
                            } else {
                                val dialog = DialogApp(
                                    id,
                                    type,
                                    name,
                                    content,
                                    myPackage,
                                    APIConstants.REDIRECT_FOLDER_URL + icon,
                                    APIConstants.REDIRECT_FOLDER_URL + bannerString,
                                    percent)
                                dialogs.add(dialog)
                            }
                        }
                    }
                }
                isLoaded = true
            } catch (e: Exception) {
                e.printStackTrace()
                Timber.d( "Error: ${e.message}")
            }
        }
    }

    fun isPackageInstalled(
        packageName: String,
        packageManager: PackageManager
    ): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }


    const val TYPE_APP = 0

    const val TYPE_TEXT_DIALOG = 1

    const val TYPE_IMAGE_DIALOG = 2

    const val TYPE_BANNER = 3
}