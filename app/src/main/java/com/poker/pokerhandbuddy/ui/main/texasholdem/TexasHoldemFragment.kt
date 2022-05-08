package com.poker.pokerhandbuddy.ui.main.texasholdem

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.poker.pokerhandbuddy.PokerApplication
import com.poker.pokerhandbuddy.R
import com.poker.pokerhandbuddy.ai.AIPlayer
import com.poker.pokerhandbuddy.cardgame.AdHelper
import com.poker.pokerhandbuddy.cardgame.Card
import com.poker.pokerhandbuddy.cardgame.CardSelectionDialog
import timber.log.Timber



/**
 * A simple [Fragment] subclass.
 * Use the [TexasHoldemFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TexasHoldemFragment : Fragment(), TexasHoldemAdapter.ItemTouched, CardSelectionDialog.CardTouched {

    private var mItems = TexasHoldemAdapter.DEFAULT_ITEMS
    private var tapOrigin = Tap(TapOrigin.CARD_1, 0)
    private var mAdapter = TexasHoldemAdapter(PokerApplication.applicationContext(), mItems, this@TexasHoldemFragment)
    private var dialog: Dialog? = null
    private var communityCardViews = mutableListOf<ImageView>()
    private var communityCard = mutableListOf(Card(), Card(), Card(), Card(), Card())
    private var discardedCardViews = mutableListOf<ImageView>()
    private var discardedCard = mutableListOf(Card(), Card(), Card(), Card(), Card())

    private lateinit var viewModel: MainViewModel

    enum class TapOrigin {
        CARD_1,
        CARD_2,
        COMMUNITY,
        DISCARDED
    }

    data class Tap(
            val origin: TapOrigin,
            val index: Int
    )


    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_texas_holdem, container, false)

        communityCardViews.clear()
        communityCardViews.add(view.findViewById(R.id.card1))
        communityCardViews.add(view.findViewById(R.id.card2))
        communityCardViews.add(view.findViewById(R.id.card3))
        communityCardViews.add(view.findViewById(R.id.card4))
        communityCardViews.add(view.findViewById(R.id.card5))
        communityCardViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                tapOrigin = Tap(TapOrigin.COMMUNITY, index)
                dialog = CardSelectionDialog.showCardSelectionDialog(requireContext(), this, getVisibleCards())
            }
        }

        discardedCardViews.clear()
        discardedCardViews.add(view.findViewById(R.id.card1Discarded))
        discardedCardViews.add(view.findViewById(R.id.card2Discarded))
        discardedCardViews.add(view.findViewById(R.id.card3Discarded))
        discardedCardViews.add(view.findViewById(R.id.card4Discarded))
        discardedCardViews.add(view.findViewById(R.id.card5Discarded))
        discardedCardViews.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                tapOrigin = Tap(TapOrigin.DISCARDED, index)
                dialog = CardSelectionDialog.showCardSelectionDialog(requireContext(), this, getVisibleCards())
            }
        }

        val plusButton = view.findViewById<Button>(R.id.plus)
        plusButton.setOnClickListener {
            if(mItems.size < 12) {
                mItems.add(HoldemPlayer(0.0, 0.0, 0.0, 0.0, 0, mutableListOf(Card(), Card())))
                mAdapter.notifyDataSetChanged()
                viewModel.getBestHand(getHoldemPlayers(), getCommunityCards(), getDiscardedCards())
            } else {
                Toast.makeText(context, "max players is 12", Toast.LENGTH_SHORT).show()
            }
        }

        val resetButton = view.findViewById<Button>(R.id.reset)
        resetButton.setOnClickListener {
            mItems.clear()
            mItems.add(HoldemPlayer(0.0, 0.0, 0.0, 0.0, 0, mutableListOf(Card(), Card())))
            mItems.add(HoldemPlayer(0.0, 0.0, 0.0, 0.0, 0, mutableListOf(Card(), Card())))
            mItems.add(HoldemPlayer(0.0, 0.0, 0.0, 0.0, 0, mutableListOf(Card(), Card())))
            mAdapter.notifyDataSetChanged()
            resetCommunityCards()
            resetDiscardedCards()
            viewModel.getBestHand(getHoldemPlayers(), getCommunityCards(), getDiscardedCards())
        }

        val discardedLayout = view.findViewById<ConstraintLayout>(R.id.burnedLayout)
        val burnButton = view.findViewById<Button>(R.id.burn)
        burnButton.setOnClickListener {
            discardedLayout.visibility = View.VISIBLE
        }


        val closeButton = view.findViewById<ImageView>(R.id.closeDiscarded)
        closeButton.setOnClickListener {
            discardedLayout.visibility = View.GONE
            resetDiscardedCards()
            viewModel.getBestHand(getHoldemPlayers(), getCommunityCards(), getDiscardedCards())
        }

        val pullToRefresh = view.findViewById<SwipeRefreshLayout>(R.id.pullToRefresh)
        pullToRefresh.setOnRefreshListener {
            viewModel.getBestHand(getHoldemPlayers(), getCommunityCards(), getDiscardedCards())
            pullToRefresh.isRefreshing = false
        }
        updateCardBacks()

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler)
        with(recyclerView) {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

        AdHelper.setupAd(requireActivity(),view, "ca-app-pub-7137320034166109/8320773103")
        return view
    }

    private fun resetCommunityCards() {
        communityCard.clear()
        communityCard.addAll(listOf(Card(), Card(), Card(), Card(), Card()))
        CardUiUtils.showCardBacks(communityCardViews)
    }

    private fun resetDiscardedCards() {
        discardedCard.clear()
        discardedCard.addAll(listOf(Card(), Card(), Card(), Card(), Card()))
        CardUiUtils.showCardBacks(discardedCardViews)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        viewModel.holdemResult.observe(viewLifecycleOwner, Observer { players ->
            for ((player, adapterPlayer) in players.zip(mItems)) {
                adapterPlayer.won = player.won
                adapterPlayer.tie = player.tie
                adapterPlayer.equity = player.equity
                adapterPlayer.numHands = player.handCount
                adapterPlayer.time = player.time
            }
            mAdapter.notifyDataSetChanged()
        })

        viewModel.percent.observe(viewLifecycleOwner, Observer { percent ->

            // todo update adapter percentage
            Timber.d("percent: $percent")
        })

        viewModel.isHoldemRunning.observe(viewLifecycleOwner, Observer { isRunning ->
            mItems[mItems.size - 1].isProgressShowing = isRunning
            mAdapter.notifyDataSetChanged()
        })
    }

    override fun onResume() {
        super.onResume()
        updateCardBacks()
    }

    fun updateCardBacks() {
        CardUiUtils.showCardBacks(discardedCardViews)
        CardUiUtils.showCardBacks(communityCardViews)
    }

    companion object {

        @JvmStatic
        fun newInstance() = TexasHoldemFragment()
    }

    override fun card1(index: Int) {
        dialog = CardSelectionDialog.showCardSelectionDialog(requireContext(), this, getVisibleCards())
        tapOrigin = Tap(TapOrigin.CARD_1, index)
    }

    override fun card2(index: Int) {
        dialog = CardSelectionDialog.showCardSelectionDialog(requireContext(), this, getVisibleCards())
        tapOrigin = Tap(TapOrigin.CARD_2, index)
    }

    override fun cancel(index: Int) {
        mItems.removeAt(index)
        viewModel.getBestHand(getHoldemPlayers(), getCommunityCards(), getDiscardedCards())
        mAdapter.notifyDataSetChanged()
    }

    override fun onCardSelected(card: Card) {
        when(tapOrigin.origin){
            TapOrigin.CARD_1 -> {
                mItems[tapOrigin.index].cards[0] = card
                mAdapter.notifyDataSetChanged()
                dialog?.dismiss()
            }
            TapOrigin.CARD_2 -> {
                mItems[tapOrigin.index].cards[1] = card
                mAdapter.notifyDataSetChanged()
                dialog?.dismiss()
            }
            TapOrigin.COMMUNITY -> {
                communityCard[tapOrigin.index] = card
                CardUiUtils.showCards(listOf(communityCardViews[tapOrigin.index]), listOf(card))
                dialog?.dismiss()
            }
            TapOrigin.DISCARDED -> {
                discardedCard[tapOrigin.index] = card
                CardUiUtils.showCards(listOf(discardedCardViews[tapOrigin.index]), listOf(card))
                dialog?.dismiss()
            }
        }
        viewModel.getBestHand(getHoldemPlayers(), getCommunityCards(), getDiscardedCards())
    }

    fun getHoldemPlayers() : List<AIPlayer.TexasHoldemPlayer> {
        val players = mutableListOf<AIPlayer.TexasHoldemPlayer>()
        for (item in mItems) {
            players.add(
                    AIPlayer.TexasHoldemPlayer(
                            item.cards.filter { it.rank != -1 }.toMutableSet(),
                            0.0,
                            0.0,
                            0.0,
                            0,
                            0.0
                    )
            )
        }
        players.removeLast() // last item is placeholder
        return players
    }

    fun getCommunityCards(): String{
        return communityCard.filter { it.rank != -1 }.joinToString(separator = "")
    }

    fun getDiscardedCards(): String{
        return discardedCard.filter { it.rank != -1 }.joinToString(separator = "")
    }

    fun getVisibleCards() : List<Card> {
        // add communnity
        val cards = communityCard.toMutableList()

        //  add players
        mItems.forEach { cards.addAll(it.cards.filter { it.rank != -1 })}

        // add discarded
        cards.addAll(discardedCard.toMutableList())

        return cards.toSet().toList()
    }

}