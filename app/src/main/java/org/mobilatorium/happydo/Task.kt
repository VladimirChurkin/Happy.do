package org.mobilatorium.happydo

data class Task(var id: String, var action: String, var completed: Boolean, var date: String) {
    constructor() : this("", "", false, "2018-08-16, вторник")
}
