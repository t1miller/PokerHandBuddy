package com.poker.pokerhandbuddy.payout

import android.content.Context
import com.poker.pokerhandbuddy.cardgame.Evaluate
import com.poker.pokerhandbuddy.settings.SettingsUtils

object PayoutCalculator {

    enum class Evaluate {
        SEQUENTIAL_ROYAL_FLUSH,
        NATURAL_ROYAL_FLUSH,
        ROYAL_FLUSH,
        FOUR_DEUCES,
        WILD_ROYAL_FLUSH,
        FIVE_OF_A_KIND,
        STRAIGHT_FLUSH,
        FOUR_OF_A_KIND,
        FULL_HOUSE,
        FLUSH,
        STRAIGHT,
        THREE_OF_A_KIND,
        TWO_PAIRS,
        JACKS_OR_BETTER_PAIR,
        NOTHING,
    }


    enum class PayTableTypesJacks{
        _6_5_95,
        _7_5_96,
        _8_5_97,
        _9_5_98,
        _9_6_99,
    }

    enum class PayTableTypesDeuces(val readableName: String) {
        _101_28("101.28% Seq Royal Flush"),
        _100_76("100.76% Full Pay"),
        _100_36("100.36% 5–8–15–25"),
        _99_89("99.89% 5-9-15-20"),
        _99_81("99.81% 5–9–12–25"),
        _98_94("98.94% 5–9–12–20"),
        _96_77("96.77% Colorado Deuces"),
        _94_82("94.82% 4–10–15–25"),
        _94_34("94.34% 3–4–9–15–25"),
        _92_05("92.05% 3–4–8–12–20")
        //don't change order
    }

