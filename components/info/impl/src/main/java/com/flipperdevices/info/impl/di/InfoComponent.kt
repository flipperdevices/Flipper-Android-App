package com.flipperdevices.info.impl.di

import com.flipperdevices.core.di.AppGraph
import com.flipperdevices.info.impl.fragment.InfoFragment
import com.flipperdevices.info.impl.viewmodel.AlarmViewModel
import com.flipperdevices.info.impl.viewmodel.ConnectViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceInfoViewModel
import com.flipperdevices.info.impl.viewmodel.DeviceStatusViewModel
import com.flipperdevices.info.impl.viewmodel.FirmwareUpdateViewModel
import com.flipperdevices.info.impl.viewmodel.FlipperColorViewModel
import com.flipperdevices.info.impl.viewmodel.FullInfoViewModel
import com.squareup.anvil.annotations.ContributesTo

@ContributesTo(AppGraph::class)
interface InfoComponent {
    fun inject(viewModel: FirmwareUpdateViewModel)
    fun inject(statusViewModel: DeviceStatusViewModel)
    fun inject(viewModel: ConnectViewModel)
    fun inject(viewModel: AlarmViewModel)
    fun inject(viewModel: DeviceInfoViewModel)
    fun inject(viewModel: FlipperColorViewModel)
    fun inject(viewModel: FullInfoViewModel)
    fun inject(fragment: InfoFragment)
}
