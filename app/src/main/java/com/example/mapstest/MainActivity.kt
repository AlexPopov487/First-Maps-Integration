package com.example.mapstest

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView


class MainActivity : AppCompatActivity() {

    lateinit var toggle: ActionBarDrawerToggle

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val drawerLayout = findViewById<DrawerLayout>(R.id.main_drawer_layout)
        val drawerNavView = findViewById<NavigationView>(R.id.drawer_nav_view)

        // create top left toggle button to open and close the drawer
        toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.accessibility_drawer_opened,
            R.string.accessibility_drawer_closed
        )
        // add the toggle to drawerLayout
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        // change toggle button to back arrow when drawer is opened
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        drawerNavView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.item_allMarkers -> {
                    Toast.makeText(this, "Show all markers", Toast.LENGTH_SHORT).show()
                }
                R.id.item_about -> {
                    Toast.makeText(this, "Show about", Toast.LENGTH_SHORT).show()
                }
            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true

        return super.onOptionsItemSelected(item)
    }
}