    private val payoutMapDeuces = mapOf(
        PayTableTypesDeuces._101_28 to mapOf(
            Evaluate.SEQUENTIAL_ROYAL_FLUSH to listOf(12000,24000,36000,48000,60000),
            Evaluate.NATURAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.FOUR_DEUCES to listOf(200,400,600,800,1000),
            Evaluate.WILD_ROYAL_FLUSH to listOf(25,50,75,100,125),
            Evaluate.FIVE_OF_A_KIND to listOf(15,30,45,60,75),
            Evaluate.STRAIGHT_FLUSH to listOf(9,18,27,36,45),
            Evaluate.FOUR_OF_A_KIND to listOf(5,10,15,20,25),
            Evaluate.FULL_HOUSE to listOf(3,6,9,12,15),
            Evaluate.FLUSH to listOf(2,4,6,8,10),
            Evaluate.STRAIGHT to listOf(2,4,6,8,10),
            Evaluate.THREE_OF_A_KIND to listOf(1,2,3,4,5)
        ),
        PayTableTypesDeuces._100_76 to mapOf(
            Evaluate.SEQUENTIAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.NATURAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.FOUR_DEUCES to listOf(200,400,600,800,1000),
            Evaluate.WILD_ROYAL_FLUSH to listOf(25,50,75,100,125),
            Evaluate.FIVE_OF_A_KIND to listOf(15,30,45,60,75),
            Evaluate.STRAIGHT_FLUSH to listOf(9,18,27,36,45),
            Evaluate.FOUR_OF_A_KIND to listOf(5,10,15,20,25),
            Evaluate.FULL_HOUSE to listOf(3,6,9,12,15),
            Evaluate.FLUSH to listOf(2,4,6,8,10),
            Evaluate.STRAIGHT to listOf(2,4,6,8,10),
            Evaluate.THREE_OF_A_KIND to listOf(1,2,3,4,5)
        ),
        PayTableTypesDeuces._100_36 to mapOf(
            Evaluate.SEQUENTIAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.NATURAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.FOUR_DEUCES to listOf(200,400,600,800,1000),
            Evaluate.WILD_ROYAL_FLUSH to listOf(25,50,75,100,125),
            Evaluate.FIVE_OF_A_KIND to listOf(15,30,45,60,75),
            Evaluate.STRAIGHT_FLUSH to listOf(8,16,24,32,40),
            Evaluate.FOUR_OF_A_KIND to listOf(5,10,15,20,25),
            Evaluate.FULL_HOUSE to listOf(3,6,9,12,15),
            Evaluate.FLUSH to listOf(2,4,6,8,10),
            Evaluate.STRAIGHT to listOf(2,4,6,8,10),
            Evaluate.THREE_OF_A_KIND to listOf(1,2,3,4,5)
        ),
        PayTableTypesDeuces._99_89 to mapOf(
            Evaluate.SEQUENTIAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.NATURAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.FOUR_DEUCES to listOf(200,400,600,800,1000),
            Evaluate.WILD_ROYAL_FLUSH to listOf(20,40,60,80,100),
            Evaluate.FIVE_OF_A_KIND to listOf(15,30,45,60,75),
            Evaluate.STRAIGHT_FLUSH to listOf(9,18,27,36,45),
            Evaluate.FOUR_OF_A_KIND to listOf(5,10,15,20,25),
            Evaluate.FULL_HOUSE to listOf(3,6,9,12,15),
            Evaluate.FLUSH to listOf(2,4,6,8,10),
            Evaluate.STRAIGHT to listOf(2,4,6,8,10),
            Evaluate.THREE_OF_A_KIND to listOf(1,2,3,4,5)
        ),
        PayTableTypesDeuces._99_81 to mapOf(
            Evaluate.SEQUENTIAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.NATURAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.FOUR_DEUCES to listOf(200,400,600,800,1000),
            Evaluate.WILD_ROYAL_FLUSH to listOf(25,50,75,100,125),
            Evaluate.FIVE_OF_A_KIND to listOf(12,24,36,48,60),
            Evaluate.STRAIGHT_FLUSH to listOf(9,18,27,36,45),
            Evaluate.FOUR_OF_A_KIND to listOf(5,10,15,20,25),
            Evaluate.FULL_HOUSE to listOf(3,6,9,12,15),
            Evaluate.FLUSH to listOf(2,4,6,8,10),
            Evaluate.STRAIGHT to listOf(2,4,6,8,10),
            Evaluate.THREE_OF_A_KIND to listOf(1,2,3,4,5)
        ),
        PayTableTypesDeuces._98_94 to mapOf(
            Evaluate.SEQUENTIAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.NATURAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.FOUR_DEUCES to listOf(200,400,600,800,1000),
            Evaluate.WILD_ROYAL_FLUSH to listOf(20,40,60,80,100),
            Evaluate.FIVE_OF_A_KIND to listOf(12,24,36,48,60),
            Evaluate.STRAIGHT_FLUSH to listOf(9,18,27,36,45),
            Evaluate.FOUR_OF_A_KIND to listOf(5,10,15,20,25),
            Evaluate.FULL_HOUSE to listOf(3,6,9,12,15),
            Evaluate.FLUSH to listOf(2,4,6,8,10),
            Evaluate.STRAIGHT to listOf(2,4,6,8,10),
            Evaluate.THREE_OF_A_KIND to listOf(1,2,3,4,5)
        ),
        PayTableTypesDeuces._96_77 to mapOf(
            Evaluate.SEQUENTIAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.NATURAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.FOUR_DEUCES to listOf(200,400,600,800,1000),
            Evaluate.WILD_ROYAL_FLUSH to listOf(25,50,75,100,125),
            Evaluate.FIVE_OF_A_KIND to listOf(16,32,48,64,80),
            Evaluate.STRAIGHT_FLUSH to listOf(13,26,39,52,65),
            Evaluate.FOUR_OF_A_KIND to listOf(4,8,12,16,20),
            Evaluate.FULL_HOUSE to listOf(3,6,9,12,15),
            Evaluate.FLUSH to listOf(2,4,6,8,10),
            Evaluate.STRAIGHT to listOf(2,4,6,8,10),
            Evaluate.THREE_OF_A_KIND to listOf(1,2,3,4,5)
        ),
        PayTableTypesDeuces._94_82 to mapOf(
            Evaluate.SEQUENTIAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.NATURAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.FOUR_DEUCES to listOf(200,400,600,800,1000),
            Evaluate.WILD_ROYAL_FLUSH to listOf(25,50,75,100,125),
            Evaluate.FIVE_OF_A_KIND to listOf(15,30,45,60,75),
            Evaluate.STRAIGHT_FLUSH to listOf(10,20,30,40,50),
            Evaluate.FOUR_OF_A_KIND to listOf(4,8,12,16,20),
            Evaluate.FULL_HOUSE to listOf(3,6,9,12,15),
            Evaluate.FLUSH to listOf(2,4,6,8,10),
            Evaluate.STRAIGHT to listOf(2,4,6,8,10),
            Evaluate.THREE_OF_A_KIND to listOf(1,2,3,4,5)
        ),
        PayTableTypesDeuces._94_34 to mapOf(
            Evaluate.SEQUENTIAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.NATURAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.FOUR_DEUCES to listOf(200,400,600,800,1000),
            Evaluate.WILD_ROYAL_FLUSH to listOf(25,50,75,100,125),
            Evaluate.FIVE_OF_A_KIND to listOf(15,30,45,60,75),
            Evaluate.STRAIGHT_FLUSH to listOf(9,18,27,36,45),
            Evaluate.FOUR_OF_A_KIND to listOf(4,8,12,16,20),
            Evaluate.FULL_HOUSE to listOf(3,6,9,12,15),
            Evaluate.FLUSH to listOf(2,4,6,8,10),
            Evaluate.STRAIGHT to listOf(2,4,6,8,10),
            Evaluate.THREE_OF_A_KIND to listOf(1,2,3,4,5)
        ),
        PayTableTypesDeuces._92_05 to mapOf(
            Evaluate.SEQUENTIAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.NATURAL_ROYAL_FLUSH to listOf(800,1600,2400,3200,4000),
            Evaluate.FOUR_DEUCES to listOf(200,400,600,800,1000),
            Evaluate.WILD_ROYAL_FLUSH to listOf(20,40,60,80,100),
            Evaluate.FIVE_OF_A_KIND to listOf(12,24,36,48,60),
            Evaluate.STRAIGHT_FLUSH to listOf(8,16,24,32,40),
            Evaluate.FOUR_OF_A_KIND to listOf(4,8,12,16,20),
            Evaluate.FULL_HOUSE to listOf(3,6,9,12,15),
            Evaluate.FLUSH to listOf(2,4,6,8,10),
            Evaluate.STRAIGHT to listOf(2,4,6,8,10),
            Evaluate.THREE_OF_A_KIND to listOf(1,2,3,4,5)
        )
    )


