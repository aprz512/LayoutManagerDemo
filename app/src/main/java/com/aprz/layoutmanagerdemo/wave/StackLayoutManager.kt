package com.aprz.layoutmanagerdemo.wave

import android.graphics.Path
import android.util.Log
import android.util.Range
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.*
import kotlin.math.abs
import kotlin.math.floor
import java.nio.file.Files.size



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
    private var maxScrollX = 0

    companion object {
        const val MAX_STACK_COUNT = 2
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

        layoutChildren(recycler, state)
    }

    override fun scrollHorizontallyBy(dx: Int, recycler: Recycler?, state: State?): Int {

        if (dx == 0 || state?.itemCount == 0) {
            return 0
        }

        layoutChildren(recycler, state)

        Log.e("con", "${consume(dx)}")

        return consume(dx)
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
        // 该值会动态更新
        lastVisiblePos = state.itemCount - 1


        val frac: Float = (abs(scrollX) % unitDistance) / (unitDistance * 1f)
        val stackOffset = (frac * stackGap).toInt()
        val viewOffset = (frac * unitDistance).toInt()

        var stackOffsetDone = false
        var viewOffsetDone = false


        for (i in firstVisiblePos until lastVisiblePos) {

            // 属于堆叠区域：
            // 但是这里就会有一个问题，这个 layoutManager 一初始化就会堆叠起来，导致前面几个的内容看不到了
            // 解决办法就是做出一个无限循环的效果，这样就会对数目有所限制，至少是知道有多少数据
            // 或者是做成动态的，一开始不会堆叠， 滑动的时候再考虑如何堆叠
            if (i - firstVisiblePos < MAX_STACK_COUNT) {
                if (!stackOffsetDone) {
                    // 明白为什么item的移动方向会与手指的滑动方向相反了
                    // 手指向左滑动，则 scrollX 的值会越来越大，frac 也会慢慢变大（0 -> 1 为一个周期）
                    // 所以 item 会向右移动
                    // 这里需要减去
                    left -= stackOffset
                    stackOffsetDone = true
                }
                left += stackGap
            } else {
                if (!viewOffsetDone) {
                    // 明白为什么item的移动方向会与手指的滑动方向相反了
                    // 手指向左滑动，则 scrollX 的值会越来越大，frac 也会慢慢变大（0 -> 1 为一个周期）
                    // 所以 item 会向右移动
                    // 这里需要减去
                    left -= viewOffset
                    viewOffsetDone = true
                }
                left += unitDistance
            }

            // 超过右边，就不放 item 了
            if (left > width - paddingRight) {
                lastVisiblePos = i
                break
            }

            val view = recycler.getViewForPosition(i)
            addView(view)
            measureChildWithMargins(view, 0, 0)

            val l = left
            val t = paddingTop
            val r = l + getDecoratedMeasuredWidth(view)
            val b = t + getDecoratedMeasuredHeight(view)
            layoutDecoratedWithMargins(view, l, t, r, b)

            if (i == MAX_STACK_COUNT - 1 && frac == 0f) {
                maxScrollX = (itemCount - i) * unitDistance
            }
        }



        recycleChildren(recycler)

    }

    /**
     * 回收需回收的Item。
     */
    private fun recycleChildren(recycler: Recycler) {
        val scrapList = recycler.scrapList
        for (i in scrapList.indices) {
            val holder = scrapList[i]
            removeAndRecycleView(holder.itemView, recycler)
        }
    }

    /**
     * dx(dy) 表示本次较于上一次的偏移量，<0为 向右(下) 滚动，>0为向左(上) 滚动；
     * 这个算法还是无法满足 fling 的要求，fling 的时候 停留的位置不对
     * 查了一些资料，可能还需要自定义一个 SnapHelper -> https://www.jianshu.com/p/0e4a93d8e2de
     */
    private fun consume(dx: Int): Int {
        val consumed: Int
        // dx < 0 表示向右滚动，需要显示左边的内容
        if (dx < 0) {
            // 到了最左边
            if (scrollX + dx < 0) {
                consumed = if (scrollX > 0) {
                    dx - scrollX
                } else {
                    0
                }
                scrollX = 0
                return consumed
            }
        }

        // dx > 0 表示向左滚动，右边的内容需要显示出来
        if (dx > 0) {
            if (scrollX + dx > maxScrollX) {
                consumed = if (scrollX < maxScrollX) {
                    maxScrollX - scrollX
                } else {
                    0
                }
                scrollX = maxScrollX
                return consumed
            }
        }

        scrollX += dx

        return dx
    }


}