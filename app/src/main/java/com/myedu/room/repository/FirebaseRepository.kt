package com.myedu.room.repository

import android.net.Uri
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.myedu.room.repository.FirebaseRepository.Singleton.downloadedUri
import com.myedu.room.repository.FirebaseRepository.Singleton.storageReference
import java.util.*

class FirebaseRepository {

    object Singleton{
        val storage_URL : String = "gs://education-cd1c9.appspot.com"
        val storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(storage_URL)

        //Current image link
       var downloadedUri : Uri? = null

    }

    //Method to store image in firebase storage
    fun uploadImage(file : Uri, callback: () -> Unit){

        if (file != null) {
            val fileName = UUID.randomUUID().toString() + ".jpg"
            val ref = storageReference.child(fileName)
            val uploadTask = ref.putFile(file)

            uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful){
                    task.exception?.let { throw it }
                }

                return@Continuation ref.downloadUrl
            }).addOnCompleteListener{ task ->
                if (task.isSuccessful){

                    //Retrieves the image
                    downloadedUri = task.result
                    callback()
                }
            }
        }
    }
}