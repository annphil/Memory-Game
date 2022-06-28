package com.hanna.mymemory.models

import com.hanna.mymemory.utils.DEFAULT_ICONS

class MemoryGame(private val boardSize: BoardSize){

    val cards: List<MemoryCard>
    var numPairsFound = 0

    private var numCardFlips = 0
    private var indexOfSingleSelectedCard: Int? = null

    init {
        val chosenImages = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedImages = (chosenImages + chosenImages).shuffled()
        cards = randomizedImages.map{ MemoryCard(it) }
        // Transform randomized images to a new list of mem cards containing all of the randomized images using map
        // it -> identifier ie, current randomized image we're mapping over
        // Constructing list of cards based on board size
    }

    fun flipCard(position: Int) : Boolean {
        numCardFlips++
        val card: MemoryCard = cards[position]
        var foundMatch: Boolean = false

        //Three cards:
        //0 cards previously flipped over = restore cards + flip over selected one
        //1 card previously flipped over = flip over selected one + check if images match
        //2 cards previously flipped over = restore cards + flip over selected one
        if (indexOfSingleSelectedCard == null) {
            //numCards flipped is 0 or 2
            restoreCards()
            indexOfSingleSelectedCard = position
        }
        else {
            // exactly 1 card previously flipped over.
            foundMatch = checkForMatch(indexOfSingleSelectedCard!!, position)
            indexOfSingleSelectedCard = null
        }
        card.isFaceUp = !card.isFaceUp
        return  foundMatch

        }

    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        if(cards[position1].identifier != cards[position2].identifier)
            return false
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound++
        return true
    }

    private fun restoreCards() {
            for (card : MemoryCard in cards) {
                if(!card.isMatched) {
                    card.isFaceUp = false
                }
            }
        }

    fun haveWonGame(): Boolean {
        return numPairsFound == boardSize.getNumPairs()
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumMoves(): Int {
        return numCardFlips / 2
    }


}