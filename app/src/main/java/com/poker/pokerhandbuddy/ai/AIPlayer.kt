package com.poker.pokerhandbuddy.ai

import android.content.Context
import com.poker.pokerhandbuddy.cardgame.Card
import com.poker.pokerhandbuddy.cardgame.Deck
import com.poker.pokerhandbuddy.cardgame.Evaluate
import com.poker.pokerhandbuddy.handevaluator.*
import com.poker.pokerhandbuddy.payout.PayoutCalculator
import timber.log.Timber

object AIPlayer {


    data class TexasHoldemPlayer(
        var hand: MutableSet<Card>,
        var equity: Double,
        var won: Double,
        var tie: Double,
        var handCount: Int,
        var time: Double,
    )

    data class VideoPokerPlayer(
        var hand: List<Card>,
        var allHands: List<Pair<List<Card>, Double>>,
        var trials: Int,
        var bet: Int,
        var time: Double
    )


    const val DEBUG = false

    fun calculateBestHandsJacks(context: Context, bet: Int, hand: List<Card>, numTrials: Int) : VideoPokerPlayer {

        val hands = hand.powerset()
        val handsEvaluated = mutableListOf<Pair<List<Card>, Double>>()

        for (h in hands) {
            val expectedValue = monteCarloEvaluationJacks(context, bet, h.toList(), numTrials)
            handsEvaluated.add(Pair(h.toMutableList(), expectedValue))
        }

        handsEvaluated.sortByDescending { it.second }

        for((idx,h) in handsEvaluated.take(if(DEBUG) 32 else 5).withIndex()) {
            Timber.d("$idx) hand ${h.first} score ${h.second}")
        }

        return VideoPokerPlayer(hand, handsEvaluated, numTrials, bet, 0.0)
    }

    private fun monteCarloEvaluationJacks(context: Context, bet: Int, hand: List<Card>, numTrials: Int) : Double {
        if(DEBUG) {
            Timber.d("====== Monte Carlo Simulation ======")
        }

        var trial = 0
        var expectedPayout = 0.0
        val evals = mutableListOf<HandRank>()
        while (trial < numTrials) {
            val tempHand = Deck.draw5Random(hand.toMutableList())
            val tempHandPC = tempHand.map { card -> PokerCard(Rank(card.rank), Suit.parse(card.suit)) }.toTypedArray()

            val eval = HandEvaluator.evaluateSpecificHand(tempHandPC)
            evals.add(eval)
            val payout = PayoutCalculator.calculatePayout(context, bet, convertEvalRank(hand,eval))

            expectedPayout += payout
            trial += 1
        }

        if(DEBUG) {
            val evalCounts = evals.groupingBy { it }.eachCount()
            Timber.d("${hand.joinToString { it.toString() }} $evalCounts ${expectedPayout/numTrials}")
            Timber.d("====================================")
        }

        return expectedPayout/numTrials
    }

    fun calculateBestHandsDeuces(context: Context, bet: Int, hand: List<Card>, numTrials: Int) : VideoPokerPlayer {

        val hands = hand.powerset()
        val handsEvaluated = mutableListOf<Pair<List<Card>, Double>>()

        for (h in hands) {
            val expectedValue = monteCarloEvaluationDeuces(context, bet, h.toList(), numTrials)
            handsEvaluated.add(Pair(h.toMutableList(), expectedValue))
        }

        handsEvaluated.sortByDescending { it.second }

        for((idx,h) in handsEvaluated.take(if(DEBUG) 32 else 5).withIndex()) {
            Timber.d("$idx) hand ${h.first} score ${h.second}")
        }

        return VideoPokerPlayer(hand, handsEvaluated, numTrials, bet, 0.0)
    }

    private fun monteCarloEvaluationDeuces(context: Context, bet: Int, hand: List<Card>, numTrials: Int) : Double {
        if(DEBUG) {
            Timber.d("====== Monte Carlo Simulation ======")
        }

        var trial = 0
        var expectedPayout = 0.0
        val evals = mutableListOf<Evaluate.Hand>()
        while (trial < numTrials) {
            val tempHand = Deck.draw5Random(hand.toMutableList())
            val eval = Evaluate.evaluate(tempHand)
            evals.add(eval.first)
            val payout = PayoutCalculator.calculatePayoutDeuces(context, bet, eval.first)
            expectedPayout += payout
            trial += 1
        }

        if(DEBUG) {
            val evalCounts = evals.groupingBy { it }.eachCount()
            Timber.d("${hand.joinToString { it.toString() }} $evalCounts ${expectedPayout/numTrials}")
            Timber.d("====================================")
        }

        return expectedPayout/numTrials
    }

    private fun convertEvalRank(hand: List<Card>, rank: HandRank) : PayoutCalculator.Evaluate{
        return when(rank) {
            HandRank.ROYAL_FLUSH -> PayoutCalculator.Evaluate.ROYAL_FLUSH // todo
            HandRank.STRAIGHT_FLUSH -> PayoutCalculator.Evaluate.STRAIGHT_FLUSH
            HandRank.FOUR_OF_A_KIND -> PayoutCalculator.Evaluate.FOUR_OF_A_KIND
            HandRank.FULL_HOUSE -> PayoutCalculator.Evaluate.FULL_HOUSE
            HandRank.FLUSH -> PayoutCalculator.Evaluate.FLUSH
            HandRank.STRAIGHT -> PayoutCalculator.Evaluate.STRAIGHT
            HandRank.THREE_OF_A_KIND -> PayoutCalculator.Evaluate.THREE_OF_A_KIND
            HandRank.TWO_PAIR -> PayoutCalculator.Evaluate.TWO_PAIRS
            HandRank.ONE_PAIR -> {
                // todo this has to be done. A better way is
                //      to put this logic in HandEvaluator.java
                if (Evaluate.isPairJackOrBetter(hand)) {
                    PayoutCalculator.Evaluate.JACKS_OR_BETTER_PAIR
                } else {
                    PayoutCalculator.Evaluate.NOTHING
                }

            }
            HandRank.HIGH_CARD -> PayoutCalculator.Evaluate.NOTHING
        }
    }


    private fun <T> Collection<T>.powerset(): Set<Set<T>> = when {
        isEmpty() -> setOf(setOf())
        else -> drop(1).powerset().let { it + it.map { it + first() } }
    }
}

// instead of checking if pair is jacks or better
// just apply probability of pair being jacks or better 4/13
//val r = Random.nextDouble()
//if(r < .3){
//    PayoutCalculator.Evaluate.JACKS_OR_BETTER_PAIR
//} else {
//    PayoutCalculator.Evaluate.NOTHING
//}