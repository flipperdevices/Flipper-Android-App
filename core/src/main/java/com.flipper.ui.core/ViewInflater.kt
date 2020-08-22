package com.flipper.ui.core

import android.view.LayoutInflater
import android.view.ViewGroup

typealias ViewInflater<T> = (LayoutInflater, ViewGroup, Boolean) -> T
