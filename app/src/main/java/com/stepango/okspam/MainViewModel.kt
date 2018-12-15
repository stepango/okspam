package com.stepango.okspam

import com.stepango.okspam.arch.ViewModel
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject


class MainViewModel : ViewModel {

    private val controllerIdSubject = BehaviorSubject.createDefault(emptyList<Int>())

    fun controllerId(): Observable<List<Int>> = controllerIdSubject
}
