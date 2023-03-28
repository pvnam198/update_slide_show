package com.example.slide.base

class InitViewTools(val layoutRes: () -> Int,
        val hasEventBus: () -> Boolean = { false }
)