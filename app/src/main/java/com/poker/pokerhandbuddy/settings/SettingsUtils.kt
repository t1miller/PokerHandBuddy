package com.poker.pokerhandbuddy.settings

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.poker.pokerhandbuddy.R
import com.poker.pokerhandbuddy.payout.PayoutCalculator
import timber.log.Timber

object SettingsUtils {

    object Defaults{
        const val SOUND = true
        const val CHOOSE_CARDBACK = 0
        const val MONTE_CARLO_TRIALS_HOLDEM = 10000
        const val PAYOUT_TABLE_JACKS = "9/6 – 99.54%"
        const val PAYOUT_TABLE_DEUCES = "payout_table_deuces"
        const val BET_JACKS = 5
        const val BET_DEUCES = 5
        const val MAX_TIME_TEXAS_HOLDEM = 1
        const val THREADS_TEXAS_HOLDEM = 2
        const val JACKS_MONTE_CARLO_TRIALS = 15000
        const val DEUCES_MONTE_CARLO_TRIALS = 15000
    }

    object Keys{
        const val SOUND = "sound"
        const val CHOOSE_CARDBACK = "choose_cardback"
        const val MONTE_CARLO_TRIALS_HOLDEM = "montecarlo_texasholdem"
        const val PAYOUT_TABLE_JACKS = "payout_table_jacks"
        const val PAYOUT_TABLE_DEUCES = "payout_table_deuces"
        const val BET_JACKS = "bet_jacks"
        const val BET_DEUCES = "bet_deuces"
        const val MAX_TIME_TEXAS_HOLDEM = "texas_holdem_max_time"
        const val THREADS_TEXAS_HOLDEM = "texas_holdem_threads"
        const val JACKS_MONTE_CARLO_TRIALS = "jacks_trials"
        const val DEUCES_MONTE_CARLO_TRIALS = "deuces_trials"
    }

    object CardBacks{
        val cardbacks = listOf(
            R.drawable.card_back_default,
            R.drawable.card_back_electric,
            R.drawable.card_back_flower,
            R.drawable.card_back_foot,
            R.drawable.card_back_gay,
            R.drawable.card_back_olympics,
            R.drawable.card_back_pinstriped,
            R.drawable.card_back_red,
            R.drawable.card_back_t_rex,
            R.drawable.cardback_empty
        )
    }

    fun getThreadsHoldem(context: Context?) : Int{
        val preferences: SharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getInt(
                Keys.THREADS_TEXAS_HOLDEM,
                Defaults.THREADS_TEXAS_HOLDEM
        )
    }

    fun getMaxTimeHoldem(context: Context?) : Int{
        val preferences: SharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getInt(
                Keys.MAX_TIME_TEXAS_HOLDEM,
                Defaults.MAX_TIME_TEXAS_HOLDEM
        )
    }

    fun getNumTrialsJacks(context: Context?) : Int{
        val preferences: SharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getInt(
                Keys.JACKS_MONTE_CARLO_TRIALS,
                Defaults.JACKS_MONTE_CARLO_TRIALS
        )
    }

    fun getNumTrialsDeuces(context: Context?) : Int{
        val preferences: SharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getInt(
            Keys.DEUCES_MONTE_CARLO_TRIALS,
            Defaults.DEUCES_MONTE_CARLO_TRIALS
        )
    }

    fun setCardBack(position: Int, context: Context) {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        with(preferences.edit()) {
            putInt(Keys.CHOOSE_CARDBACK, position)
            apply()
        }
    }

    fun getCardBack(context: Context) : Int {
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val position = preferences.getInt(
                Keys.CHOOSE_CARDBACK,
                Defaults.CHOOSE_CARDBACK
        )
        return CardBacks.cardbacks[position]
    }

    fun isSoundEnabled(context: Context) : Boolean{
        val preferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getBoolean(
                Keys.SOUND,
                Defaults.SOUND
        )
    }

    fun showChangeCardBackDialog(context: Context)  {

        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.change_card_dialog_layout)

        val yesBtn = dialog.findViewById(R.id.btn_yes) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }

        val recyclerView = dialog.findViewById(R.id.recyclerView) as RecyclerView
        val adapter = CardBackAdapter(object : CardTapped {
            override fun onCardTapped(position: Int) {
                setCardBack(position, context)
                dialog.dismiss()
                Toast.makeText(context, "cardback selected", Toast.LENGTH_LONG).show()
            }
        })

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        Timber.d("showing cardback dialog")
        dialog.show()
        val window: Window? = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    fun getPayoutTableJacks(context: Context?) : PayoutCalculator.PayTableTypesJacks{
        val preferences: SharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)

        return when(preferences.getString(Keys.PAYOUT_TABLE_JACKS, Defaults.PAYOUT_TABLE_JACKS)){
            "95.12% - 6/5" -> PayoutCalculator.PayTableTypesJacks._6_5_95
            "96.17% - 7/5" -> PayoutCalculator.PayTableTypesJacks._7_5_96
            "97.25% - 8/5" -> PayoutCalculator.PayTableTypesJacks._8_5_97
            "98.33% - 9/5" -> PayoutCalculator.PayTableTypesJacks._9_5_98
            "99.54% - 9/6" -> PayoutCalculator.PayTableTypesJacks._9_6_99
            else -> {
                PayoutCalculator.PayTableTypesJacks._9_6_99
            }
        }
    }

    fun getPayoutTableDeuces(context: Context?) : PayoutCalculator.PayTableTypesDeuces{
        val preferences: SharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)

        return when(preferences.getString(Keys.PAYOUT_TABLE_DEUCES, Defaults.PAYOUT_TABLE_DEUCES)){
            "101.28% Seq Royal" -> PayoutCalculator.PayTableTypesDeuces._101_28
            "100.76% Full Pay" -> PayoutCalculator.PayTableTypesDeuces._100_76
            "100.36% 5–8–15" -> PayoutCalculator.PayTableTypesDeuces._100_36
            "99.89% 5–9–15" -> PayoutCalculator.PayTableTypesDeuces._99_89
            "99.81% 5–9–12" -> PayoutCalculator.PayTableTypesDeuces._99_81
            "98.94% 5–9–12" -> PayoutCalculator.PayTableTypesDeuces._98_94
            "96.77% Colorado" -> PayoutCalculator.PayTableTypesDeuces._96_77
            "94.82% 4–10–15" -> PayoutCalculator.PayTableTypesDeuces._94_82
            "94.34% 3–4–9–15" -> PayoutCalculator.PayTableTypesDeuces._94_34
            "92.05% 3–4–8–12" -> PayoutCalculator.PayTableTypesDeuces._92_05
            else -> {
                PayoutCalculator.PayTableTypesDeuces._100_76
            }
        }
    }

    fun getPayoutTableDeucesString(context: Context?) : String{
        val preferences: SharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(Keys.PAYOUT_TABLE_DEUCES, Defaults.PAYOUT_TABLE_DEUCES) ?: Defaults.PAYOUT_TABLE_DEUCES
    }

    fun getPayoutTableJacksString(context: Context?) : String{
        val preferences: SharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(Keys.PAYOUT_TABLE_JACKS, Defaults.PAYOUT_TABLE_JACKS) ?: Defaults.PAYOUT_TABLE_JACKS
    }

    fun getBetJacks(context: Context?) : Int{
        val preferences: SharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getInt(
            Keys.BET_JACKS,
            Defaults.BET_JACKS
        )
    }

    fun getBetDeuces(context: Context?) : Int{
        val preferences: SharedPreferences = androidx.preference.PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getInt(
            Keys.BET_DEUCES,
            Defaults.BET_DEUCES
        )
    }

}