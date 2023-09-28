package com.pg.testgraph.features

import com.pg.testgraph.core.data.network.ioDispatcherName
import com.pg.testgraph.features.feature_graph.presentation.GraphViewModel
import com.pg.testgraph.features.feature_welcome.presentation.WelcomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module

val appModule = module {
    viewModel { WelcomeViewModel(get()) }
    viewModel { GraphViewModel(get(), get(named(ioDispatcherName))) }
}