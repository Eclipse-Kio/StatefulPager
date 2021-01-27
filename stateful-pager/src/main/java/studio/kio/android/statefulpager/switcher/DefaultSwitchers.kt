package studio.kio.android.statefulpager.switcher

import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.core.view.children
import androidx.core.view.contains
import studio.kio.android.statefulpager.SwitchAnimationProvider
import studio.kio.android.statefulpager.ViewStateSwitcher

/**
 * created by KIO on 2021/1/25
 */
class DefaultSwitcher(
    private val container: ViewGroup,
    private val switchAnimationProvider: SwitchAnimationProvider? = null
) : ViewStateSwitcher {

    private var alternateView = AlternateWrapperLayout(container.context)

    override fun switchDefault() {
        switchAnimationProvider?.otherLeave()?.run {
            alternateView.startAnimation(this)
        }
        container.removeView(alternateView)
        switchAnimationProvider?.defaultEnter()?.run {
            container.children.filterVisibility(View.VISIBLE).startAnimation(this)
        }
    }

    override fun switchAlternate(view: View) {

        //need replace alternate content
        if (view != alternateView.contentView) {

            if (container.contains(alternateView)) {
                //animate other leave only alternate view is visible
                switchAnimationProvider?.otherLeave()?.run {
                    alternateView.contentView?.startAnimation(this)
                }
            }
            alternateView.removeAllViews()

            alternateView.addView(view, container.width, container.height)
            switchAnimationProvider?.otherEnter()?.run {
                view.startAnimation(this)
            }
        }

        if (!container.contains(alternateView)) {
            switchAnimationProvider?.defaultLeave()?.run {
                container.children.startAnimation(this)
            }

            container.addView(alternateView, container.width, container.height)
        }
    }

    private fun Sequence<View>.filterVisibility(visibility: Int) = filter {
        it.visibility == visibility
    }

    private fun Sequence<View>.startAnimation(animation: Animation) {
        forEach {
            it.startAnimation(animation)
        }
    }
}