    private val payoutMapJacks = mapOf(
        PayTableTypesJacks._9_6_99 to mapOf(
            Evaluate.ROYAL_FLUSH to listOf(250, 500, 750, 1000, 4000),
            Evaluate.STRAIGHT_FLUSH to listOf(50, 100, 150, 200, 250),
            Evaluate.FOUR_OF_A_KIND to listOf(25, 50, 75, 100, 125),
            Evaluate.FULL_HOUSE to listOf(9, 18, 27, 36, 45),
            Evaluate.FLUSH to listOf(6, 12, 18, 24, 30),
            Evaluate.STRAIGHT to listOf(4, 8, 12, 16, 20),
            Evaluate.THREE_OF_A_KIND to listOf(3, 6, 9, 12, 15),
            Evaluate.TWO_PAIRS to listOf(2, 4, 6, 8, 10),
            Evaluate.JACKS_OR_BETTER_PAIR to listOf(1, 2, 3, 4, 5),
            Evaluate.NOTHING to listOf(-1, -2, -3, -4, -5)),

        PayTableTypesJacks._9_5_98 to mapOf(
            Evaluate.ROYAL_FLUSH to listOf(250, 500, 750, 1000, 4000),
            Evaluate.STRAIGHT_FLUSH to listOf(50, 100, 150, 200, 250),
            Evaluate.FOUR_OF_A_KIND to listOf(25, 50, 75, 100, 125),
            Evaluate.FULL_HOUSE to listOf(9, 18, 27, 36, 45),
            Evaluate.FLUSH to listOf(5, 10, 15, 20, 25),
            Evaluate.STRAIGHT to listOf(4, 8, 12, 16, 20),
            Evaluate.THREE_OF_A_KIND to listOf(3, 6, 9, 12, 15),
            Evaluate.TWO_PAIRS to listOf(2, 4, 6, 8, 10),
            Evaluate.JACKS_OR_BETTER_PAIR to listOf(1, 2, 3, 4, 5),
            Evaluate.NOTHING to listOf(-1, -2, -3, -4, -5)),

        PayTableTypesJacks._8_5_97 to mapOf(
            Evaluate.ROYAL_FLUSH to listOf(250, 500, 750, 1000, 4000),
            Evaluate.STRAIGHT_FLUSH to listOf(50, 100, 150, 200, 250),
            Evaluate.FOUR_OF_A_KIND to listOf(25, 50, 75, 100, 125),
            Evaluate.FULL_HOUSE to listOf(8, 16, 24, 32, 40),
            Evaluate.FLUSH to listOf(5, 10, 15, 20, 25),
            Evaluate.STRAIGHT to listOf(4, 8, 12, 16, 20),
            Evaluate.THREE_OF_A_KIND to listOf(3, 6, 9, 12, 15),
            Evaluate.TWO_PAIRS to listOf(2, 4, 6, 8, 10),
            Evaluate.JACKS_OR_BETTER_PAIR to listOf(1, 2, 3, 4, 5),
            Evaluate.NOTHING to listOf(-1, -2, -3, -4, -5)),

        PayTableTypesJacks._7_5_96 to mapOf(
            Evaluate.ROYAL_FLUSH to listOf(250, 500, 750, 1000, 4000),
            Evaluate.STRAIGHT_FLUSH to listOf(50, 100, 150, 200, 250),
            Evaluate.FOUR_OF_A_KIND to listOf(25, 50, 75, 100, 125),
            Evaluate.FULL_HOUSE to listOf(7, 14, 21, 28, 35),
            Evaluate.FLUSH to listOf(5, 10, 15, 20, 25),
            Evaluate.STRAIGHT to listOf(4, 8, 12, 16, 20),
            Evaluate.THREE_OF_A_KIND to listOf(3, 6, 9, 12, 15),
            Evaluate.TWO_PAIRS to listOf(2, 4, 6, 8, 10),
            Evaluate.JACKS_OR_BETTER_PAIR to listOf(1, 2, 3, 4, 5),
            Evaluate.NOTHING to listOf(-1, -2, -3, -4, -5)),

        PayTableTypesJacks._6_5_95 to mapOf(
            Evaluate.ROYAL_FLUSH to listOf(250, 500, 750, 1000, 4000),
            Evaluate.STRAIGHT_FLUSH to listOf(50, 100, 150, 200, 250),
            Evaluate.FOUR_OF_A_KIND to listOf(25, 50, 75, 100, 125),
            Evaluate.FULL_HOUSE to listOf(6, 12, 18, 24, 30),
            Evaluate.FLUSH to listOf(5, 10, 15, 20, 25),
            Evaluate.STRAIGHT to listOf(4, 8, 12, 16, 20),
            Evaluate.THREE_OF_A_KIND to listOf(3, 6, 9, 12, 15),
            Evaluate.TWO_PAIRS to listOf(2, 4, 6, 8, 10),
            Evaluate.JACKS_OR_BETTER_PAIR to listOf(1, 2, 3, 4, 5),
            Evaluate.NOTHING to listOf(-1, -2, -3, -4, -5))
    )

