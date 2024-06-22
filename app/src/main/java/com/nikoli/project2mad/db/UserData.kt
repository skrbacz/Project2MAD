package com.nikoli.project2mad.db

import java.sql.Date

/**
 * User data
 *
 * @property name
 * @property sex
 * @property age
 * @constructor Create empty User data
 */
data class UserData (
    var name: String = "",
    var sex: String= "",
    var age: Int = 0
) {
    constructor() : this( "", "", 0)
}