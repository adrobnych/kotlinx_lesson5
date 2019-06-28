package com.codegemz.kotlinx.lesson5

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainView().setContentView(this)
    }

    // save fo shared preferences
    fun setNotesToPrefs(text: String) {
        val sharedPrefs = this.getSharedPreferences("lesson5", Context.MODE_PRIVATE)
        with (sharedPrefs.edit()) {
            putString("notes", text)
            apply()
        }
    }

    // read from shared preferences
    fun getNotesFromPrefs(): String {
        val sharedPrefs = this.getSharedPreferences("lesson5", Context.MODE_PRIVATE)
        val noteString = sharedPrefs.getString("notes", "")
        noteString?.let {
            return noteString
        }
        return ""
    }

    // get note list from pref string
    internal fun getNoteItemList(): List<NoteItem> {
        val noteList = getNotesFromPrefs().split(";")
        return noteList.map {
            NoteItem(it)
        }
    }
}

class MainView : AnkoComponent<MainActivity> {
    override fun createView(ui: AnkoContext<MainActivity>) = with(ui) {
        relativeLayout {
            val notesAdapter = NotesAdapter(ctx, owner.getNoteItemList())

            textView {
                // ids can be created in res/values/ids.xml
                id = R.id.noteTitle
                text = resources.getString(R.string.notes)
                textSize = 24f
                textColor = Color.GRAY
            }.lparams(width = wrapContent) {
                topMargin = dip(25)
                centerHorizontally()
            }

            listView {
                id = R.id.noteList
                adapter = notesAdapter
            }.lparams(width = matchParent) {
                topMargin = dip(15)
                below(R.id.noteTitle)
                topOf(R.id.buttons_layout)
            }

            verticalLayout {
                id = R.id.buttons_layout
                padding = dip(5)

                val inputEditText = editText {
                    hint = "Enter note"
                }

                linearLayout {
                    gravity = Gravity.CENTER_HORIZONTAL
                    button {
                        text = resources.getString(R.string.clear_all)
                        onClick {
                            notesAdapter.clear()
                            owner.setNotesToPrefs("")
                        }
                    }.lparams(weight = 1F)
                    button {
                        text = resources.getString(R.string.save)
                        onClick {
                            val enteredText = inputEditText.text.toString()
                            if (enteredText.isNotBlank()) {
                                inputEditText.setText("")
                                notesAdapter.add(NoteItem(enteredText))
                                owner.setNotesToPrefs(owner.getNotesFromPrefs() + enteredText)
                            }
                        }
                    }.lparams(weight = 1F)
                }
                val backgroundColor = ContextCompat.getColor(ctx, R.color.colorGray)
                background = ColorDrawable(backgroundColor)
            }.lparams(width = matchParent) {
                topMargin = dip(20)
                alignParentBottom()
            }

            val backgroundColor = ContextCompat.getColor(ctx, R.color.colorGray)
            background = ColorDrawable(backgroundColor)
        }
    }
}

internal class NotesAdapter(ctx: Context, items: List<ListItem>) : ListItemAdapter(ctx, items) {
    // All ListItem implementations
    override val listItemClasses = listOf(NoteItem::class.java)
}

// Default implementation
// DSL preview plugin requires AnkoComponent inheritors to have an empty constructor
internal class NoteItem(text: String = "") : TextListItem(text)
