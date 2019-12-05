package com.mobal.hangvigation.ui.indoor_info

import android.content.Context
import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.mobal.hangvigation.R
import com.mobal.hangvigation.model.GetDivisionResponseData

class DivisionAdapter(private val context: Context, private var divisionItems: ArrayList<GetDivisionResponseData>) : RecyclerView.Adapter<DivisionViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DivisionViewHolder {
        val mainView: View = LayoutInflater.from(parent.context).inflate(R.layout.item_division_list, parent, false)

        return DivisionViewHolder(mainView)
    }

    override fun getItemCount(): Int = divisionItems.size

    override fun onBindViewHolder(holder: DivisionViewHolder, position: Int) {
        holder.division_btn.setOnClickListener {
            Intent(context, PlaceListActivity::class.java).let {
                it.putExtra("DIVISION_IDX", divisionItems[position].division_idx)

                if ((context as PlaceListActivity).title.contains("::"))
                    it.putExtra("TITLE", context.title + " :: ${divisionItems[position].name}")
                else
                    it.putExtra("TITLE", context.title + " - ${divisionItems[position].name}")

                context.startActivity(it)
            }
        }
    }
}