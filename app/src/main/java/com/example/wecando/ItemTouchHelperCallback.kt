package com.example.wecando

import android.graphics.*
import android.util.Log
import android.view.View
import androidx.core.view.size
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.detail_item.view.*
import kotlinx.android.synthetic.main.detail_item_finish.view.*
import java.util.Collections.max
import java.util.Collections.min
import kotlin.math.max
import kotlin.math.min


class ItemTouchHelperCallback(val listener: ItemActionListener, val isDetail: Boolean) : ItemTouchHelper.Callback() {

    private var currentPosition: Int? = null
    private var previousPosition: Int? = null
    private var currentDx = 0f
    private var clamp = 0f
    private var runningHolder = DetailAdapter.RunningViewHolder::class.java
    private var finishHolder = DetailAdapter.FinishViewHolder::class.java

    private var paint: Paint = Paint()
    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        val holderType = viewHolder::class.java
        val dragFlags = ItemTouchHelper.DOWN or ItemTouchHelper.UP
        val swipeFlags = ItemTouchHelper.START
        if (isDetail) {
            if (holderType != runningHolder && holderType != finishHolder) {
                return makeMovementFlags(dragFlags, 0)
            }
        }
        return makeMovementFlags(dragFlags, swipeFlags)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        listener.onItemMoved(viewHolder!!.adapterPosition, target!!.adapterPosition)
        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
//        listener.onItemSwiped(viewHolder!!.adapterPosition)
    }

    override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
        var holderType = viewHolder::class.java

        if (isDetail) {
            if (holderType == runningHolder || holderType == finishHolder) {
                currentDx = 0f
                previousPosition = viewHolder.adapterPosition
                getDefaultUIUtil().clearView(getView(viewHolder))
            }

        }
    }

    override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {

        if (isDetail) {
            viewHolder?.let {
                currentPosition = viewHolder.adapterPosition
                getDefaultUIUtil().onSelected(getView(it))
            }
        }

    }

    override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {

        if (isDetail) {
            val isClamped: Boolean = getTag(viewHolder)
            // 현재 View가 고정되어있지 않고 사용자가 -clamp 이상 swipe시 isClamped true로 변경 아닐시 false로 변경
            setTag(viewHolder, !isClamped && currentDx <= -clamp)
        }

        return 2f
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        var holderType = viewHolder::class.java
        if (isDetail) {
            if (holderType == runningHolder || holderType == finishHolder) {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                    val view = getView(viewHolder)
                    val isClamped = getTag(viewHolder)
                    val x =  clampViewPositionHorizontal(view, dX, isClamped, isCurrentlyActive)

                    currentDx = x
                    getDefaultUIUtil().onDraw(
                        c,
                        recyclerView,
                        view,
                        x,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }
            }
        }
    }

    private fun clampViewPositionHorizontal(
        view: View,
        dX: Float,
        isClamped: Boolean,
        isCurrentlyActive: Boolean
    ) : Float {
        // View의 가로 길이의 절반까지만 swipe 되도록
        val min: Float = -view.width.toFloat()/2
        // RIGHT 방향으로 swipe 막기
        val max: Float = 0f

        val x = if (isClamped) {
            // View가 고정되었을 때 swipe되는 영역 제한
            if (isCurrentlyActive) dX - clamp else -clamp
        } else {
            dX
        }

        return min(max(min, x), max)
    }

    private fun setTag(viewHolder: RecyclerView.ViewHolder, isClamped: Boolean) {
        // isClamped를 view의 tag로 관리
//        if (isDetail) viewHolder.itemView.tag = isClamped
        if (isDetail) viewHolder.itemView.tag = isClamped
    }

    private fun getTag(viewHolder: RecyclerView.ViewHolder) : Boolean {
        // isClamped를 view의 tag로 관리
        return viewHolder.itemView.tag as? Boolean ?: false
    }

    fun setClamp(clamp: Float) {
        this.clamp = clamp
    }

    // 다른 View가 swipe 되거나 터치되면 고정 해제
    fun removePreviousClamp(recyclerView: RecyclerView) {
        if (isDetail) {
            if (currentPosition == previousPosition)
                return
            previousPosition?.let {
                val viewHolder = recyclerView.findViewHolderForAdapterPosition(it) ?: return
                var holderType = viewHolder::class.java
                if (holderType == runningHolder || holderType == finishHolder) {
                    getView(viewHolder).translationX = 0f
                    setTag(viewHolder, false)
                    previousPosition = null
                }
            }
        }
    }

    private fun getView(viewHolder: RecyclerView.ViewHolder): View {

        var holderType = viewHolder::class.java

        when(holderType) {
            runningHolder -> {
                return viewHolder.itemView.ll_detail_title_wrapper!!
            }
            finishHolder -> {
                return viewHolder.itemView.ll_finish_detail_title_wrapper!!
            }
            else -> {
                return viewHolder.itemView.ll_detail_title_wrapper!!
            }
        }

    }

    override fun getSwipeEscapeVelocity(defaultValue: Float): Float {
        return defaultValue * 10
    }

    override fun isLongPressDragEnabled(): Boolean = false

}