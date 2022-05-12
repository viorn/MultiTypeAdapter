package com.viorn.multytypeadapter

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.viorn.multitypeadapter.MultiTypeAdapter

data class SimpleAdapterItem(val text: String) : MultiTypeAdapter.AdapterItem {

}

class SimpleViewRenderer: MultiTypeAdapter.Renderer<SimpleAdapterItem>() {
    override fun createViewHolder(parent: ViewGroup): MultiTypeAdapter.MultiTypeViewHolder {
        return MultiTypeAdapter.MultiTypeViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.simple_item, parent, false)
        )
    }

    override fun bindItem(
        viewHolder: MultiTypeAdapter.MultiTypeViewHolder,
        model: SimpleAdapterItem
    ) {
        viewHolder.itemView.apply {
            findViewById<AppCompatTextView>(R.id.tv_text).text = model.text
        }
    }
}

class MainActivity : AppCompatActivity() {
    val myadapter by lazy {
        MultiTypeAdapter()
            .registerRenderer(SimpleAdapterItem::class.java, SimpleViewRenderer())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<RecyclerView>(R.id.rv_main).apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            this.adapter = myadapter
        }

        myadapter.applyData(listOf(
            SimpleAdapterItem("1"),
            SimpleAdapterItem("2"),
            SimpleAdapterItem("3"),
            SimpleAdapterItem("4"),
        ))
    }
}