package com.example.AproManager.kotlinCode.models

import android.media.Image
import android.os.Parcel
import android.os.Parcelable

data class Card(
    val cardId: String = "",
    val name: String = "",
    val createdBy: String = "",
    val assignedTo: ArrayList<String> = ArrayList(),
    val labelColor: String = "",
    val dueDate:Long=0,
    val description:String?="",
    val descriptionImageUrl: String?="",
    var commentList: ArrayList<Comments> = ArrayList(),
) : Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readString()!!,
        source.createStringArrayList()!!,
        source.readString()!!,
        source.readLong(),
        source.readString(),
        source.readString(),
        source.createTypedArrayList(Comments.CREATOR)!!,
    )

    override fun describeContents() = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(cardId)
        writeString(name)
        writeString(createdBy)
        writeStringList(assignedTo)
        writeString(labelColor)
        writeLong(dueDate)
        writeString(description)
        writeString(descriptionImageUrl)
        writeTypedList(commentList)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Card> = object : Parcelable.Creator<Card> {
            override fun createFromParcel(source: Parcel): Card = Card(source)
            override fun newArray(size: Int): Array<Card?> = arrayOfNulls(size)
        }
    }
}