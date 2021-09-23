package com.saeed.marleyspoon.utils.glide

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.internal.ViewUtils.dpToPx
import com.google.android.material.shape.CornerFamily

@BindingAdapter("imageCenterCrop")
fun loadImageCenterCrop(imageView: ImageView, url: String?) {
    Glide.with(imageView.context)
        .load(url)
        .apply(RequestOptions.centerCropTransform())
        .into(imageView)
}

@SuppressLint("RestrictedApi")
@BindingAdapter("roundCorners")
fun roundTopCorners(imageView: ShapeableImageView, radius: Int) {
    imageView.shapeAppearanceModel = imageView.shapeAppearanceModel
        .toBuilder()
        .setAllCorners(CornerFamily.ROUNDED, dpToPx(imageView.context,radius))
        .build()
}

@BindingAdapter("imageResource")
fun setImageResource(imageView: ImageView, drawable: Drawable?) {
    imageView.setImageDrawable(drawable)
}