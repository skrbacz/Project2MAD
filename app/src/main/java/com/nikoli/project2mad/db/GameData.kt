package com.nikoli.project2mad.db

import java.sql.Date
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Game data
 *
 * @property name
 * @property date
 * @property accuracy
 * @property reactionTime
 * @constructor Create empty Game data
 */
data class GameData(
    var name: String = "",
    var date: String="",
    var accuracy: Double = 0.0,
    var reactionTime: Double = 0.0,
){
    constructor(): this( "","", 0.0, 0.0)
}
