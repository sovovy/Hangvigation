package com.mobal.hangvigation.ui.indoor_info

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobal.hangvigation.R
import com.mobal.hangvigation.model.GetDivisionResponseData

class PlaceAdapter(private val context: Context, private var placeItems: ArrayList<GetDivisionResponseData>) : RecyclerView.Adapter<PlaceViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaceViewHolder {
        val mainView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_place_list, parent, false)

        return PlaceViewHolder(mainView)
    }

    override fun getItemCount(): Int = placeItems.size

    override fun onBindViewHolder(holder: PlaceViewHolder, position: Int) {
        holder.name_tv.text = placeItems[position].name?: "${placeItems[position].building} ${placeItems[position].num}호"

        holder.place_cl.setOnClickListener {
            Intent(context, IndoorInfoActivity::class.java).let {
                it.putExtra("INDOOR_PLACE_IDX", placeItems[position].indoor_place_idx)
                context.startActivity(it)
            }
        }
    }
}