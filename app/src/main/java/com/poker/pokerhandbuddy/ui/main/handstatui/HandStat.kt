package com.poker.pokerhandbuddy.ui.main.handstatui

import com.poker.pokerhandbuddy.cardgame.Card

data class HandStat(val recommendedHand: List<Card>, val fullHand: List<Card>, val expectedValue: Double)