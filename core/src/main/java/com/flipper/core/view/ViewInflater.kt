package com.flipper.core.view

import android.view.LayoutInflater
import android.view.ViewGroup

typealias ViewInflater<T> = (LayoutInflater, parent: ViewGroup, attachToParent: Boolean) -> T
