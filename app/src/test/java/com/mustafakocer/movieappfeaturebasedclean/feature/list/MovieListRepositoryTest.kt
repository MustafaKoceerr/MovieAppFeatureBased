package com.mustafakocer.movieappfeaturebasedclean.feature.list

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.paging.testing.asSnapshot
import app.cash.turbine.test
import com.mustafakocer.core_preferences.models.LanguagePreference
import com.mustafakocer.core_preferences.repository.LanguageRepository
import com.mustafakocer.movieappfeaturebasedclean.feature.list.data.local.dao.MovieListDao
import com.mustafakocer.movieappfeaturebasedclean.feature.list.data.mediator.MovieListRemoteMediator
import com.mustafakocer.movieappfeaturebasedclean.feature.list.data.mediator.MovieListRemoteMediatorFactory
import com.mustafakocer.movieappfeaturebasedclean.feature.list.data.repository.MovieListRepository
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.local.entity.MovieListEntity
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.mapper.toEntity
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.domain.model.MovieCategory
import com.mustafakocer.movieappfeaturebasedclean.testutil.MainDispatcherRule
import com.mustafakocer.movieappfeaturebasedclean.testutil.movieDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito

@OptIn(ExperimentalPagingApi::class)
class MovieListRepositoryTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val language = MutableStateFlow(LanguagePreference.ENGLISH)

    /** (category endpoint, language) pairs the Room DAO was asked for. */
    private val daoQueries = mutableListOf<Pair<String, String>>()

    /** (category, language) pairs the remote mediator was created for. */
    private val mediatorsCreated = mutableListOf<Pair<MovieCategory, String>>()

    private fun entitiesFor(category: String, language: String): List<MovieListEntity> = listOf(
        movieDto(1, "First ($language)").toEntity(category, page = 1, position = 0, language = language),
        movieDto(2, "Second ($language)").toEntity(category, page = 1, position = 1, language = language),
    )

    private fun pagingSourceOf(entities: List<MovieListEntity>) =
        object : PagingSource<Int, MovieListEntity>() {
            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, MovieListEntity> =
                LoadResult.Page(data = entities, prevKey = null, nextKey = null)

            override fun getRefreshKey(state: PagingState<Int, MovieListEntity>): Int? = null
        }

    private val dao: MovieListDao = Mockito.mock(MovieListDao::class.java) { invocation ->
        if (invocation.method.name == "getMoviesForCategory") {
            val category = invocation.getArgument<String>(0)
            val language = invocation.getArgument<String>(1)
            daoQueries += category to language
            pagingSourceOf(entitiesFor(category, language))
        } else {
            null
        }
    }

    /** The cache is the source of truth in these tests, so the mediator never has to load. */
    private val mediator: MovieListRemoteMediator =
        Mockito.mock(MovieListRemoteMediator::class.java) { invocation ->
            when (invocation.method.name) {
                "initialize" -> RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH
                "load" -> RemoteMediator.MediatorResult.Success(endOfPaginationReached = true)
                else -> null
            }
        }

    private val mediatorFactory: MovieListRemoteMediatorFactory =
        Mockito.mock(MovieListRemoteMediatorFactory::class.java) { invocation ->
            if (invocation.method.name == "create") {
                mediatorsCreated += invocation.getArgument<MovieCategory>(0) to invocation.getArgument<String>(1)
                mediator
            } else {
                null
            }
        }

    private val languageRepository: LanguageRepository =
        Mockito.mock(LanguageRepository::class.java) { invocation ->
            if (invocation.method.name == "getLanguageFlow") language else null
        }

    private val repository = MovieListRepository(mediatorFactory, dao, languageRepository)

    @Test
    fun `movies come from the local cache mapped to domain items`() = runTest {
        val items = repository.getMoviesByCategory(MovieCategory.POPULAR).asSnapshot()

        assertEquals(listOf(1, 2), items.map { it.id })
        assertEquals(listOf("First (en-US)", "Second (en-US)"), items.map { it.title })
        assertEquals(listOf("2024", "2024"), items.map { it.releaseYear })
    }

    @Test
    fun `the cache and mediator are keyed by category and language`() = runTest {
        repository.getMoviesByCategory(MovieCategory.TOP_RATED).asSnapshot()

        assertEquals(listOf("top_rated" to "en-US"), daoQueries.distinct())
        assertEquals(listOf(MovieCategory.TOP_RATED to "en-US"), mediatorsCreated.distinct())
    }

    @Test
    fun `a different language reads a different cache`() = runTest {
        language.value = LanguagePreference.TURKISH

        val items = repository.getMoviesByCategory(MovieCategory.POPULAR).asSnapshot()

        assertEquals(listOf("First (tr-TR)", "Second (tr-TR)"), items.map { it.title })
        assertEquals(listOf("popular" to "tr-TR"), daoQueries.distinct())
        assertEquals(listOf(MovieCategory.POPULAR to "tr-TR"), mediatorsCreated.distinct())
    }

    @Test
    fun `a new pager is created when the language changes`() = runTest {
        repository.getMoviesByCategory(MovieCategory.POPULAR).test {
            awaitItem()

            language.value = LanguagePreference.FRENCH

            awaitItem()
            cancelAndIgnoreRemainingEvents()
        }
    }
}
