package com.example.facear

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_face_filter_selection.*
import java.util.ArrayList

class FaceFilterSelectionActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: MyAdapter
    private lateinit var viewManager: RecyclerView.LayoutManager

    private var currentResId: Int = 0

    var leftEyeResId: Int = -1
    var rightEyeResId: Int = -1
    var noseResId: Int = -1
    var mustacheResId: Int = -1
    var mouthResId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_face_filter_selection)

        val myDataset = GetAvailableFilter()
        SetRecyclerView(myDataset)
        currentResId = myDataset[0].imageResource;

        setButtons()

    }

    private fun setButtons() {
        btn_left_eye.setOnClickListener {
            it.background = getDrawable(this.currentResId)
            leftEyeResId = this.currentResId;
        }
        btn_right_eye.setOnClickListener {
            it.background = getDrawable(this.currentResId)
            rightEyeResId = this.currentResId;
        }
        btn_nose.setOnClickListener {
            it.background = getDrawable(this.currentResId)
            noseResId = this.currentResId;
        }

        btn_mouth.setOnClickListener {
            it.background = getDrawable(this.currentResId)
            mouthResId = this.currentResId;
        }
        btn_mustache.setOnClickListener {
            it.background = getDrawable(this.currentResId)
            mustacheResId = this.currentResId;
        }



        btn_apply.setOnClickListener {
            val intent = Intent(this, CustomFilterActivity::class.java).apply {
                putExtra("leftEyeResId", leftEyeResId)
                putExtra("rightEyeResId", rightEyeResId)
                putExtra("noseResId", noseResId)
                putExtra("mouthResId", mouthResId)
                putExtra("mustacheResId", mustacheResId)
            }
            startActivity(intent)
        }
    }

    private fun SetRecyclerView(myDataset: ArrayList<FilterItem>) {
        viewManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        viewAdapter = MyAdapter(myDataset)
        viewAdapter.setOnItemClickListener { position ->
            var filterItem = myDataset.get(position)
            if (filterItem.imageResourceType == imageResourceType.TEXTURE)
                currentResId = filterItem.imageResource
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

    private fun GetAvailableFilter(): ArrayList<FilterItem> {
        return object : ArrayList<FilterItem>() {
            init {
                add(FilterItem(R.drawable.star, imageResourceType.TEXTURE, "Mustache 1"))
                add(FilterItem(R.drawable.red_clownnose, imageResourceType.TEXTURE, "Red Lips"))
                add(FilterItem(R.drawable.mustache, imageResourceType.TEXTURE, "first test"))
            }
        }

    }
}
