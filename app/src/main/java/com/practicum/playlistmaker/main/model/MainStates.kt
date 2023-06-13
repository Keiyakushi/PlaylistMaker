package com.practicum.playlistmaker.main.model

sealed class MainStates {
    object Search : MainStates()
    object Library : MainStates()
    object Settings : MainStates()
}
