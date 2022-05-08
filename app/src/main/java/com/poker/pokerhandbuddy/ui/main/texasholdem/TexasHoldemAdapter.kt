package com.poker.pokerhandbuddy.ui.main.texasholdem

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.poker.pokerhandbuddy.R
import com.poker.pokerhandbuddy.cardgame.Card


data class HoldemPlayer(
    var equity: Double,
    var won: Double,
    var tie: Double,
    var time: Double,
    var numHands: Int,
    var cards: MutableList<Card>,
    var isProgressShowing: Boolean = false
)


class TexasHoldemAdapter(
    private val context: Context,
    private val values: MutableList<HoldemPlayer>,
    private val touched: ItemTouched
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    interface ItemTouched {
        fun card1(index: Int)
        fun card2(index: Int)
        fun cancel(index: Int)
    }

    companion object {
        val DEFAULT_ITEMS = mutableListOf(
            HoldemPlayer(0.0, 0.0, 0.0, 0.0,0, mutableListOf(Card(), Card())),
            HoldemPlayer(0.0, 0.0, 0.0, 0.0,0, mutableListOf(Card(), Card())),
            // place holder for progress
            HoldemPlayer(0.0, 0.0, 0.0, 0.0,0, mutableListOf(Card(), Card()))
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        when(viewType){
            0 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_pull_to_refresh, parent, false)
                return ViewHolderPullToRefresh(view)
            }
            1 -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_holdem_player, parent, false)
                return ViewHolder(view)
            }
        }
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_holdem_player, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = values[position]
        when(holder.itemViewType){
            0 -> {
                holder as ViewHolderPullToRefresh
                if(item.isProgressShowing){
                    holder.pullToRefresh.visibility = View.INVISIBLE
                    holder.progressBar.visibility = View.VISIBLE
                    holder.time.visibility = View.INVISIBLE
                    holder.handsEvaluated.visibility = View.INVISIBLE
                } else {
                    holder.pullToRefresh.visibility = View.VISIBLE
                    holder.progressBar.visibility = View.INVISIBLE
                    holder.time.visibility = View.VISIBLE
                    holder.handsEvaluated.visibility = View.VISIBLE

                    if (position-1 > 0) {
                        val prevPlayer = values[position-1]
                        holder.time.text = context.getString(R.string.time, prevPlayer.time)
                        holder.handsEvaluated.text =
                            context.getString(R.string.hands_evaluated, prevPlayer.numHands)
                    } else {
                        holder.time.text = context.getString(R.string.time, 0.0)
                        holder.handsEvaluated.text = context.getString(R.string.hands_evaluated, 0)
                    }
                }
            }
            1 -> {
                holder as ViewHolder
                holder.win.text = context.getString(R.string.win, item.won)
                holder.equity.text = context.getString(R.string.equity, item.equity)
                holder.tie.text = context.getString(R.string.tie, item.tie)
                holder.close.setOnClickListener {
                    touched.cancel(position)
                }
                holder.card1.setOnClickListener {
                    touched.card1(position)
                }
                holder.card2.setOnClickListener {
                    touched.card2(position)
                }
                CardUiUtils.showCards(listOf(holder.card1, holder.card2), item.cards)
            }
        }
    }


    override fun getItemViewType(position: Int): Int {
        return if(position == (itemCount - 1)){
            0
        } else {
            1
        }
    }

    override fun getItemCount(): Int = values.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val win: TextView = view.findViewById(R.id.win)
        val equity: TextView = view.findViewById(R.id.equity)
        val tie: TextView = view.findViewById(R.id.tie)
        val close: ImageView = view.findViewById(R.id.close)
        val card1: ImageView = view.findViewById(R.id.card1)
        val card2: ImageView = view.findViewById(R.id.card2)
    }

    inner class ViewHolderPullToRefresh(view: View) : RecyclerView.ViewHolder(view) {
        val pullToRefresh: TextView = view.findViewById(R.id.pullToRefresh)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar2)
        val handsEvaluated: TextView = view.findViewById(R.id.handsEvaluated)
        val time: TextView = view.findViewById(R.id.handTime)
    }
}