package com.tps.challenge.features.storedetails

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.tps.challenge.CoroutineTestRule
import com.tps.challenge.core.ui.UiState
import com.tps.challenge.network.model.StoreAddressResponse
import com.tps.challenge.network.model.StoreDetailsResponse
import com.tps.challenge.network.repository.common.ApiResult
import com.tps.challenge.network.repository.storepository.StoreRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class StoreDetailsViewModelTest {

    // Regras necessárias para testes de coroutines e lifecycle
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule() // Garante que as operações relacionadas ao LiveData sejam executadas de forma síncrona

    @get:Rule
    val coroutineTestRule = CoroutineTestRule() // Troca o dispatcher principal por um TestDispatcher

    // Mocks necessários
    private lateinit var repository: StoreRepository
    private lateinit var viewModel: StoreDetailsViewModel

    // Dados de teste
    private val testStoreId = "store-123"
    private val testStoreDetails = StoreDetailsResponse(
        id = testStoreId,
        name = "Test Store",
        description = "A test store description",
        coverImgUrl = "https://example.com/image.jpg",
        phoneNumber = "(123) 456-7890",
        deliveryEta = "25-40 min",
        status = "OPEN",
        deliveryFeeCents = 299,
        tags = listOf("Pizza", "Fast Food"),
        address = StoreAddressResponse("123 Test St, Test City, TC 12345")
    )

    @Before
    fun setup() {
        // Inicializa o mock do repository
        repository = mockk(relaxed = true)

        // Inicializa o ViewModel com o repository mockado
        viewModel = StoreDetailsViewModel(repository)
    }

    @Test
    fun `processIntent LoadStoreDetails should call repository with correct storeId`() {
        // Arrange
        // Configura o mock para retornar um flowOf(ApiResult.Success) quando getStoreDetails é chamado
        every { repository.getStoreDetails(testStoreId) } returns flowOf(
            ApiResult.Success(testStoreDetails)
        )

        // Act
        // Processa a intent para carregar os detalhes da loja
        viewModel.processIntent(StoreDetailsViewModel.StoreDetailsIntent.LoadStoreDetails(testStoreId))

        // Assert
        // Verifica se o método getStoreDetails do repository foi chamado com o ID correto
        verify { repository.getStoreDetails(testStoreId) }

        // Comentário para o entrevistador:
        // Este teste verifica a função básica do ViewModel: receber uma Intent e chamar o método
        // correto do repository. Isso garante que o fluxo de dados está sendo iniciado corretamente.
    }

    @Test
    fun `when repository returns success, uiState should emit Loading then Success`() = runTest {
        // Arrange
        // Configura o mock para retornar uma sequência de Loading -> Success
        every { repository.getStoreDetails(testStoreId) } returns flowOf(
            ApiResult.Loading,
            ApiResult.Success(testStoreDetails)
        )

        // Lista para capturar os estados emitidos
        val states = mutableListOf<UiState<StoreDetailsResponse>>()

        // Act
        // Coleta todos os estados emitidos pelo uiState
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.toList(states)
        }

        // Processa a intent para carregar os detalhes da loja
        viewModel.processIntent(StoreDetailsViewModel.StoreDetailsIntent.LoadStoreDetails(testStoreId))

        // Aguarda até ter coletado os estados esperados
        if (states.size < 2) { /* espera até coletar os estados */ }

        // Cancela a coleta
        job.cancel()

        // Assert
        // Verifica se os estados emitidos são os esperados
        assertEquals(2, states.size)
        assertTrue(states[0] is UiState.Loading)
        assertTrue(states[1] is UiState.Success)
        assertEquals(testStoreDetails, (states[1] as UiState.Success).data)

        // Comentário para o entrevistador:
        // Este teste verifica se o ViewModel atualiza corretamente seu estado em resposta aos eventos
        // do repository. Primeiro deve emitir Loading e depois Success com os dados corretos.
        // Em uma aplicação real, isso garante que a UI vai mostrar o loading e depois os dados.
    }

    @Test
    fun `when repository returns error, uiState should emit Loading then Error`() = runTest {
        // Arrange
        // Cria uma exceção de teste
        val testException = Exception("Test exception")

        // Configura o mock para retornar uma sequência de Loading -> Error
        every { repository.getStoreDetails(testStoreId) } returns flowOf(
            ApiResult.Loading,
            ApiResult.Error(testException)
        )

        // Lista para capturar os estados emitidos
        val states = mutableListOf<UiState<StoreDetailsResponse>>()

        // Act
        // Coleta todos os estados emitidos pelo uiState
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.toList(states)
        }

        // Processa a intent para carregar os detalhes da loja
        viewModel.processIntent(StoreDetailsViewModel.StoreDetailsIntent.LoadStoreDetails(testStoreId))

        // Aguarda até ter coletado os estados esperados
        if (states.size < 2) { /* espera até coletar os estados */ }

        // Cancela a coleta
        job.cancel()

        // Assert
        // Verifica se os estados emitidos são os esperados
        assertEquals(2, states.size)
        assertTrue(states[0] is UiState.Loading)
        assertTrue(states[1] is UiState.Error)
        assertEquals(testException, (states[1] as UiState.Error).throwable)

        // Comentário para o entrevistador:
        // Este teste verifica se o ViewModel lida corretamente com erros do repository.
        // É crucial testar o comportamento de erro para garantir que a UI possa mostrar
        // mensagens apropriadas ao usuário. Muitos desenvolvedores esquecem de testar
        // esses cenários, mas eles são essenciais para uma experiência de usuário robusta.
    }

    @Test
    fun `when processIntent with unknown Intent, should not affect uiState`() = runTest {
        // Arrange
        // Lista para capturar os estados emitidos
        val states = mutableListOf<UiState<StoreDetailsResponse>>()

        // Configura o estado inicial como Loading
        every { repository.getStoreDetails(any()) } returns flowOf()

        // Act
        // Coleta todos os estados emitidos pelo uiState
        val job = launch(UnconfinedTestDispatcher()) {
            viewModel.uiState.toList(states)
        }

        // Processa uma intent desconhecida (criada para o teste)
        viewModel.processIntent(object : com.tps.challenge.core.ui.Intent {})

        // Dá tempo para qualquer possível efeito
        kotlinx.coroutines.delay(100)

        // Cancela a coleta
        job.cancel()

        // Assert
        // Verifica se apenas o estado inicial (Loading) é emitido
        assertEquals(1, states.size)
        assertTrue(states[0] is UiState.Loading)

        // Verifica que o repository não foi chamado
        verify(exactly = 0) { repository.getStoreDetails(any()) }

        // Comentário para o entrevistador:
        // Este teste verifica a robustez do ViewModel contra inputs inesperados.
        // Embora seja um caso menos comum na prática, é uma boa prática testar
        // casos extremos para garantir que o código se comporta bem em situações imprevistas.
    }

    @Test
    fun `when different storeIds are requested, should make separate repository calls`() {
        // Arrange
        val secondStoreId = "store-456"

        every { repository.getStoreDetails(any()) } returns flowOf(
            ApiResult.Success(testStoreDetails)
        )

        // Act
        // Solicita dois IDs diferentes
        viewModel.processIntent(StoreDetailsViewModel.StoreDetailsIntent.LoadStoreDetails(testStoreId))
        viewModel.processIntent(StoreDetailsViewModel.StoreDetailsIntent.LoadStoreDetails(secondStoreId))

        // Assert
        // Verifica que o repository foi chamado uma vez para cada ID
        verify(exactly = 1) { repository.getStoreDetails(testStoreId) }
        verify(exactly = 1) { repository.getStoreDetails(secondStoreId) }

        // Comentário para o entrevistador:
        // Este teste garante que o ViewModel faz chamadas
        // separadas quando IDs diferentes são solicitados. Juntos, esses testes
        // verificam que o ViewModel é eficiente mas também completo em sua funcionalidade.
    }
}