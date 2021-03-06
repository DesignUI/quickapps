package com.yoavst.quickapps.recorder

import com.yoavst.quickapps.util.QCircleActivity
import com.lge.qcircle.template.QCircleTemplate
import kotlin.properties.Delegates
import com.lge.qcircle.template.TemplateType
import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import at.markushi.ui.CircleButton
import android.graphics.drawable.Drawable
import java.util.Timer
import com.yoavst.quickapps.clock.StopwatchManager
import butterknife.bindView
import com.yoavst.quickapps.R
import com.malinskiy.materialicons.IconDrawable
import com.malinskiy.materialicons.Iconify
import com.lge.qcircle.template.TemplateTag
import android.view.LayoutInflater
import java.io.File
import com.mobsandgeeks.ake.getColor
import com.mobsandgeeks.ake.postDelayed
import android.view.View
import com.mobsandgeeks.ake.hide
import com.mobsandgeeks.ake.show
import com.yoavst.util.qCircleToast
import com.yoavst.util.r
import android.widget.Toast
import android.view.Gravity
import android.os.Environment
import android.os.Handler
import com.lge.qcircle.template.QCircleDialog

/**
 * Created by Yoav.
 */
public class CRecorderActivity : QCircleActivity() {
    override val template: QCircleTemplate by Delegates.lazy { QCircleTemplate(this, TemplateType.CIRCLE_EMPTY) }
    val time: TextView by bindView(R.id.time)
    val recorder: RecordButton by bindView(R.id.recorder)
    val pause: CircleButton by bindView(R.id.pause)
    val trash: CircleButton by bindView(R.id.trash)
    val pauseDrawable: Drawable by Delegates.lazy { IconDrawable(this, Iconify.IconValue.md_pause).colorRes(R.color.md_red_500).sizeDp(32) }
    val resumeDrawable: Drawable by Delegates.lazy { IconDrawable(this, Iconify.IconValue.md_play_arrow).colorRes(R.color.md_red_500).sizeDp(32) }
    var audioRecorder: AudioRecorder? = null
    var timer: Timer? = null
    var timerTask: StopwatchManager.Stopwatch? = null
    var timeSinceStart: Long = 0
    var recordName: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val layout = template.getLayoutById(TemplateTag.CONTENT_MAIN)
        layout.addView(LayoutInflater.from(this).inflate(R.layout.recorder_circle_layout, layout, false))
        template.setBackButton { v ->
            if (audioRecorder != null && audioRecorder!!.isRecording()) {
                audioRecorder!!.pause(object : AudioRecorder.OnPauseListener {
                    override fun onPaused(activeRecordFileName: String) {
                        try {
                            File(activeRecordFileName).delete()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                    }

                    override fun onException(e: Exception) {
                    }
                })
            }
        }
        template.setBackButtonTheme(true)
        template.setBackgroundColor(getColor(R.color.md_red_500), true)
        setContentView(template.getView())
        trash.setImageDrawable(IconDrawable(this, Iconify.IconValue.md_delete).colorRes(R.color.md_red_500).sizeDp(32))
        trash.setOnClickListener { v -> onTrashClicked(v) }
        pause.setImageDrawable(pauseDrawable)
        pause.setOnClickListener { v -> onPauseClicked(v) }

        recorder.setOnClickListener { v -> recordOrStop() }
    }

    fun hideHelperButtons() {
        trash.hide()
        pause.hide()
    }

    fun showHelperButtons() {
        trash.show()
        pause.show()
    }

    fun recordOrStop() {
        recorder.setEnabled(false)
        if (audioRecorder == null) {
            val folder = File(Environment.getExternalStorageDirectory(), "VoiceRecorder")
            if (!folder.exists()) folder.mkdir()
            recordName = "record_" + System.currentTimeMillis() + ".mp4"
            audioRecorder = AudioRecorder.build(this, File(folder, recordName).getAbsolutePath())
            startRecording()
            Handler().postDelayed(500) {
                recorder.setEnabled(true)
            }
        } else {
            hideHelperButtons()
            if (audioRecorder!!.isRecording())
                pause {
                    showRecordedDialog()
                }
            else
                showRecordedDialog()
            stopCounting()
            recorder.setNotRecording()
            audioRecorder = null
        }
    }

