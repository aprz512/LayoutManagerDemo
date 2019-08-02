package com.aprz.layoutmanagerdemo.wave

import android.graphics.Path
import android.graphics.PathMeasure
import kotlin.math.atan2

/**
 * @author by liyunlei
 *
 * write on 2019/7/31
 *
 * Class desc:
 */
class PathUtil {

    lateinit var keyFrames: KeyFrames
        private set

    fun init(path: Path): ArrayList<LayoutPoint> {
        /*
        forceClosed，简单的说，就是Path最终是否需要闭合，如果为True的话，则不管关联的Path是否是闭合的，都会被闭合。

        但是这个参数对Path和PathMeasure的影响是需要解释下的：
            forceClosed参数对绑定的Path不会产生任何影响，例如一个折线段的Path，
                        本身是没有闭合的，forceClosed设置为True的时候，
                        PathMeasure计算的Path是闭合的，但Path本身绘制出来是不会闭合的。

            forceClosed参数对PathMeasure的测量结果有影响，还是例如前面说的一个折线段的Path，
                        本身没有闭合，forceClosed设置为True，
                        PathMeasure的计算就会包含最后一段闭合的路径，与原来的Path不同。
         */
        val pathMeasure = PathMeasure(path, false)

        val length = pathMeasure.length

        val pointNum = length.toInt() + 1

        val pointArray = ArrayList<LayoutPoint>()

        val pos = FloatArray(2) { 0f }
        val tan = FloatArray(2) { 0f }

        /**
         * 左闭右开区间
         */
        for (i in 0 until pointNum) {
            val distance = i.toFloat() * length / pointNum
            pathMeasure.getPosTan(distance, pos, tan)
            // 使用 Math.atan2(tan[1], tan[0]) 获取到正切角的弧度值。
            pointArray[i] = LayoutPoint(
                pos[0],
                pos[1],
                fixAngle(atan2(tan[1].toDouble(), tan[0].toDouble()) * 180 / Math.PI)
            )
        }

        keyFrames = KeyFrames(pointArray)
        return pointArray
    }

    private fun fixAngle(angle: Double): Float {
        val twoPi = 360F
        var tempAngle = angle
        if (tempAngle < 0) {
            tempAngle += twoPi
        }
        return (tempAngle % twoPi).toFloat()
    }

}