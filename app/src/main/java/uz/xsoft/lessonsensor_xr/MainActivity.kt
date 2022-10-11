package uz.xsoft.lessonsensor_xr

import android.app.AlertDialog
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.forEachIndexed
import kotlin.math.abs

class MainActivity : AppCompatActivity(), SensorEventListener {
    private val sensorManager: SensorManager by lazy { getSystemService(Context.SENSOR_SERVICE) as SensorManager }
    private val sensor: Sensor by lazy { sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) }
    private lateinit var ballImg: ImageView
    private val repository: Repository = RepositoryImpl()
    private val map = repository.getMapByLevel(1)
    private var cWidth = 0
    private var cHeight = 0
    private var ballPI = -1
    private var ballPJ = -1



    private var _height: Int? = null
    private var _width: Int? = null
    private lateinit var container: RelativeLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        dialogOver = AlertDialog.Builder(this).setTitle("Game Over")
            .setNegativeButton("Quit"){p0, p1 ->
                finish()
            }
            .setPositiveButton("Retry"){p0,p1 ->
                recreate()
            }
            .create()


        dialogFinish = AlertDialog.Builder(this).setTitle("Finish")
            .setNegativeButton("Quit"){p0, p1 ->
                finish()
            }
            .setPositiveButton("Retry"){p0,_ ->
                recreate()
            }
            .create()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);

        container = findViewById(R.id.container)


        container.post {
            cWidth = container.width
            cHeight = container.height
            loadView()
        }


    }

    override fun onResume() {
        super.onResume()

        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_GAME)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        p0?.let {
            val y = it.values[0]*2
            val x = it.values[1]*2
            val z = it.values[2]

            if (_height != null) {
                var ballNewX = ballImg.x
                var ballNewY = ballImg.y

                if (x > 0) {
                    var wallX = 10000f

                    if (map[ballPI][ballPJ + 1] == 1) {
                        wallX = _width!! * (ballPJ + 1) * 1f

                    }

                    ballNewX = if (ballImg.x < (wallX - 5 - _width!!)) {
                        ballImg.x + x
                    } else {
                        ballImg.x
                    }

                    val newX = _width!! * (ballPJ + 1) * 1f

                    if ((y > 0 && map[ballPI + 1][ballPJ] == 1) || (y < 0 && map[ballPI - 1][ballPJ] == 1)) {
                        if (ballImg.x > (newX - 5)) {
                            ballPJ += 1
                        }
                    } else {
                        if (ballImg.x > (newX + 5 - _width!!)) {
                            ballPJ += 1
                        }
                    }

                } else {
                    var wallX = 0f

                    if (map[ballPI][ballPJ - 1] == 1) {
                        wallX = _width!! * (ballPJ - 1) * 1f
                    }

                    ballNewX = if (ballImg.x > (wallX + 5 + _width!!)) {
                        ballImg.x + x
                    } else {
                        ballImg.x
                    }

                    val newX = _width!! * (ballPJ - 1) * 1f

                    if ((y > 0 && map[ballPI + 1][ballPJ] == 1) || (y < 0 && map[ballPI - 1][ballPJ] == 1)) {
                        if (ballImg.x < (newX + 5)) {
                            ballPJ -= 1
                        }
                    } else {
                        if (ballImg.x < (newX - 5 + _width!!)) {
                            ballPJ -= 1
                        }
                    }
                }

                if (y > 0) {
                    var wallY = 10000f

                    if (map[ballPI + 1][ballPJ] == 1) {
                        wallY = _height!! * (ballPI + 1) * 1f
                    }

                    ballNewY = if (ballImg.y < (wallY - 5 - _height!!)) {
                        ballImg.y + y
                    } else {
                        ballImg.y
                    }

                    val newY = _height!! * (ballPI + 1) * 1f

                    if ((x > 0 && map[ballPI][ballPJ + 1] == 1) || (x < 0 && map[ballPI][ballPJ - 1] == 1)) {
                        if (ballImg.y > (newY - 5)) {
                            ballPI += 1
                        }
                    } else {
                        if (ballImg.y > (newY + 5 - _height!!)) {
                            ballPI += 1
                        }
                    }

                } else {
                    var wallY = 0f

                    if (map[ballPI - 1][ballPJ] == 1) {
                        wallY = _height!! * (ballPI - 1) * 1f
                    }

                    ballNewY = if (ballImg.y > (wallY + 5 + _height!!)) {
                        ballImg.y + y
                    } else {
                        ballImg.y
                    }

                    val newY = _height!! * (ballPI - 1) * 1f

                    if ((x > 0 && map[ballPI][ballPJ + 1] == 1) || (x < 0 && map[ballPI][ballPJ - 1] == 1)) {
                        if (ballImg.y < (newY + 5)) {
                            ballPI -= 1
                        }
                    } else {
                        if (ballImg.y < (newY - 5 + _height!!)) {
                            ballPI -= 1
                        }
                    }
                }

                Log.d("WWW", "wallI: $ballPI    wallJ: $ballPJ")


                var delta = 0f
                var ballWidth = 0f
                var trapWith = 0f

                if (map[ballPI][ballPJ] == 2) {
                    container.forEachIndexed{index, view ->
                        if (view.tag == "trap")    {
                            trapWith = (container.width - view.x + view.width/2)
                        }
                        if(view.tag == "ball"){
                            ballWidth = (container.width - view.x + view.width/2)
                        }
                    }

                    delta = abs(trapWith-ballWidth)


                    Log.d("TRAP", "trap width = $trapWith   ball width = $ballWidth   delta = $delta")

                    if(delta < 50){
                        gameOver()
                    }
                }

                var finishWith = 0f

                if (map[ballPI][ballPJ] == 3) {
                    container.forEachIndexed{index, view ->
                        if (view.tag == "finish")    {
                            finishWith = (container.width - view.x + view.width/2)
                        }
                        if(view.tag == "ball"){
                            ballWidth = (container.width - view.x + view.width/2)
                        }
                    }

                    delta = abs(finishWith-ballWidth)

                    if(delta < 50){
                        win()
                    }
                }

                ballImg.x = ballNewX
                ballImg.y = ballNewY

                Log.d("TTT", "x is ${it.values[0]}")
                Log.d("TTT", "y is ${it.values[1]}")
                Log.d("TTT", "z is ${it.values[2]}")
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

    }

    private fun loadView() {
        _height = cHeight / 20
        _width = cWidth / 30
        var bI = 0
        var bJ = 0
        map.forEachIndexed { i, rows ->
            rows.forEachIndexed { j, value ->
                if (value == 4) {
                    bI = i
                    bJ = j
                }

                if (value == 1) {
                    val img = ImageView(this)
                    img.scaleType = ImageView.ScaleType.FIT_XY
                    container.addView(img)
                    img.layoutParams.apply {
                        width = _width!!
                        height = _height!!
                    }
                    img.x = _width!! * j * 1f
                    img.y = _height!! * i * 1f

                    img.setImageResource(R.drawable.wall)
                }

                if (value == 3){
                    addFinish(i,j, _width!!, _height!!)
                }

                if (value == 2){
                    addTrap(i,j, _width!!, _height!!)
                }
            }
        }

        addBall(bI, bJ, _width!!, _height!!)
    }

    private fun addBall(i: Int, j: Int, _width: Int, _height: Int) {
        ballPI = i
        ballPJ = j
        ballImg = ImageView(this)
        ballImg.setImageResource(R.drawable.metallball)
        ballImg.scaleType = ImageView.ScaleType.FIT_XY
        container.addView(ballImg)
        ballImg.layoutParams.apply {
            height = _height
            width = _width
        }

        ballImg.tag = "ball"
        ballImg.x = _width * j * 1f
        ballImg.y = _height * i * 1f

    }

    private fun addTrap(i: Int, j: Int, _width: Int, _height: Int){
        val trap = ImageView(this)
        trap.setImageResource(R.drawable.trap)
        trap.tag = "trap"
        container.addView(trap)
        trap.layoutParams.apply {
            height = _height
            width = _width
        }

        trap.scaleType = ImageView.ScaleType.FIT_XY
        trap.x = _width * j * 1f
        trap.y = _height * i * 1f
    }

    private fun addFinish(i: Int, j: Int, _width: Int, _height: Int){
        val finish = ImageView(this)
        finish.setImageResource(R.drawable.hole)
        finish.tag = "finish"
        container.addView(finish)
        finish.layoutParams.apply {
            height = _height
            width = _width
        }

        finish.scaleType = ImageView.ScaleType.FIT_XY
        finish.x = _width * j * 1f
        finish.y = _height * i * 1f
    }
    var dialogOver: AlertDialog? = null
    var dialogFinish: AlertDialog? = null
    private fun gameOver(){



        dialogOver?.show()
    }

    private fun win(){


        dialogFinish?.show()
    }
}