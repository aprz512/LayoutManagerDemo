package com.aprz.layoutmanagerdemo

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.accessibility.AccessibilityManager
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.TextView

/**
 * @author by liyunlei
 *
 * write on 2019/7/31
 *
 * Class desc:
 */
class TextViewEx @JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null,
    defStyleAttr: Int = 0
) : TextView(context, attributeSet, defStyleAttr) {

    init {
        setAccessibilityDelegate(object : AccessibilityDelegate() {

            override fun performAccessibilityAction(host: View?, action: Int, args: Bundle?): Boolean {
                return true
            }

            override fun onInitializeAccessibilityNodeInfo(host: View?, info: AccessibilityNodeInfo?) {
            }

            override fun sendAccessibilityEvent(host: View?, eventType: Int) {
            }

        })

        val accessibilityManager: AccessibilityManager =
            context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        accessibilityManager.accessibilityServiceList.forEach {
            val appInfo = context.packageManager.getApplicationInfo(it.packageName, PackageManager.GET_META_DATA)
            Log.e("package", context.packageManager.getApplicationLabel(appInfo).toString() + it.packageName)
//            Log.e("package", context.packageManager.getApplicationLabel(appInfo).toString() )
        }

    }

    override fun performAccessibilityAction(action: Int, arguments: Bundle?): Boolean {
        return true
    }

//    override fun setAccessibilityDelegate(@Nullable delegate: AccessibilityDelegate) {
//    }

}

//Context context, @Nullable AttributeSet attrs, int defStyleAttr