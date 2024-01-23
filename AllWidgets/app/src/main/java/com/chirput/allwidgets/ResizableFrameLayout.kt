package com.chirput.allwidgets

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.widget.FrameLayout

class ResizableFrameLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    private val TAG = "YourTouchListener"
    private var startX = 0f
    private var startY = 0f

    private var isLeftEdge: Boolean = false
    private var isRightEdge: Boolean = false
    private var isTopEdge: Boolean = false
    private var isBottomEdge: Boolean = false

    private var lastX: Float = 0f
    private var lastY: Float = 0f

    private val gestureDetector: GestureDetector
    private var isLongPressed: Boolean = false

    init {
        isClickable = true

        gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                // Handle long press here
                // You can add your code to perform actions only on long press
                // For example, you can call a method like handleLongPress()
                isLongPressed = true
                setBackgroundColor(Color.RED)
                onTouchEvent(e)
            }
        })
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(ev) || super.onInterceptTouchEvent(ev)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {

        if (!isLongPressed) {
            return true
        }

        var newLeft = left
        var newTop = top
        var newRight = right
        var newBottom = bottom

        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                startX = event.x
                startY = event.y

                val viewWidth = width
                val viewHeight = height

                isLeftEdge = startX <= (viewWidth * 0.2)
                isRightEdge = startX >= (viewWidth * 0.8)
                isTopEdge = startY <= (viewHeight * 0.2)
                isBottomEdge = startY >= (viewHeight * 0.8)

                if (isLeftEdge) {
                    Log.d(TAG, "ACTION_DOWN - Left edge touched initially")
                } else if (isRightEdge) {
                    Log.d(TAG, "ACTION_DOWN - Right edge touched initially")
                }

                if (isTopEdge) {
                    Log.d(TAG, "ACTION_DOWN - Top edge touched initially")
                } else if (isBottomEdge) {
                    Log.d(TAG, "ACTION_DOWN - Bottom edge touched initially")
                }

                lastX = event.rawX
                lastY = event.rawY
            }

            MotionEvent.ACTION_MOVE -> {
                val currentX = event.rawX
                val currentY = event.rawY

                val deltaX = currentX - lastX
                val deltaY = currentY - lastY

                if (isLeftEdge) {
                    Log.d(TAG, "ACTION_MOVE - Left edge touched initially")
                    newLeft = (left + deltaX).toInt()
                    newRight = right
                } else if (isRightEdge) {
                    Log.d(TAG, "ACTION_MOVE - Right edge touched initially")
                    newRight = (right + deltaX).toInt()
                    newLeft = left
                }

                if (isTopEdge) {
                    Log.d(TAG, "ACTION_MOVE - Top edge touched initially")
                    newTop = (top + deltaY).toInt()
                    newBottom = bottom
                } else if (isBottomEdge) {
                    Log.d(TAG, "ACTION_MOVE - Bottom edge touched initially")
                    newBottom = (bottom + deltaY).toInt()
                    newTop = top
                }

                // If none of the edges is selected, perform the general move logic
                if (!isLeftEdge && !isRightEdge && !isTopEdge && !isBottomEdge) {
                    newLeft = (left + deltaX).toInt()
                    newTop = (top + deltaY).toInt()
                    newRight = (right + deltaX).toInt()
                    newBottom = (bottom + deltaY).toInt()
                }

                layout(newLeft, newTop, newRight, newBottom)

                layout(newLeft, newTop, newRight, newBottom)

                lastX = currentX
                lastY = currentY

                Log.d(TAG, "$width $height")

//                resize(width = width, height = height, hostView = hostView)

            }

            MotionEvent.ACTION_UP -> {
                isLongPressed = false
                setBackgroundColor(Color.CYAN)
            }

            else -> {
                // Handle other MotionEvent cases if needed
            }
        }
        return true
    }

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val savedState = Bundle()
        savedState.putParcelable("superState", superState)
        savedState.putFloat("lastX", lastX)
        savedState.putFloat("lastY", lastY)
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable("superState"))
            lastX = state.getFloat("lastX", 0f)
            lastY = state.getFloat("lastY", 0f)
        } else {
            super.onRestoreInstanceState(state)
        }
    }

}





