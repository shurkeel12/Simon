package com.example.simon

import android.app.Dialog
import android.content.Context
import android.media.AudioManager
import android.media.SoundPool
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.TextView
import java.util.*
import kotlin.collections.ArrayList

class SimonActivity : AppCompatActivity(), View.OnClickListener {

    var soundPool: SoundPool? = null
    val simonImageView = arrayOfNulls<SimonCellType>(4)

    private var simonSoundPoolLoaded = false
    private var simonLevel = 1
    private var simonCellOnList: ArrayList<Int>? = null
    private var simonCount = 0
    private var simonCurrentCellIndex = 0
    private var simonTimer: Timer? = null
    private var simonTimerTask: TimerTask? = null
    private val simonSoundID = IntArray(4)
    private var simonSoundVolume = 0f
    private var simonPrevSoundStreamID = 0
    private var simonScore = 0
    private var simonTimerForClick: CountDownTimer? = null
    private var simonSoundOn = true
    private var simonCountDownTimer: CountDownTimer? = null
    private var simonTimerTaskCompleted = true
    private var simonInstanceSaved = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_simon)

        simonImageView[0] = findViewById<View>(R.id.greenCell) as SimonCellType
        simonImageView[1] = findViewById<View>(R.id.redCell) as SimonCellType
        simonImageView[2] = findViewById<View>(R.id.yellowCell) as SimonCellType
        simonImageView[3] = findViewById<View>(R.id.blueCell) as SimonCellType
        simonImageView[0]!!.setCellType(SimonCellType.CELL_TYPE_GREEN)
        simonImageView[1]!!.setCellType(SimonCellType.CELL_TYPE_RED)
        simonImageView[2]!!.setCellType(SimonCellType.CELL_TYPE_YELLOW)
        simonImageView[3]!!.setCellType(SimonCellType.CELL_TYPE_BLUE)


        for (i in 0..3) {
            simonImageView[i]!!.setOnClickListener(this)
            simonImageView[i]!!.setOff()
        }

        if (savedInstanceState != null) {
            simonInstanceSaved = true
            val simonArray = savedInstanceState.getIntArray(CELL)
            simonCellOnList = ArrayList()

            for (i in simonArray!!.indices) {
                simonCellOnList!!.add(simonArray[i])
            }

            simonScore = savedInstanceState.getInt(SCORE)
            simonLevel = savedInstanceState.getInt(LEVEL)

            simonTimerTaskCompleted = savedInstanceState.getBoolean(TIMER)

            gameSounds()
        } else {
            gameSounds()
            
            startGame()
        }

    }

    private fun startGame() {

        val round = Random(Calendar.getInstance().timeInMillis)

        if (simonCellOnList == null) {
            simonCellOnList = ArrayList()
        }
        val number = round.nextInt(4)
        simonCellOnList!!.add(number)
        
        if (simonSoundPoolLoaded) {
            litCells()
        }
    }

    private fun litCells() {
        simonInstanceSaved = false
        simonCurrentCellIndex = 0
        
        if (simonTimer != null) {
            simonTimer!!.cancel()
        }
        simonTimer = Timer()
        simonTimerTask = object: TimerTask() {
            override fun run() {
                runOnUiThread {
                    simonTimerTaskCompleted = false
                    if (simonCurrentCellIndex < simonCellOnList!!.size) {
                        litCellsTwo(simonCellOnList!![simonCurrentCellIndex++])
                    } else {
                        simonTimerTaskCompleted = true
                    }
                }
            }
        }
        simonTimer!!.schedule(simonTimerTask, 0, 700)
    }

    private fun litCellsTwo(litCellsIndex: Int) {
        val litCell = simonImageView[litCellsIndex]
        if (simonPrevSoundStreamID != 0) {
            soundPool!!.stop(simonPrevSoundStreamID)
        }
        if (simonSoundOn) {
            simonPrevSoundStreamID = soundPool!!.play(simonSoundID[litCellsIndex],
            simonSoundVolume, simonSoundVolume, 1, 0, 1f)
        }

        litCell!!.setOn()
        startTimer(litCell)
    }

    private fun startTimer(litCell: SimonCellType?) {
        val startTimer: CountDownTimer = object: CountDownTimer(300, 400) {
            override fun onTick(l: Long) {}
            override fun onFinish() {
                litCell!!.setOff()
            }
        }
        startTimer.start()
    }

    private fun gameSounds() {
        if (soundPool == null) {
            soundPool = SoundPool(50, AudioManager.STREAM_MUSIC, 8)
            simonSoundID[0] = soundPool!!.load(this, R.raw.greensound, 1)
            simonSoundID[1] = soundPool!!.load(this, R.raw.redsound, 1)
            simonSoundID[2] = soundPool!!.load(this, R.raw.yellowsound, 1)
            simonSoundID[3] = soundPool!!.load(this, R.raw.bluesound, 1)
            soundPool!!.setOnLoadCompleteListener { _, sampleID, _ ->
                simonSoundPoolLoaded = true
                if (!simonTimerTaskCompleted && simonInstanceSaved) {
                    litCells()
                }
                if (!simonInstanceSaved && sampleID == simonSoundID[3]) {
                    litCells()
                }
            }
            val audioManager = this.getSystemService(Context.AUDIO_SERVICE) as AudioManager
            val actualVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC).toFloat()
            val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC).toFloat()
            simonSoundVolume = actualVolume / maxVolume
            volumeControlStream = AudioManager.STREAM_MUSIC

        }
    }
    private fun simonCellClicked (cellNumber: Int, cell: SimonCellType) {
        if (simonTimerForClick != null) {
            simonTimerForClick!!.cancel()
        }
        cell.setOn()
        if (simonPrevSoundStreamID != 0) {
            soundPool!!.stop(simonPrevSoundStreamID)
        }
        if (simonSoundOn) {
            simonPrevSoundStreamID = soundPool!!.play(simonSoundID[cellNumber],
            simonSoundVolume, simonSoundVolume, 1, 0, 1f)
        }
        startTimer(cell)
        if (simonCellOnList!![simonCount] != cellNumber) {
            showAlert("Game Over\n\nScore $simonScore", GAME_OVER)
            return
        }
        simonCount++
        if (simonCount == simonLevel) {
            simonLevel ++
            simonCount = 0
            simonScore += 1000
            showAlert("LEVEL" + (simonLevel - 1).toString() + " Completed\n\nScore" +
                    simonScore, LEVEL_UP)

        }
    }

    private fun showAlert(s: String, dlgCode: Int) {
        val d = Dialog(this, android.R.style.Theme_Translucent_NoTitleBar)
        d.setContentView(R.layout.activity_simon)

        val tv = d.findViewById<View>(R.id.scoreMessage) as TextView
        tv.textSize = 48f
        tv.text = s

        simonCountDownTimer = object: CountDownTimer(1000, 1000) {
            override fun onTick(l: Long) {}

            override fun onFinish() {
                d.dismiss()
                if (dlgCode == LEVEL_UP) {
                    startGame()
                } else if (dlgCode == GAME_OVER) {
                    finish()
                }
            }
        }
        (simonCountDownTimer as CountDownTimer).start()
        d.show()
    }

    override fun onClick(view: View) {
        val id = view.id

        when(id) {
            R.id.greenCell -> simonCellClicked(0, view as SimonCellType)
            R.id.redCell -> simonCellClicked(1, view as SimonCellType)
            R.id.yellowCell -> simonCellClicked(2, view as SimonCellType)
            R.id.blueCell -> simonCellClicked(3, view as SimonCellType)
        }
    }
    companion object {
        private const val LEVEL_UP = 1
        private const val GAME_OVER = 2
        private const val CELL = "SimonCell"
        private const val SCORE = "Score"
        private const val LEVEL = "Level"
        private const val TIMER = "GameDisplayTimerCompleted"
    }
}
