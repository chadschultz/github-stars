package com.example.githubstars

import android.view.View
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {

    //TODO: UI tests for this - or would it have to be a UI test?
    /**
     * Show one [View] and hide the other, based on the value of [showFirst]. Handy way to toggle
     * between two [View]s, such as a [RecyclerView] and a [View] to show when there are no items.
     *
     * This code could have been inlined in the final [Fragment], but it's the sort of code that could
     * be reused and should be tested, so it's better to centralize it.
     * An extension function on [Fragment] could have been used instead. I feel a Base class results
     * in better organization, but the advantages are slight.
     */
    fun showOneOf(firstView: View, secondView: View, showFirst: Boolean) {
        firstView.visibility = if (showFirst) View.VISIBLE else View.GONE
        secondView.visibility = if (showFirst) View.GONE else View.VISIBLE
    }

}
