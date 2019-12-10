package com.example.kotlinassignment

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // Get the text fragment instance
        val assignmentFragment = AssignmentFragment()

        // Get the support fragment manager instance
        val manager = supportFragmentManager
        // Begin the fragment transition using support fragment manager
        val transaction = manager.beginTransaction()
        // Replace the fragment on container
        transaction.replace(R.id.title_fragment, assignmentFragment)
        // Finishing the transition
        transaction.commit()
    }
}
