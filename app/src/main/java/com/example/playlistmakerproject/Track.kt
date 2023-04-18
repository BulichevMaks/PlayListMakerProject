package com.example.playlistmakerproject

import java.io.Serializable

data class Track(
    val id: Int,
    val trackName: String, // Название композиции
    val artistName: String, // Имя исполнителя
    val trackTimeMillis: String?, // Продолжительность трека
    val artworkUrl100: String, // Ссылка на изображение обложки
    val collectionName: String, // Ссылка на изображение обложки
    val releaseDate: String, // Ссылка на изображение обложки
    val primaryGenreName: String, // Ссылка на изображение обложки
    val country: String, // Ссылка на изображение обложки
    )  : Serializable
