package com.example.ccpapplication.pages.clients

import com.example.ccpapplication.data.model.AddVisitResponse
import com.example.ccpapplication.data.model.UpdateVisitResponse
import com.example.ccpapplication.data.model.Visit
import com.example.ccpapplication.data.model.VisitAdd
import com.example.ccpapplication.data.repository.VisitRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ScheduleVisitViewModelTest {

    private lateinit var viewModel: ScheduleVisitViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val fakeErrorMessages = ValidationErrorMessages(
        commentsEmpty = "Notes cannot be empty"
    )

    private val fakeVisitRepository = object : VisitRepository {
        override suspend fun addVisit(visit: VisitAdd): Result<AddVisitResponse> {
            return Result.success(
                AddVisitResponse(id = "12345", createdAt = "2025-10-01T00:00:00Z")
            )
        }

        override suspend fun updateVisit(visit: Visit): Result<UpdateVisitResponse> {
            return Result.success(
                UpdateVisitResponse(id = "12345", message="visita actualizada", updatedAt = "2025-10-01T00:00:00Z")
            )
        }
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = ScheduleVisitViewModel(
            errorMessages = fakeErrorMessages,
            visitRepository = fakeVisitRepository,
            dispatcher = testDispatcher
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenCommentsAreEmpty_showError() = runTest {
        viewModel.comments = ""  // Comentarios vacíos

        var onCompleteCalled = false

        viewModel.addVisit { success ->
            onCompleteCalled = true
            assertFalse(success) // Debe fallar
        }

        testScheduler.advanceUntilIdle()

        assertTrue(onCompleteCalled)
        assertEquals("Notes cannot be empty", viewModel.commentsError)
    }

    @Test
    fun whenCommentsAreFilled_noError() = runTest {
        viewModel.comments = "Visit for client"

        var onCompleteCalled = false

        viewModel.addVisit { success ->
            onCompleteCalled = true
            assertTrue(success) // Debe pasar
        }

        testScheduler.advanceUntilIdle()

        assertTrue(onCompleteCalled)
        assertNull(viewModel.commentsError)
    }

    @Test
    fun whenSubmittingVisit_loadingStateChanges() = runTest {
        viewModel.comments = "Another visit"

        assertFalse(viewModel.isLoading) // Antes de enviar, no debe estar cargando

        var onCompleteCalled = false

        viewModel.addVisit { success ->
            onCompleteCalled = true
            assertTrue(success)
        }

        // Hacemos avanzar el dispatcher para procesar el launch
        testScheduler.advanceUntilIdle()

        // Ahora sí, evaluamos
        assertFalse(viewModel.isLoading) // Ya no debería estar cargando
        assertTrue(onCompleteCalled)
    }
}

