package com.example.facear.Data

class FilterItem (val imageResource : Int, val imageResourceType: imageResourceType, val description : String)

enum class imageResourceType{
    MODEL_RENDERABLE,
    TEXTURE
}