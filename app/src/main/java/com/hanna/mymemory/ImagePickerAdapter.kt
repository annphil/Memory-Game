package com.hanna.mymemory

import android.content.Context
import android.net.Uri
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.ActionMenuItemView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.hanna.mymemory.models.BoardSize
import kotlin.math.min

class ImagePickerAdapter(
        private val context: Context,
        private val imageUris: List<Uri>,
        private val boardSize: BoardSize,
        private val imageClickListener: ImageClickListener
        ) : RecyclerView.Adapter<ImagePickerAdapter.ViewHolder>() {

    //interface btw adapter which knows of click from bind() in class ViewHolder and CreateActivity.
    interface ImageClickListener{
        fun onPlaceHolderClicked()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view : View = LayoutInflater.from(context).inflate(R.layout.card_image, parent, false)

        val cardWidth : Int = parent.width / boardSize.getWidth() //- (2 * MemoryBoardAdapter.MARGIN_SIZE)
        val cardHeight : Int = parent.height / boardSize.getHeight() //- (2 * MemoryBoardAdapter.MARGIN_SIZE)
        val cardSideLength : Int = min(cardHeight, cardWidth)
        val layoutParams : ViewGroup.LayoutParams = view.findViewById<ImageView>(R.id.ivCustomimage).layoutParams
        layoutParams.width = cardSideLength
        layoutParams.height = cardSideLength
 
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position < imageUris.size) {
            holder.bind(imageUris[position]) }//Binds Uri at that position
        else {
            holder.bind() }
    }

    override fun getItemCount() = boardSize.getNumPairs()

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private val ivCustomImage = itemView.findViewById<ImageView>(R.id.ivCustomimage)

        // We aren't including functionality to change image on a box which already has an image in it.
        //Nothing happens when a box with image is clicked
        fun bind(uri: Uri) {
            ivCustomImage.setImageURI(uri)
            ivCustomImage.setOnClickListener(null)
        }

        fun bind() {
            ivCustomImage.setOnClickListener {
                //Launch intent for user to select photos
                imageClickListener.onPlaceHolderClicked()
            }
        }

    }
}
