package com.keygle.cnbetax

import android.text.TextUtils
import androidx.annotation.DrawableRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

/**
 * Created by didik on 2017/2/9.
 * 统一处理标题等等
 */
abstract class BaseActivity : AppCompatActivity() {
    protected fun setToolBar(
        toolbar: Toolbar,
        title: CharSequence?,
        backIcon: Boolean,
        subTitle: CharSequence?,
        @DrawableRes logo: Int
    ) {
        if (logo != 0) {
            toolbar.setLogo(logo)
        }
        if (!TextUtils.isEmpty(subTitle)) {
            toolbar.subtitle = subTitle
        }
        if (!TextUtils.isEmpty(title)) {
            toolbar.title = title
        }
        setSupportActionBar(toolbar)
        if (backIcon) {
            getSupportActionBar()?.setHomeButtonEnabled(true)
            getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)
        }
    }

    protected fun setToolBar(
        toolbar: Toolbar,
        title: CharSequence?,
        backIcon: Boolean
    ) {
        setToolBar(toolbar, title, backIcon, null, 0)
    }

    protected fun setToolBar(toolbar: Toolbar, title: CharSequence?) {
        setToolBar(toolbar, title, false, null, 0)
    }

}
