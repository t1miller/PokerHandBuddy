package com.poker.pokerhandbuddy.ui.main.handstatui

import android.app.Dialog
import android.content.Context
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.poker.jacksorbetter.handstatui.HandStatAdapter
import com.poker.pokerhandbuddy.R
import com.poker.pokerhandbuddy.cardgame.Card
import com.poker.pokerhandbuddy.ai.AIPlayer
import timber.log.Timber

object StatDialogUtils {

    fun showDialog(context: Context, aiDecision: AIPlayer.VideoPokerPlayer?, fullHand: List<Card>?, selectedHand: List<Card>?, expectedValue: Double?) {
        if(fullHand == null || aiDecision == null) return

        if(fullHand.size < 5){
            Toast.makeText(context, "Have ${fullHand.size}/5 cards, need 5", Toast.LENGTH_LONG).show()
            return
        }

        val dialog = Dialog(context)

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.stat_dialog_layout)

        val yesBtn = dialog.findViewById(R.id.btn_yes) as Button
        yesBtn.setOnClickListener {
            dialog.dismiss()
        }

        val cardViews = mutableListOf<ImageView>()
        cardViews.add(dialog.findViewById(R.id.card1))
        cardViews.add(dialog.findViewById(R.id.card2))
        cardViews.add(dialog.findViewById(R.id.card3))
        cardViews.add(dialog.findViewById(R.id.card4))
        cardViews.add(dialog.findViewById(R.id.card5))

        fullHand.forEachIndexed { index, card ->
            cardViews[index].setImageResource(CardUiUtils.cardToImage(card))
            if(selectedHand != null && !selectedHand.contains(card)) {
                cardViews[index].setColorFilter(
                    ContextCompat.getColor(context,
                        R.color.colorGrey), android.graphics.PorterDuff.Mode.MULTIPLY)
            }
        }
        val yourExpectedValueText = dialog.findViewById(R.id.expectedHandValue) as TextView
        yourExpectedValueText.text = context.getString(R.string.expected_value, expectedValue)

        val recyclerView = dialog.findViewById(R.id.handList) as RecyclerView

        val handStats = aiDecision.allHands.map { HandStat(it.first, fullHand, it.second) }.toMutableList()
        val adapter = HandStatAdapter(context, handStats)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)

        Timber.d("showing stat dialog")
        dialog.show()
        val window: Window? = dialog.window
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }
}

