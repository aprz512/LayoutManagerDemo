package com.aprz.layoutmanagerdemo

/**
 * @author by liyunlei
 *
 * write on 2019/8/1
 *
 * Class desc:
 */
fun checkNull(any: Any?, func: (Any) -> Any?) {
    any?.apply {
        func.invoke(this)
    }
}