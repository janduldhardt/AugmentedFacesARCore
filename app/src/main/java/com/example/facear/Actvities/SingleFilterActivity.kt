package com.example.facear.Actvities

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.facear.*
import com.example.facear.Data.FilterItem
import com.example.facear.Data.MyAdapter
import com.example.facear.Data.imageResourceType
import com.example.facear.Fragments.FaceArFragment
import com.example.facear.Helper.PhotoHelper
import com.google.ar.core.ArCoreApk
import com.google.ar.core.AugmentedFace
import com.google.ar.core.TrackingState
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.Renderable
import com.google.ar.sceneform.rendering.Texture
import com.google.ar.sceneform.ux.AugmentedFaceNode
import kotlinx.android.synthetic.main.activity_single_filter.*
import java.util.*
import kotlin.collections.HashMap


class SingleFilterActivity : AppCompatActivity() {

    companion object {
        const val MIN_OPENGL_VERSION = 3.0
    }

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: MyAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    lateinit var arFragment: FaceArFragment
    private var faceMeshTexture: Texture? = null
    private var glasses: ArrayList<ModelRenderable> = ArrayList()
    private var faceRegionsRenderable: ModelRenderable? = null

    var faceNodeMap = HashMap<AugmentedFace, AugmentedFaceNode>()

    private var isChangeModel: Boolean = false
    private var isChangeTexture: Boolean = false

    val photoHelper = PhotoHelper()


    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            photoHelper.takePhoto(this, arFragment.arSceneView, coordinator)
            return true;
        }

        return super.onKeyDown(keyCode, event)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkIsSupportedDeviceOrFinish()) {
            return
        }

        setContentView(R.layout.activity_single_filter)


        val myDataset = GetAvailableFilter()

        SetRecyclerView(myDataset)


        arFragment = face_fragment as FaceArFragment
        Texture.builder()
            .setSource(this, R.drawable.mustache1)
            .build()
            .thenAccept { texture -> faceMeshTexture = texture }


        ModelRenderable.builder()
            .setSource(this, R.raw.sunglasses)
            .build()
            .thenAccept { modelRenderable ->
                glasses.add(modelRenderable)
                faceRegionsRenderable = modelRenderable
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false
            }

        ModelRenderable.builder()
            .setSource(this, R.raw.yellow_sunglasses)
            .build()
            .thenAccept { modelRenderable ->
                glasses.add(modelRenderable)
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false
            }

        val sceneView = arFragment.arSceneView
        sceneView.cameraStreamRenderPriority = Renderable.RENDER_PRIORITY_FIRST
        val scene = sceneView.scene

        fap.setOnClickListener {
            photoHelper.takePhoto(
                this.applicationContext,
                arFragment.arSceneView,
                coordinator
            )
        }

        scene.addOnUpdateListener {
            if (faceRegionsRenderable != null) {
                sceneView.session
                    ?.getAllTrackables(AugmentedFace::class.java)?.let {
                        for (f in it) {
                            if (!faceNodeMap.containsKey(f)) {
                                val faceNode = AugmentedFaceNode(f)
                                faceNode.setParent(scene)
                                faceNode.faceRegionsRenderable = faceRegionsRenderable
                                faceNodeMap.put(f, faceNode)
                            } else if (isChangeModel) {
                                faceNodeMap.getValue(f).faceRegionsRenderable =
                                    faceRegionsRenderable
                            } else if (isChangeTexture) {
                                faceNodeMap.getValue(f).faceMeshTexture =
                                    faceMeshTexture
                            }
                        }
                        isChangeModel = false
                        isChangeTexture = false

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

    private fun SetRecyclerView(myDataset: ArrayList<FilterItem>) {
        viewManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        viewAdapter = MyAdapter(myDataset)
        viewAdapter.setOnItemClickListener { position ->
            var filterItem = myDataset.get(position)
            if (filterItem.imageResourceType == imageResourceType.TEXTURE)
                changeTexture(filterItem.imageResource)
            if (filterItem.imageResourceType == imageResourceType.MODEL_RENDERABLE)
                changeModelRenderable(filterItem.imageResource)
        }

        recyclerView = findViewById<RecyclerView>(R.id.rv_filter).apply {
            // use this setting to improve performance if you know that changes
            // in content do not change the layout size of the RecyclerView
            setHasFixedSize(true)

            // use a linear layout manager
            layoutManager = viewManager

            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }


    private fun changeModelRenderable(imageResource: Int) {
        ModelRenderable.builder()
            .setSource(this, imageResource)
            .build()
            .thenAccept { modelRenderable ->
                faceRegionsRenderable = modelRenderable
                modelRenderable.isShadowCaster = false
                modelRenderable.isShadowReceiver = false
                isChangeModel = true
            }
    }

    private fun changeTexture(imageResource: Int) {
        Texture.builder()
            .setSource(this, imageResource)
            .build()
            .thenAccept { texture ->
                faceMeshTexture = texture
                isChangeTexture = true
            }
    }

    private fun GetAvailableFilter(): ArrayList<FilterItem> {
        return object : ArrayList<FilterItem>() {
            init {
                add(
                    FilterItem(
                        R.drawable.mustache1,
                        imageResourceType.TEXTURE,
                        "Mustache 1"
                    )
                )
                add(
                    FilterItem(
                        R.raw.fox_face,
                        imageResourceType.MODEL_RENDERABLE,
                        "Mustache 1"
                    )
                )
                add(
                    FilterItem(
                        R.drawable.redlips,
                        imageResourceType.TEXTURE,
                        "Red Lips"
                    )
                )
                add(
                    FilterItem(
                        R.drawable.first_test,
                        imageResourceType.TEXTURE,
                        "first test"
                    )
                )
                add(
                    FilterItem(
                        R.drawable.canonical_face_texture,
                        imageResourceType.TEXTURE,
                        "canonical face texture"
                    )
                )
                add(
                    FilterItem(
                        R.drawable.canonical_mesh_mustache,
                        imageResourceType.TEXTURE,
                        "canonical face texture"
                    )
                )
                add(
                    FilterItem(
                        R.drawable.blackeyelash,
                        imageResourceType.TEXTURE,
                        "black eye lash"
                    )
                )
                add(
                    FilterItem(
                        R.drawable.harrypotterscar,
                        imageResourceType.TEXTURE,
                        "harry potter scar"
                    )
                )
                add(
                    FilterItem(
                        R.drawable.salmon_lips,
                        imageResourceType.TEXTURE,
                        "salmon lips"
                    )
                )
            }
        }

    }

    fun checkIsSupportedDeviceOrFinish(): Boolean {
        if (ArCoreApk.getInstance().checkAvailability(this) == ArCoreApk.Availability.UNSUPPORTED_DEVICE_NOT_CAPABLE) {
            Toast.makeText(this, "Augmented Faces requires ARCore", Toast.LENGTH_LONG).show()
            finish()
            return false
        }
        val openGlVersionString = (getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager)
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