    fun calculatePayout(context: Context?, bet: Int?, evalHand: Evaluate?) : Int {
        if(bet == null) return -1
        return payoutMapJacks[SettingsUtils.getPayoutTableJacks(context)]?.get(evalHand)?.get(bet-1) ?: -1*bet
    }

//    fun calculatePayoutDeuces(context: Context?, bet: Int?, evalHand: Evaluate?) : Int {
//        if(bet == null) return -1
//        return payoutMapDeuces[SettingsUtils.getPayoutTableDeuces(context)]?.get(evalHand)?.get(bet-1) ?: -1*bet
//    }

    fun calculatePayoutDeuces(context: Context?, bet: Int?, evalHand: com.poker.pokerhandbuddy.cardgame.Evaluate.Hand?) : Int {
        if(bet == null) return -1
        val eval = getEvaluate(evalHand)
        return payoutMapDeuces[SettingsUtils.getPayoutTableDeuces(context)]?.get(eval)?.get(bet-1) ?: -1*bet
    }

    private fun getEvaluate(hand: com.poker.pokerhandbuddy.cardgame.Evaluate.Hand?) : Evaluate{
        return when(hand){
            com.poker.pokerhandbuddy.cardgame.Evaluate.Hand.SEQUENTIAL_ROYAL_FLUSH -> Evaluate.SEQUENTIAL_ROYAL_FLUSH
            com.poker.pokerhandbuddy.cardgame.Evaluate.Hand.NATURAL_ROYAL_FLUSH -> Evaluate.NATURAL_ROYAL_FLUSH
            com.poker.pokerhandbuddy.cardgame.Evaluate.Hand.FOUR_DEUCES -> Evaluate.FOUR_DEUCES
            com.poker.pokerhandbuddy.cardgame.Evaluate.Hand.WILD_ROYAL_FLUSH -> Evaluate.WILD_ROYAL_FLUSH
            com.poker.pokerhandbuddy.cardgame.Evaluate.Hand.FIVE_OF_A_KIND -> Evaluate.FIVE_OF_A_KIND
            com.poker.pokerhandbuddy.cardgame.Evaluate.Hand.STRAIGHT_FLUSH -> Evaluate.STRAIGHT_FLUSH
            com.poker.pokerhandbuddy.cardgame.Evaluate.Hand.FOUR_OF_A_KIND -> Evaluate.FOUR_OF_A_KIND
            com.poker.pokerhandbuddy.cardgame.Evaluate.Hand.FULL_HOUSE -> Evaluate.FULL_HOUSE
            com.poker.pokerhandbuddy.cardgame.Evaluate.Hand.FLUSH -> Evaluate.FLUSH
            com.poker.pokerhandbuddy.cardgame.Evaluate.Hand.STRAIGHT -> Evaluate.STRAIGHT
            com.poker.pokerhandbuddy.cardgame.Evaluate.Hand.THREE_OF_A_KIND -> Evaluate.THREE_OF_A_KIND
            com.poker.pokerhandbuddy.cardgame.Evaluate.Hand.NOTHING -> Evaluate.NOTHING
            else -> Evaluate.NOTHING
        }
    }
}