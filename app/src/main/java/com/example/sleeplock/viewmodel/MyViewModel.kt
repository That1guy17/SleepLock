package com.example.sleeplock.viewmodel

import android.app.Application
import android.graphics.Color
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.sleeplock.R
import com.example.sleeplock.feature.isTimerPaused
import com.example.sleeplock.feature.isTimerRunning
import com.example.sleeplock.model.Repository
import com.example.sleeplock.model.isServiceRunning
import com.example.sleeplock.ui.itemIndex
import com.example.sleeplock.utils.getResourceString
import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.subscribeBy


class MyViewModel(application: Application) : AndroidViewModel(application) {

    // Observed by Main Fragment
    private val clickedItemIndex = MutableLiveData<Int>() // item clicked from recycler view
    private val buttonEnabled = MutableLiveData<Boolean>()
    private val buttonColor = MutableLiveData<Int>()
    private val buttonText = MutableLiveData<String>()


    fun getClickedItemIndex(): LiveData<Int> = clickedItemIndex
    fun getButtonEnabled(): LiveData<Boolean> = buttonEnabled
    fun getButtonColor(): LiveData<Int> = buttonColor
    fun getButtonText(): LiveData<String> = buttonText

    // From repository
    fun getCurrentTime() = repository.getCurrentTime()

    fun getTimerStarted() = repository.getTimerStarted()
    fun getTimerPaused() = repository.getTimerPaused()
    fun getTimerCompleted() = repository.getTimerCompleted()


    private val repository = Repository(application)

    private val compositeDisposable = CompositeDisposable()

    private var isTimeChosen = BehaviorRelay.createDefault(false)
    private var isSoundChosen = BehaviorRelay.createDefault(false)

    /*
    If the time and sound are chosen the start button will turn light blue and will be enabled(clickable), else it will turn dull/dark blue
    and will be disabled until the user selects both a time and a sound
     */
    private val isTimeAndSoundChosen =
        Observables.combineLatest(isTimeChosen, isSoundChosen) { timeChosen, soundChosen ->
            if (timeChosen && soundChosen) {
                buttonEnabled.value = true
                setButtonColor(true)
            } else {
                buttonEnabled.value = false
                setButtonColor(false)
            }
        }
            .doOnSubscribe { disposable -> compositeDisposable.addAll(disposable) }
            .subscribe()


    var startButtonClicked = true // used for switching from start/pause functionality
    var reverseAnim = false

    // Passed during run time
    var millis: Long? = null
    var index: Int? = null

    private val itemSelected =
        itemIndex.doOnSubscribe { disposable -> compositeDisposable.addAll(disposable) }
            .subscribeBy { index ->
                this.index = index
                isSoundChosen.accept(true)

                if (!isTimerRunning) clickedItemIndex.value = index // only updates card view data if the timer isn't running
            }


    fun passDialogTime(millis: Long) {
        this.millis = millis
        isTimeChosen.accept(true)
    }

    private fun startSoundAndTimer() {
        // Only called if millis & index  != null
        repository.startSoundAndTimer(millis!!, index!!)
        startButtonClicked = false
    }

    private fun pauseSoundAndTimer() {
        repository.pauseSoundAndTimer()
        startButtonClicked = true
    }

    private fun resumeSoundAndTimer() {
        repository.resumeSoundAndTimer()
        startButtonClicked = false
    }

    private fun resetSoundAndTimer() {
        // Invokes the timerCompleted Live Data
        repository.resetSoundAndTimer()
    }

    fun bindToService() = repository.bindToService()

    private fun setButtonColor(isTimeAndSoundChosen: Boolean) {
        if (isTimeAndSoundChosen) {
            buttonColor.value = Color.parseColor("#4dd0e1")  // light blue
        } else {
            buttonColor.value = Color.parseColor("#0B3136") // dark/dull blue
        }
    }

    fun setButtonText(isButtonStart: Boolean) {
        return if (isButtonStart) {
            buttonText.value = getApplication<Application>().getResourceString(R.string.pause)
        } else {
            buttonText.value = getApplication<Application>().getResourceString(R.string.resume)
        }
    }


    fun startPauseButtonClick(start: Boolean) {
        if (start) startButtonClick() else pauseButtonClick()
    }


    private fun startButtonClick() {
        if (isServiceRunning) resumeSoundAndTimer() else startSoundAndTimer()
        setButtonText(true)
    }

    private fun pauseButtonClick() {
        pauseSoundAndTimer()
        setButtonText(false)
    }

    fun resetButtonClick() {
        resetSoundAndTimer()
    }

    fun resetAll() {
        resetBooleans()
        resetButton()
    }

    private fun resetBooleans() {
        // resets booleans to default state
        isTimeChosen.accept(false)
        isSoundChosen.accept(false)
        startButtonClicked = true
    }

    private fun resetButton() {
        buttonText.value = getApplication<Application>().getResourceString(R.string.start)
        isSoundChosen.accept(false)
        isTimeChosen.accept(false)
    }

    fun restoreState() {
        startButtonClicked = if (isTimerPaused) {
            setButtonText(false)
            true
        } else {
            setButtonText(true)
            false
        }

        setButtonColor(true)

        isSoundChosen.accept(true)
        isTimeChosen.accept(true)

        clickedItemIndex.value = index
    }


    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}