package com.aprz.layoutmanagerdemo

import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        text.setOnClickListener {
//            Toast.makeText(this, "hhh", Toast.LENGTH_SHORT).show()
//        }

        recycler.adapter = StackAdapter()
        recycler.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                super.getItemOffsets(outRect, view, parent, state)

                outRect.bottom = 10
                outRect.left = 10
                outRect.right = 10
                outRect.right = 10

            }
        })
    }


}

class StackAdapter : RecyclerView.Adapter<StackViewHolder>() {

    override fun getItemCount() = 20

    override fun onBindViewHolder(holder: StackViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StackViewHolder {
        val root = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stack, parent, false)
        return StackViewHolder(root)
    }

}

class StackViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    private val text: TextView = itemView.findViewById(R.id.text)
    private val root: View = itemView.findViewById(R.id.root)

    fun bind(position: Int) {
        text.text = "$position"
        val rnd = Random()
        val color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
        root.setBackgroundColor(color)
    }
}
