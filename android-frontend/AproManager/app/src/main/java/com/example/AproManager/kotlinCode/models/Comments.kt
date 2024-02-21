package com.example.AproManager.kotlinCode.models

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.common.internal.safeparcel.SafeParcelWriter.writeStringList
import java.util.Date

data class Comments(var comment: String="",
                    var commentBy:String ="",
                    val timeStamp: Date= Date(),
      var likedBy: ArrayList<String> = ArrayList(),
      var dislikedBy:ArrayList<String> = ArrayList()
):Parcelable {
    constructor(source: Parcel) : this(
        source.readString()!!,
        source.readString()!!,
        source.readSerializable() as Date,
        source.createStringArrayList()!!,
        source.createStringArrayList()!!

    )


    override fun describeContents(): Int {
     return 0 ;
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(comment)
        dest.writeString(commentBy)
        dest.writeSerializable(timeStamp)
        dest.writeStringList(likedBy)
        dest.writeStringList(dislikedBy)
    }


    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Comments> = object : Parcelable.Creator<Comments> {
            override fun createFromParcel(source: Parcel): Comments = Comments(source)
            override fun newArray(size: Int): Array<Comments?> = arrayOfNulls(size)
        }
    }

}
