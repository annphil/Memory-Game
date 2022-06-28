package com.hanna.mymemory.models

//Data class to repr one memory card to list every attribute of a mem card
data class MemoryCard(
        val identifier: Int, //Underlying resource integer ID
        var isFaceUp: Boolean = false,
        var isMatched: Boolean = false

)
