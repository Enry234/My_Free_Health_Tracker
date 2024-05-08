package com.example.myfreehealthtracker.Models

import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.myfreehealthtracker.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ExpandableFloatingActionButton(
    container: ViewGroup,
    expanderId: Int,
    vararg childrenId: Int
) {

    private var isExpanded = true

    private val expander = container.findViewById<FloatingActionButton>(expanderId)
    private val children: List<FloatingActionButton> = childrenId.map {
        container.findViewById(it)
    }

    private val expandAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(container.context, R.anim.fab_expand)
    }
    private val collapseAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(container.context, R.anim.fab_collapse)
    }
    private val rotateClockWiseAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(container.context, R.anim.fab_expander_rotate_clock_wise)
    }
    private val rotateAntiClockWiseAnimation: Animation by lazy {
        AnimationUtils.loadAnimation(container.context, R.anim.fab_expander_rotate_anti_clock_wise)
    }

    fun enable() {
        expander.setOnClickListener {
            if (isExpanded) collapse() else expand()
        }
    }

    fun getChild(id: Int) = children.find { it.id == id }

    private fun collapse() {
        expander.startAnimation(rotateClockWiseAnimation)
        children.forEach { it.startAnimation(collapseAnimation) }
        isExpanded = !isExpanded
    }

    private fun expand() {
        expander.startAnimation(rotateAntiClockWiseAnimation)
        children.forEach { it.startAnimation(expandAnimation) }
        isExpanded = !isExpanded
    }
}