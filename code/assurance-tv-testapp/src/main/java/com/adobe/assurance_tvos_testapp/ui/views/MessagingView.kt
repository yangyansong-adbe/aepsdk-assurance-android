///*
// * Copyright 2024 Adobe. All rights reserved.
// * This file is licensed to you under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License. You may obtain a copy
// * of the License at http://www.apache.org/licenses/LICENSE-2.0
// * Unless required by applicable law or agreed to in writing, software distributed under
// * the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR REPRESENTATIONS
// * OF ANY KIND, either express or implied. See the License for the specific language
// * governing permissions and limitations under the License.
// */
//
//package com.adobe.assurance_tvos_testapp.ui.views
//
//import android.annotation.SuppressLint
//import androidx.compose.foundation.layout.Arrangement
//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.Row
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.Button
//import androidx.compose.material.Text
//import androidx.compose.ui.platform.testTag
//import androidx.compose.ui.res.stringResource
//import androidx.compose.ui.text.font.FontWeight
//import androidx.compose.ui.text.style.TextDecoration
//import androidx.compose.ui.unit.dp
//import androidx.compose.ui.unit.sp
//import androidx.lifecycle.SavedStateHandle
//import com.adobe.assurance_tvos_testapp.AssuranceTestAppConstants
//import com.adobe.marketing.mobile.Event
//import com.adobe.marketing.mobile.MobileCore
//import com.adobe.assurance_tvos_testapp.R
//import com.adobe.assurance_tvos_testapp.ui.viewmodel.AssuranceTestAppViewModel
//import android.os.Bundle
//import android.util.Log
//import android.widget.ImageButton
//import androidx.appcompat.app.AppCompatActivity
//import androidx.compose.foundation.layout.Spacer
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.height
//import androidx.compose.foundation.layout.padding
//import androidx.compose.foundation.layout.width
//import androidx.compose.foundation.lazy.LazyRow
//import androidx.compose.foundation.lazy.items
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.livedata.observeAsState
//import androidx.compose.runtime.rememberCoroutineScope
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.platform.ViewCompositionStrategy
//import androidx.compose.ui.text.TextStyle
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.MutableLiveData
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.ViewModelProvider
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import androidx.lifecycle.viewModelScope
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.adobe.marketing.mobile.Messaging
//import com.adobe.marketing.mobile.aepcomposeui.AepUI
//import com.adobe.marketing.mobile.aepcomposeui.SmallImageUI
//import com.adobe.marketing.mobile.aepcomposeui.components.SmallImageCard
//import com.adobe.marketing.mobile.aepcomposeui.style.AepCardStyle
//import com.adobe.marketing.mobile.aepcomposeui.style.AepRowStyle
//import com.adobe.marketing.mobile.aepcomposeui.style.AepTextStyle
//import com.adobe.marketing.mobile.aepcomposeui.style.SmallImageUIStyle
//import com.adobe.marketing.mobile.messaging.ContentCardEventObserver
//import com.adobe.marketing.mobile.messaging.ContentCardMapper
//import com.adobe.marketing.mobile.messaging.ContentCardUIEventListener
//import com.adobe.marketing.mobile.messaging.ContentCardUIProvider
//import com.adobe.marketing.mobile.messaging.Surface
//import kotlinx.coroutines.awaitAll
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.forEach
//import kotlinx.coroutines.launch
//
//
//private lateinit var contentCardUIProvider: ContentCardUIProvider
//
//@Composable
//internal fun MessagingScreen() {
//    Text("December Deals", fontSize = 20.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(16.dp))
//
//    Spacer(modifier = Modifier.height(8.dp))
//
//    Column(modifier = Modifier.testTag(AssuranceTestAppConstants.TEST_TAG_MESSAGING_SCREEN)) {
//        Row(modifier = Modifier.padding(64.dp)) {
//            ContentCardSection()
//        }
//    }
//}
//
//@SuppressLint("CoroutineCreationDuringComposition")
//@Composable
//private fun ContentCardSection() {
//
//    val aepUIListLiveData = MutableLiveData<List<AepUI<*, *>>>()
//    val aepUIList by aepUIListLiveData.observeAsState(emptyList())
//
//
//    // Channel name androidTV: GW Android TV Content Card
//    val surfaces = mutableListOf<Surface>()
//    val surface = Surface("homepage") // Location or path // Surface name should for android TV
//    surfaces.add(surface)
//
//    contentCardUIProvider = ContentCardUIProvider(surface)
//    val coroutineScope = rememberCoroutineScope()
//
//
//    Messaging.updatePropositionsForSurfaces(surfaces)
//
//
//    coroutineScope.launch {
//        contentCardUIProvider.getContentCardUI().collect { aepUiResult ->
//            aepUiResult.onSuccess { aepUi ->
//                aepUIListLiveData.value = aepUi
//
//            }
//            aepUiResult.onFailure { throwable ->
//                Log.d("ContentCardUIProvider", "Error fetching AepUI list: ${throwable}")
//            }
//        }
//    }
//
//    LazyRow {
//        items(aepUIList) { aepUI ->
//
//            when (aepUI) {
//                is SmallImageUI -> {
//                    SmallImageCard(
//                        ui = aepUI,
//                        style = SmallImageUIStyle.Builder().build(),
//                        observer = ContentCardEventObserver(null)
//                    )
//                }
//            }
//        }
//    }
//}
//
//
//
//class AepContentCardViewModel(private val contentCardUIProvider: ContentCardUIProvider) : ViewModel() {
//    // State to hold AepUI list
//    private val _aepUIList = MutableStateFlow<List<AepUI<*, *>>>(emptyList())
//    val aepUIList: StateFlow<List<AepUI<*, *>>> = _aepUIList.asStateFlow()
//
//    init {
//        // Launch a coroutine to fetch the aepUIList from the ContentCardUIProvider
//        // when the ViewModel is created
//        viewModelScope.launch {
//            contentCardUIProvider.getContentCardUI().collect { aepUiResult ->
//                aepUiResult.onSuccess { aepUi ->
//                    _aepUIList.value = aepUi
//                    Log.d("ContentCardUIProvider", "AepUI list: ${aepUi}")
//                }
//                aepUiResult.onFailure { throwable ->
//                    Log.d("ContentCardUIProvider", "Error fetching AepUI list: ${throwable}")
//                }
//            }
//        }
//    }
//}