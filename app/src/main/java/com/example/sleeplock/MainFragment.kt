package com.example.sleeplock


import android.os.Bundle
import android.transition.TransitionManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_main.*


class MainFragment : Fragment() {

    private lateinit var viewModel: ViewModel
    private val dataSource: DataSource = DataSource()

    //Animation stuff
    private val defaultLayout = ConstraintSet()
    private val animatedLayout = ConstraintSet()
    private lateinit var thisLayout: ConstraintLayout
    private var startAnim = true


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)
        thisLayout = view.findViewById(R.id.main_fragment_layout)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fab.setOnClickListener {
            showDialog()
        }

        // we're gonna have alot of logic here, maybe use RX to emit a button to our view model, and we do our logic there
        start_pause_button.setOnClickListener { button ->
            // todo move this business logic into your view model if you can
            if (viewModel.startButton) {

                viewModel.startButtonClick(viewModel.startButton)

                if (startAnim) startAnimation()

            } else {
                viewModel.pauseButtonClick(viewModel.startButton)
            }
        }




        reset_button.setOnClickListener {
            viewModel.resetButtonClick()
            startAnimation()
        }

        defaultLayout.clone(thisLayout)
        animatedLayout.clone(context, R.layout.fragment_main_animation)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(activity!!).get(ViewModel::class.java)

        viewModel.updateCurrentTime.observe(viewLifecycleOwner, observeCurrentTime())

        viewModel.clickedItemIndex.observe(viewLifecycleOwner, observeItemIndex())

        viewModel.enabledOrDisabled.observe(viewLifecycleOwner, observeEnabledDisabled())

        viewModel.updateButtonColor.observe(viewLifecycleOwner, observeButtonColor())

        viewModel.updateButtonText.observe(viewLifecycleOwner, observeButtonText())

        viewModel.notifyAnimation.observe(viewLifecycleOwner, observeNotifyAnim())


    }


    private fun showDialog() {
        val dialog = TimeOptionDialog()
        viewModel.subscribeToDialog(dialog.dialogTime)
        if (!dialog.isAdded) {
            val fragmentManager: FragmentManager? = activity?.supportFragmentManager
            if (fragmentManager != null) dialog.show(fragmentManager, "Time Dialog")
        }
    }


    private fun updateCardViewText(index: Int, dataSource: DataSource) {
        Glide.with(context!!)
            .asBitmap()
            .load(dataSource.ITEM_PIC[index])
            .into(card_view_pic)

        card_view_text.text = dataSource.ITEM_TEXT[index]
    }

    // UI handles animations
    private fun startAnimation() {
        TransitionManager.beginDelayedTransition(thisLayout)

        if (startAnim) { // true = startButton    false = revert
            animatedLayout.applyTo(thisLayout)
            startAnim = !startAnim
        } else {
            defaultLayout.applyTo(thisLayout)
            startAnim = !startAnim
        }
    }


    override fun onPause() {
        super.onPause()
        viewModel.maybeStartService()
    }


    override fun onResume() {
        super.onResume()
        viewModel.destroyService()
        if (isServiceRunning) {
            startAnimation()
            viewModel.restoreButton()
        }
    }


    // Live Data Observers

    private fun observeCurrentTime(): Observer<String> { // Updates our text view with the current time
        return Observer { time -> current_time_text_view.text = time }
    }


    private fun observeItemIndex(): Observer<Int> { // sets card view text and image to the item selected in the recycler view
        return Observer { index -> updateCardViewText(index, dataSource) }
    }


    private fun observeButtonColor(): Observer<Int> { // sets the button color
        return Observer { color -> start_pause_button.setTextColor(color) }
    }


    private fun observeEnabledDisabled(): Observer<Boolean> { // Sets button clickability
        return Observer { aBoolean -> start_pause_button.isClickable = aBoolean }
    }

    private fun observeButtonText(): Observer<String> { // Sets button text
        return Observer { text -> start_pause_button.text = text }
    }


    private fun observeNotifyAnim(): Observer<Boolean> {
        return Observer { aBoolean -> startAnimation() }
    }

}
