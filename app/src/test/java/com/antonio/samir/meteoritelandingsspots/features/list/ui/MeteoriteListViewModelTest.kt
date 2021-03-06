package com.antonio.samir.meteoritelandingsspots.features.list.ui

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.paging.DataSource
import androidx.paging.PagedList
import com.antonio.samir.meteoritelandingsspots.data.Result
import com.antonio.samir.meteoritelandingsspots.data.repository.MeteoriteRepository
import com.antonio.samir.meteoritelandingsspots.data.repository.model.Meteorite
import com.antonio.samir.meteoritelandingsspots.rule.CoroutineTestRule
import com.antonio.samir.meteoritelandingsspots.service.AddressServiceInterface
import com.antonio.samir.meteoritelandingsspots.util.GPSTrackerInterface
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule

@FlowPreview
@ExperimentalCoroutinesApi
class MeteoriteListViewModelTest {

    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    var coroutinesTestRule = CoroutineTestRule()

    private lateinit var viewModel: MeteoriteListViewModel

    private val mockRepository: MeteoriteRepository = mock()
    private val mockGPSTracker: GPSTrackerInterface = mock()
    private val addressService: AddressServiceInterface = mock()

    @Before
    fun setUp() {

        viewModel = MeteoriteListViewModel(mockRepository, mockGPSTracker, addressService, coroutinesTestRule.testDispatcherProvider)
    }

    @Test
    fun `test getRecoveryAddressStatus success`() {

        whenever(addressService.recoveryAddress()).thenReturn(flow {
            emit(Result.InProgress<Nothing>())
            emit(Result.Success<Nothing>())
        })

        val observer: Observer<Result<Float>> = mock()
        viewModel.getRecoverAddressStatus().observeForever(observer)

        verify(observer).onChanged(Result.InProgress<Float>())
        verify(observer).onChanged(Result.Success<Nothing>())
    }

    @Test
    fun `test getNetworkLoadStatus success`() {

        whenever(mockRepository.loadDatabase()).thenReturn(flow {
            emit(Result.InProgress<Nothing>())
            emit(Result.Success<Nothing>())
        })

        val observer: Observer<Result<Int>> = mock()
        viewModel.getNetworkLoadingStatus().observeForever(observer)

        verify(observer).onChanged(Result.InProgress<Nothing>())
        verify(observer).onChanged(Result.Success<Nothing>())
    }

    @Test
    fun `test getNetworkLoadStatus error`() {

        val value = Result.Error(Exception("test"))

        whenever(mockRepository.loadDatabase()).thenReturn(flow {
            emit(value)
        })

        val observer: Observer<Result<Int>> = mock()
        viewModel.getNetworkLoadingStatus().observeForever(observer)

        verify(observer).onChanged(value)
    }

    @Test
    fun getMeteorites() = runBlockingTest {

        whenever(mockGPSTracker.location).thenReturn(flow {
            emit(null)
        })

        val mockMet: DataSource.Factory<Int, Meteorite> = mock()

        whenever(mockRepository.loadMeteorites(any(), any(), any())).thenReturn(mockMet)

        viewModel.loadMeteorites()

        val observer: Observer<PagedList<Meteorite>> = mock()
        viewModel.getMeteorites().observeForever(observer)

        verify(observer).onChanged(isRequired)

    }

    @Test
    fun updateLocation() = runBlockingTest {

        viewModel.updateLocation()

        verify(mockGPSTracker).startLocationService()

    }

    @Test
    fun `test isAuthorizationRequested required`() {

        val isRequired = true
        whenever(mockGPSTracker.needAuthorization).thenReturn(flow {
            emit(isRequired)
        })

        val observer: Observer<Boolean> = mock()
        viewModel.isAuthorizationRequested().observeForever(observer)

        verify(observer).onChanged(isRequired)

    }

    @Test
    fun `test isAuthorizationRequested not required`() {

        val isRequired = false
        whenever(mockGPSTracker.needAuthorization).thenReturn(flow {
            emit(isRequired)
        })

        val observer: Observer<Boolean> = mock()
        viewModel.isAuthorizationRequested().observeForever(observer)

        verify(observer).onChanged(isRequired)

    }
}