package com.dicoding.membership.core.utils.datamapper

import com.dicoding.core.data.source.local.entity.FavoriteStoryEntity
import com.dicoding.core.data.source.remote.response.test.ListStoryItem
import com.dicoding.core.data.source.remote.response.test.Story
import com.dicoding.membership.core.domain.story.tester.model.StoryDomainTester

object StoryDataMapper {
    fun mapListStoryToDomain(listStory: List<ListStoryItem>): List<StoryDomainTester> {
        return listStory.map {
            StoryDomainTester(
                id = it.id,
                name = it.name,
                description = it.description,
                photoUrl = it.photoUrl,
                createdAt = it.createdAt,
                lat = it.lat,
                lon = it.lon
            )
        }
    }

    fun mapResponseToDomain(response: ListStoryItem): StoryDomainTester {
        return StoryDomainTester(
            id = response.id,
            name = response.name,
            description = response.description,
            photoUrl = response.photoUrl,
            createdAt = response.createdAt,
            lat = response.lat,
            lon = response.lon
        )
    }

    fun mapDetailStoryToDomain(story: Story): StoryDomainTester {
        return StoryDomainTester(
            id = story.id ?: "",
            name = story.name ?: "",
            description = story.description ?: "",
            photoUrl = story.photoUrl ?: "",
            createdAt = story.createdAt ?: "",
            lat = story.lat ?: 0.0,
            lon = story.lon ?: 0.0
        )
    }

    fun mapEntitiesToDomain(entities: List<FavoriteStoryEntity>): List<StoryDomainTester> {
        return entities.map {
            StoryDomainTester(
                id = it.id,
                name = it.name,
                description = it.description,
                photoUrl = it.photoUrl,
                createdAt = it.createdAt,
                lat = it.lat,
                lon = it.lon
            )
        }
    }

    fun mapEntityToDomain(entity: FavoriteStoryEntity): StoryDomainTester {
        return StoryDomainTester(
            id = entity.id,
            name = entity.name,
            description = entity.description,
            photoUrl = entity.photoUrl,
            createdAt = entity.createdAt,
            lat = entity.lat,
            lon = entity.lon
        )
    }

    fun mapDomainToEntity(domain: StoryDomainTester): FavoriteStoryEntity {
        return FavoriteStoryEntity(
            id = domain.id,
            name = domain.name ?: "",
            description = domain.description ?: "",
            photoUrl = domain.photoUrl ?: "",
            createdAt = domain.createdAt ?: "",
            lat = domain.lat ?: 0.0,
            lon = domain.lon ?: 0.0
        )
    }

}
