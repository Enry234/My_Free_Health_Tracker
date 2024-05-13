package com.example.myfreehealthtracker

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExpandableFloatingActionButton(
    container: ViewGroup,
    private val expander: FloatingActionButton,
    vararg children: FloatingActionButton
) {
    private var isExpanded = false
    private val children: List<FloatingActionButton> = children.map { it }
    private val expandAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(expander.context, R.anim.fab_expand)
    }
    private val collapseAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(expander.context, R.anim.fab_collapse)
    }
    private val rotateClockWise: Animation by lazy {
        AnimationUtils.loadAnimation(expander.context, R.anim.fab_expander_icon_rotate_clock_wise)
    }
    private val rotateAntiClockWise: Animation by lazy {
        AnimationUtils.loadAnimation(expander.context, R.anim.fab_expander_icon_rotate_anti_clock_wise)
    }

    init {
        expander.setOnClickListener {
            if (isExpanded) collapse() else expand()
        }
        container.setOnClickListener {
            collapseIfExpanded()
        }
    }

    fun setChildActionListener(child: FloatingActionButton, action: (View) -> Unit) {
        child.setOnClickListener {
            action(it)
            collapse()
        }
    }

    fun collapseIfExpanded() {
        if (isExpanded) collapse()
    }
    private fun expand() {
        expander.startAnimation(rotateClockWise)
        children.forEach {
            it.startAnimation(expandAnimation)
            it.isClickable = true
            it.visibility = View.VISIBLE
        }
        isExpanded = !isExpanded
    }

    private fun collapse() {
        expander.startAnimation(rotateAntiClockWise)
        children.forEach {
            it.startAnimation(collapseAnimation)
            it.isClickable = false
            it.visibility = View.INVISIBLE
        }
        isExpanded = !isExpanded
    }
}