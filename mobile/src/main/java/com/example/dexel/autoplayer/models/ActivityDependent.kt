package com.example.dexel.autoplayer.models

import android.app.Activity
import com.example.dexel.autoplayer.MainActivity
import javax.inject.Inject

class ActivityDependent @Inject constructor(val activity: Activity)
