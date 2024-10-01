package com.lyf.study

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class MainActivity : AppCompatActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
  }

  fun onClick(view: View) {
    val pendActName=Class.forName("com.lyf.study."+(view as Button).text)
    startActivity(Intent(this,pendActName))
  }
}