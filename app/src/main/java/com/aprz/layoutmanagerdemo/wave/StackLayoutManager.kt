package com.aprz.layoutmanagerdemo.wave

import android.graphics.Path
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import kotlin.math.abs
import kotlin.math.floor

/**
 * @author by liyunlei
 *
 * write on 2019/7/31
 *
 * Class desc:
 */
class StackLayoutManager : RecyclerView.LayoutManager() {

    private lateinit var points: ArrayList<LayoutPoint>
    private val pathUtil = PathUtil()
    private var scrollX = 0
    private var firstVisiblePos = 0
    private var lastVisiblePos = 0
    private var unitDistance = -1
    private val gap = 20
    private val stackGap = 10

    companion object {
        const val MAX_STACK_COUNT = 6
    }

    fun setPath(path: Path) {
        points = pathUtil.init(path)
    }


    /**
     * 生成 layoutParams
     */
    override fun generateDefaultLayoutParams(): LayoutParams {
        return LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        )
    }

    override fun canScrollHorizontally(): Boolean {
        return true
    }

    override fun canScrollVertically(): Boolean {
        return false
    }

    /**
     * 将 child 放入 recyclerView
     */
    override fun onLayoutChildren(recycler: Recycler?, state: State?) {
        super.onLayoutChildren(recycler, state)

        // 这个方法看不出来有啥意义啊，应该是根据
        // androidx.recyclerview.widget.LinearLayoutManager.onLayoutChildren
        // copy 出来的
        if (state?.itemCount == 0) {
            recycler?.apply {
                removeAndRecycleAllViews(this)
            }
            return
        }

//        recycler?.apply {
//            detachAndScrapAttachedViews(this)
//        }

//        resetChildWidth()

        fill(recycler, state, 0)

    }

    private fun resetChildWidth() {
        unitDistance = -1
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: Recycler?, state: State?): Int {

        if (dx == 0 || state?.itemCount == 0) {
            return 0
        }

        val distance = fill(recycler, state, dx)

        scrollX += distance

        return distance
    }

    /**
     * dx(dy) 表示本次较于上一次的偏移量，<0为 向右(下) 滚动，>0为向左(上) 滚动；
     */
    private fun fill(
        recycler: Recycler?,
        state: State?,
        dx: Int
    ): Int {

        var result: Int

        if (reachBound(dx)) {
            result = 0
        } else if (dx < 0 && scrollX < -dx) {
            result = scrollX
        } else if (dx > 0 && scrollX + dx > width - paddingRight) {
            result = width - paddingRight - scrollX
        } else {
            result = dx
        }

        layoutChildren(recycler, state)

        return result
    }

    private fun layoutChildren(
        recycler: Recycler?,
        state: State?
    ) {

        if (recycler == null || state == null) {
            return
        }

        detachAndScrapAttachedViews(recycler)

        var left = paddingLeft

        // 因为有自动选中效果，所以需要每个child一样大小
        val firstView: View = recycler.getViewForPosition(firstVisiblePos)
        measureChildWithMargins(firstView, 0, 0)
        unitDistance = getDecoratedMeasuredWidth(firstView) + gap
        // 用完了再放回去，为了后面的逻辑统一处理
        recycler.recycleView(firstView)

        // 根据 scroll 的距离来计算 firstPos 的位置
        firstVisiblePos = floor(abs(scrollX).toDouble() / unitDistance).toInt()

        var visibleViewCount = 0
        // 不要被这个 true 吓到了
        while (true) {
            // 属于堆叠区域：
            // 但是这里就会有一个问题，这个 layoutManager 一初始化就会堆叠起来，导致前面几个的内容看不到了
            // 解决办法就是做出一个无限循环的效果，这样就会对数目有所限制，至少是知道有多少数据
            // 或者是做成动态的，一开始不会堆叠， 滑动的时候再考虑如何堆叠

            left += if (firstVisiblePos + visibleViewCount < MAX_STACK_COUNT) {
                stackGap
            } else {
                unitDistance
            }

            if (left > width - paddingRight) {
                lastVisiblePos = firstVisiblePos + visibleViewCount
                break
            }

            val view = recycler.getViewForPosition(visibleViewCount)
            addView(view)
            measureChildWithMargins(view, 0, 0)

            val l = left
            val t = paddingTop
            val r = l + getDecoratedMeasuredWidth(view)
            val b = t + getDecoratedMeasuredHeight(view)
            layoutDecoratedWithMargins(view, l, t, r, b)

            visibleViewCount++
        }
    }

    private fun reachBound(dx: Int): Boolean {
        // dx < 0 表示向右滚动，需要显示左边的内容
        if (dx < 0) {
            // 到了最左边
            if (scrollX <= 0) {
                return true
            }
        }

        // dx > 0 表示向左滚动，右边的内容需要显示出来
        if (dx > 0) {
            // 到了最右边
            if (lastVisiblePos - firstVisiblePos <= MAX_STACK_COUNT - 1) {
                return true
            }
        }

        return false
    }


}