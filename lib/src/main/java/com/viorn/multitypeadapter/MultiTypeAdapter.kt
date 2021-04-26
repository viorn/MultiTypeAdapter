package com.viorn.multitypeadapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import kotlin.reflect.KClassifier

class MultiTypeAdapter : RecyclerView.Adapter<MultiTypeAdapter.MultiTypeViewHolder>() {
    open class MultiTypeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        open fun onViewDetachedFromWindow() {

        }
    }

    interface AdapterItem {
        fun getItemId(): Long {
            return hashCode().toLong()
        }
    }

    abstract class Renderer<T : AdapterItem> {
        fun bind(viewHolder: MultiTypeViewHolder, model: AdapterItem) {
            bindItem(viewHolder, model as T)
        }

        abstract fun createViewHolder(parent: ViewGroup): MultiTypeViewHolder
        abstract fun bindItem(viewHolder: MultiTypeViewHolder, model: T)
    }

    private val rendererMap = LinkedHashMap<Class<out AdapterItem>, Renderer<out AdapterItem>>()

    private val data = ArrayList<AdapterItem>()

    fun getData(): Collection<AdapterItem> {
        return data.toList()
    }

    @Synchronized
    fun setData(data: Collection<AdapterItem>) {
        this.data.clear()
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    @Synchronized
    fun applyData(data: Collection<AdapterItem>) {
        val diffResult = DiffUtil.calculateDiff(DiffCallback(this.data.toList(), data.toList()))
        this.data.clear()
        this.data.addAll(data)
        diffResult.dispatchUpdatesTo(this);
    }

    fun <T: AdapterItem> registerRenderer(cls: Class<T>, renderer: Renderer<out T>): MultiTypeAdapter {
        rendererMap[cls] = renderer
        return this
    }

    /*fun registerRenderer(vararg renderer: Renderer<out AdapterItem>): MultiTypeAdapter {
        renderer.forEach {
            registerRenderer(it)
        }
        return this
    }*/

    override fun onViewDetachedFromWindow(holder: MultiTypeViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder.onViewDetachedFromWindow()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MultiTypeViewHolder {
        val renderer = rendererMap.values.elementAt(viewType)
        val holder = renderer.createViewHolder(parent)
        return holder
    }

    override fun onBindViewHolder(holder: MultiTypeViewHolder, position: Int) {
        val item = data[position]
        val renderer = rendererMap.values.elementAt(holder.itemViewType)
        renderer.bind(holder, item)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemId(position: Int): Long {
        return getItemViewType(position)*Int.MAX_VALUE + data[position].getItemId()
    }

    override fun getItemViewType(position: Int): Int {
        val item = data[position]
        //val ktypes = arrayListOf<KClassifier>(item::class)
        //ktypes.addAll(item::class.supertypes.dropLast(1).mapNotNull { it.classifier })
        val viewType = rendererMap.keys.indexOf(item.javaClass)
        if (viewType < 0) {
            throw Throwable("ViewType not found: ${item::class.simpleName}, rendererKeys: ${rendererMap.keys}")
        }
        return viewType
    }

    class DiffCallback(
        private val oldList: List<AdapterItem>,
        private val newList: List<AdapterItem>
    ) : DiffUtil.Callback() {


        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldProduct: AdapterItem = oldList[oldItemPosition]
            val newProduct: AdapterItem = newList[newItemPosition]
            return oldProduct.getItemId() == newProduct.getItemId()
        }

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            val oldItem = oldList[oldItemPosition]
            val newItem = newList[newItemPosition]
            return oldItem == newItem
        }
    }
}