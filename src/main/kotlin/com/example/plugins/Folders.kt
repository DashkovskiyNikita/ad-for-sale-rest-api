package com.example.plugins

import com.example.utils.Constants
import java.io.File

fun configureFolders() {
    val folder = File(Constants.IMAGE_FOLDER)
    if (!folder.exists()) folder.mkdir()
}