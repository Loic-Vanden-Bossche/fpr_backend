package com.esgi.infrastructure.persistence.strategies

import org.hibernate.boot.model.naming.Identifier
import org.hibernate.boot.model.naming.PhysicalNamingStrategy
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment
import java.util.*


class PhysicalStrategy : PhysicalNamingStrategy {
    override fun toPhysicalCatalogName(identifier: Identifier?, jdbcEnv: JdbcEnvironment?): Identifier? {
        return convertToSnakeCase(identifier)
    }

    override fun toPhysicalColumnName(identifier: Identifier?, jdbcEnv: JdbcEnvironment?): Identifier? {
        return convertToSnakeCase(identifier)
    }

    override fun toPhysicalSchemaName(identifier: Identifier?, jdbcEnv: JdbcEnvironment?): Identifier? {
        return convertToSnakeCase(identifier)
    }

    override fun toPhysicalSequenceName(identifier: Identifier?, jdbcEnv: JdbcEnvironment?): Identifier? {
        return convertToSnakeCase(identifier)
    }

    override fun toPhysicalTableName(identifier: Identifier?, jdbcEnv: JdbcEnvironment?): Identifier? {
        return convertToSnakeCase(identifier)
    }

    private fun convertToSnakeCase(identifier: Identifier?): Identifier? {
        if (identifier == null) return null

        val regex = "([a-z])([A-Z])"
        val replacement = "$1_$2"
        val newName: String = identifier.text
            .replace(regex, replacement)
            .uppercase(Locale.getDefault())
        return Identifier(newName, true)
    }
}