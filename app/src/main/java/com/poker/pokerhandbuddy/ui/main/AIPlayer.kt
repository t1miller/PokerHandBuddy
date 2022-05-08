package com.poker.pokerhandbuddy.ui.main

import com.poker.pokerhandbuddy.cardgame.Deck
import com.poker.pokerhandbuddy.cardgame.Card
import com.poker.pokerhandbuddy.handevaluatorfast.RankPokerHandPublic
import timber.log.Timber
import java.util.*


object AIPlayer {

    interface Progress{
        fun onPercentDone(percent: Int)
    }

    data class TexasHoldemPlayer(
        var community: MutableSet<Card>,
        var hand: MutableSet<Card>,
        var won: Int,
        var loss: Int,
        var tie: Int,
        var expectedHand: RankPokerHandPublic.Combination = RankPokerHandPublic.Combination.HIGH_CARD,
//        var evalCounts: MutableMap<HandRank, Int> =
//        mutableMapOf(
//            HandRank.ROYAL_FLUSH to 0,
//            HandRank.STRAIGHT_FLUSH to 0,
//            HandRank.FOUR_OF_A_KIND to 0,
//            HandRank.FULL_HOUSE to 0,
//            HandRank.FLUSH to 0,
//            HandRank.STRAIGHT to 0,
//            HandRank.THREE_OF_A_KIND to 0,
//            HandRank.TWO_PAIR to 0,
//            HandRank.ONE_PAIR to 0,
//            HandRank.HIGH_CARD to 0
//        )
    ){
        override fun toString(): String {
            return "community: $community hand: $hand won: $won loss: $loss tie:$tie"
        }
    }


    init {
        RankPokerHandPublic.initRankPokerHand7()
    }

    // todo make multi threed with a thread per player

    // todo for each monte carlo trial
    //      - draw fresh missing cards for all the players
    //      - evaluate all players hands and assign a int rank
    //      - compare players int ranks tallying number of wins/ties/lose
    //      - (won,loss,tie)
    //      - (Hand.Eval) += 1
    //
    // todo At the end
    //      - Each player has
    //            - (Royal: 2, Straight: 10, ... HighCard: 10000)
    //            - (Won: 100 Loss: 21 Tie: 43)
    //            - Win = won_count/total_won_count
    //            - Loss = loss_count/total_loss_count
    //            - Tie = tie_count/total_tie
    fun calculateHoldemGamePlayer(players: List<TexasHoldemPlayer>, discarded: List<Card>, numTrials: Int, progress: Progress) {
        val buffer = IntArray(4)
        val compareByValue: Comparator<Pair<Int,Int>> = compareBy { -1*it.second }
        val pQueue = PriorityQueue(players.size, compareByValue)
        val playerScores = IntArray(players.size)
        val percent = numTrials/100

        val evals = MutableList(players.size){ mutableListOf<RankPokerHandPublic.Combination>()}


        for (i in 0..numTrials) {
            val cardsOnTable = players[0].community.toMutableSet()
            cardsOnTable += discarded
            for ((j,player) in players.withIndex()) {

                // todo this is wrong. Full house vs two pair wrong
                val sevenCards = Deck.draw7Random(player.hand.toMutableList(),cardsOnTable).toMutableList()
                cardsOnTable += sevenCards

                val (ranks, suits) = sevenCards.toFastEvalFormat()
                val rank = RankPokerHandPublic.rankPokerHand7(ranks, suits, buffer)
                pQueue.add(Pair(i, rank))
                playerScores[j] = rank
                Timber.d("Rank ${RankPokerHandPublic.Combination.fromRank(rank.shr(26))}")

                evals[j].add(RankPokerHandPublic.Combination.fromRank(rank.shr(26)))

            }

            val topScore = pQueue.peek()?.second
            val topScoreCount = pQueue.count { it.second == topScore }

            for (j in players.indices){
                if(playerScores[j] == topScore && topScoreCount == 1) {
                    players[j].won += 1
                } else if(playerScores[j] == topScore && topScoreCount > 1) {
                    players[j].tie += 1
                } else {
                    players[j].loss += 1
                }
            }

            if(i % percent == 0) {
                progress.onPercentDone(i/percent)
            }
            pQueue.clear()
        }

        for((i,player) in players.withIndex()) {
            player.expectedHand = evals[i].groupingBy { it }.eachCount().maxByOrNull { it.value }?.key ?: RankPokerHandPublic.Combination.HIGH_CARD
        }
    }

//    fun calculateBestHandsJacks(context: Context, bet: Int, hand: List<Card>, numTrials: Int) : AIDecision {
//
//        val hands = hand.powerset()
//        val handsEvaluated = mutableListOf<Pair<List<Card>, Double>>()
//
//
//        for (h in hands) {
//            val expectedValue =
//                monteCarloEvaluation(
//                    context,
//                    bet,
//                    h.toList(),
//                    numTrials
//                )
//            handsEvaluated.add(Pair(h.toMutableList(), expectedValue))
//        }
//
//        handsEvaluated.sortByDescending { it.second }
//
//        for((idx, h) in handsEvaluated.take(if (DEBUG) 32 else 5).withIndex()) {
//            Timber.d("$idx) hand ${h.first} score ${h.second}")
//        }
//        return AIDecision(
//            numTrials,
//            bet,
//            hand,
//            handsEvaluated
//        )
//    }

//    private fun monteCarloEvaluation(context: Context, bet: Int, hand: List<Card>, numTrials: Int) : Double {
//        if(DEBUG) {
//            Timber.d("====== Monte Carlo Simulation ======")
//        }
//
//        var trial = 0
//        var expectedPayout = 0.0
//        val evals = mutableListOf<HandRank>()
//        while (trial < numTrials) {
//            val tempHand = Deck.draw5Random(hand.toMutableList())
//            val tempHandPC = tempHand.map { card -> PokerCard(
//                Rank(card.face),
//                Suit.parse(card.suit)
//            ) }.toTypedArray()
//
//            val eval = HandEvaluator.evaluateSpecificHand(tempHandPC)
//            evals.add(eval)
//            val payout = payout[eval] ?: -5
//
//            expectedPayout += payout
//            trial += 1
//        }
//
//        if(DEBUG) {
//            val evalCounts = evals.groupingBy { it }.eachCount()
//            Timber.d("${hand.joinToString { it.toString() }} $evalCounts ${expectedPayout / numTrials}")
//            Timber.d("====================================")
//        }
//        return expectedPayout/numTrials
//    }
//
//

//    private fun <T> Collection<T>.powerset(): Set<Set<T>> = when {
//        isEmpty() -> setOf(setOf())
//        else -> drop(1).powerset().let { it + it.map { it + first() } }
//    }

    private fun List<Card>.toFastEvalFormat(): Pair<IntArray,IntArray> {
        val suits = IntArray(7)
        val ranks = IntArray(7)

//        Timber.d("$this")
        for ((i, card) in this.withIndex()){
            suits[i] = Card.SUITS.indexOf(card.suit)
            ranks[i] = card.rank - 2
        }
        return Pair(ranks, suits)
    }
}