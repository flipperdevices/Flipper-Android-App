package com.flipperdevices.info.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.info.impl.fragment.InfoFragment
import com.flipperdevices.info.impl.viewmodel.AlarmViewModel
import com.flipperdevices.info.impl.viewmodel.ConnectViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceInfoViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceViewModel
import com.flipperdevices.info.impl.viewmodel.FirmwareUpdateViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface InfoComponent {
    fun inject(viewModel: FirmwareUpdateViewModel)
    fun inject(viewModel: DeviceViewModel)
    fun inject(viewModel: ConnectViewModel)
    fun inject(viewModel: AlarmViewModel)
    fun inject(viewModel: DeviceInfoViewModel)
    fun inject(fragment: InfoFragment)
}