    fun showRecordedDialog() {
        QCircleDialog.Builder()
                .setTitle(getString(R.string.voice_recorded))
                .setMode(QCircleDialog.DialogMode.Ok)
                .setText(getString(R.string.successfully_record_audio))
                .setPositiveButtonListener { v -> recorder.setEnabled(true) }
                .create()
                .show(this, template)
    }

    fun onTrashClicked(v: View) {
        if (audioRecorder != null) {
            v.setEnabled(false)
            if (audioRecorder!!.isRecording())
                audioRecorder!!.pause(object : AudioRecorder.OnPauseListener {
                    override fun onPaused(activeRecordFileName: String) {
                        try {
                            doDelete(activeRecordFileName)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }

                        v.setEnabled(true)
                    }

                    override fun onException(e: Exception) {
                        v.setEnabled(true)

                    }
                })
            else {
                if (recordName != null)
                    doDelete(recordName!!)
                v.setEnabled(true)
            }
        }
    }

    fun doDelete(filename: String) {
        File(filename).delete()
        hideHelperButtons()
        stopCounting()
        recorder.setNotRecording()
        audioRecorder = null
        qCircleToast(R.string.delete_record)
    }

    fun pause(onSuccess: (() -> Unit)? = null) {
        if (audioRecorder != null) {
            audioRecorder!!.pause(object : AudioRecorder.OnPauseListener {
                override fun onPaused(activeRecordFileName: String) {
                    onSuccess?.invoke()
                }

                override fun onException(e: Exception) {
                    qCircleToast("Error with pause recording")
                    audioRecorder = null
                }
            })
        }
    }

    fun stopCounting() {
        timer?.cancel()
        timeSinceStart = 0
        timer = null
        time.setText(R.string.start_record)
    }

    fun pauseCounting() {
        timerTask?.isRunning(false)
    }

    fun resumeCounting() {
        timerTask?.isRunning(true)
    }

    fun onPauseClicked(v: View) {
        if (audioRecorder != null) {
            v.setEnabled(false)
            if (audioRecorder!!.isRecording()) {
                pause.setImageDrawable(resumeDrawable)
                recorder.setPause()
                pauseCounting()
                pause {
                    v.setEnabled(true)
                }
            } else {
                pause.setImageDrawable(pauseDrawable)
                resumeCounting()
                startRecording()
                v.setEnabled(true)
            }
        }
    }

    fun startCounting() {
        stopCounting()
        timer = Timer()
        timerTask = object : StopwatchManager.Stopwatch() {
            override fun runCode() {
                timeSinceStart += 1
                runOnUiThread(r { time.setText(wrap((timeSinceStart / 60 / 60).toInt()) + ":" + wrap((timeSinceStart / 60 % 60).toInt()) + ":" + wrap((timeSinceStart % 60).toInt())) })
            }
        }
        timer!!.schedule(timerTask, 0, 1000)
    }

    fun startRecording() {
        audioRecorder!!.start(object : AudioRecorder.OnStartListener {
            override fun onStarted() {
                showHelperButtons()
                if (timeSinceStart == 0L)
                    startCounting()
                else
                    resumeCounting()
                runOnUiThread(r { recorder.setRecording() })
            }

            override fun onException(e: Exception) {
                val toast = Toast.makeText(this@CRecorderActivity, "Error with recording", Toast.LENGTH_SHORT)
                toast.setGravity(Gravity.TOP or Gravity.CENTER_HORIZONTAL, 0, 0)
                toast.show()
                audioRecorder = null
            }
        })
    }

    private fun wrap(num: Int): String {
        if (num < 10)
            return "0" + num
        else
            return "" + num
    }

    override fun getIntentToShow(): Intent? {
        pause {
            audioRecorder = null
        }
        try {
            return getPackageManager().getLaunchIntentForPackage("com.lge.voicerecorder").addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        } catch (e: Exception) {
            return null
        }

    }


}