package com.mustafakocer.movieappfeaturebasedclean.feature.movies

import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.mapper.toDomainList
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.mapper.toEntity
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.mapper.toEntityList
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.mapper.toHomeMovieEntity
import com.mustafakocer.movieappfeaturebasedclean.feature.movies.shared.data.mapper.toHomeMovieEntityList
import com.mustafakocer.movieappfeaturebasedclean.testutil.movieDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class MovieMappersTest {

    @Test
    fun `a complete dto maps to a list item`() {
        val item = movieDto(id = 5, title = "Matrix", overview = "Wake up", releaseDate = "1999-03-31").toDomainList()

        assertEquals(5, item.id)
        assertEquals("Matrix", item.title)
        assertEquals("Wake up", item.overview)
        assertEquals("/poster5.jpg", item.posterUrl)
        assertEquals("1999", item.releaseYear)
        assertEquals(7.5, item.voteAverage, 0.0)
        assertEquals(100, item.voteCount)
    }

    @Test
    fun `missing or blank text is mapped to empty strings for the ui to localize`() {
        val item = movieDto(id = 1, title = null, overview = "   ", releaseDate = null).toDomainList()

        assertEquals("", item.title)
        assertEquals("", item.overview)
        assertEquals("", item.releaseYear)
    }

    @Test
    fun `a short release date does not crash the year extraction`() {
        assertEquals("20", movieDto(releaseDate = "20").toDomainList().releaseYear)
        assertEquals("", movieDto(releaseDate = "").toDomainList().releaseYear)
    }

    @Test
    fun `list entities and home entities map to the same list item as the dto`() {
        val dto = movieDto(id = 3, title = "Heat", releaseDate = "1995-12-15")

        val fromDto = dto.toDomainList()
        val fromListEntity = dto.toEntity("popular", page = 1, position = 0, language = "en-US").toDomainList()
        val fromHomeEntity = dto.toHomeMovieEntity("popular", "en-US").toDomainList()

        assertEquals(fromDto, fromListEntity)
        assertEquals(fromDto, fromHomeEntity)
    }

    @Test
    fun `list entities keep category language page and position`() {
        val entities = listOf(movieDto(1), movieDto(2), movieDto(3))
            .toEntityList(category = "top_rated", page = 3, pageSize = 20, language = "tr-TR")

        assertEquals(listOf(40, 41, 42), entities.map { it.position })
        assertEquals(setOf("top_rated"), entities.map { it.category }.toSet())
        assertEquals(setOf("tr-TR"), entities.map { it.language }.toSet())
        assertEquals(setOf(3), entities.map { it.page }.toSet())
        entities.forEach { assertNotNull(it.cacheMetadata) }
    }

    @Test
    fun `home entities keep category and language`() {
        val entities = listOf(movieDto(1), movieDto(2)).toHomeMovieEntityList("upcoming", "de-DE")

        assertEquals(setOf("upcoming"), entities.map { it.category }.toSet())
        assertEquals(setOf("de-DE"), entities.map { it.language }.toSet())
        assertEquals(listOf(1, 2), entities.map { it.id })
    }
}
