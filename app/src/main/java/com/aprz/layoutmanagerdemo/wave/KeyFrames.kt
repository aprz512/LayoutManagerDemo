package com.aprz.layoutmanagerdemo.wave

/**
 * @author by liyunlei
 *
 * write on 2019/7/31
 *
 * Class desc:
 */
data class KeyFrames(val points: ArrayList<LayoutPoint>)

data class LayoutPoint(val x: Float, val y: Float, val tan: Float)