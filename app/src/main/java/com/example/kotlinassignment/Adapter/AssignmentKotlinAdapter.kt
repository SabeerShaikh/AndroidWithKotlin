package com.example.kotlinassignment.Adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.kotlinassignment.DataModel.AssignmentModel
import com.example.kotlinassignment.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.testdemo_raw.view.*


/**
 * Created by Sabeer Shaikh on 12/10/2019.
 *
 */
class AssignmentKotlinAdapter(val assignmentModel: List<AssignmentModel>) :
    RecyclerView.Adapter<AssignmentKotlinAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
            : ViewHolder {
        val v: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.testdemo_raw, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Log.d("Response---", assignmentModel[position].title)

        holder.title.text = assignmentModel[position].title
        holder.description.text = assignmentModel[position].description
        Picasso.get()
            .load(assignmentModel[position].imageHref)
            .fit()
            .error(R.drawable.ic_launcher)
            .into(holder.imageHref, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    //Success image already loaded into the view
                }

                override fun onError(e: Exception) {
                    //Error placeholder image already loaded into the view, do further handling of this situation here
                   // holder.imageHref.setImageResource(R.drawable.ic_launcher)

                }


            })


    }

    override fun getItemCount(): Int {
        return assignmentModel.size
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.title_text
        val description = itemView.description_text
        val imageHref = itemView.image_test
    }
}