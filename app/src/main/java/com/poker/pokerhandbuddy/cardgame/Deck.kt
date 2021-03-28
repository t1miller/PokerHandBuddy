package com.poker.pokerhandbuddy.cardgame


object Deck {

    private var cards = mutableListOf<Card>()

    init {
        newDeck()
    }

    fun newDeck() {
        cards.clear()
        for (suit in Card.SUITS) {
            for (face in Card.FACES){
                cards.add(
                    Card(
                    Card.FACES.indexOf(face) + 2,
                        suit
                    )
                )
            }
        }
        cards.shuffle()
    }

    fun draw5(): MutableList<Card> {
        val hand = mutableListOf<Card>()
        for (i in 0..4) {
            hand.add(draw1())
        }
        return hand
    }

    fun draw1(): Card {
        return cards.removeAt(0)
    }

    fun removeCards(hand: Set<Card>?) {
        hand?.let { cards.removeAll(hand) }
    }

    fun draw5Random(hand: MutableList<Card>) : List<Card>{
        while (hand.size < 5) {
            val suit = Card.SUITS[(0..3).random()]
            val rank = (2..14).random()
            val randomCard = Card(rank, suit)
            if (!hand.contains(randomCard)){
                hand.add(randomCard)
            }
        }
        return hand
    }

    fun draw7Random(hand: MutableList<Card>, burn: Set<Card>) : List<Card>{
        while (hand.size < 7) {
            val suit = Card.SUITS[(0..3).random()]
            val rank = (2..14).random()
            val randomCard = Card(rank, suit)
            if (!hand.contains(randomCard) && !burn.contains(randomCard)){
                hand.add(randomCard)
            }
        }
        return hand
    }

//    private fun MutableList<Card>.toReadableString(): String{
//        val sorted = this.sortedBy { it.face }
//        var readableString = "====== DECK ======\n"
//        readableString += "size: ${this.size}\n"
//        readableString += sorted.joinToString (separator = "\n"){ it.toString() }
//        readableString += "\n=================\n"
//        return readableString
//    }

    fun suitColor(suit: Char) : String {
        return when(suit) {
            's' -> "black"
            'h' -> "red"
            'd' -> "red"
            'c' -> "black"
            else -> "green"
        }
    }

    fun isSuitRed(suit: Char) : Boolean {
        return suitColor(suit) == "red"
    }
}