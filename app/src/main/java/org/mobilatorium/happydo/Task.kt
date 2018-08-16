package org.mobilatorium.happydo

data class Task(var action: String, var completed: Boolean, var date: String) {
    constructor() : this("", false, "2018-08-16")
}
