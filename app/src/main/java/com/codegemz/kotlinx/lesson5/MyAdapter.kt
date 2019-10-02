package com.codegemz.kotlinx.lesson5

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class MyAdapter(private val activity : MainActivity, internal var list : MutableList<NoteEntity>) : BaseAdapter() {
    private var editIsLocked: Boolean = false

    // view for note item
    override fun getView(position : Int, view : View?, parent : ViewGroup?) : View {
        val item = getItem(position)
        return with(parent!!.context) {
            relativeLayout {
                textView(item.text) {
                    textSize = 18f
                }
                imageView {
                    imageResource = R.drawable.ic_starred_selector
                    isSelected = item.isStarred
                    onClick {
                        if (!editIsLocked) {
                            isSelected = !isSelected // inverse starred
                            list[position].isStarred = isSelected // set in list
                            activity.setNotesToPrefs(list)
                        }
                    }
                    visibility = View.GONE
                }.lparams {
                    alignParentBottom()
                    alignParentRight()
                }

                topPadding = dip(10)
                bottomPadding = dip(10)
                leftPadding = dip(15)
                rightPadding = dip(15)
            }
        }
    }

    override fun getItem(position : Int) : NoteEntity {
        return list[position]
    }

    override fun getItemId(position : Int) : Long {
        return position.toLong()
    }

    override fun getCount() : Int {
        return list.size
    }

    internal fun clear() {
        activity.setNotesToPrefs(mutableListOf())
        list.clear()
        notifyDataSetChanged()
    }

    internal fun filterByStarred(isEnabled: Boolean) {
        // filtering
        notifyDataSetChanged()
        editIsLocked = isEnabled // lock edit if list filtered
    }
}
