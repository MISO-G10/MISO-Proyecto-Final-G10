package com.example.ccpapplication.pages.register

import com.example.ccpapplication.data.model.UserRegistration
import com.example.ccpapplication.data.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    private lateinit var viewModel: RegisterViewModel
    private val testDispatcher = StandardTestDispatcher()

    private val fakeErrorMessages = ValidationErrorMessages(
        firstNameEmpty = "First name cannot be empty",
        lastNameEmpty = "Last name cannot be empty",
        emailEmpty = "Email cannot be empty",
        emailInvalid = "Invalid email format",
        passwordEmpty = "Password cannot be empty",
        passwordLength = "Password must be at least 8 characters",
        confirmPasswordEmpty = "Confirm password cannot be empty",
        passwordMismatch = "Passwords do not match"
    )

    private val fakeUserRepository = object : UserRepository {
        var shouldSucceed = true
        
        override suspend fun registerUser(user: UserRegistration): Result<Any> {
            return if (shouldSucceed) {
                Result.success(Any())
            } else {
                Result.failure(Exception("Registration failed"))
            }
        }
        
        // Implementar otros métodos requeridos por la interfaz
        override suspend fun loginUser(username: String, password: String): Result<Any> {
            return Result.failure(Exception("Not implemented"))
        }
    }

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RegisterViewModel(
            errorMessages = fakeErrorMessages,
            userRepository = fakeUserRepository
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun whenFirstNameIsEmpty_showError() = runTest {
        viewModel.firstName = ""
        viewModel.lastName = "Pérez"
        viewModel.email = "test@example.com"
        viewModel.password = "password123"
        viewModel.confirmPassword = "password123"

        var onCompleteCalled = false

        viewModel.register { success ->
            onCompleteCalled = true
            assertFalse(success)
        }

        testScheduler.advanceUntilIdle()

        assertTrue(onCompleteCalled)
        assertEquals("First name cannot be empty", viewModel.firstNameError)
    }

    @Test
    fun whenLastNameIsEmpty_showError() = runTest {
        viewModel.firstName = "Juan"
        viewModel.lastName = ""
        viewModel.email = "test@example.com"
        viewModel.password = "password123"
        viewModel.confirmPassword = "password123"

        var onCompleteCalled = false

        viewModel.register { success ->
            onCompleteCalled = true
            assertFalse(success)
        }

        testScheduler.advanceUntilIdle()

        assertTrue(onCompleteCalled)
        assertEquals("Last name cannot be empty", viewModel.lastNameError)
    }

    @Test
    fun whenEmailIsInvalid_showError() = runTest {
        viewModel.firstName = "Juan"
        viewModel.lastName = "Pérez"
        viewModel.email = "invalid-email"
        viewModel.password = "password123"
        viewModel.confirmPassword = "password123"

        var onCompleteCalled = false

        viewModel.register { success ->
            onCompleteCalled = true
            assertFalse(success)
        }

        testScheduler.advanceUntilIdle()

        assertTrue(onCompleteCalled)
        assertEquals("Invalid email format", viewModel.emailError)
    }

    @Test
    fun whenPasswordIsTooShort_showError() = runTest {
        viewModel.firstName = "Juan"
        viewModel.lastName = "Pérez"
        viewModel.email = "test@example.com"
        viewModel.password = "short"
        viewModel.confirmPassword = "short"

        var onCompleteCalled = false

        viewModel.register { success ->
            onCompleteCalled = true
            assertFalse(success)
        }

        testScheduler.advanceUntilIdle()

        assertTrue(onCompleteCalled)
        assertEquals("Password must be at least 8 characters", viewModel.passwordError)
    }

    @Test
    fun whenPasswordsDoNotMatch_showError() = runTest {
        viewModel.firstName = "Juan"
        viewModel.lastName = "Pérez"
        viewModel.email = "test@example.com"
        viewModel.password = "password123"
        viewModel.confirmPassword = "different123"

        var onCompleteCalled = false

        viewModel.register { success ->
            onCompleteCalled = true
            assertFalse(success)
        }

        testScheduler.advanceUntilIdle()

        assertTrue(onCompleteCalled)
        assertEquals("Passwords do not match", viewModel.confirmPasswordError)
    }

    @Test
    fun whenAllFieldsAreValid_registrationSucceeds() = runTest {
        // Configurar el repositorio para que tenga éxito
        fakeUserRepository.shouldSucceed = true

        viewModel.firstName = "Juan"
        viewModel.lastName = "Pérez"
        viewModel.email = "test@example.com"
        viewModel.password = "password123"
        viewModel.confirmPassword = "password123"

        var onCompleteCalled = false

        viewModel.register { success ->
            onCompleteCalled = true
            assertTrue(success)
        }

        testScheduler.advanceUntilIdle()

        assertTrue(onCompleteCalled)
        assertTrue(viewModel.registrationSuccessful)
        assertNull(viewModel.firstNameError)
        assertNull(viewModel.lastNameError)
        assertNull(viewModel.emailError)
        assertNull(viewModel.passwordError)
        assertNull(viewModel.confirmPasswordError)
    }

    @Test
    fun whenRegistrationFails_showsErrorMessage() = runTest {
        // Configurar el repositorio para que falle
        fakeUserRepository.shouldSucceed = false

        viewModel.firstName = "Juan"
        viewModel.lastName = "Pérez"
        viewModel.email = "test@example.com"
        viewModel.password = "password123"
        viewModel.confirmPassword = "password123"

        var onCompleteCalled = false

        viewModel.register { success ->
            onCompleteCalled = true
            assertFalse(success)
        }

        testScheduler.advanceUntilIdle()

        assertTrue(onCompleteCalled)
        assertFalse(viewModel.registrationSuccessful)
        assertEquals("Registration failed", viewModel.errorMessage)
    }

    @Test
    fun resetForm_clearsAllFields() = runTest {
        // Establecer valores en todos los campos
        viewModel.firstName = "Juan"
        viewModel.lastName = "Pérez"
        viewModel.email = "test@example.com"
        viewModel.password = "password123"
        viewModel.confirmPassword = "password123"
        viewModel.firstNameError = "Error"
        viewModel.lastNameError = "Error"
        viewModel.emailError = "Error"
        viewModel.passwordError = "Error"
        viewModel.confirmPasswordError = "Error"
        viewModel.isLoading = true
        viewModel.registrationSuccessful = true
        viewModel.errorMessage = "Error"

        // Resetear el formulario
        viewModel.resetForm()

        // Verificar que todos los campos se hayan limpiado
        assertEquals("", viewModel.firstName)
        assertEquals("", viewModel.lastName)
        assertEquals("", viewModel.email)
        assertEquals("", viewModel.password)
        assertEquals("", viewModel.confirmPassword)
        assertEquals("TENDERO", viewModel.clientType)
        assertNull(viewModel.firstNameError)
        assertNull(viewModel.lastNameError)
        assertNull(viewModel.emailError)
        assertNull(viewModel.passwordError)
        assertNull(viewModel.confirmPasswordError)
        assertFalse(viewModel.isLoading)
        assertFalse(viewModel.registrationSuccessful)
        assertNull(viewModel.errorMessage)
    }
}
