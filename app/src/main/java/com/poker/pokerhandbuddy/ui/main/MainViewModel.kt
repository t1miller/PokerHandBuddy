package com.poker.pokerhandbuddy.ui.main

import android.app.Application
import android.os.CountDownTimer
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.poker.pokerhandbuddy.ai.AIPlayer
import com.poker.pokerhandbuddy.cardgame.Card
import com.poker.pokerhandbuddy.evalnative.EvalNativeBridge
import com.poker.pokerhandbuddy.evalnative.EvalResult
import com.poker.pokerhandbuddy.settings.SettingsUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import kotlin.system.measureTimeMillis


class MainViewModel(application: Application) : AndroidViewModel(application) {

    var isDeucesRunning: MutableLiveData<Boolean> = MutableLiveData(false)
    var deucesResult: MutableLiveData<AIPlayer.VideoPokerPlayer> = MutableLiveData()
    var isJacksRunning: MutableLiveData<Boolean> = MutableLiveData(false)
    var jacksResult: MutableLiveData<AIPlayer.VideoPokerPlayer> = MutableLiveData()
    var isHoldemRunning: MutableLiveData<Boolean> = MutableLiveData(false)
    var holdemResult: MutableLiveData<List<AIPlayer.TexasHoldemPlayer>> = MutableLiveData()

    var percent: MutableLiveData<Int> = MutableLiveData()
    var timer = object : CountDownTimer(SettingsUtils.getMaxTimeHoldem(application)*1000.toLong(), 250) {
        override fun onTick(millisUntilFinished: Long) {
            val perc = 100 - millisUntilFinished/100
            percent.postValue(perc.toInt())
        }

        override fun onFinish() {
            percent.postValue(100)
        }
    }

    fun getBestHandDeuces(hand: List<Card>,
                         bet: Int = SettingsUtils.getBetDeuces(getApplication()),
                         numTrials: Int = SettingsUtils.getNumTrialsDeuces(getApplication())) {
        viewModelScope.launch {
            runGetBestHandDeuces(bet, hand, numTrials)
        }
    }

    private suspend fun runGetBestHandDeuces(bet:Int, hand: List<Card>, numTrials: Int) {
        if(hand.size < 5) Toast.makeText(getApplication(), "Choose 5 cards: ${hand.size}/5", Toast.LENGTH_LONG).show()

        withContext(Dispatchers.IO) {
            isDeucesRunning.postValue(true)
            var result: AIPlayer.VideoPokerPlayer
            val timeInMillis = measureTimeMillis {
                result = AIPlayer.calculateBestHandsDeuces(getApplication(), bet,hand, numTrials)
            }
            Timber.d("Deuces monteCarlo simulation took $timeInMillis ms")

            result.time = timeInMillis/1000.0
            deucesResult.postValue(result)
            isDeucesRunning.postValue(false)
        }
    }

    fun getBestHandJacks(hand: List<Card>,
                         bet: Int = SettingsUtils.getBetJacks(getApplication()),
                         numTrials: Int = SettingsUtils.getNumTrialsJacks(getApplication())) {
        viewModelScope.launch {
            runGetBestHandJacks(bet, hand, numTrials)
        }
    }

    private suspend fun runGetBestHandJacks(bet:Int, hand: List<Card>, numTrials: Int) {
        if(hand.size < 5) Toast.makeText(getApplication(), "Choose 5 cards: ${hand.size}/5", Toast.LENGTH_LONG).show()
        withContext(Dispatchers.IO) {
            isJacksRunning.postValue(true)
            var result: AIPlayer.VideoPokerPlayer
            val timeInMillis = measureTimeMillis {
                result = AIPlayer.calculateBestHandsJacks(getApplication(), bet,hand, numTrials)
            }
            Timber.d("Jacks or better monteCarlo simulation took $timeInMillis ms")

            result.time = timeInMillis/1000.0
            jacksResult.postValue(result)
            isJacksRunning.postValue(false)
        }
    }

    fun getBestHand(
        players: List<AIPlayer.TexasHoldemPlayer>,
        board: String,
        dead: String,
        threads: Int = SettingsUtils.getThreadsHoldem(getApplication()),
        stopTime: Double = SettingsUtils.getMaxTimeHoldem(getApplication()).toDouble(),
        stdError: Double = .0000002
    ) {
        viewModelScope.launch {
            runGetBestHand(players, board, dead, threads, stopTime, stdError)
        }
    }


    private suspend fun runGetBestHand(
        pPlayers: List<AIPlayer.TexasHoldemPlayer>,
        board: String,
        dead: String,
        threads: Int,
        stopTime: Double,
        stdError: Double
    ) {

        timer.cancel()
        timer.start()
        isHoldemRunning.postValue(true)
        val playersCards = mutableListOf<String>()
        for (p in pPlayers){
            val handString = p.hand.joinToString(separator = "")
            playersCards.add(if (handString.isEmpty()) "random" else handString)
        }
        Timber.d("board: $board dead: $dead playercards: $playersCards threads: ${threads} stop time: ${stopTime}")

        var result: EvalResult
        withContext(Dispatchers.IO) {
            val timeInMillis = measureTimeMillis {
                result = EvalNativeBridge.evaluateJNI(
                    stopTime,
                    threads,
                    stdError,
                    board,
                    dead,
                    playersCards.toTypedArray()
                )
            }

            timer.cancel()
            percent.postValue(100)
            for ((i, player) in pPlayers.withIndex()){
                player.equity = result.equity[i]*100
                player.won = (result.win[i]/result.hands.toDouble())*100
                player.tie = (result.tie[i]/result.hands.toDouble())*100
                player.handCount = result.hands
                player.time = result.time
            }
            holdemResult.postValue(pPlayers)
            isHoldemRunning.postValue(false)

            Timber.d("texas holdem monteCarlo simulation took $timeInMillis ms")
            Timber.d("hands: ${result.hands} speed: ${result.speed} time: ${result.time}")
            for ((i, r) in result.equity.withIndex()){
                Timber.d("player $i: equity: ${result.equity[i]} win: ${result.win[i]} tie: ${result.tie[i]} ")
            }
        }
    }
}