package core.di

import chat.view.ChatViewModel
import org.koin.compose.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import steps.domain.view.StepViewModel

val sharedModule = module {
    viewModelOf(::ChatViewModel)
    viewModelOf(::StepViewModel)
}