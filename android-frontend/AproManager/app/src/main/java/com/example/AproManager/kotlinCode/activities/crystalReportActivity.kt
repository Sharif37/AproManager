package com.example.AproManager.kotlinCode.activities

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.AproManager.kotlinCode.models.Board
import com.example.AproManager.kotlinCode.models.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.File
import java.io.FileOutputStream
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.layout.Document
import com.itextpdf.layout.element.Paragraph
import java.util.Date

class crystalReportActivity {

    private val databaseReference =
        FirebaseDatabase.getInstance("https://apromanager-972c5-default-rtdb.asia-southeast1.firebasedatabase.app/").reference

    fun getDailyReportData(activity: Activity) {
        // Retrieve users
        databaseReference.child("users").addListenerForSingleValueEvent(object :
            ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val userList = ArrayList<User>()
                for (userSnapshot in snapshot.children) {
                    val user = userSnapshot.getValue(User::class.java)
                    if (user != null) {
                        userList.add(user)
                    }
                }

                // Retrieve boards
                getBoardData(activity, userList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(activity, "Failed to retrieve user data", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getBoardData(activity: Activity, userList: ArrayList<User>) {
        databaseReference.child("boards")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val boardList = ArrayList<Board>()
                    for (boardSnapshot in snapshot.children) {
                        val board = boardSnapshot.getValue(Board::class.java)
                        if (board != null) {
                            boardList.add(board)
                        }
                    }

                    // Now pass userList and boardList to generate PDF
                    generatePDF(activity, userList, boardList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(activity, "Failed to retrieve board data", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun generatePDF(context: Context, userList: ArrayList<User>, boardList: ArrayList<Board>) {
        val fileName = "Daily_Report_${System.currentTimeMillis()}.pdf"
        val filePath = File(context.getExternalFilesDir(null), fileName)

        // Set up the PdfWriter and PdfDocument
        val pdfWriter = PdfWriter(FileOutputStream(filePath))
        val pdfDocument = PdfDocument(pdfWriter)
        val document = Document(pdfDocument)

        try {
            // Title
            document.add(Paragraph("Daily User and Board Statistics").setBold().setFontSize(18F))
            document.add(Paragraph("Date: ${Date()}"))
            document.add(Paragraph("\n"))

            // User Stats
            document.add(Paragraph("User Statistics:").setBold())
            for (user in userList) {
                document.add(Paragraph("Name: ${user.name}, Email: ${user.email}"))
            }

            document.add(Paragraph("\n")) // Add spacing

            // Board Stats
            document.add(Paragraph("Board Statistics:").setBold())
            for (board in boardList) {
                document.add(Paragraph("Board: ${board.name}, Created by: ${board.createdBy}, Total Tasks: ${board.taskList.size}"))
            }

            // Close the document
            document.close()

            // Open the generated PDF
            openPDF(context, filePath)

        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error generating PDF", Toast.LENGTH_SHORT).show()
        } finally {
            // Ensure that resources are released properly
            pdfDocument.close()
            pdfWriter.close()
        }
    }

    private fun openPDF(context: Context, file: File) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", file)
        intent.setDataAndType(uri, "application/pdf")
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)

        try {
            context.startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            Toast.makeText(context, "No PDF viewer installed", Toast.LENGTH_SHORT).show()
        }
    }
}
