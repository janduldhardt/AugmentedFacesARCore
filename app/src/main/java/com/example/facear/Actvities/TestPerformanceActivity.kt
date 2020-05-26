package com.example.facear.Actvities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.facear.Fragments.FaceArFragment
import com.example.facear.R
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.AugmentedFaceNode
import kotlinx.android.synthetic.main.activity_test_performance.*

class TestPerformanceActivity : AppCompatActivity() {
    companion object {
        const val MIN_OPENGL_VERSION = 3.0
    }

    lateinit var arFragment: FaceArFragment
    private var faceMeshTexture: Texture? = null
    var faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()
    private var positive = 0
    private var negative = 0
    private var isTesting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_performance)

        btn_start_test.setOnClickListener {
            isTesting = !isTesting
            positive = 0
            negative = 0
        }

        arFragment = face_fragment as FaceArFragment

        val sceneView = arFragment.arSceneView
        sceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST

        Texture.builder()
            .setSource(this, R.drawable.greenlines)
            .build()
            .thenAccept { texture -> faceMeshTexture = texture }
        val scene = sceneView.scene
        scene.addOnUpdateListener {
            sceneView.session
                ?.getAllTrackables(AugmentedFace::class.java)?.let {
                    if (it.isEmpty() && isTesting) {
                        negative++
                        updateTestString()
                    } else if (isTesting) {
                        positive++
                        updateTestString()
                    }
                    for (f in it) {
                        if (!faceNodeMap.containsKey(f)) {
                            val faceNode = AugmentedFaceNode(f)
                            faceNode.setParent(scene)
                            faceNodeMap.put(f, faceNode)
                            faceNodeMap.getValue(f).faceMeshTexture =
                                faceMeshTexture
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
    }

    private fun updateTestString() {
        var ratio = positive.toDouble() / negative.toDouble()
        var percent = positive.toDouble() / ( positive + negative).toDouble()
        tv_result.text = "positive: $positive\nnegative: $negative\nratio: ${ratio.format(2)}\nPercentage: ${percent.format(2)}"
    }

    fun Double.format(digits: Int) = "%.${digits}f".format(this)
}
