package com.poker.pokerhandbuddy.ui.main.jacksorbetter

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.poker.pokerhandbuddy.ui.main.handstatui.StatDialogUtils
import com.poker.pokerhandbuddy.R
import com.poker.pokerhandbuddy.cardgame.*
import com.poker.pokerhandbuddy.settings.SettingsActivity
import com.poker.pokerhandbuddy.ui.main.texasholdem.TexasHoldemFragment

/**
 * A simple [Fragment] subclass.
 * Use the [JacksOrBetterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class JacksOrBetterFragment : Fragment(), CardSelectionDialog.CardTouched {

    private var cardViews = mutableListOf<ImageView>()
    private var cards = Deck.draw5()
    private var dialog: Dialog? = null
    private var holdViews = mutableListOf<TextView>()
    private var tapOrigin = TexasHoldemFragment.Tap(TexasHoldemFragment.TapOrigin.COMMUNITY, 0)
    private lateinit var viewModel: MainViewModel
    private lateinit var expectedPayText: TextView
    private lateinit var paytableText: TextView
    private lateinit var betText: TextView
    private lateinit var handsEvaluatedText: TextView
    private lateinit var timeText: TextView
    private lateinit var pullDownText: TextView
    private lateinit var progressBar: ProgressBar


    companion object {

        @JvmStatic
        fun newInstance() = JacksOrBetterFragment()
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_jacks_or_better, container, false)

        expectedPayText = view.findViewById(R.id.expectedPayout)
        betText = view.findViewById(R.id.bet)
        paytableText = view.findViewById(R.id.paytable)
        handsEvaluatedText = view.findViewById(R.id.handsEvaluated)
        timeText = view.findViewById(R.id.time)
        pullDownText = view.findViewById(R.id.pullDownTip)
        progressBar = view.findViewById(R.id.progressBar)

        cardViews.clear()
        cardViews.add(view.findViewById(R.id.card1))
        cardViews.add(view.findViewById(R.id.card2))
        cardViews.add(view.findViewById(R.id.card3))
        cardViews.add(view.findViewById(R.id.card4))
        cardViews.add(view.findViewById(R.id.card5))
        holdViews.clear()
        holdViews.add(view.findViewById(R.id.hold1))
        holdViews.add(view.findViewById(R.id.hold2))
        holdViews.add(view.findViewById(R.id.hold3))
        holdViews.add(view.findViewById(R.id.hold4))
        holdViews.add(view.findViewById(R.id.hold5))

        cardViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                tapOrigin = TexasHoldemFragment.Tap(TexasHoldemFragment.TapOrigin.COMMUNITY, index)
                dialog = CardSelectionDialog.showCardSelectionDialog(requireContext(),this, getVisibleCards())
            }
            imageView.setOnLongClickListener {
                CardUiUtils.toggleHighlightHeldCards(holdViews,index)
                setExpectedValue(getHeldCards())
                true
            }
        }

        val resetButton = view.findViewById<Button>(R.id.reset)
        resetButton.setOnClickListener {
            CardUiUtils.unhighlightHeldCards(holdViews)
            cards = Deck.draw5Random(mutableListOf()).toMutableList()
            CardUiUtils.showCards(cardViews, cards)
            viewModel.getBestHandJacks(cards)
        }

        val clearHolds = view.findViewById<Button>(R.id.clearHolds)
        clearHolds.setOnClickListener {
            CardUiUtils.unhighlightHeldCards(holdViews)
        }

        val bestHold = view.findViewById<Button>(R.id.bestHold)
        bestHold.setOnClickListener {
            CardUiUtils.unhighlightHeldCards(holdViews)
            viewModel.getBestHandJacks(cards)
        }

        val moneylayout = view.findViewById<ConstraintLayout>(R.id.moneylayout)
        moneylayout.setOnClickListener {
            val intent = Intent(context, SettingsActivity::class.java)
            startActivity(intent)
        }

        val jacksAdLayout = view.findViewById<ConstraintLayout>(R.id.jacksorBetterAdLayout)
        jacksAdLayout.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse(
                    "https://play.google.com/store/apps/details?id=com.poker.jacksorbetter")
                setPackage("com.android.vending")
            }
            startActivity(intent)
        }

        val expectedPayout = view.findViewById<ConstraintLayout>(R.id.stats)
        expectedPayout.setOnClickListener {
            val expectedValue = viewModel.jacksResult.value?.allHands?.find { it.first.toMutableSet() == getHeldCards().toMutableSet() }?.second
            StatDialogUtils.showDialog(requireContext(),viewModel.jacksResult.value,getVisibleCards(),getHeldCards(), expectedValue)
        }

        val pullToRefresh = view.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            viewModel.getBestHandJacks(getVisibleCards())
            pullToRefresh.isRefreshing = false
        }

        CardUiUtils.showCardBacks(cardViews)
        AdHelper.setupAd(requireActivity(),view, "ca-app-pub-7137320034166109/8320773103")

        return view
    }

    override fun onResume() {
        super.onResume()
        setPayTableAndBet()
        CardUiUtils.showCards(cardViews, cards)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.jacksResult.observe(viewLifecycleOwner, Observer { jacks ->
            CardUiUtils.highlightHeldCards(holdViews,jacks.hand,jacks.allHands[0].first)
            setExpectedValue(getHeldCards())
            handsEvaluatedText.text = getString(R.string.hands_evaluated, jacks.trials*32)
            timeText.text = getString(R.string.time, jacks.time)
            CardUiUtils.showCards(cardViews,jacks.hand)
        })
        viewModel.isJacksRunning.observe(viewLifecycleOwner, Observer { jacksRunning ->
            if(!jacksRunning){
                progressBar.visibility = View.INVISIBLE
                handsEvaluatedText.visibility = View.VISIBLE
                timeText.visibility = View.VISIBLE
                pullDownText.visibility = View.VISIBLE
            } else {
                progressBar.visibility = View.VISIBLE
                handsEvaluatedText.visibility = View.INVISIBLE
                timeText.visibility = View.INVISIBLE
                pullDownText.visibility = View.INVISIBLE
            }
        })
        viewModel.getBestHandJacks(cards)
        setExpectedValue(emptyList())
    }

    private fun getVisibleCards() : List<Card>{
        return cards.filter{it.rank != -1}
    }

    private fun getHeldCards() : List<Card>{
        val held = mutableListOf<Card>()
        for (i in 0..4) {
            if(holdViews[i].visibility == View.VISIBLE && cards[i].rank != -1){
                held.add(cards[i])
            }
        }
        return held
    }

    private fun setExpectedValue(hand: List<Card>) {
        val expectedValue = viewModel.jacksResult.value?.allHands?.find { it.first.toMutableSet() == hand.toMutableSet() }?.second ?: 0.0
        expectedPayText.text = getString(R.string.expected_payout, expectedValue)
    }

    private fun setPayTableAndBet() {
        paytableText.text = getString(R.string.paytable, SettingsUtils.getPayoutTableJacksString(context))
        betText.text = getString(R.string.bet, SettingsUtils.getBetJacks(context))
    }

    override fun onCardSelected(card: Card) {
        if(tapOrigin.index < cards.size) {
            cards[tapOrigin.index] = card
            CardUiUtils.showCards(listOf(cardViews[tapOrigin.index]), listOf(card))
            dialog?.dismiss()
            viewModel.getBestHandJacks(getVisibleCards())
        } else {
            Toast.makeText(context,"oops",Toast.LENGTH_LONG).show()
        }
    }
}