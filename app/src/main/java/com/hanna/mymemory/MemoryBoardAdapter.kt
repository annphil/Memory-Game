package com.hanna.mymemory

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.hanna.mymemory.models.BoardSize
import com.hanna.mymemory.models.MemoryCard
import kotlin.math.min

class MemoryBoardAdapter(
        private val context: Context, // Object of MainActivity
        private val boardSize: BoardSize, // num of pieces
        private val cards: List<MemoryCard>,
        private val cardClickListener: CardClickListener
        ) : RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {
        /*MemoryBoardAdapter.ViewHolder --> an object which can access all views of a recycler view.*/

    //To write a function or any member of the class that can be called without having the instance of the class
    //Similar to using static in Java
    companion object{
        private const val MARGIN_SIZE = 10
        private const val TAG = "MemoryBoardAdapter" //Using class name by convention
    }

    //Constructor used cos bind() knows of changes in state which must be notified to main which will notify MemoryGame class
    //Whoever constructs adapter is responsible for sending instance of interface. Hence added as a data member
    interface CardClickListener {
        fun onCardClicked(position: Int)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //How to create one view of recycler view

        val cardWidth : Int = parent.width / boardSize.getWidth() - (2 * MARGIN_SIZE)
        val cardHeight : Int = parent.height / boardSize.getHeight() - (2 * MARGIN_SIZE)
        val cardSideLength : Int = min(cardHeight, cardWidth)

        //To inflate a single card
        val view : View = LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)
        //context-> object of MainActivity.
        //R.layout.memory_card -> layout resource which is to be inflated.
        //parent-> ViewGroup -> the recycler view
        //false-> do not attach to root layout of screen as items have to come in and out of the screen

        //To set the size of card
        val layoutParams : ViewGroup.MarginLayoutParams = view.findViewById<CardView>(R.id.cardView).layoutParams as ViewGroup.MarginLayoutParams
        //view.findViewById<CardView>(R.id.cardView) -> Pulling out CardView from inflated RecyclerView (view.) using it's ID (R.id.cardView)
        //.layoutParams -> Get reference to CardView's layoutParameters
        //as ViewGroup.MarginLayoutParams -> We need to set(notify it has) the margin when we grab CardView ref from the inflated view
        layoutParams.width = cardSideLength
        layoutParams.height = cardSideLength
        //setting its width & height ^
        layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        //setting margin on layoutParams^
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        //Takes data at that position and binds to given ViewHolder
        holder.bind(position)
    }

    override fun getItemCount() = boardSize.numCards

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)
        //For each individual card
         
        fun bind(position: Int) {
            val memoryCard = cards[position]
            imageButton.setImageResource(if (memoryCard.isFaceUp) memoryCard.identifier else R.drawable.ic_launcher_background )

            //For user to differentiate identified pairs and rest.
            imageButton.alpha = if(memoryCard.isMatched) 0.4f else 1.0f
            //alpha -> opacity
            val colorStateList = if(memoryCard.isMatched) ContextCompat.getColorStateList(context, R.color.color_grey) else null
            //changing bg to greyed out yellow
            ViewCompat.setBackgroundTintList(imageButton, colorStateList)
            //setBackgroundTintList() -> to set a bg/shade on imageButton


            imageButton.setOnClickListener {
                Log.i(TAG, "Clicked on position $position")
                //.i -> Setting at info level Log.
                cardClickListener.onCardClicked(position)
            }
        }
    }

}
