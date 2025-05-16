package com.example.ccpapplication.pages.register

import com.example.ccpapplication.data.model.UserRegistration
import com.example.ccpapplication.data.model.UserRegistrationResponse
import com.example.ccpapplication.data.model.UserLogin
import com.example.ccpapplication.data.model.AuthResponse
import com.example.ccpapplication.data.model.User
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
        firstNameEmpty = "First name cannot be empty", // register_validation_error_firstname_empty
        lastNameEmpty = "Last name cannot be empty", // register_validation_error_lastname_empty
        emailEmpty = "Email cannot be empty", // register_validation_error_email_empty
        emailInvalid = "The email address is not valid", // register_validation_error_email_invalid
        passwordEmpty = "Password cannot be empty", // register_validation_error_password_empty
        passwordLength = "The password must be at least 8 characters long", // register_validation_error_pwd_length
        confirmPasswordEmpty = "Please confirm your password", // register_validation_error_passwordconf_empty
        passwordMismatch = "Passwords do not match" // register_validation_error_pwd_mismatch
    )

    private val fakeErrorMessagesEs = ValidationErrorMessages(
        firstNameEmpty = "El nombre no puede estar vacío", // register_validation_error_firstname_empty
        lastNameEmpty = "El apellido no puede estar vacío", // register_validation_error_lastname_empty
        emailEmpty = "El correo electrónico no puede estar vacío", // register_validation_error_email_empty
        emailInvalid = "El correo electrónico no es válido", // register_validation_error_email_invalid
        passwordEmpty = "La contraseña no puede estar vacía", // register_validation_error_password_empty
        passwordLength = "La contraseña debe tener al menos 8 caracteres", // register_validation_error_pwd_length
        confirmPasswordEmpty = "Por favor confirme su contraseña", // register_validation_error_passwordconf_empty
        passwordMismatch = "Las contraseñas no coinciden" // register_validation_error_pwd_mismatch
    )

    private val fakeUserRepository = object : UserRepository {
        var shouldSucceed = true
        
        override suspend fun registerUser(user: UserRegistration): Result<UserRegistrationResponse> {
            return if (shouldSucceed) {
                Result.success(UserRegistrationResponse(id = "test-id", createdAt = "2025-01-01T00:00:00Z"))
            } else {
                Result.failure(Exception("Registration failed"))
            }
        }
        
        override suspend fun login(userLogin: UserLogin): Result<AuthResponse> {
            return Result.success(
                AuthResponse(
                    token = "fake-token",
                    id = "test-id",
                    expireAt = "2025-01-01T00:00:00Z"
                )
            )
        }
        
        override suspend fun getUser(): Result<User> {
            return Result.success(
                User(
                    id = "test-id",
                    username = "test@example.com",
                    password = "Nombre Test",
                    name = "Apellido Test",
                    rol = "TENDERO",
                    direccion = "Calle 1",
                    telefono = "1234567890"
                )
            )
        }
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        
        // Configurar el ViewModel con los mensajes en inglés para las pruebas
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
        assertEquals(fakeErrorMessages.firstNameEmpty, viewModel.firstNameError)
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
        assertEquals(fakeErrorMessages.lastNameEmpty, viewModel.lastNameError)
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
        assertEquals(fakeErrorMessages.emailInvalid, viewModel.emailError)
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
        assertEquals(fakeErrorMessages.passwordLength, viewModel.passwordError)
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
        assertEquals(fakeErrorMessages.passwordMismatch, viewModel.confirmPasswordError)
    }

    @Test
    fun whenRegistrationSucceeds_setsSuccessFlag() = runTest {
        viewModel.firstName = "Juan"
        viewModel.lastName = "Pérez"
        viewModel.email = "test@example.com"
        viewModel.password = "password123"
        viewModel.confirmPassword = "password123"
        
        fakeUserRepository.shouldSucceed = true

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
        viewModel.firstName = "Juan"
        viewModel.lastName = "Pérez"
        viewModel.email = "test@example.com"
        viewModel.password = "password123"
        viewModel.confirmPassword = "password123"
        
        fakeUserRepository.shouldSucceed = false

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
        viewModel.registrationSuccessful = true
        viewModel.errorMessage = "Error"

        viewModel.resetForm()

        assertEquals("", viewModel.firstName)
        assertEquals("", viewModel.lastName)
        assertEquals("", viewModel.email)
        assertEquals("", viewModel.password)
        assertEquals("", viewModel.confirmPassword)
        assertNull(viewModel.firstNameError)
        assertNull(viewModel.lastNameError)
        assertNull(viewModel.emailError)
        assertNull(viewModel.passwordError)
        assertNull(viewModel.confirmPasswordError)
        assertFalse(viewModel.registrationSuccessful)
        assertNull(viewModel.errorMessage)
    }
}
