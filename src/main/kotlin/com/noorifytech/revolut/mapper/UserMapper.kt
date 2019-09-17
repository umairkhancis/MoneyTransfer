package com.noorifytech.revolut.mapper

import com.noorifytech.revolut.dto.UserDto
import com.noorifytech.revolut.entity.Users
import org.jetbrains.exposed.sql.ResultRow

object UserMapper {
    fun toUserDto(row: ResultRow): UserDto =
            UserDto(id = row[Users.id],
                    name = row[Users.name],
                    email = row[Users.email],
                    dateUpdated = row[Users.dateUpdated]
            )
}