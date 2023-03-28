package com.example.slide.util

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

fun File.copyFolder(toDir: File){
    if (exists() && isDirectory) {
        listFiles()?.forEach { file ->
            val copyFile = File(toDir.absolutePath, file.name)
            file.copyTo(copyFile, true)
        }
    }
}

fun runOnUI(onExeCute: () -> Unit) {
    CoroutineScope(Dispatchers.Main).launch {
        onExeCute.invoke()
    }
}