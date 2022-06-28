package com.hanna.mymemory.models

//An enum is a special "class" that represents a group of constants (unchangeable variables, like final variables).
enum class BoardSize(val numCards : Int) {
    EASY(8),
    MEDIUM(18),
    HARD(24);

    fun getWidth() : Int {
        return when (this) {
            EASY -> 2
            MEDIUM -> 3
            HARD -> 4
        }
    }

    fun getHeight() : Int {
        return numCards / getWidth()
    }

    fun getNumPairs() : Int{
        return numCards / 2
    }
}