package com.noorifytech.revolut.dao.impl

import com.noorifytech.revolut.dao.UserDao
import com.noorifytech.revolut.dao.impl.db.H2Database
import com.noorifytech.revolut.dto.UserDto
import com.noorifytech.revolut.entity.Users
import com.noorifytech.revolut.mapper.UserMapper
import org.jetbrains.exposed.sql.*

class UserDaoImpl(private val db: H2Database, private val userMapper: UserMapper) : UserDao {
    override suspend fun get(): List<UserDto> = db.query {
        Users.selectAll().map { userMapper.toUserDto(it) }
    }

    override suspend fun get(id: Int): UserDto? = db.query {
        Users.select { (Users.id eq id) }
                .mapNotNull { userMapper.toUserDto(it) }
                .singleOrNull()
    }

    override suspend fun create(data: UserDto): UserDto {
        var key = 0
        db.query {
            key = (Users.insert {
                it[name] = data.name
                it[email] = data.email
                it[dateUpdated] = System.currentTimeMillis()
            } get Users.id)
        }

        return get(key)!!
    }

    override suspend fun update(data: UserDto): UserDto? {
        val id = data.id
        db.query {
            Users.update({ Users.id eq id }) {
                it[name] = data.name
                it[email] = data.email
                it[dateUpdated] = System.currentTimeMillis()
            }
        }

        return get(id)
    }

    override suspend fun delete(id: Int): Boolean = db.query {
        Users.deleteWhere { Users.id eq id } > 0
    }
}