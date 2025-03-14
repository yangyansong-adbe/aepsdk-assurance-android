/*
 * Copyright 2022 Adobe. All rights reserved.
 * This file is licensed to you under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software distributed under
 * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
 * OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

package com.adobe.assurance_tvos_testapp

import android.app.Application
import android.util.Log
import com.adobe.assurance_tvos_testapp.AssuranceTestAppConstants.TAG
import com.adobe.marketing.mobile.Assurance
import com.adobe.marketing.mobile.LoggingMode
import com.adobe.marketing.mobile.MobileCore

class AssuranceTestApp : Application() {

    override fun onCreate() {
        super.onCreate()

        MobileCore.setApplication(this)
        MobileCore.setLogLevel(LoggingMode.VERBOSE)

        MobileCore.registerExtensions(
            listOf(
                Assurance.EXTENSION
            )
        ) {
            MobileCore.configureWithAppID("launch-EN8fc4e9cda45e4514b075f0cf5e249742-development")
            MobileCore.lifecycleStart(null)
            Log.d(TAG, "AEP Mobile SDK initialization complete.");
        }
    }

}