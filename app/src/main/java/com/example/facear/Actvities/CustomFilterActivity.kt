package com.example.facear.Actvities

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.facear.Data.CustomFaceNode
import com.example.facear.Fragments.FaceArFragment
import com.example.facear.Helper.PhotoHelper
import com.example.facear.R
import com.google.ar.core.ArCoreApk
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.rendering.Renderable
import kotlinx.android.synthetic.main.activity_custom_filter.*
import kotlinx.android.synthetic.main.activity_custom_filter.coordinator
import kotlinx.android.synthetic.main.activity_custom_filter.fap

class CustomFilterActivity : AppCompatActivity() {
    companion object {
        const val MIN_OPENGL_VERSION = 3.0
    }

    lateinit var arFragment: FaceArFragment
    var faceNodeMap = HashMap<AugmentedFace, CustomFaceNode>()

    val photoHelper = PhotoHelper()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkIsSupportedDeviceOrFinish()) {
            return
        }

        setContentView(R.layout.activity_custom_filter)
        arFragment = face_fragment as FaceArFragment

        val leftEyeRes = intent.getIntExtra("leftEyeResId", 0)
        val rightEyeRes = intent.getIntExtra("rightEyeResId", 0)
        val noseRes = intent.getIntExtra("noseResId",0)
        val mustacheRes = intent.getIntExtra("mustacheResId",0)
        val mouthRes = intent.getIntExtra("mouthResId",0)

        btn_face.setOnClickListener {
            faceButtonClicked()
        }
        fap.setOnClickListener {
            photoHelper.takePhoto(
                this.applicationContext,
                arFragment.arSceneView,
                coordinator
            )
        }

        val sceneView = arFragment.arSceneView
        sceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST
        val scene = sceneView.scene

        scene.addOnUpdateListener {
            sceneView.session
                ?.getAllTrackables(AugmentedFace::class.java)?.let {
                    for (f in it) {
                        if (!faceNodeMap.containsKey(f)) {
                            val faceNode =
                                CustomFaceNode(
                                    f,
                                    this,
                                    leftEyeRes,
                                    rightEyeRes,
                                    noseRes,
                                    mouthRes,
                                    mustacheRes
                                )
                            faceNode.setParent(scene)
                            faceNodeMap.put(f, faceNode)
                        }
                    }
                    // Remove any AugmentedFaceNodes associated with an AugmentedFace that stopped tracking.
                    val iter = faceNodeMap.entries.iterator()
                    while (iter.hasNext()) {
                        val entry = iter.next()
                        val face = entry.key
                        if (face.trackingState == TrackingState.STOPPED) {
                            val faceNode = entry.value
                            faceNode.setParent(null)
                            iter.remove()
                        }
                    }
                }
        }
    }

    private fun faceButtonClicked() {
        super.finish()
    }

    private fun checkIsSupportedDeviceOrFinish() : Boolean {
        if (ArCoreApk.getInstance().checkAvailability(this) == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE) {
            Toast.makeText(this, "Augmented Faces requires ARCore", Toast.LENGTH_LONG).show()
            finish()
            return false
        }
        val openGlVersionString =  (getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)
            ?.deviceConfigurationInfo
            ?.glEsVersion

        openGlVersionString?.let { s ->
            if (java.lang.Double.parseDouble(openGlVersionString) < MIN_OPENGL_VERSION) {
                Toast.makeText(this, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                    .show()
                finish()
                return false
            }
        }
        return true
    }

}
