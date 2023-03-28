package com.example.slide.model

import java.io.Serializable

class StickerPropertyModel: Serializable {
    private val serialVersionUID = 3800737478616389410L

    //贴纸id
    private var stickerId: Long = 0

    //文本
    private var text: String? = null

    //x坐标
    private var xLocation = 0f

    //y坐标
    private var yLocation = 0f

    //角度
    private var degree = 0f

    //缩放值
    private var scaling = 0f

    //气泡顺序
    private var order = 0

    //水平镜像 1镜像 2未镜像
    private var horizonMirror = 0

    //贴纸PNG URL
    private var stickerURL: String? = null

    fun getHorizonMirror(): Int {
        return horizonMirror
    }

    fun setHorizonMirror(horizonMirror: Int) {
        this.horizonMirror = horizonMirror
    }

    fun getStickerURL(): String? {
        return stickerURL
    }

    fun setStickerURL(stickerURL: String?) {
        this.stickerURL = stickerURL
    }

    fun getStickerId(): Long {
        return stickerId
    }

    fun setStickerId(stickerId: Long) {
        this.stickerId = stickerId
    }

    fun getText(): String? {
        return text
    }

    fun setText(text: String?) {
        this.text = text
    }

    fun getxLocation(): Float {
        return xLocation
    }

    fun setxLocation(xLocation: Float) {
        this.xLocation = xLocation
    }

    fun getyLocation(): Float {
        return yLocation
    }

    fun setyLocation(yLocation: Float) {
        this.yLocation = yLocation
    }

    fun getDegree(): Float {
        return degree
    }

    fun setDegree(degree: Float) {
        this.degree = degree
    }

    fun getScaling(): Float {
        return scaling
    }

    fun setScaling(scaling: Float) {
        this.scaling = scaling
    }

    fun getOrder(): Int {
        return order
    }

    fun setOrder(order: Int) {
        this.order = order
    }
}