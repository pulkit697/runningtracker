package com.example.runtracker.runningapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.runtracker.R
import com.example.runtracker.common.Utility
import com.example.runtracker.runningapp.database.Run
import kotlinx.android.synthetic.main.fragment_statistics.view.*
import kotlinx.android.synthetic.main.item_run.view.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class RunsRVAdapter:RecyclerView.Adapter<RunsRVAdapter.RunViewHolder>() {

    inner class RunViewHolder(itemview:View):RecyclerView.ViewHolder(itemview)

    val diffCallback = object : DiffUtil.ItemCallback<Run>(){
        override fun areItemsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Run, newItem: Run): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }

    val differ = AsyncListDiffer(this,diffCallback)

    fun submitList(list : List<Run>) = differ.submitList(list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunViewHolder {
        return RunViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.item_run,
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: RunViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.itemView.apply {
            //image
            Glide.with(this)
                .load(item.image)
                .into(ivRun)
            //date
            val calendar = Calendar.getInstance().apply {
                timeInMillis = item.datetimestamp
            }
            val dateFormat = SimpleDateFormat("dd/MM/yy",Locale.getDefault())
            tvDate.text = dateFormat.format(calendar.time)
            //calories
            val calories = "${item.calories}Kcal"
            tvCal.text = calories
            //time
            tvTime.text = Utility.formattedString(item.timeMilliSec)
            //distance
            val dist = "${item.distanceMeters/1000f}km"
            tvDist.text = dist
            //speed
            val speed = "${item.avgspeedkmph}Km/hr"
            tvSpeed.text = speed
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}