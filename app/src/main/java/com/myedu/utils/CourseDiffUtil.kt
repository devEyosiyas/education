package com.myedu.utils

import androidx.recyclerview.widget.DiffUtil
import com.myedu.model.Course

class CourseDiffUtil(
    private val oldAnnouncements: List<Course>?,
    private val newAnnouncements: List<Course>?
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldAnnouncements?.size ?: 0
    }

    override fun getNewListSize(): Int {
        return newAnnouncements?.size ?: 0
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldAnnouncements!![oldItemPosition].id == newAnnouncements!![newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldAnnouncements!![oldItemPosition].id == newAnnouncements!![newItemPosition].id && oldAnnouncements[oldItemPosition].title == newAnnouncements[newItemPosition].title
    }
}