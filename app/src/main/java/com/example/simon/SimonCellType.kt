package com.example.simon

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView

class SimonCellType : AppCompatImageView {

    private val mContext: Context

    var litImage = 0
    var offImage = 0
    var state = 0
    var cellType = 0

    fun setOn() {
        setImageDrawable(resources.getDrawable(litImage))
        refreshDrawableState()
    }

    constructor(ctx: Context, attr: AttributeSet?) : super(ctx, attr) {
        mContext = ctx
    }

    constructor(ctx: Context) : super(ctx) {
        mContext = ctx
    }

    fun setOff() {
        state = OFF
        setImageDrawable(resources.getDrawable(offImage))
        refreshDrawableState()
    }

    @JvmName("setCellType1")
    fun setCellType(cellType: Int) {
        this.cellType = cellType
        when (this.cellType) {
            CELL_TYPE_BLUE -> {
                litImage = R.drawable.bluelitup
                offImage = R.drawable.blue
            }
            CELL_TYPE_GREEN -> {
                litImage = R.drawable.greenlitup
                offImage = R.drawable.green
            }
            CELL_TYPE_RED -> {
                litImage = R.drawable.redlitup
                offImage = R.drawable.red
            }
            CELL_TYPE_YELLOW -> {
                litImage = R.drawable.yellowlitup
                offImage = R.drawable.yellow
            }
        }
    }

    companion object {
        const val ON = 1
        const val OFF = 2
        const val CELL_TYPE_RED = 1
        const val CELL_TYPE_BLUE = 2
        const val CELL_TYPE_GREEN = 3
        const val CELL_TYPE_YELLOW = 4
    }
}