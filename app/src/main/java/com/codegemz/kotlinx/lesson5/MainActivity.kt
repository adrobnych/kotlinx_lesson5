package com.codegemz.kotlinx.lesson5

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.serialization.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonConfiguration
import org.jetbrains.anko.*
import org.jetbrains.anko.sdk27.coroutines.onClick

class MainActivity : AppCompatActivity() {
    private val sharedPrefs by lazy {
        getSharedPreferences("main", Context.MODE_PRIVATE)
    }
    private val json by lazy {
        Json(JsonConfiguration.Stable)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MainView().setContentView(this)
    }

    // save noteItemList to shared preferences
    internal fun setNotesToPrefs(noteItemList: List<TextListItem>) {
        val noteString = json.stringify(TextListItem.serializer().list, noteItemList)
        with (sharedPrefs.edit()) {
            putString("notes", noteString)
            apply()
        }
    }

    // read from shared preferences
    private fun getNoteStringFromPrefs(): String {
        val noteString = sharedPrefs.getString("notes", "[]")
        noteString?.let {
            return noteString
        }
        return "[]"
    }

    // get note list from serialized pref string
    internal fun getNoteItemList(): List<TextListItem> {
        val noteString = getNoteStringFromPrefs()
        return json.parse(TextListItem.serializer().list, noteString)
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
                            owner.setNotesToPrefs(emptyList())
                        }
                    }.lparams(weight = 1F)
                    button {
                        text = resources.getString(R.string.save)
                        onClick {
                            val enteredText = inputEditText.text.toString()
                            if (enteredText.isNotBlank()) {
                                inputEditText.setText("")
                                notesAdapter.add(TextListItem(enteredText))
                                val noteItemList = owner.getNoteItemList().toMutableList()
                                noteItemList.add(TextListItem(enteredText))
                                owner.setNotesToPrefs(noteItemList)
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
    override val listItemClasses = listOf(TextListItem::class.java)
}

// Default implementation
// DSL preview plugin requires AnkoComponent inheritors to have an empty constructor
internal class NoteItem(text: String = "") : TextListItem(text)
