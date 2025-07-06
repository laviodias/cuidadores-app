package com.example.cuidadores.ui.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.content.ContextCompat
import com.example.cuidadores.R

class TagView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AppCompatTextView(context, attrs, defStyleAttr) {

    enum class TagType {
        GREEN, BLUE, RED, ORANGE, GRAY
    }

    init {
        // Configurações base para todas as tags
        setPadding(
            dpToPx(12f).toInt(),
            dpToPx(6f).toInt(),
            dpToPx(12f).toInt(),
            dpToPx(6f).toInt()
        )
        
        textSize = 12f
        
        // Processar atributos personalizados
        attrs?.let {
            val typedArray = context.obtainStyledAttributes(it, R.styleable.TagView)
            
            val tagTypeValue = typedArray.getInt(R.styleable.TagView_tagType, 0)
            val tagType = TagType.values()[tagTypeValue]
            
            val tagText = typedArray.getString(R.styleable.TagView_tagText)
            
            setTagType(tagType)
            tagText?.let { text = it }
            
            typedArray.recycle()
        }
    }

    fun setTagType(type: TagType) {
        when (type) {
            TagType.GREEN -> {
                setBackgroundResource(R.drawable.tag_background_green)
                setTextColor(ContextCompat.getColor(context, R.color.tag_text_green))
            }
            TagType.BLUE -> {
                setBackgroundResource(R.drawable.tag_background_blue)
                setTextColor(ContextCompat.getColor(context, R.color.tag_text_blue))
            }
            TagType.RED -> {
                setBackgroundResource(R.drawable.tag_background_red)
                setTextColor(ContextCompat.getColor(context, R.color.tag_text_red))
            }
            TagType.ORANGE -> {
                setBackgroundResource(R.drawable.tag_background_orange)
                setTextColor(ContextCompat.getColor(context, R.color.tag_text_orange))
            }
            TagType.GRAY -> {
                setBackgroundResource(R.drawable.tag_background_gray)
                setTextColor(ContextCompat.getColor(context, R.color.tag_text_gray))
            }
        }
    }

    fun setText(text: String, type: TagType) {
        this.text = text
        setTagType(type)
    }

    private fun dpToPx(dp: Float): Float {
        return dp * context.resources.displayMetrics.density
    }
} 