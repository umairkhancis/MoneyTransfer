package com.noorifytech.revolut.service

import com.noorifytech.revolut.dao.impl.db.H2Database.query
import com.noorifytech.revolut.dto.ChangeType
import com.noorifytech.revolut.dto.NewWidgetDto
import com.noorifytech.revolut.dto.Notification
import com.noorifytech.revolut.dto.WidgetDto
import com.noorifytech.revolut.entity.Widgets
import org.jetbrains.exposed.sql.*

class WidgetService {

    private val listeners = mutableMapOf<Int, suspend (Notification<WidgetDto?>) -> Unit>()

    fun addChangeListener(id: Int, listener: suspend (Notification<WidgetDto?>) -> Unit) {
        listeners[id] = listener
    }

    fun removeChangeListener(id: Int) = listeners.remove(id)

    private suspend fun onChange(type: ChangeType, id: Int, entity: WidgetDto? = null) {
        listeners.values.forEach {
            it.invoke(Notification(type, id, entity))
        }
    }

    suspend fun getAllWidgets(): List<WidgetDto> = query {
        Widgets.selectAll().map { toWidget(it) }
    }

    suspend fun getWidget(id: Int): WidgetDto? = query {
        Widgets.select {
            (Widgets.id eq id)
        }.mapNotNull { toWidget(it) }
                .singleOrNull()
    }

    suspend fun updateWidget(widget: NewWidgetDto): WidgetDto? {
        val id = widget.id
        return if (id == null) {
            addWidget(widget)
        } else {
            query {
                    Widgets.update({ Widgets.id eq id }) {
                        it[name] = widget.name
                        it[quantity] = widget.quantity
                        it[dateUpdated] = System.currentTimeMillis()
                    }
            }

            getWidget(id).also {
                onChange(ChangeType.UPDATE, id, it)
            }
        }
    }

    suspend fun addWidget(widget: NewWidgetDto): WidgetDto {
        var key = 0
        query {
            key = (Widgets.insert {
                it[name] = widget.name
                it[quantity] = widget.quantity
                it[dateUpdated] = System.currentTimeMillis()
            } get Widgets.id)
        }
        return getWidget(key)!!.also {
            onChange(ChangeType.CREATE, key, it)
        }
    }

    suspend fun deleteWidget(id: Int): Boolean {
        return query {
            Widgets.deleteWhere { Widgets.id eq id } > 0
        }.also {
            if(it) onChange(ChangeType.DELETE, id)
        }
    }

    private fun toWidget(row: ResultRow): WidgetDto =
            WidgetDto(
                    id = row[Widgets.id],
                    name = row[Widgets.name],
                    quantity = row[Widgets.quantity],
                    dateUpdated = row[Widgets.dateUpdated]
            )
}
